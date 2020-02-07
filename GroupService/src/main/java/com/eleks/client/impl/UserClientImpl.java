package com.eleks.client.impl;

import com.eleks.client.UserClient;
import com.eleks.dto.UserResponseDto;
import com.eleks.dto.UsersBulkRequestDto;
import com.eleks.exception.UserServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class UserClientImpl implements UserClient {

    private String baseEndPointsUrl;

    private RestTemplate restTemplate;

    @Autowired
    public UserClientImpl(@Value("${users.endPointHost}") String baseEndPointsUrl, RestTemplate restTemplate) {
        this.baseEndPointsUrl = baseEndPointsUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean areUserIdsValid(UsersBulkRequestDto usersBulkRequestDto) throws UserServiceException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UsersBulkRequestDto> httpEntity = new HttpEntity<>(usersBulkRequestDto, headers);

        ResponseEntity<List<UserResponseDto>> usersListResponseEntity;

        try {
            usersListResponseEntity = restTemplate.exchange(baseEndPointsUrl + "/users/search",
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<List<UserResponseDto>>() {
                    });

        } catch (HttpClientErrorException ex) {
            return false;
        } catch (HttpServerErrorException ex) {
            throw new UserServiceException(ex.getMessage(), ex);
        }

        return usersListResponseEntity.hasBody() && usersListResponseEntity.getBody().size() == usersBulkRequestDto.getUserIds().size();
    }

    @Override
    public List<UserResponseDto> retrieveUsersByIds(UsersBulkRequestDto usersBulkRequestDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UsersBulkRequestDto> httpEntity = new HttpEntity<>(usersBulkRequestDto, headers);

        ResponseEntity<List<UserResponseDto>> usersListResponseEntity;

        try {
            usersListResponseEntity = restTemplate.exchange(baseEndPointsUrl + "/users/search",
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<List<UserResponseDto>>() {
                    });

            if (!usersListResponseEntity.hasBody()) {
                throw new UserServiceException("Response from user client has no body");
            }

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new UserServiceException(ex.getMessage(), ex);
        }

        return usersListResponseEntity.getBody();
    }
}
