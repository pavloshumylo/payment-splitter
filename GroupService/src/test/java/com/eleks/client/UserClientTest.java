package com.eleks.client;

import com.eleks.client.config.RestTemplateConfig;
import com.eleks.client.impl.UserClientImpl;
import com.eleks.dto.UserResponseDto;
import com.eleks.dto.UsersBulkRequestDto;
import com.eleks.exception.UserServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserClientImpl.class, RestTemplateConfig.class})
public class UserClientTest {

    @Autowired
    private UserClientImpl userClient;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        Field field = ReflectionUtils.findField(UserClientImpl.class, "baseEndPointsUrl");
        field.setAccessible(true);
        ReflectionUtils.setField(field, userClient, "http://localhost:" + wireMockRule.port());
    }

    @Test
    public void searchUsersByIds_usersIdsCountSameAsReturnedUsersCount_shouldReturnTrue() throws JsonProcessingException {

        UsersBulkRequestDto usersBulkRequestDto = new UsersBulkRequestDto(Arrays.asList(1L, 4L));

        UserResponseDto userResponseDtoExpectedFirst =
                new UserResponseDto(1L, "testUserName", "testFirstName", "testLastName",
                        LocalDate.of(2010, 10, 10), "testEmail", true);

        UserResponseDto userResponseDtoExpectedSecond =
                new UserResponseDto(4L, "testUserName", "testFirstName", "testLastName",
                        LocalDate.of(2010, 10, 10), "testEmail", true);


        stubFor(post(urlMatching("/users/search"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(usersBulkRequestDto)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(Arrays.asList(userResponseDtoExpectedFirst, userResponseDtoExpectedSecond)))));

        assertTrue(userClient.areUserIdsValid(usersBulkRequestDto));
    }

    @Test
    public void searchUsersByIds_usersIdsCountNotSameAsReturnedUsersCount_shouldReturnFalse() throws JsonProcessingException {

        UsersBulkRequestDto usersBulkRequestDto = new UsersBulkRequestDto(Arrays.asList(1L, 4L));

        UserResponseDto userResponseDtoExpected =
                new UserResponseDto(1L, "testUserName", "testFirstName", "testLastName",
                        LocalDate.of(2010, 10, 10), "testEmail", true);

        stubFor(post(urlMatching("/users/search"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(usersBulkRequestDto)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(Collections.singletonList(userResponseDtoExpected)))));

        assertFalse(userClient.areUserIdsValid(usersBulkRequestDto));
    }

    @Test
    public void searchUsersByIds_responseWithBadRequestHttpStatus_shouldReturnFalse() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(400)));

        assertFalse(userClient.areUserIdsValid(new UsersBulkRequestDto(Arrays.asList(1L, 4L))));
    }

    @Test
    public void searchUsersByIds_responseWithNotFoundHttpStatus_shouldReturnFalse() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(404)));

        assertFalse(userClient.areUserIdsValid(new UsersBulkRequestDto(Arrays.asList(1L, 4L))));
    }

    @Test
    public void searchUsersByIds_responseWithConflictHttpStatus_shouldReturnFalse() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(409)));

        assertFalse(userClient.areUserIdsValid(new UsersBulkRequestDto(Arrays.asList(1L, 4L))));
    }

    @Test
    public void searchUsersByIds_responseWithInternalServerErrorHttpStatus_shouldThrowUserServiceException() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> userClient.areUserIdsValid(new UsersBulkRequestDto(Arrays.asList(1L, 4L))))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("500 Server Error");
    }

    @Test
    public void searchUsersByIds_responseWithServiceUnavailableHttpStatus_shouldThrowUserServiceException() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(503)));

        assertThatThrownBy(() -> userClient.areUserIdsValid(new UsersBulkRequestDto(Arrays.asList(1L, 4L))))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("503 Service Unavailable");
    }

    @Test
    public void retrieveUsersByIds_responseEntityHasBody_shouldReturnListOfUserResponseDto() throws JsonProcessingException {

        UsersBulkRequestDto usersBulkRequestDto = new UsersBulkRequestDto(Arrays.asList(1L, 4L));

        List<UserResponseDto> userResponseDtoListExpected = Arrays.asList(new UserResponseDto(4L, "testUserName", "testFirstName", "testLastName",
                LocalDate.of(2010, 10, 10), "testEmail", true), new UserResponseDto(1L, "testUserName", "testFirstName", "testLastName",
                LocalDate.of(2010, 10, 10), "testEmail", true));

        stubFor(post(urlMatching("/users/search"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(usersBulkRequestDto)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(userResponseDtoListExpected))));

        assertEquals(userResponseDtoListExpected, userClient.retrieveUsersByIds(usersBulkRequestDto));
    }

    @Test
    public void retrieveUsersByIds_responseEntityHasNoBody_shouldThrowUserServiceException() throws JsonProcessingException {

        UsersBulkRequestDto usersBulkRequestDto = new UsersBulkRequestDto(Arrays.asList(1L, 4L));

        stubFor(post(urlMatching("/users/search"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(usersBulkRequestDto)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)));

        assertThatThrownBy(() -> userClient.retrieveUsersByIds(new UsersBulkRequestDto(Arrays.asList(1L, 4L))))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("Response from user client has no body");
    }

    @Test
    public void retrieveUsersByIds_responseWithBadRequestHttpStatus_shouldThrowUserServiceException() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(400)));

        assertThatThrownBy(() -> userClient.retrieveUsersByIds(new UsersBulkRequestDto(Arrays.asList(1L, 4L))))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("400 Bad Request");
    }

    @Test
    public void retrieveUsersByIds_responseWithNotFoundHttpStatus_shouldThrowUserServiceException() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(404)));

        assertThatThrownBy(() -> userClient.retrieveUsersByIds(new UsersBulkRequestDto(Arrays.asList(1L, 4L))))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("404 Not Found");
    }

    @Test
    public void retrieveUsersByIds_responseWithConflictHttpStatus_shouldThrowUserServiceException() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(409)));

        assertThatThrownBy(() -> userClient.retrieveUsersByIds(new UsersBulkRequestDto(Arrays.asList(1L, 4L))))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("409 Conflict");
    }

    @Test
    public void retrieveUsersByIds_responseWithInternalServerErrorHttpStatus_shouldThrowUserServiceException() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> userClient.retrieveUsersByIds(new UsersBulkRequestDto(Arrays.asList(1L, 4L))))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("500 Server Error");
    }

    @Test
    public void retrieveUsersByIds_responseWithServiceUnavailableHttpStatus_shouldThrowUserServiceException() {

        stubFor(post(urlMatching("/users/search"))
                .willReturn(aResponse().withStatus(503)));

        assertThatThrownBy(() -> userClient.retrieveUsersByIds(new UsersBulkRequestDto(Arrays.asList(1L, 4L))))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("503 Service Unavailable");
    }
}
