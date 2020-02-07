package com.eleks.service.impl;

import com.eleks.client.UserClient;
import com.eleks.dto.*;
import com.eleks.entity.GroupEntity;
import com.eleks.entity.PaymentEntity;
import com.eleks.exception.InvalidRequestException;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.mapper.GroupMapper;
import com.eleks.repository.GroupRepository;
import com.eleks.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.RoundingMode.UP;

@Service
public class GroupServiceImpl implements GroupService {

    private GroupRepository groupRepository;

    private GroupMapper groupMapper;

    private UserClient userClient;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, GroupMapper groupMapper, UserClient userClient) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.userClient = userClient;
    }

    @Transactional
    public void createGroup(GroupRequestDto groupRequestDto) {
        if (groupRepository.findByGroupName(groupRequestDto.getGroupName()).isPresent()) {
            throw new InvalidRequestException("Group name should be unique");
        }

        if (userClient.areUserIdsValid(new UsersBulkRequestDto(groupRequestDto.getMembers()))) {
            groupRepository.save(groupMapper.convertToEntity(groupRequestDto));
        } else {
            throw new InvalidRequestException("Members ids " + groupRequestDto.getMembers() + " are not valid");
        }
    }

    @Override
    public GroupResponseDto retrieveGroup(Long groupId) {
        return groupMapper.convertToDto(groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group entity with id " + groupId + " doesn't exist")));
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        if (groupRepository.findById(groupId).isPresent()) {
            groupRepository.delete(groupId);
        } else {
            throw new ResourceNotFoundException("Group entity with id " + groupId + " doesn't exist");
        }
    }

    @Transactional
    public void updateGroup(Long groupId, GroupRequestDto groupRequestDto) {
        groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group entity with id " + groupId + " doesn't exist"));

        if (userClient.areUserIdsValid(new UsersBulkRequestDto(groupRequestDto.getMembers()))) {
            groupRepository.save(groupMapper.convertToEntity(groupId, groupRequestDto));
        } else {
            throw new InvalidRequestException("Members ids " + groupRequestDto.getMembers() + " are not valid");
        }
    }

    @Override
    public List<UserStatusResponse> retrieveGroupMemberOwings(Long groupId, Long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group entity with id " + groupId + " doesn't exist"));

        groupEntity.getMembers().stream()
                .filter(groupMember -> groupMember.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with " + userId + " isn't member of group"));

        Map<Long, UserStatusResponse> userStatusResponseMap = new HashMap<>();

        groupEntity.getMembers()
                .forEach(groupMember -> userStatusResponseMap.put(groupMember.getUserId(),
                        UserStatusResponse.builder()
                                .userId(groupMember.getUserId())
                                .value(new BigDecimal(0))
                                .currency(groupEntity.getCurrency())
                                .build()));

        groupEntity.getPayments()
                .forEach(paymentEntity -> calculateOwings(paymentEntity, userStatusResponseMap, userId));

        addUserNamesToUserStatusResponses(userStatusResponseMap);

        return userStatusResponseMap.values().stream()
                .filter(userStatusResponse -> !userStatusResponse.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    private void calculateOwings(PaymentEntity paymentEntity, Map<Long, UserStatusResponse> userStatusResponseMap, Long userId) {
        Set<Long> groupMembersIds = userStatusResponseMap.keySet();

        List<Long> owersIds = groupMembersIds.stream()
                .filter(groupMemberId -> !paymentEntity.getCoPayers().contains(groupMemberId))
                .collect(Collectors.toList());

        if (!owersIds.isEmpty()) {
            double owingSum = (paymentEntity.getPrice() / groupMembersIds.size()) / paymentEntity.getCoPayers().size();

            //when user id present in co payers list then add owing sum to your owers (+) or vise versa (-) (to whom you owe)
            if (paymentEntity.getCoPayers().contains(userId)) {
                owersIds.forEach(owerId -> {
                    UserStatusResponse userStatusResponseOwer = userStatusResponseMap.get(owerId);
                    userStatusResponseOwer.setValue(userStatusResponseOwer.getValue().add(new BigDecimal(owingSum)).setScale(3, UP));
                });
            } else {
                paymentEntity.getCoPayers().forEach(coPayer -> {
                    UserStatusResponse userStatusResponseCoPayer = userStatusResponseMap.get(coPayer);
                    userStatusResponseCoPayer.setValue(userStatusResponseCoPayer.getValue().subtract(new BigDecimal(owingSum)).setScale(3, UP));
                });
            }
        }
    }

    private void addUserNamesToUserStatusResponses(Map<Long, UserStatusResponse> userStatusResponseMap) {
        userClient.retrieveUsersByIds(new UsersBulkRequestDto(new ArrayList<>(userStatusResponseMap.keySet()))).stream()
                .filter(userResponseDto -> Objects.nonNull(userStatusResponseMap.get(userResponseDto.getId())))
                .forEach(userResponseDto -> userStatusResponseMap.get(userResponseDto.getId()).setUserName(userResponseDto.getUserName()));
    }
}
