package com.eleks.security.config;

import com.eleks.security.AuthJwtSecurityFilter;
import com.eleks.security.AuthenticationPrincipalSecurityUtil;
import com.eleks.util.JwtManagementUtil;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

public class BaseSecurityConfig extends WebSecurityConfigurerAdapter {

    private AuthenticationPrincipalSecurityUtil authenticationSecurityUtil;

    public BaseSecurityConfig(AuthenticationPrincipalSecurityUtil authenticationSecurityUtil) {
        this.authenticationSecurityUtil = authenticationSecurityUtil;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(new BasicAuthenticationEntryPoint())
                .and().sessionManagement().sessionCreationPolicy(STATELESS)
                .and().addFilterBefore(new AuthJwtSecurityFilter(authenticationSecurityUtil), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/actuator/**");
    }
}
