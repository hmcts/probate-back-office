package uk.gov.hmcts.probate.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtils utils;

    @MockBean
    private FeeService feeService;

    @Test(expected = BadRequestException.class)
    public void shouldErrorForLegalStatement() throws IOException {
        BindingResult bindingResultMock = Mockito.mock(BindingResult.class);
        CallbackRequest callbackRequestMock = Mockito.mock(CallbackRequest.class);
        FeeController underTest = new FeeController(feeService, null, null);
        when(bindingResultMock.hasErrors()).thenReturn(true);

        underTest.getFee(callbackRequestMock, bindingResultMock);
    }

    @Test
    public void getFeesShouldReturnOkAndJson() throws Exception {
        FeeServiceResponse feeServiceResponse = FeeServiceResponse.builder()
            .applicationFee(BigDecimal.valueOf(155))
            .feeForUkCopies(BigDecimal.valueOf(0.5))
            .feeForNonUkCopies(BigDecimal.valueOf(0.5))
            .total(BigDecimal.valueOf(156))
            .build();

        when(feeService.getTotalFee(BigDecimal.valueOf(10000).setScale(2, BigDecimal.ROUND_HALF_UP), 1L, 1L))
            .thenReturn(feeServiceResponse);

        mockMvc.perform(post("/fee")
            .content(utils.getJsonFromFile("success.json"))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.data.applicationFee").value(BigDecimal.valueOf(15500)))
            .andExpect(jsonPath("$.data.totalFee").value(BigDecimal.valueOf(15600)));
    }
}
