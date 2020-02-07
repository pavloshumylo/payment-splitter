package com.eleks.security;

import com.eleks.dto.UserPrincipal;
import com.eleks.util.JwtManagementUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationPrincipalSecurityUtil {

    private JwtManagementUtil jwtManagementUtil;

    public AuthenticationPrincipalSecurityUtil(JwtManagementUtil jwtManagementUtil) {
        this.jwtManagementUtil = jwtManagementUtil;
    }

    public void setAuthenticationPrincipal(String jwt)  throws AuthenticationServiceException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(jwtManagementUtil.retrieveUserPrincipalFromJwt(jwt), null, null));
    }

    public UserPrincipal retrievePrincipal() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
