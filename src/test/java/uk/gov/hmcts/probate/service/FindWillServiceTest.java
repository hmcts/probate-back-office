package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.DocumentType.WILL;

public class FindWillServiceTest {
    @InjectMocks
    private FindWillService findWillService;

    @Mock
    private List<UploadDocument> listOfUploadedWills;

    @Mock
    private PDFManagementService pdfManagementService;

    @MockBean
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSuccessfulFindWill() throws JsonProcessingException {
        SolsAddress address = SolsAddress.builder().addressLine1("Address 1")
                .addressLine2("Address 2")
                .postCode("EC2")
                .country("UK")
                .build();
        DocumentLink documentLink = DocumentLink.builder()
                .documentUrl("http://localhost")
                .build();
        UploadDocument will = UploadDocument.builder()
                .documentLink(documentLink)
                .documentType(DocumentType.WILL)
                .comment("")
                .build();
        CollectionMember<UploadDocument> collectionMember = new CollectionMember<>(will);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMember);
        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("first")
                .primaryApplicantSurname("last")
                .primaryApplicantAddress(address)
                .boDocumentsUploaded(uploadDocumentsList)
                .scannedDocuments(null)
                .build();

        when(listOfUploadedWills.add(will)).thenReturn(true);
        String json = "{}";
        objectMapper = new ObjectMapper();
        json = objectMapper.writeValueAsString(will);
        when(pdfManagementService.generateAndUpload(json, DocumentType.WILL))
                .thenReturn(Document.builder().documentType(WILL).build());

    }
}
