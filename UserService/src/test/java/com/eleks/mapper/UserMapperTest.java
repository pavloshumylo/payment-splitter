package com.eleks.mapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.eleks.dto.UserRequestDto;
import com.eleks.dto.UserResponseDto;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class UserMapperTest {

    private UserMapper userMapper = new UserMapper();

    private UserRequestDto userRequestDtoExpected;

    private UserResponseDto userResponseDtoExpected;

    private UserEntity userEntityExpected;

    @Before
    public void init() {
        userRequestDtoExpected = new UserRequestDto("testUserName", "testFirstName", "testLastName",
                LocalDate.of(2010, 1, 1), "testEmail", true, "testPassword");

        userResponseDtoExpected = new UserResponseDto(2L, "testUserName", "testFirstName", "testLastName",
                LocalDate.of(2010, 1, 1), "testEmail", true);

        userEntityExpected = UserEntity.builder()
                .userName("testUserName")
                .firstName("testFirstName")
                .lastName("testLastName")
                .dateOfBirth(LocalDate.of(2010, 1, 1))
                .email("testEmail")
                .receiveNotifications(true)
                .password("testPassword")
                .build();
    }

    @Test
    public void convertToEntity_userRequestDtosListNotNull_shouldReturnProperEntity() {
        List<UserEntity> userEntityListActual =
                userMapper.convertToEntity(Collections.singletonList(userRequestDtoExpected));

        assertThat(userEntityListActual).usingElementComparatorIgnoringFields("password")
                .isEqualTo(Collections.singletonList(userEntityExpected));

        assertTrue(new BCryptPasswordEncoder().matches(userRequestDtoExpected.getPassword(), userEntityListActual.get(0).getPassword()));
    }

    @Test
    public void convertToEntity_userRequestDtosListIsNull_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> userMapper.convertToEntity(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("List of user requests dtos is null or empty");
    }

    @Test
    public void convertToEntity_userRequestDtosListIsEmpty_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> userMapper.convertToEntity(Collections.emptyList()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("List of user requests dtos is null or empty");
    }

    @Test
    public void convertToEntityWithId_userRequestDtosListNotNull_shouldReturnProperEntity() {
        userEntityExpected.setId(2L);

        List<UserEntity> userEntityListActual =
                userMapper.convertToEntity(2L, Collections.singletonList(userRequestDtoExpected));

        assertThat(userEntityListActual).usingElementComparatorIgnoringFields("password")
                .isEqualTo(Collections.singletonList(userEntityExpected));

        assertTrue(new BCryptPasswordEncoder().matches(userRequestDtoExpected.getPassword(), userEntityListActual.get(0).getPassword()));
    }

    @Test
    public void convertToEntityWithId_userRequestDtosListIsNull_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> userMapper.convertToEntity(2L,null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("List of user requests dtos is null or empty");
    }

    @Test
    public void convertToEntityWithId_userRequestDtosListIsEmpty_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> userMapper.convertToEntity(2L,Collections.emptyList()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("List of user requests dtos is null or empty");
    }

    @Test
    public void convertToDto_userEntityListNotNull_shouldReturnProperUserResponseDto() {
        userEntityExpected.setId(2L);

        assertEquals(Collections.singletonList(userResponseDtoExpected), userMapper.convertToDto(Collections.singletonList(userEntityExpected)));
    }

    @Test
    public void convertToDto_userEntityListIsNull_shouldThrowInvalidRequestException() {
        assertThatThrownBy(() -> userMapper.convertToDto(null))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("User entity list is null");
    }
}
