package uk.gov.hmcts.probate.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.HmrcDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;

import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DataExtractControllerTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @MockBean
    private HmrcDataExtractService hmrcDataExtractService;
    @MockBean
    private IronMountainDataExtractService ironMountainDataExtractService;
    @MockBean
    private ExelaDataExtractService exelaDataExtractService;
    @MockBean
    private SmeeAndFordDataExtractService smeeAndFordDataExtractService;
    @MockBean
    private DataExtractDateValidator dataExtractDateValidator;
    @MockBean
    private AppInsights appInsights;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void ironMountainShouldReturnOkResponseOnValidDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/iron-mountain?date=2019-03-13"))
            .andExpect(status().isAccepted())
            .andExpect(content().string("Perform Iron Mountain data extract finished"));
    }

    @Test
    public void ironMountainShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/iron-mountain"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldThrowClientExceptionWithBadRequestForIronMountainWithIncorrectDateFormat() throws Exception {
        doThrow(new ClientException(HttpStatus.BAD_REQUEST.value(), "")).when(dataExtractDateValidator)
            .dateValidator("2019-2-3");
        mockMvc.perform(post("/data-extract/iron-mountain?date=2019-2-3"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void hmrcShouldReturnOkResponseOnValidDatesFormat() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc?fromDate=2019-03-13&toDate=2019-04-13"))
            .andExpect(status().isAccepted())
            .andExpect(content().string("Perform HMRC data extract finished"));
    }

    @Test
    public void hmrcShouldReturnOkResponseOnSameDates() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc?fromDate=2019-03-13&toDate=2019-03-13"))
            .andExpect(status().isAccepted())
            .andExpect(content().string("Perform HMRC data extract finished"));
    }

    @Test
    public void hmrcShouldReturnErroResponseOnInvalidDates() throws Exception {
        doThrow(new ClientException(HttpStatus.BAD_REQUEST.value(), "")).when(dataExtractDateValidator)
            .dateValidator("2019-09-13", "2019-04-13");
        mockMvc.perform(post("/data-extract/hmrc?fromDate=2019-09-13&toDate=2019-04-13"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void hmrcShouldReturnErroResponseOnMissingDates() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc?fromDate=2019-09-13"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void hmrcShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldThrowClientExceptionWithBadRequestForHmrcWithIncorrectDateFormat() throws Exception {
        doThrow(new ClientException(HttpStatus.BAD_REQUEST.value(), "")).when(dataExtractDateValidator)
            .dateValidator("2019-2-3");
        mockMvc.perform(post("/data-extract/hmrc?date=2019-2-3"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void exelaShouldReturnOkResponseOnValidDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/exela?fromDate=2019-02-13&toDate=2019-02-13"))
            .andExpect(status().isAccepted())
            .andExpect(content().string("Exela data extract finished"));
    }

    @Test
    public void exelaShouldReturnOkResponseOnValidDateRangeFormat() throws Exception {
        mockMvc.perform(post("/data-extract/exela?fromDate=2019-02-13&toDate=2019-02-14"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Exela data extract finished"));
    }

    @Test
    public void exelaShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/exela"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void smeeAndFordShouldReturnOkResponseOnValidDateRangeFormat() throws Exception {
        mockMvc.perform(post("/data-extract/smee-and-ford?fromDate=2019-02-13&toDate=2019-02-13"))
            .andExpect(status().isAccepted());
    }

    @Test
    public void smeeAndFordShouldReturnErrorWithNoDateOnPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/smee-and-ford"))
            .andExpect(status().is4xxClientError());
    }
}
