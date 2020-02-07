package com.eleks.controller;

import com.eleks.dto.ErrorResponseDto;
import com.eleks.dto.PaymentRequestDto;
import com.eleks.dto.PaymentResponseDto;
import com.eleks.handler.ErrorResponseExceptionHandler;
import com.eleks.service.PaymentService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class PaymentControllerTest {

    private static final String PAYMENT_CONTROLLER_GENERAL_PATH = "/groups/3/payments";

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentServiceMock;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private PaymentRequestDto paymentRequestDto;

    @Before
    public void init() {
        paymentRequestDto = new PaymentRequestDto("paymentDescriptionTest", 12.3, Arrays.asList(1L, 2L));

        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new ErrorResponseExceptionHandler())
                .build();
    }

    @Test
    public void createPayment_paymentRequestDtoIsValid_shouldReturnCreatedHttpStatus() throws Exception {
        mockMvc.perform(post(PAYMENT_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andExpect(status().isCreated());

        verify(paymentServiceMock).createPayment(paymentRequestDto, 3L);
    }

    @Test
    public void createPayment_paymentDescriptionSizeMoreThan200Symbols_shouldReturnBadRequestHttpStatus() throws Exception {
        paymentRequestDto.setPaymentDescription("testtesttesttesttesttesttesttesttesttesttesttestttetesttesttesttesttestte" +
                "sttesttesttesttesttesttestttetesttesttesttesttesttesttesttesttesttesttesttestttetesttest" +
                "testtesttesttesttesttesttesttesttesttest");

        MvcResult mvcResult = mockMvc.perform(post(PAYMENT_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Payment description shouldn't be more than 200 symbols.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createPayment_paymentDescriptionIsBlank_shouldReturnBadRequestHttpStatus() throws Exception {
        paymentRequestDto.setPaymentDescription(" ");

        MvcResult mvcResult = mockMvc.perform(post(PAYMENT_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Payment description shouldn't be blank.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createPayment_priceIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        paymentRequestDto.setPrice(null);

        MvcResult mvcResult = mockMvc.perform(post(PAYMENT_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Price shouldn't be null.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void createPayment_coPayersListIsNull_shouldReturnBadRequestHttpStatus() throws Exception {
        paymentRequestDto.setCoPayers(null);

        MvcResult mvcResult = mockMvc.perform(post(PAYMENT_CONTROLLER_GENERAL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        ErrorResponseDto errorResponseDtoActual = objectMapper.readValue(responseString, ErrorResponseDto.class);

        assertEquals("Validation failed: Co-payers shouldn't be null.", errorResponseDtoActual.getMessage());
        assertEquals(400, errorResponseDtoActual.getStatusCode());
        assertNotNull(errorResponseDtoActual.getTimestamp());
    }

    @Test
    public void retrievePayments_pathVariableGroupIdPresent_shouldReturnOkHttpStatusWithRetrievedListOfPayments() throws Exception {
        PaymentResponseDto paymentResponseDtoFirstExpected = new PaymentResponseDto(1L, 22L, "testPaymentDescription", 1.2, Arrays.asList(1L, 2L), 4L,
                LocalDateTime.of(2012, 1, 1, 10, 15, 3));

        PaymentResponseDto paymentResponseDtoSecondExpected = new PaymentResponseDto(1L, 22L, "testPaymentDescription", 1.2, Arrays.asList(1L, 2L), 4L,
                LocalDateTime.of(2012, 1, 1, 10, 15, 3));

        when(paymentServiceMock.retrievePayments(3L)).thenReturn(Arrays.asList(paymentResponseDtoFirstExpected, paymentResponseDtoSecondExpected));

        MvcResult mvcResult = mockMvc.perform(get(PAYMENT_CONTROLLER_GENERAL_PATH))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<PaymentResponseDto> paymentResponseDtoListActual = objectMapper.readValue(responseString, new TypeReference<List<PaymentResponseDto>>() {
        });

        assertEquals(Arrays.asList(paymentResponseDtoFirstExpected, paymentResponseDtoSecondExpected), paymentResponseDtoListActual);
    }

    @Test
    public void retrievePayments_urlNotFound_shouldReturnNotFoundHttpStatus() throws Exception {
        mockMvc.perform(get("/wrongGroup/3/payments"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void retrievePayment_pathVariablesGroupIdAndPaymentIdPresent_shouldReturnOkHttpStatusWithRetrievedPayment() throws Exception {
        PaymentResponseDto paymentResponseDtoExpected = new PaymentResponseDto(1L, 22L, "testPaymentDescription", 1.2, Arrays.asList(1L, 2L), 4L,
                LocalDateTime.of(2012, 1, 1, 10, 15, 3));

        when(paymentServiceMock.retrievePayment(3L, 4L)).thenReturn(paymentResponseDtoExpected);

        MvcResult mvcResult = mockMvc.perform(get(PAYMENT_CONTROLLER_GENERAL_PATH + "/4"))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        PaymentResponseDto paymentResponseDtoActual = objectMapper.readValue(responseString, PaymentResponseDto.class);

        assertEquals(paymentResponseDtoExpected, paymentResponseDtoActual);
    }

    @Test
    public void deletePayment_pathVariablesGroupIdAndPaymentIdPresent_shouldReturnOkHttpStatus() throws Exception {
        mockMvc.perform(delete(PAYMENT_CONTROLLER_GENERAL_PATH + "/4"))
                .andExpect(status().isOk());

        verify(paymentServiceMock).deletePayment(3L, 4L);
    }
}
