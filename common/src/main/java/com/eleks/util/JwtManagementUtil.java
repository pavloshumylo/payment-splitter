package com.eleks.util;

import com.eleks.dto.UserPrincipal;
import com.eleks.exception.InvalidRequestException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

@Component
public class JwtManagementUtil {

    private String jwtSecret;

    private Integer jwtValidInterval;

    @Autowired
    public JwtManagementUtil(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.validInterval}") Integer jwtValidInterval) {
        this.jwtSecret = jwtSecret;
        this.jwtValidInterval = jwtValidInterval;
    }

    public String generateJwt(String username, Long userId) throws InvalidRequestException {

        Date issueTime = new Date();
        Date expTime = new Date(issueTime.getTime() + jwtValidInterval);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issueTime(issueTime)
                .expirationTime(expTime)
                .claim("username", username)
                .claim("userId", userId)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            signedJWT.sign(new MACSigner(MessageDigest.getInstance("SHA-256").digest(jwtSecret.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception ex) {
            throw new InvalidRequestException(ex.getMessage());
        }

        return signedJWT.serialize();
    }

    public UserPrincipal retrieveUserPrincipalFromJwt(String jwt) throws AuthenticationServiceException {

        try {
            SignedJWT signedJwt = SignedJWT.parse(jwt);

            verifyJwt(signedJwt);

            JWTClaimsSet jwtClaims = signedJwt.getJWTClaimsSet();
            String username = jwtClaims.getStringClaim("username");
            Long userId = jwtClaims.getLongClaim("userId");

            return new UserPrincipal(username, userId);
        } catch (Exception ex) {
            throw new AuthenticationServiceException(ex.getMessage());
        }
    }

    private void verifyJwt(SignedJWT signedJwt) throws Exception {

        boolean verifyJwt = signedJwt.verify(new MACVerifier(MessageDigest.getInstance("SHA-256")
                .digest(jwtSecret.getBytes(StandardCharsets.UTF_8))));

        if (!verifyJwt) {
            throw new InvalidRequestException("The JWT signature is invalid.");
        }

        if (new Date().after(signedJwt.getJWTClaimsSet().getExpirationTime())) {
            throw new InvalidRequestException("Jwt has expired.");
        }
    }
}
