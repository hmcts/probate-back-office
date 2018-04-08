package uk.gov.hmcts.probate.service.template.pdf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.Link;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFile;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.evidencemanagement.upload.UploadService;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.template.PDFServiceTemplate.LEGAL_STATEMENT;

public class PDFManagementServiceTest {

    @Mock
    private PDFGeneratorService pdfGeneratorService;

    @Mock
    private UploadService uploadService;

    @Mock
    private EvidenceManagementFileUpload evidenceManagementFileUpload;

    @Mock
    private EvidenceManagementFile evidenceManagementFile;

    @Mock
    private Link link;

    @InjectMocks
    private PDFManagementService underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void generateAndUpload() throws IOException {
        String json = "{}";
        String fileName = "filename";
        String href = "href";

        when(pdfGeneratorService.generatePdf(LEGAL_STATEMENT, json)).thenReturn(evidenceManagementFileUpload);
        when(uploadService.store(evidenceManagementFileUpload)).thenReturn(evidenceManagementFile);
        when(evidenceManagementFile.getLink(Link.REL_SELF)).thenReturn(link);
        when(evidenceManagementFile.getLink("binary")).thenReturn(link);
        when(evidenceManagementFile.getOriginalDocumentName()).thenReturn(fileName);

        when(link.getHref()).thenReturn(href);

        CCDDocument response = underTest.generateAndUpload(LEGAL_STATEMENT, json);

        assertNotNull(response);
        assertEquals(fileName, response.getDocumentFilename());
        assertEquals(href, response.getDocumentBinaryUrl());
        assertEquals(href, response.getDocumentUrl());
    }
}
