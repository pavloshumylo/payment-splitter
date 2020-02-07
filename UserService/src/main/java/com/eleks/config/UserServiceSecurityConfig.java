package com.eleks.config;

import com.eleks.security.AuthenticationPrincipalSecurityUtil;
import com.eleks.security.config.BaseSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class UserServiceSecurityConfig extends BaseSecurityConfig {

    private UserDetailsService userDetailsService;

    @Autowired
    public UserServiceSecurityConfig(UserDetailsService userDetailsService, AuthenticationPrincipalSecurityUtil authenticationSecurityUtil) {
        super(authenticationSecurityUtil);
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.POST,"/login", "/users");
        super.configure(web);
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
