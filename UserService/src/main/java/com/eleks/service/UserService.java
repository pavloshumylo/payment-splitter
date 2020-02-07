package com.eleks.service;

import com.eleks.dto.UserRequestDto;
import com.eleks.dto.UserResponseDto;
import com.eleks.dto.UsersBulkRequestDto;

import java.util.List;

public interface UserService {

    UserResponseDto retrieveUser(Long userId);

    void deleteUser(Long userId);

    void createUser(UserRequestDto userRequestDto);

    void updateUser(Long userId, UserRequestDto userRequestDto);

    List<UserResponseDto> bulkSearch(UsersBulkRequestDto bulkRequestDto);
}
