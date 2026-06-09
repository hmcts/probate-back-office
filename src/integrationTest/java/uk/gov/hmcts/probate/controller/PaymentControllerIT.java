package uk.gov.hmcts.probate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.payments.PaymentsService;
import uk.gov.hmcts.probate.util.TestUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils testUtils;

    @MockitoBean
    private PaymentsService paymentsService;

    @MockitoBean
    private SecurityUtils authS2sUtil;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldInvokeSolicitorGrantPaymentCallback() throws Exception {

        String solicitorPaymentCallbackPayload = testUtils.getStringFromFile("solicitorPaymentCallbackPayload.json");
        when(authS2sUtil.checkIfServiceIsAllowed(anyString())).thenReturn(true);

        mockMvc.perform(put("/payment/gor-payment-request-update")
                        .header("ServiceAuthorization", "AUTH_TOKEN")
                        .content(solicitorPaymentCallbackPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldInvokeSolicitorCaveatPaymentCallback() throws Exception {

        String solicitorPaymentCallbackPayload = testUtils.getStringFromFile("solicitorPaymentCallbackPayload.json");
        when(authS2sUtil.checkIfServiceIsAllowed(anyString())).thenReturn(true);

        mockMvc.perform(put("/payment/caveat-payment-request-update")
                        .header("ServiceAuthorization", "AUTH_TOKEN")
                        .content(solicitorPaymentCallbackPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
