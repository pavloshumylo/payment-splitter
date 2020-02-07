package com.eleks.mapper;

import com.eleks.dto.UserRequestDto;
import com.eleks.dto.UserResponseDto;
import com.eleks.entity.UserEntity;
import com.eleks.exception.InvalidRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public List<UserEntity> convertToEntity(List<UserRequestDto> userDtos) {
        if (Objects.isNull(userDtos) || userDtos.isEmpty()) {
            throw new InvalidRequestException("List of user requests dtos is null or empty");
        }

        return userDtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    public List<UserEntity> convertToEntity(Long userId, List<UserRequestDto> userDtos) {
        if (Objects.isNull(userDtos) || userDtos.isEmpty()) {
            throw new InvalidRequestException("List of user requests dtos is null or empty");
        }

        return userDtos.stream()
                .map(userDto -> mapToEntityWithId(userId, userDto))
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> convertToDto(List<UserEntity> userEntities) {
        if (Objects.isNull(userEntities)) {
            throw new InvalidRequestException("User entity list is null");
        }

        return userEntities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private UserEntity mapToEntity(UserRequestDto userDto) {

        return UserEntity.builder()
                .userName(userDto.getUserName())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .dateOfBirth(userDto.getDateOfBirth())
                .email(userDto.getEmail())
                .receiveNotifications(userDto.getReceiveNotifications())
                .password(Objects.nonNull(userDto.getPassword()) ? new BCryptPasswordEncoder().encode(userDto.getPassword()) : null)
                .build();
    }

    private UserEntity mapToEntityWithId(Long userId, UserRequestDto userDto) {

        return UserEntity.builder()
                .id(userId)
                .userName(userDto.getUserName())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .dateOfBirth(userDto.getDateOfBirth())
                .email(userDto.getEmail())
                .receiveNotifications(userDto.getReceiveNotifications())
                .password(Objects.nonNull(userDto.getPassword()) ? new BCryptPasswordEncoder().encode(userDto.getPassword()) : null)
                .build();
    }

    private UserResponseDto mapToDto(UserEntity userEntity) {

        return new UserResponseDto(userEntity.getId(), userEntity.getUserName(), userEntity.getFirstName(),
                userEntity.getLastName(), userEntity.getDateOfBirth(), userEntity.getEmail(), userEntity.getReceiveNotifications());
    }
}
