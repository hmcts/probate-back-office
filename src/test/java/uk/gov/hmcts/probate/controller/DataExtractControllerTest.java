package uk.gov.hmcts.probate.controller;

import com.google.common.collect.ImmutableList;
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
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.filebuilder.HmrcFileService;
import uk.gov.hmcts.probate.service.filebuilder.IronMountainFileService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DataExtractControllerTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    @MockBean
    private CaseQueryService caseQueryService;

    @MockBean
    private IronMountainFileService ironMountainFileService;

    @MockBean
    private HmrcFileService hmrcFileService;

    @MockBean
    private FileTransferService fileTransferService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private AppInsights appInsights;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        CollectionMember<ScannedDocument> scannedDocument = new CollectionMember<>(new ScannedDocument("1",
            "test", "other", "will", LocalDateTime.now(), DocumentLink.builder().build(),
            "test", LocalDateTime.now()));
        CollectionMember<ScannedDocument> scannedDocumentNullSubType = new CollectionMember<>(new ScannedDocument("1",
            "test", "other", null, LocalDateTime.now(), DocumentLink.builder().build(),
            "test", LocalDateTime.now()));
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>();
        scannedDocuments.add(scannedDocument);
        scannedDocuments.add(scannedDocumentNullSubType);

        CaseData caseData = CaseData.builder()
            .deceasedSurname("smith")
            .scannedDocuments(scannedDocuments)
            .build();
        List<ReturnedCaseDetails> returnedCases = new ImmutableList.Builder<ReturnedCaseDetails>().add(new
            ReturnedCaseDetails(caseData, LAST_MODIFIED, 1L)).build();

        when(caseQueryService.findCasesWithDatedDocument(any())).thenReturn(returnedCases);
        when(caseQueryService.findCaseStateWithinTimeFrame(any(), any())).thenReturn(returnedCases);
        when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.CREATED.value());
    }

    @Test
    public void ironMountainShouldReturnOkResponseOnValidDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/iron-mountain/2019-03-13"))
            .andExpect(status().isOk())
            .andExpect(content().string("1 cases successfully found for date: 2019-03-13"));
    }

    @Test
    public void ironMountainShouldReturnOkWithYesterdayDateOnEmptyPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/iron-mountain"))
            .andExpect(status().isOk())
            .andExpect(content().string("1 cases successfully found for date: " + DATE_FORMAT.format(LocalDate
                .now().minusDays(1L))));
    }

    @Test
    public void shouldThrowClientExceptionWithBadRequestForIronMountainWithIncorrectDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/iron-mountain/2019-2-3"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void hmrcShouldReturnOkResponseOnValidDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc/2019-03-13"))
            .andExpect(status().isOk())
            .andExpect(content().string("1 cases successfully found for date: 2019-03-13 for HMRC"));
    }

    @Test
    public void hmrcShouldReturnOkResponseOnValidDatesFormat() throws Exception {
        mockMvc.perform(post("/data-extract/hmrcFromTo?fromDate=2019-03-13&toDate=2019-04-13"))
            .andExpect(status().isOk())
            .andExpect(content().string("1 cases successfully found for from 2019-03-13 to 2019-04-13 for HMRC"));
    }

    @Test
    public void hmrcShouldReturnErroResponseOnInvalidDates() throws Exception {
        mockMvc.perform(post("/data-extract/hmrcFromTo?fromDate=2019-09-13&toDate=2019-04-13"))
            .andExpect(status().is4xxClientError())
            .andExpect(content().string(containsString("Error on extract dates, fromDate is not before toDate: 2019-09-13,2019-04-13")));
    }

    @Test
    public void hmrcShouldReturnErroResponseOnMissingDates() throws Exception {
        mockMvc.perform(post("/data-extract/hmrcFromTo?fromDate=2019-09-13"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void hmrcShouldReturnOkWithYesterdayDateOnEmptyPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc"))
            .andExpect(status().isOk())
            .andExpect(content().string("1 cases successfully found for date: " + DATE_FORMAT.format(LocalDate
                .now().minusDays(1L)) + " for HMRC"));
    }

    @Test
    public void shouldThrowClientExceptionWithBadRequestForHmrcWithIncorrectDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/hmrc/2019-2-3"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void excelaShouldReturnOkResponseOnValidDateFormat() throws Exception {
        mockMvc.perform(post("/data-extract/excela/2019-02-13"))
            .andExpect(status().isOk())
            .andExpect(content().string("1 cases found and emailed for date: 2019-02-13"));
    }

    @Test
    public void excelaShouldReturnOkWithYesterdayDateOnEmptyPathParam() throws Exception {
        mockMvc.perform(post("/data-extract/excela"))
            .andExpect(status().isOk())
            .andExpect(content().string("1 cases found and emailed for date: " + DATE_FORMAT.format(LocalDate
                .now().minusDays(1L))));
    }

    @Test
    public void ironMountainShouldThrowExceptionOnStatusNotCreated() throws Exception {
        when(fileTransferService.uploadFile(any())).thenReturn(HttpStatus.SERVICE_UNAVAILABLE.value());
        mockMvc.perform(post("/data-extract/iron-mountain/2019-03-13"))
            .andExpect(status().is5xxServerError());
    }
}
