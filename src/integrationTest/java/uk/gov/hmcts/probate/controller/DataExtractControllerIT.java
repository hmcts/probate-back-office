package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.LocalDateTimeSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.HmrcDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;

import java.math.BigDecimal;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class DataExtractControllerIT {

    public static final String PERFORM_HMRC_DATA_EXTRACT_FINISHED = "Perform HMRC data extract finished";
    public static final String PERFORM_IM_DATA_EXTRACT_FINISHED = "Perform Iron Mountain data extract finished";

    @MockitoBean
    private HmrcDataExtractService hmrcDataExtractService;
    @MockitoBean
    private IronMountainDataExtractService ironMountainDataExtractService;
    @MockitoBean
    private ExelaDataExtractService exelaDataExtractService;
    @MockitoBean
    private SmeeAndFordDataExtractService smeeAndFordDataExtractService;
    @MockitoBean
    private DataExtractDateValidator dataExtractDateValidator;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void ironMountainShouldReturnOkResponseOnValidDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/iron-mountain?date=2019-03-13"))
            .andExpect(status().isAccepted())
            .andExpect(content().string("Perform Iron Mountain data extract finished"));
    }

    @Test
    void ironMountainShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/iron-mountain"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void resendIronMountainShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/resend-iron-mountain"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void hmrcShouldReturnOkResponseOnValidContentToResend() throws Exception {
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new LocalDateTimeSerializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        objectMapper.registerModule(javaTimeModule);

        CaseDetails caseDetails = new CaseDetails(CaseData.builder()
            .resendDate("2023-01-01").registryLocation("bristol").build(), null, null);
        CallbackRequest request = new CallbackRequest(caseDetails);
        String bodyText = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/data-extract/resend-iron-mountain")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyText)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForIronMountainWithIncorrectDateFormat() throws Exception {
        doThrow(new ClientException(HttpStatus.BAD_REQUEST.value(), "")).when(dataExtractDateValidator)
            .dateValidator("2019-2-3");
        mockMvc.perform(post("/data-extract/iron-mountain?date=2019-2-3"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void hmrcShouldReturnOkResponseOnValidDatesFormat() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc?fromDate=2019-03-13&toDate=2019-04-13"))
            .andExpect(status().isAccepted())
            .andExpect(content().string(PERFORM_HMRC_DATA_EXTRACT_FINISHED));
    }

    @Test
    void hmrcShouldReturnOkResponseOnSameDates() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc?fromDate=2019-03-13&toDate=2019-03-13"))
            .andExpect(status().isAccepted())
            .andExpect(content().string(PERFORM_HMRC_DATA_EXTRACT_FINISHED));
    }

    @Test
    void hmrcShouldReturnErroResponseOnInvalidDates() throws Exception {
        doThrow(new ClientException(HttpStatus.BAD_REQUEST.value(), "")).when(dataExtractDateValidator)
            .dateValidator("2019-09-13", "2019-04-13");
        mockMvc.perform(post("/data-extract/hmrc?fromDate=2019-09-13&toDate=2019-04-13"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void hmrcShouldReturnErroResponseOnMissingDates() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc?fromDate=2019-09-13"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void hmrcShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldThrowClientExceptionWithBadRequestForHmrcWithIncorrectDateFormat() throws Exception {
        doThrow(new ClientException(HttpStatus.BAD_REQUEST.value(), "")).when(dataExtractDateValidator)
            .dateValidator("2019-2-3");
        mockMvc.perform(post("/data-extract/hmrc?date=2019-2-3"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void exelaShouldReturnOkResponseOnValidDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/exela?fromDate=2019-02-13&toDate=2019-02-13"))
            .andExpect(status().isAccepted())
            .andExpect(content().string("Exela data extract finished"));
    }

    @Test
    void exelaShouldReturnOkResponseOnValidDateRangeFormat() throws Exception {
        mockMvc.perform(post("/data-extract/exela?fromDate=2019-02-13&toDate=2019-02-14"))
            .andExpect(status().isAccepted())
            .andExpect(content().string("Exela data extract finished"));
    }

    @Test
    void exelaShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/exela"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void smeeAndFordShouldReturnOkResponseOnValidDateRangeFormat() throws Exception {
        mockMvc.perform(post("/data-extract/smee-and-ford?fromDate=2019-02-13&toDate=2019-02-13"))
            .andExpect(status().isAccepted());
    }

    @Test
    void smeeAndFordShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/smee-and-ford"))
            .andExpect(status().is4xxClientError());
    }
}
