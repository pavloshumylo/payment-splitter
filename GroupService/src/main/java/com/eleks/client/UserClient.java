package com.eleks.client;

import com.eleks.dto.UserResponseDto;
import com.eleks.dto.UsersBulkRequestDto;
import com.eleks.exception.UserServiceException;

import java.util.List;

public interface UserClient {

    boolean areUserIdsValid(UsersBulkRequestDto usersBulkRequestDto) throws UserServiceException;

    List<UserResponseDto> retrieveUsersByIds(UsersBulkRequestDto usersBulkRequestDto);
}
