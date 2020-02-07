package com.eleks.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eleks.dto.ErrorResponseDto;
import com.eleks.dto.UserRequestDto;
import com.eleks.dto.UserResponseDto;
import com.eleks.dto.UsersBulkRequestDto;
import com.eleks.handler.ErrorResponseExceptionHandler;
import com.eleks.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private static final String USER_CONTROLLER_GENERAL_PATH = "/users";

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userServiceMock;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserResponseDto userResponseDtoExpected;

    private UserRequestDto userRequestDto;

    @Before
    public void init() {
        userResponseDtoExpected =
                new UserResponseDto(1L, "testUserName", "testFirstName", "testLastName", LocalDate.of(2010, 10, 10), "testEmail", true);

        userRequestDto =
                new UserRequestDto("testUserName", "testFirstName", "testLastName", LocalDate.of(2010, 10, 10), "testEmail@com", true, "testPassword");

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ErrorResponseExceptionHandler())
                .build();
    }

    @Test
    public void retrieveUser_passUserId_shouldReturnOkHttpStatusWithRetrievedUser() throws Exception {
        when(userServiceMock.retrieveUser(eq(3L))).thenReturn(userResponseDtoExpected);

        MvcResult mvcResult = mockMvc.perform(get(USER_CONTROLLER_GENERAL_PATH + "/3"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        UserResponseDto userResponseDtoActual = objectMapper.readValue(responseString, UserResponseDto.class);

        assertEquals(userResponseDtoExpected, userResponseDtoActual);
    }

    @Test
    public void retrieveUser_urlNotFound_shouldReturnNotFoundHttpStatus() throws Exception {
        mockMvc.perform(get("/wrongUrl"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser_passUserId_shouldReturnOkHttpStatus() throws Exception {
        mockMvc.perform(delete(USER_CONTROLLER_GENERAL_PATH + "/3"))
                .andExpect(status().isOk());

        verify(userServiceMock).deleteUser(3L);
    }

    @Test
    public void deleteUser_urlNotFound_shouldReturnNotFoundHttpStatus() throws Exception {
        mockMvc.perform(delete("/wrongUrl"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createUser_passValidUserRequestDto_shouldReturnCreatedHttpStatus() throws Exception {
        mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated());

        verify(userServiceMock).createUser(userRequestDto);
    }

    @Test
    public void createUser_userNameSizeMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setUserName("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: User name shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_userNameIsBlank_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setUserName("  ");

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: User name shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_firstNameSizeMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setFirstName("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: First name shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_firstNameIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setFirstName(null);

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: First name shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_lastNameSizeMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setLastName("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Last name shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_lastNameIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setLastName(null);

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Last name shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_dateOfBirthIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setDateOfBirth(null);

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Date of birth shouldn't be null.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_emailHasWrongFormat_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setEmail("wrongEmail");

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Email should have email like format.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_receiveNotificationsIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setReceiveNotifications(null);

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Receive notifications shouldn't be null.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_passwordIsEmtpy_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setPassword("   ");

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Password shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createUser_passwordSizeIsMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setPassword("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Password shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_passValidUserRequestDto_shouldReturnOkHttpStatus() throws Exception {
        mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk());

        verify(userServiceMock).updateUser(1l, userRequestDto);
    }

    @Test
    public void updateUser_userNameSizeMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setUserName("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: User name shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_userNameIsBlank_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setUserName("  ");

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: User name shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_firstNameSizeMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setFirstName("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: First name shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_firstNameIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setFirstName(null);

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: First name shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_lastNameSizeMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setLastName("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Last name shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_lastNameIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setLastName(null);

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Last name shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_dateOfBirthIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setDateOfBirth(null);

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Date of birth shouldn't be null.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_emailHasWrongFormat_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setEmail("wrongEmail");

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Email should have email like format.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_receiveNotificationsIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setReceiveNotifications(null);

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Receive notifications shouldn't be null.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_passwordIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setPassword(null);

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Password shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateUser_passwordSizeIsMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        userRequestDto.setPassword("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(put(USER_CONTROLLER_GENERAL_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Password shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void bulkSearch_usersBulkRequestDtoIsValid_shouldReturnOkHttpStatusAndProperResponseBody() throws Exception {
        UsersBulkRequestDto bulkRequestDto = new UsersBulkRequestDto(Arrays.asList(1L, 4L));

        when(userServiceMock.bulkSearch(bulkRequestDto)).thenReturn(Collections.singletonList(userResponseDtoExpected));

        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bulkRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<UserResponseDto> usersResponseDtoActual = objectMapper.readValue(responseString, new TypeReference<List<UserResponseDto>>() {});

        assertEquals(Collections.singletonList(userResponseDtoExpected), usersResponseDtoActual);
    }

    @Test
    public void bulkSearch_userIdsFieldIsEmpty_shouldReturnBadRequestHttpStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UsersBulkRequestDto(Collections.emptyList()))))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Can not be null or empty.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void bulkSearch_userIdsFieldIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(USER_CONTROLLER_GENERAL_PATH + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UsersBulkRequestDto())))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Can not be null or empty.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }
}
