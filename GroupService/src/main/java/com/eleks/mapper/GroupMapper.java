package com.eleks.mapper;

import com.eleks.dto.GroupRequestDto;
import com.eleks.dto.GroupResponseDto;
import com.eleks.entity.GroupEntity;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GroupMapper {

    public GroupResponseDto convertToDto(GroupEntity groupEntity) {
        if (Objects.isNull(groupEntity)) {
            throw new InvalidRequestException("Group entity can not be null");
        }

        return new GroupResponseDto(groupEntity.getId(), groupEntity.getGroupName(), groupEntity.getCurrency(),
                groupEntity.getMembers().stream()
                .map(UserEntity::getUserId)
                .collect(Collectors.toList()));
    }

    public GroupEntity convertToEntity(Long groupId, GroupRequestDto groupRequestDtoExpected) {
        if (Objects.isNull(groupRequestDtoExpected)) {
            throw new InvalidRequestException("Group request dto can not be null");
        }

        return GroupEntity.builder()
                .id(groupId)
                .groupName(groupRequestDtoExpected.getGroupName())
                .currency(groupRequestDtoExpected.getCurrency())
                .members(groupRequestDtoExpected.getMembers().stream()
                        .map(userId -> UserEntity.builder().userId(userId).build())
                        .collect(Collectors.toList()))
                .build();
    }

    public GroupEntity convertToEntity(GroupRequestDto groupRequestDtoExpected) {
        if (Objects.isNull(groupRequestDtoExpected)) {
            throw new InvalidRequestException("Group request dto can not be null");
        }

        return GroupEntity.builder()
                .groupName(groupRequestDtoExpected.getGroupName())
                .currency(groupRequestDtoExpected.getCurrency())
                .members(groupRequestDtoExpected.getMembers().stream()
                        .map(userId -> UserEntity.builder().userId(userId).build())
                        .collect(Collectors.toList()))
                .build();
    }
}
