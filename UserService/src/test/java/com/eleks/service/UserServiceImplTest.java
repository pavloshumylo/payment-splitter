package com.eleks.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.eleks.dto.UserRequestDto;
import com.eleks.dto.UserResponseDto;
import com.eleks.dto.UsersBulkRequestDto;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import com.eleks.exception.ResourceNotFoundException;
import com.eleks.mapper.UserMapper;
import com.eleks.repository.UserRepository;
import com.eleks.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long USER_ID = 3L;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    UserRepository userRepositoryMock;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDto userRequestDtoExpected;

    private UserEntity userEntityExpected;

    private UserResponseDto userResponseDtoExpected;

    @Before
    public void init() {
        userRequestDtoExpected =
                new UserRequestDto("testUserName", "testFirstName", "testLastName",
                        LocalDate.of(2010, 1, 1), "testEmail", true, "testPassword");

        userEntityExpected = new UserEntity(USER_ID, "testUserName", "testFirstName", "testLastName",
                LocalDate.of(2010, 1, 1), "testEmail", true, "testPassword");

        userResponseDtoExpected = new UserResponseDto(USER_ID, "testUserName", "testFirstName", "testLastName",
                LocalDate.of(2010, 1, 1), "testEmail", true);
    }

    @Test
    public void createUser_userDtoIsCorrect_shouldInvokeRepositoryMethodSaveOnce() {
        when(userRepositoryMock.findByUserName(userRequestDtoExpected.getUserName())).thenReturn(Optional.empty());
        when(userRepositoryMock.findByEmail(userRequestDtoExpected.getEmail())).thenReturn(Optional.empty());
        when(userMapperMock.convertToEntity(Collections.singletonList(userRequestDtoExpected))).thenReturn(Collections.singletonList(userEntityExpected));

        userService.createUser(userRequestDtoExpected);

        verify(userRepositoryMock).save(Collections.singletonList(userEntityExpected));
    }

    @Test
    public void createUser_userDtoDoesNotHaveUniqueName_shouldThrowInvalidUserDtoException() {
        when(userRepositoryMock.findByUserName(userRequestDtoExpected.getUserName())).thenReturn(Optional.of(new UserEntity()));

        assertThatThrownBy(() -> userService.createUser(userRequestDtoExpected))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("User name should be unique");
    }

    @Test
    public void createUser_userDtoDoesNotHaveUniqueEmail_shouldThrowInvalidUserDtoException() {
        when(userRepositoryMock.findByUserName(userRequestDtoExpected.getUserName())).thenReturn(Optional.empty());
        when(userRepositoryMock.findByEmail(userRequestDtoExpected.getEmail())).thenReturn(Optional.of(new UserEntity()));

        assertThatThrownBy(() -> userService.createUser(userRequestDtoExpected))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("User email should be unique");
    }

    @Test
    public void deleteUser_userExistInDb_shouldInvokeRepositoryMethodSaveOnce() {
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(new UserEntity()));

        userService.deleteUser(USER_ID);

        verify(userRepositoryMock).deleteById(USER_ID);
    }

    @Test
    public void deleteUser_userIsNotFoundInDbById_shouldThrowResourceNotFoundException() {
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User entity with id " + USER_ID + " doesn't exist");
    }

    @Test
    public void retrieveUser_userExistInDb_shouldReturnUserDtoWithProperId() {
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(userEntityExpected));
        when(userMapperMock.convertToDto(Collections.singletonList(userEntityExpected))).thenReturn(Collections.singletonList(userResponseDtoExpected));

        assertEquals(userResponseDtoExpected, userService.retrieveUser(USER_ID));
    }

    @Test
    public void retrieveUser_userIsNotFoundInDbById_shouldThrowResourceNotFoundException() {
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.retrieveUser(USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User entity with id " + USER_ID + " doesn't exist");
    }

    @Test
    public void updateUser_userDtoExist_shouldInvokeRepositoryMethodSaveOnce() {
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(new UserEntity()));
        when(userMapperMock.convertToEntity(USER_ID, Collections.singletonList(userRequestDtoExpected))).thenReturn(Collections.singletonList(userEntityExpected));

        userService.updateUser(USER_ID, userRequestDtoExpected);

        verify(userRepositoryMock).save(Collections.singletonList(userEntityExpected));
    }

    @Test
    public void updateUser_userDtoDoesNotExist_shouldThrowResourceNotFoundException() {
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(USER_ID, new UserRequestDto()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User entity with id " + USER_ID + " doesn't exist");
    }

    @Test
    public void bulkSearch_allUsersWithListedIdsExist_shouldReturnListOfProperUsers() {
        UserEntity userEntityExpectedSecond  =
                new UserEntity(2L, "testUserName", "testFirstName", "testLastName", LocalDate.of(2010, 1, 1), "testEmail", true, "testPassword");

        UserResponseDto userResponseDtoExpectedSecond = new UserResponseDto(2L, "testUserName", "testFirstName", "testLastName",
                LocalDate.of(2010, 1, 1), "testEmail", true);

        when(userRepositoryMock.findById(3L)).thenReturn(Optional.of(userEntityExpected));
        when(userRepositoryMock.findById(2L)).thenReturn(Optional.of(userEntityExpectedSecond));
        when(userMapperMock.convertToDto(Arrays.asList(userEntityExpected, userEntityExpectedSecond)))
                .thenReturn(Arrays.asList(userResponseDtoExpected, userResponseDtoExpectedSecond));

       assertEquals(Arrays.asList(userResponseDtoExpected, userResponseDtoExpectedSecond), userService.bulkSearch(new UsersBulkRequestDto(Arrays.asList(3L, 2L))));
    }

    @Test
    public void bulkSearch_usersWithListedIdsDoNotExist_shouldReturnEmptyListOfUsers() {
        when(userRepositoryMock.findById(3L)).thenReturn(Optional.empty());
        when(userRepositoryMock.findById(2L)).thenReturn(Optional.empty());
        when(userMapperMock.convertToDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), userService.bulkSearch(new UsersBulkRequestDto(Arrays.asList(3L, 2L))));
    }
}
