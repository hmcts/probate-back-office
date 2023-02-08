package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class DocumentOrderingServiceTest {

    @InjectMocks
    private DocumentOrderingService documentOrderingService;

    @Mock
    private CaseData caseData;

    @Mock
    private CaseDetails caseDetails;

    @Test
    public void shouldOrderScannedDocuments() throws IOException {
        MockitoAnnotations.openMocks(this);
        TestUtils testUtils = new TestUtils();
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>();
        String scannedDocsString = testUtils.getStringFromFile("orderingScannedDocuments.json");
        scannedDocsString = scannedDocsString.split("\\[")[1].split("\\]")[0];
        String[] scannedDocs = scannedDocsString.split("\"value\":");
        for (int i = 1; i < scannedDocs.length; i++) {
            ScannedDocument scannedDocument;
            if (scannedDocs[i].contains("subtype")) {
                scannedDocument = ScannedDocument.builder()
                    .controlNumber(scannedDocs[i].split("\"controlNumber\": \"")[1].split("\",")[0])
                    .fileName(scannedDocs[i].split("\"fileName\": \"")[1].split("\",")[0])
                    .type(scannedDocs[i].split("\"type\": \"")[1].split("\",")[0])
                    .subtype(scannedDocs[i].split("\"subtype\": \"")[1].split("\",")[0])
                    .scannedDate(LocalDateTime.now())
                    .url(mock(DocumentLink.class))
                    .exceptionRecordReference(scannedDocs[i].split("\"exceptionRecordReference\": \"")[1]
                            .split("\",")[0])
                    .deliveryDate(LocalDateTime.now())
                    .build();
            } else {
                scannedDocument = ScannedDocument.builder()
                    .controlNumber(scannedDocs[i].split("\"controlNumber\": \"")[1].split("\",")[0])
                    .fileName(scannedDocs[i].split("\"fileName\": \"")[1].split("\",")[0])
                    .type(scannedDocs[i].split("\"type\": \"")[1].split("\",")[0])
                    .scannedDate(LocalDateTime.now())
                    .url(mock(DocumentLink.class))
                    .exceptionRecordReference(scannedDocs[i].split("\"exceptionRecordReference\": \"")[1]
                            .split("\",")[0])
                    .deliveryDate(LocalDateTime.now())
                    .build();
            }
            scannedDocuments.add(new CollectionMember<>(scannedDocument));
        }

        when(caseData.getScannedDocuments()).thenReturn(scannedDocuments);
        when(caseDetails.getData()).thenReturn(caseData);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);

        String correctOrderOfTypes = "coversheet, form, will, forensic_sheets, "
                + "supporting_documents, iht, pps_legal_statement, cherished, other";

        String beforeOrdering = callbackRequest.getCaseDetails().getData().getScannedDocuments().get(0).getValue()
                .getType().toString();
        for (int i = 1; i < callbackRequest.getCaseDetails().getData().getScannedDocuments().size(); i++) {
            beforeOrdering += ", " + callbackRequest.getCaseDetails().getData().getScannedDocuments().get(i).getValue()
                    .getType().toString();
        }
        assertNotEquals(beforeOrdering, correctOrderOfTypes);

        documentOrderingService.orderScannedDocuments(callbackRequest);
        String afterOrdering = callbackRequest.getCaseDetails().getData().getScannedDocuments().get(0).getValue()
                .getType().toString();
        for (int i = 1; i < callbackRequest.getCaseDetails().getData().getScannedDocuments().size(); i++) {
            afterOrdering += ", " + callbackRequest.getCaseDetails().getData().getScannedDocuments().get(i).getValue()
                    .getType().toString();
        }
        assertEquals(afterOrdering, correctOrderOfTypes);
    }
}
