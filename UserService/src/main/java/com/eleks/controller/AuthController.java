package com.eleks.controller;

import com.eleks.dto.UserDetailsCustom;
import com.eleks.dto.UserJwtResponse;
import com.eleks.dto.UserLoginRequest;
import com.eleks.service.AuthenticationService;
import com.eleks.service.impl.UserDetailsServiceImpl;
import com.eleks.util.JwtManagementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
public class AuthController {

    private AuthenticationService authenticationService;

    private JwtManagementUtil jwtManagementUtil;

    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthController(AuthenticationService authenticationService, JwtManagementUtil jwtManagementUtil, UserDetailsServiceImpl userDetailsService) {
        this.authenticationService = authenticationService;
        this.jwtManagementUtil = jwtManagementUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/login")
    UserJwtResponse login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        authenticationService.authenticateUser(userLoginRequest);

        UserDetailsCustom userDetails = userDetailsService.loadUserByUsername(userLoginRequest.getUserName());
        return new UserJwtResponse(jwtManagementUtil.generateJwt(userDetails.getUsername(), userDetails.getUserId()));
    }
}
