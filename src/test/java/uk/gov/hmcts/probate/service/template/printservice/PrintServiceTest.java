package uk.gov.hmcts.probate.service.template.printservice;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.template.DocumentResponse;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;

public class PrintServiceTest {

    @InjectMocks
    private PrintService underTest;

    @Mock
    private FileSystemResourceService fileSystemResourceServiceMock;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @Mock
    AppInsights appInsights;

    @Before
    public void setup() {
        initMocks(this);
        ReflectionTestUtils.setField(underTest, "templatesDirectory", "someTemplateDirectory/");
        ReflectionTestUtils.setField(underTest, "printServiceHost", "somePrintServiceHost");
        ReflectionTestUtils.setField(underTest, "printServicePath", "somePrintServicePath/%s/probate/");
        when(fileSystemResourceServiceMock.getFileFromResourceAsString("someTemplateDirectory/caseDetailsSOL.html"))
                .thenReturn("some Solicitor template");
        when(fileSystemResourceServiceMock.getFileFromResourceAsString("someTemplateDirectory/caseDetailsPA.html"))
                .thenReturn("some Personal template");
    }

    @Test
    public void shouldReturnAllSolicitorDocuments() {
        when(caseDetailsMock.getId()).thenReturn(1000L);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn("Solicitor");
        List<DocumentResponse> docs = underTest.getAllDocuments(caseDetailsMock);

        assertEquals(1, docs.size());
        assertEquals("Print Case Details", docs.get(0).getName());
        assertEquals("HTML", docs.get(0).getType());
        assertEquals("somePrintServiceHostsomePrintServicePath/1000/probate/sol", docs.get(0).getUrl());
    }

    @Test
    public void shouldReturnAllPADocuments() {
        when(caseDetailsMock.getId()).thenReturn(1000L);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn("Personal");
        List<DocumentResponse> docs = underTest.getAllDocuments(caseDetailsMock);

        assertEquals(1, docs.size());
        assertEquals("Print Case Details", docs.get(0).getName());
        assertEquals("HTML", docs.get(0).getType());
        assertEquals("somePrintServiceHostsomePrintServicePath/1000/probate/pa", docs.get(0).getUrl());
    }

    @Test
    public void shouldGetSolicitorTemplateForCaseDetails() {
        String template = underTest.getSolicitorCaseDetailsTemplateForPrintService();
        assertEquals("some Solicitor template", template);
    }

    @Test
    public void shouldGetPATemplateForCaseDetails() {
        String template = underTest.getPACaseDetailsTemplateForPrintService();
        assertEquals("some Personal template", template);
    }
}
