package com.eleks.service;

import com.eleks.dto.UserLoginRequest;

public interface AuthenticationService {

    void authenticateUser(UserLoginRequest userLoginRequest);
}
