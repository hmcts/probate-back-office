package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
class ScannedDocumentOrderingServiceTest {

    @InjectMocks
    private ScannedDocumentOrderingService scannedDocumentOrderingService;

    @Mock
    private CaseData caseData;

    @Mock
    private CaseDetails caseDetails;

    private List<CollectionMember<ScannedDocument>> createScannedDocumentListFromJsonFile(String fileName)
            throws IOException {
        TestUtils testUtils = new TestUtils();
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>();
        String scannedDocsString = testUtils.getStringFromFile(fileName);
        JSONObject scannedDocsJson = new JSONObject(scannedDocsString);
        int size = scannedDocsJson.getJSONArray("scannedDocuments").length();
        for (int i = 0; i < Integer.valueOf(size); i++) {
            JSONObject doc = scannedDocsJson.getJSONArray("scannedDocuments")
                    .getJSONObject(i).getJSONObject("value");
            ScannedDocument scannedDocument = ScannedDocument.builder()
                .controlNumber(doc.getString("controlNumber"))
                .fileName(doc.getString("fileName"))
                .type(doc.getString("type"))
                .subtype(getSubtype(doc))
                .scannedDate(LocalDateTime.of(2023,01,01, 00, 00))
                .url(null)
                .exceptionRecordReference(doc.getString("exceptionRecordReference"))
                .deliveryDate(LocalDateTime.of(2023,01,01, 00, 00))
                .build();
            scannedDocuments.add(new CollectionMember<>(scannedDocument));
        }
        return scannedDocuments;
    }

    private String getSubtype(JSONObject doc) {
        if (doc.has("subtype") && doc.get("subtype") != null) {
            return doc.getString("subtype");
        } else {
            return null;
        }
    }

    @Test
    public void shouldOrderScannedDocuments() throws IOException {
        MockitoAnnotations.openMocks(this);
        List<CollectionMember<ScannedDocument>> unorderedScannedDocuments
                = createScannedDocumentListFromJsonFile("unorderedScannedDocuments.json");
        List<CollectionMember<ScannedDocument>> expectedOrderedScannedDocuments
                = createScannedDocumentListFromJsonFile("expectedOrderedScannedDocuments.json");
        when(caseData.getScannedDocuments()).thenReturn(unorderedScannedDocuments);
        when(caseDetails.getData()).thenReturn(caseData);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        assertNotEquals(unorderedScannedDocuments, expectedOrderedScannedDocuments);

        scannedDocumentOrderingService
                .orderScannedDocuments(callbackRequest.getCaseDetails().getData().getScannedDocuments());
        List<CollectionMember<ScannedDocument>> afterOrderingScannedDocuments
                = callbackRequest.getCaseDetails().getData().getScannedDocuments();
        assertEquals(afterOrderingScannedDocuments, expectedOrderedScannedDocuments);

        scannedDocumentOrderingService
                .orderScannedDocuments(callbackRequest.getCaseDetails().getData().getScannedDocuments());
        afterOrderingScannedDocuments
                = callbackRequest.getCaseDetails().getData().getScannedDocuments();
        assertEquals(afterOrderingScannedDocuments, expectedOrderedScannedDocuments);
    }

    @Test
    public void shouldNotChangeOrderedScannedDocuments() throws IOException {
        MockitoAnnotations.openMocks(this);
        List<CollectionMember<ScannedDocument>> orderedScannedDocuments
                = createScannedDocumentListFromJsonFile("orderedScannedDocuments.json");
        List<CollectionMember<ScannedDocument>> expectedOrderedScannedDocuments
                = createScannedDocumentListFromJsonFile("expectedOrderedScannedDocuments.json");
        when(caseData.getScannedDocuments()).thenReturn(orderedScannedDocuments);
        when(caseDetails.getData()).thenReturn(caseData);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        assertEquals(orderedScannedDocuments, expectedOrderedScannedDocuments);

        scannedDocumentOrderingService
                .orderScannedDocuments(callbackRequest.getCaseDetails().getData().getScannedDocuments());
        assertEquals(orderedScannedDocuments, expectedOrderedScannedDocuments);
    }

    @Test
    public void shouldOrderSubtypesAlphabetically() throws IOException {
        MockitoAnnotations.openMocks(this);
        List<CollectionMember<ScannedDocument>> unorderedScannedSupportingDocs
                = createScannedDocumentListFromJsonFile("unorderedScannedSupportingDocs.json");
        List<CollectionMember<ScannedDocument>> expectedScannedSupportingDocs
                = createScannedDocumentListFromJsonFile("expectedScannedSupportingDocs.json");
        when(caseData.getScannedDocuments()).thenReturn(unorderedScannedSupportingDocs);
        when(caseDetails.getData()).thenReturn(caseData);
        CallbackRequest callbackRequest = new CallbackRequest(caseDetails);
        assertNotEquals(unorderedScannedSupportingDocs, expectedScannedSupportingDocs);

        scannedDocumentOrderingService
                .orderScannedDocuments(callbackRequest.getCaseDetails().getData().getScannedDocuments());
        List<CollectionMember<ScannedDocument>> afterOrderingScannedDocuments
                = callbackRequest.getCaseDetails().getData().getScannedDocuments();
        assertEquals(afterOrderingScannedDocuments, expectedScannedSupportingDocs);

        scannedDocumentOrderingService
                .orderScannedDocuments(callbackRequest.getCaseDetails().getData().getScannedDocuments());
        afterOrderingScannedDocuments
                = callbackRequest.getCaseDetails().getData().getScannedDocuments();
        assertEquals(afterOrderingScannedDocuments, expectedScannedSupportingDocs);
    }
}
