package com.eleks.config;

import com.eleks.security.AuthenticationPrincipalSecurityUtil;
import com.eleks.security.config.BaseSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class GroupServiceSecurityConfig extends BaseSecurityConfig {

    @Autowired
    public GroupServiceSecurityConfig(AuthenticationPrincipalSecurityUtil authenticationSecurityUtil) {
        super(authenticationSecurityUtil);
    }
}
