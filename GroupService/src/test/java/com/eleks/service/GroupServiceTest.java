package com.eleks.service;

import com.eleks.client.UserClient;
import com.eleks.dto.*;
import com.eleks.entity.GroupEntity;
import com.eleks.entity.PaymentEntity;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.mapper.GroupMapper;
import com.eleks.repository.GroupRepository;
import com.eleks.service.impl.GroupServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupServiceTest {

    @Mock
    private GroupMapper groupMapperMock;

    @Mock
    private GroupRepository groupRepositoryMock;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private GroupServiceImpl groupService;

    private GroupRequestDto groupRequestDtoExpected;

    private GroupEntity groupEntityExpected;

    private GroupResponseDto groupResponseDtoExpected;

    @Before
    public void init() {
        groupRequestDtoExpected = new GroupRequestDto("testGroupName", "USD", Arrays.asList(1L, 2L));

        groupEntityExpected = GroupEntity.builder()
                .groupName("testGroupName")
                .currency("USD")
                .members(Arrays.asList(UserEntity.builder().userId(1L).build(), UserEntity.builder().userId(2L).build()))
                .build();

        groupResponseDtoExpected = new GroupResponseDto(1L, "testGroupName", "USD", Arrays.asList(1L, 2L));
    }

    @Test
    public void retrieveGroup_groupExistInDb_shouldReturnGroupDtoWithProperId() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));
        when(groupMapperMock.convertToDto(groupEntityExpected)).thenReturn(groupResponseDtoExpected);

        assertEquals(groupResponseDtoExpected, groupService.retrieveGroup(3L));
    }

    @Test
    public void retrieveGroup_groupIsNotFoundInDbById_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.retrieveGroup(3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Group entity with id " + 3 + " doesn't exist");
    }

    @Test
    public void deleteGroup_groupExistInDb_shouldInvokeRepositoryMethodDeleteOnce() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(new GroupEntity()));

        groupService.deleteGroup(3L);

        verify(groupRepositoryMock).delete(3L);
    }

    @Test
    public void deleteGroup_groupIsNotFoundInDbById_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.deleteGroup(3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Group entity with id " + 3 + " doesn't exist");
    }

    @Test
    public void updateGroup_groupWithProperIdExists_shouldInvokeRepositoryMethodSaveOnce() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(new GroupEntity()));
        when(groupMapperMock.convertToEntity(3L, groupRequestDtoExpected)).thenReturn(groupEntityExpected);
        when(userClient.areUserIdsValid(new UsersBulkRequestDto(groupRequestDtoExpected.getMembers()))).thenReturn(true);

        groupService.updateGroup(3L, groupRequestDtoExpected);

        verify(groupRepositoryMock).save(groupEntityExpected);
    }

    @Test
    public void updateGroup_groupWithProperIdDoesNotExists_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.updateGroup(3L, new GroupRequestDto()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Group entity with id " + 3 + " doesn't exist");
    }

    @Test
    public void updateGroup_membersIdsAreNotValid_shouldThrowInvalidRequestException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(new GroupEntity()));
        when(userClient.areUserIdsValid(new UsersBulkRequestDto(groupRequestDtoExpected.getMembers()))).thenReturn(false);

        assertThatThrownBy(() -> groupService.updateGroup(3L, groupRequestDtoExpected))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Members ids " + groupRequestDtoExpected.getMembers() + " are not valid");
    }

    @Test
    public void createGroup_groupNameIsNotUnique_shouldThrowInvalidRequestException() {
        when(groupRepositoryMock.findByGroupName(groupRequestDtoExpected.getGroupName())).thenReturn(Optional.of(new GroupEntity()));

        assertThatThrownBy(() -> groupService.createGroup(groupRequestDtoExpected))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Group name should be unique");
    }

    @Test
    public void createGroup_membersIdsAreNotValid_shouldThrowInvalidRequestException() {
        when(groupRepositoryMock.findByGroupName(groupRequestDtoExpected.getGroupName())).thenReturn(Optional.empty());
        when(userClient.areUserIdsValid(new UsersBulkRequestDto(groupRequestDtoExpected.getMembers())))
                .thenReturn(false);

        assertThatThrownBy(() -> groupService.createGroup(groupRequestDtoExpected))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Members ids " + groupRequestDtoExpected.getMembers() + " are not valid");
    }

    @Test
    public void createGroup_membersIdsAreValid_shouldInvokeRepositoryMethodSaveOnce() {
        when(groupRepositoryMock.findByGroupName(groupRequestDtoExpected.getGroupName())).thenReturn(Optional.empty());
        when(userClient.areUserIdsValid(new UsersBulkRequestDto(groupRequestDtoExpected.getMembers()))).thenReturn(true);
        when(groupMapperMock.convertToEntity(groupRequestDtoExpected)).thenReturn(groupEntityExpected);

        groupService.createGroup(groupRequestDtoExpected);

        verify(groupRepositoryMock).save(groupEntityExpected);
    }

    @Test
    public void retrieveGroupMemberOwings_groupWithProperIdDoesNotExist_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.retrieveGroupMemberOwings(3L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Group entity with id 3 doesn't exist");
    }

    @Test
    public void retrieveGroupMemberOwings_userWithProperIdIsNotMemberOfGroup_shouldThrowResourceNotFoundException() {
        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        groupEntityExpected.setMembers(Arrays.asList(
                UserEntity.builder().userId(1L).build(),
                UserEntity.builder().userId(3L).build(),
                UserEntity.builder().userId(4L).build()));

        assertThatThrownBy(() -> groupService.retrieveGroupMemberOwings(3L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with 2 isn't member of group");
    }

    @Test
    public void retrieveGroupMemberOwings_groupIdAndUserIdValidParamsWith4Payments_shouldReturnProperListOfUserStatusResponses() {
        PaymentEntity paymentEntityFirst = PaymentEntity.builder()
                .coPayers(Arrays.asList(1L, 2L, 4L))
                .price(450.25)
                .build();

        PaymentEntity paymentEntitySecond = PaymentEntity.builder()
                .coPayers(Arrays.asList(2L, 4L))
                .price(505.5)
                .build();

        PaymentEntity paymentEntityThird = PaymentEntity.builder()
                .coPayers(Collections.singletonList(4L))
                .price(300.0)
                .build();

        PaymentEntity paymentEntityFourth = PaymentEntity.builder()
                .coPayers(Arrays.asList(1L, 3L, 4L, 5L))
                .price(1500.0)
                .build();

        groupEntityExpected.setMembers(Arrays.asList(
                UserEntity.builder().userId(1L).build(),
                UserEntity.builder().userId(2L).build(),
                UserEntity.builder().userId(3L).build(),
                UserEntity.builder().userId(4L).build(),
                UserEntity.builder().userId(5L).build()));

        List<UserStatusResponse> userStatusResponsesExpected = Arrays.asList(
                UserStatusResponse.builder().userId(1L).userName("testFirstName").currency("USD").value(new BigDecimal("-24.450")).build(),
                UserStatusResponse.builder().userId(3L).userName("testThirdName").currency("USD").value(new BigDecimal("5.567")).build(),
                UserStatusResponse.builder().userId(4L).userName("testFourthName").currency("USD").value(new BigDecimal("-135.000")).build(),
                UserStatusResponse.builder().userId(5L).userName("testFifthName").currency("USD").value(new BigDecimal("5.567")).build());

        groupEntityExpected.setPayments(Arrays.asList(paymentEntityFirst, paymentEntitySecond, paymentEntityThird, paymentEntityFourth));

        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        when(userClient.retrieveUsersByIds(new UsersBulkRequestDto(Arrays.asList(1L, 2L, 3L, 4L, 5L)))).thenReturn(Arrays.asList(
                UserResponseDto.builder().id(1L).userName("testFirstName").build(),
                UserResponseDto.builder().id(2L).userName("testSecondName").build(),
                UserResponseDto.builder().id(3L).userName("testThirdName").build(),
                UserResponseDto.builder().id(4L).userName("testFourthName").build(),
                UserResponseDto.builder().id(5L).userName("testFifthName").build()));

        assertEquals(userStatusResponsesExpected, groupService.retrieveGroupMemberOwings(3L, 2L));
    }

    @Test
    public void retrieveGroupMemberOwings_groupIdAndUserIdValidParamsWith1Payment_shouldReturnProperListOfUserStatusResponses() {
        PaymentEntity paymentEntityFirst = PaymentEntity.builder()
                .coPayers(Collections.singletonList(2L))
                .price(100.0)
                .build();

        groupEntityExpected.setMembers(Arrays.asList(
                UserEntity.builder().userId(1L).build(),
                UserEntity.builder().userId(2L).build(),
                UserEntity.builder().userId(3L).build(),
                UserEntity.builder().userId(4L).build()));

        groupEntityExpected.setPayments(Collections.singletonList(paymentEntityFirst));

        List<UserStatusResponse> userStatusResponsesExpected = Arrays.asList(
                UserStatusResponse.builder().userName("testSecondName").userId(2L).currency("USD").value(new BigDecimal("-25.000")).build(),
                UserStatusResponse.builder().userName("testThirdName").userId(3L).currency("USD").value(new BigDecimal("0")).build(),
                UserStatusResponse.builder().userName("testFourthName").userId(4L).currency("USD").value(new BigDecimal("0")).build());

        when(groupRepositoryMock.findById(3L)).thenReturn(Optional.of(groupEntityExpected));

        when(userClient.retrieveUsersByIds(new UsersBulkRequestDto(Arrays.asList(1L, 2L, 3L, 4L)))).thenReturn(Arrays.asList(
                UserResponseDto.builder().id(1L).userName("testFirstName").build(),
                UserResponseDto.builder().id(2L).userName("testSecondName").build(),
                UserResponseDto.builder().id(3L).userName("testThirdName").build(),
                UserResponseDto.builder().id(4L).userName("testFourthName").build()));

        assertEquals(userStatusResponsesExpected, groupService.retrieveGroupMemberOwings(3L, 1L));
    }
}
