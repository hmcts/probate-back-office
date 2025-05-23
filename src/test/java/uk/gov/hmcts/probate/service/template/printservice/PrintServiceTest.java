package uk.gov.hmcts.probate.service.template.printservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.template.DocumentResponse;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class PrintServiceTest {

    @InjectMocks
    private PrintService underTest;

    @Mock
    private FileSystemResourceService fileSystemResourceServiceMock;

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
        ReflectionTestUtils.setField(underTest, "templatesDirectory", "someTemplateDirectory/");
        ReflectionTestUtils.setField(underTest, "printServiceHost", "somePrintServiceHost");
        ReflectionTestUtils.setField(underTest, "printServicePath", "somePrintServicePath/%s/probate/");
        when(fileSystemResourceServiceMock.getFileFromResourceAsString("someTemplateDirectory/caseDetailsSOL.html"))
                .thenReturn("some Solicitor template");
        when(fileSystemResourceServiceMock.getFileFromResourceAsString("someTemplateDirectory/caseDetailsPA.html"))
                .thenReturn("some Personal template");
    }

    @Test
    void shouldReturnAllSolicitorDocuments() {
        when(caseDetailsMock.getId()).thenReturn(1000L);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.SOLICITOR);
        List<DocumentResponse> docs = underTest.getAllDocuments(caseDetailsMock);

        assertEquals(1, docs.size());
        assertEquals("Print Case Details", docs.get(0).getName());
        assertEquals("HTML", docs.get(0).getType());
        assertEquals("somePrintServiceHostsomePrintServicePath/1000/probate/sol", docs.get(0).getUrl());
    }

    @Test
    void shouldReturnAllPADocuments() {
        when(caseDetailsMock.getId()).thenReturn(1000L);
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDataMock.getApplicationType()).thenReturn(ApplicationType.PERSONAL);
        List<DocumentResponse> docs = underTest.getAllDocuments(caseDetailsMock);

        assertEquals(1, docs.size());
        assertEquals("Print Case Details", docs.get(0).getName());
        assertEquals("HTML", docs.get(0).getType());
        assertEquals("somePrintServiceHostsomePrintServicePath/1000/probate/pa", docs.get(0).getUrl());
    }

    @Test
    void shouldGetSolicitorTemplateForCaseDetails() {
        String template = underTest.getSolicitorCaseDetailsTemplateForPrintService();
        assertEquals("some Solicitor template", template);
    }

    @Test
    void shouldGetPATemplateForCaseDetails() {
        String template = underTest.getPACaseDetailsTemplateForPrintService();
        assertEquals("some Personal template", template);
    }
}
