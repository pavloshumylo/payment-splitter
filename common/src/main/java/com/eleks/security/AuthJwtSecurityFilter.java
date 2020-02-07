package com.eleks.security;

import com.eleks.util.JwtManagementUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthJwtSecurityFilter extends OncePerRequestFilter {

    private static final String BEARER_HEADER = "Bearer ";

    private AuthenticationPrincipalSecurityUtil authenticationSecurityUtil;

    public AuthJwtSecurityFilter(AuthenticationPrincipalSecurityUtil authenticationSecurityUtil) {
        this.authenticationSecurityUtil = authenticationSecurityUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            String jwt = retrieveJwtFromHeader(request);

            authenticationSecurityUtil.setAuthenticationPrincipal(jwt);

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        }
    }

    private String retrieveJwtFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.isEmpty(authHeader) && authHeader.startsWith(BEARER_HEADER)) {
            String jwt = authHeader.substring(BEARER_HEADER.length());

            if (StringUtils.isEmpty(jwt)) {
                throw new AuthenticationServiceException("Passed header with Bearer but without jwt");
            }

            return jwt.trim();

        } else {
            throw new AuthenticationServiceException("Invalid Authorization header. Missing \"Bearer \".");
        }
    }
}
