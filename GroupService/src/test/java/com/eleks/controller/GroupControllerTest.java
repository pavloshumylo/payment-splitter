package com.eleks.controller;

import com.eleks.dto.ErrorResponseDto;
import com.eleks.dto.GroupRequestDto;
import com.eleks.dto.GroupResponseDto;
import com.eleks.dto.UserStatusResponse;
import com.eleks.handler.ErrorResponseExceptionHandler;
import com.eleks.service.GroupService;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class GroupControllerTest {

    private static final String GROUP_CONTROLLER_GENERAL_PATH = "/groups";

    @InjectMocks
    private GroupController groupController;

    @Mock
    private GroupService groupServiceMock;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private GroupRequestDto groupRequestDto;

    @Before
    public void init() {
        groupRequestDto = new GroupRequestDto("testGroupName", "USD", Arrays.asList(1L, 2L));

        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
                .setControllerAdvice(new ErrorResponseExceptionHandler())
                .build();
    }

    @Test
    public void createGroup_passValidGroupRequestDto_shouldReturnCreatedHttpStatus() throws Exception {
        mockMvc.perform(post(GROUP_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isCreated());

        verify(groupServiceMock).createGroup(groupRequestDto);
    }

    @Test
    public void createGroup_groupNameIsBlank_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setGroupName("  ");

        MvcResult mvcResult = mockMvc.perform(post(GROUP_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Group name shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createGroup_groupNameSizeMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setGroupName("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(post(GROUP_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Group name shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createGroup_membersFiledIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setMembers(null);

        MvcResult mvcResult = mockMvc.perform(post(GROUP_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Members field shouldn't be null.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createGroup_membersSizeLessThan1_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setMembers(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(post(GROUP_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Members size should be min 1.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createGroup_invalidCurrency_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setCurrency("invalidCurrency");

        MvcResult mvcResult = mockMvc.perform(post(GROUP_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Supported currencies: UA, USD, EUR.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void retrieveGroup_passGroupId_shouldReturnOkHttpStatusWithRetrievedGroup() throws Exception {
        GroupResponseDto groupResponseDtoExpected =
                new GroupResponseDto(1L, "testGroupName", "USD", Arrays.asList(1L, 2L));

        when(groupServiceMock.retrieveGroup(eq(3L))).thenReturn(groupResponseDtoExpected);

        MvcResult mvcResult = mockMvc.perform(get(GROUP_CONTROLLER_GENERAL_PATH + "/3"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        GroupResponseDto groupResponseDtoActual = objectMapper.readValue(responseString, GroupResponseDto.class);

        assertEquals(groupResponseDtoExpected, groupResponseDtoActual);
    }

    @Test
    public void retrieveGroup_urlNotFound_shouldReturnNotFoundHttpStatus() throws Exception {
        mockMvc.perform(get("/wrongUrl"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteGroup_passGroupId_shouldReturnOkHttpStatus() throws Exception {
        mockMvc.perform(delete(GROUP_CONTROLLER_GENERAL_PATH + "/3"))
                .andExpect(status().isOk());

        verify(groupServiceMock).deleteGroup(3L);
    }

    @Test
    public void deleteGroup_urlNotFound_shouldReturnNotFoundHttpStatus() throws Exception {
        mockMvc.perform(delete("/wrongUrl"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateGroup_passValidGroupRequestDto_shouldReturnOkHttpStatus() throws Exception {
        mockMvc.perform(put(GROUP_CONTROLLER_GENERAL_PATH + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isOk());

        verify(groupServiceMock).updateGroup(3L, groupRequestDto);
    }

    @Test
    public void updateGroup_groupNameIsBlank_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setGroupName("  ");

        MvcResult mvcResult = mockMvc.perform(put(GROUP_CONTROLLER_GENERAL_PATH + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Group name shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateGroup_groupNameSizeMoreThan50Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setGroupName("testtesttesttesttesttesttesttesttesttesttesttesttte");

        MvcResult mvcResult = mockMvc.perform(put(GROUP_CONTROLLER_GENERAL_PATH + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Group name shouldn't be more than 50 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateGroup_membersFiledIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setMembers(null);

        MvcResult mvcResult = mockMvc.perform(put(GROUP_CONTROLLER_GENERAL_PATH + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Members field shouldn't be null.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateGroup_membersSizeLessThan1_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setMembers(Collections.emptyList());

        MvcResult mvcResult = mockMvc.perform(put(GROUP_CONTROLLER_GENERAL_PATH + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Members size should be min 1.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void updateGroup_invalidCurrency_shouldReturnBadRequestHttpStatus() throws Exception {
        groupRequestDto.setCurrency("invalidCurrency");

        MvcResult mvcResult = mockMvc.perform(put(GROUP_CONTROLLER_GENERAL_PATH + "/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Supported currencies: UA, USD, EUR.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void retrieveGroupMemberOwings_pathVariablesGroupIdAndUserIdPresent_shouldReturnOkHttpStatusWithProperListOfUserStatuses() throws Exception {
        List<UserStatusResponse> userStatusResponsesExpected = Arrays.asList(
                new UserStatusResponse(4L, "testUserNameFirst", "UA", new BigDecimal("2.5")),
                        new UserStatusResponse(4L, "testUserNameFirst", "UA", new BigDecimal("2.5")));

        when(groupServiceMock.retrieveGroupMemberOwings(3L, 4L)).thenReturn(userStatusResponsesExpected);

        MvcResult mvcResult = mockMvc.perform(get(GROUP_CONTROLLER_GENERAL_PATH + "/3/users/4/status"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<UserStatusResponse> userStatusResponsesActual = objectMapper.readValue(responseString, new TypeReference<List<UserStatusResponse>>() {
        });

        assertEquals(userStatusResponsesExpected, userStatusResponsesActual);
    }
}
