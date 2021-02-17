package uk.gov.hmcts.probate.service.document;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.WillDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.probate.model.Constants.YES;

public class FindWillsServiceTest {
    @InjectMocks
    private FindWillsService findWillService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSuccessfulFindWillForGOP() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.WILL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberWill = new CollectionMember(will);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMemberWill);

        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl")
                .documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        List<CollectionMember<ScannedDocument>> scannedDocumentsList = new ArrayList<>();
        scannedDocumentsList.add(collectionMemberScan);

        CaseData caseData = CaseData.builder()
            .boDocumentsUploaded(uploadDocumentsList)
            .scannedDocuments(scannedDocumentsList)
            .caseType(DocumentCaseType.GOP.getCaseType())
            .build();

        List<Document> wills = findWillService.findWills(caseData);
        assertEquals(2, wills.size());
        assertEquals("uploadFileName", wills.get(0).getDocumentFileName());
        assertEquals("uploadUrl", wills.get(0).getDocumentLink().getDocumentUrl());
        assertEquals("uploadBinaryUrl", wills.get(0).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("will", wills.get(0).getDocumentType().getTemplateName());
        assertEquals("Scanned", wills.get(1).getDocumentFileName());
        assertEquals("scanUrl", wills.get(1).getDocumentLink().getDocumentUrl());
        assertEquals("scanBinaryUrl", wills.get(1).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("will", wills.get(1).getDocumentType().getTemplateName());
    }

    @Test
    public void testSuccessfulFindWillForNonWillUploadAndScansForGOP() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.WILL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberWill = new CollectionMember(will);
        UploadDocument email = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.EMAIL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberEmaill = new CollectionMember(email);
        UploadDocument codicil = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.IHT)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberCodicil = new CollectionMember(codicil);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMemberWill);
        uploadDocumentsList.add(collectionMemberEmaill);
        uploadDocumentsList.add(collectionMemberCodicil);

        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl")
                .documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        ScannedDocument scanNonWill = ScannedDocument.builder()
            .fileName("Scanned1")
            .type("other")
            .subtype("somethingelse")
            .url(DocumentLink.builder().documentFilename("scanFileName1").documentUrl("scanUrl1")
                .documentBinaryUrl("scanBinaryUrl1").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScanNonWill = new CollectionMember(scanNonWill);
        ScannedDocument scanNonOther = ScannedDocument.builder()
            .fileName("Scanned2")
            .type("NotOther")
            .subtype("somethingelse")
            .url(DocumentLink.builder().documentFilename("scanFileName2").documentUrl("scanUrl2")
                .documentBinaryUrl("scanBinaryUrl2").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScanNonOther = new CollectionMember(scanNonOther);
        List<CollectionMember<ScannedDocument>> scannedDocumentsList = new ArrayList<>();
        scannedDocumentsList.add(collectionMemberScan);
        scannedDocumentsList.add(collectionMemberScanNonWill);
        scannedDocumentsList.add(collectionMemberScanNonOther);

        CaseData caseData = CaseData.builder()
            .boDocumentsUploaded(uploadDocumentsList)
            .scannedDocuments(scannedDocumentsList)
            .caseType(DocumentCaseType.GOP.getCaseType())
            .build();

        List<Document> wills = findWillService.findWills(caseData);
        assertEquals(3, wills.size());
        assertEquals("uploadFileName", wills.get(0).getDocumentFileName());
        assertEquals("uploadUrl", wills.get(0).getDocumentLink().getDocumentUrl());
        assertEquals("uploadBinaryUrl", wills.get(0).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("will", wills.get(0).getDocumentType().getTemplateName());
        assertEquals("uploadFileName", wills.get(1).getDocumentFileName());
        assertEquals("uploadUrl", wills.get(1).getDocumentLink().getDocumentUrl());
        assertEquals("uploadBinaryUrl", wills.get(1).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("IHT", wills.get(1).getDocumentType().getTemplateName());
        assertEquals("Scanned", wills.get(2).getDocumentFileName());
        assertEquals("scanUrl", wills.get(2).getDocumentLink().getDocumentUrl());
        assertEquals("scanBinaryUrl", wills.get(2).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("will", wills.get(2).getDocumentType().getTemplateName());
    }

    @Test
    public void testSuccessfulFindWillForAdmonWill() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.WILL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberWill = new CollectionMember(will);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMemberWill);

        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl")
                .documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        List<CollectionMember<ScannedDocument>> scannedDocumentsList = new ArrayList<>();
        scannedDocumentsList.add(collectionMemberScan);

        CaseData caseData = CaseData.builder()
            .boDocumentsUploaded(uploadDocumentsList)
            .scannedDocuments(scannedDocumentsList)
            .caseType(DocumentCaseType.ADMON_WILL.getCaseType())
            .build();

        List<Document> wills = findWillService.findWills(caseData);
        assertEquals(2, wills.size());
        assertEquals("uploadFileName", wills.get(0).getDocumentFileName());
        assertEquals("uploadUrl", wills.get(0).getDocumentLink().getDocumentUrl());
        assertEquals("uploadBinaryUrl", wills.get(0).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("will", wills.get(0).getDocumentType().getTemplateName());
        assertEquals("Scanned", wills.get(1).getDocumentFileName());
        assertEquals("scanUrl", wills.get(1).getDocumentLink().getDocumentUrl());
        assertEquals("scanBinaryUrl", wills.get(1).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("will", wills.get(1).getDocumentType().getTemplateName());
    }

    @Test
    public void testSuccessfulFindWillForNonWillUploadAndScansForAdmonWill() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.WILL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberWill = new CollectionMember(will);
        UploadDocument email = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.EMAIL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberEmaill = new CollectionMember(email);
        UploadDocument codicil = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.IHT)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberCodicil = new CollectionMember(codicil);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMemberWill);
        uploadDocumentsList.add(collectionMemberEmaill);
        uploadDocumentsList.add(collectionMemberCodicil);

        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl")
                .documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        ScannedDocument scanNonWill = ScannedDocument.builder()
            .fileName("Scanned1")
            .type("other")
            .subtype("somethingelse")
            .url(DocumentLink.builder().documentFilename("scanFileName1").documentUrl("scanUrl1")
                .documentBinaryUrl("scanBinaryUrl1").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScanNonWill = new CollectionMember(scanNonWill);
        ScannedDocument scanNonOther = ScannedDocument.builder()
            .fileName("Scanned2")
            .type("NotOther")
            .subtype("somethingelse")
            .url(DocumentLink.builder().documentFilename("scanFileName2").documentUrl("scanUrl2")
                .documentBinaryUrl("scanBinaryUrl2").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScanNonOther = new CollectionMember(scanNonOther);
        List<CollectionMember<ScannedDocument>> scannedDocumentsList = new ArrayList<>();
        scannedDocumentsList.add(collectionMemberScan);
        scannedDocumentsList.add(collectionMemberScanNonWill);
        scannedDocumentsList.add(collectionMemberScanNonOther);

        CaseData caseData = CaseData.builder()
            .boDocumentsUploaded(uploadDocumentsList)
            .scannedDocuments(scannedDocumentsList)
            .caseType(DocumentCaseType.ADMON_WILL.getCaseType())
            .build();

        List<Document> wills = findWillService.findWills(caseData);
        assertEquals(3, wills.size());
        assertEquals("uploadFileName", wills.get(0).getDocumentFileName());
        assertEquals("uploadUrl", wills.get(0).getDocumentLink().getDocumentUrl());
        assertEquals("uploadBinaryUrl", wills.get(0).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("will", wills.get(0).getDocumentType().getTemplateName());
        assertEquals("uploadFileName", wills.get(1).getDocumentFileName());
        assertEquals("uploadUrl", wills.get(1).getDocumentLink().getDocumentUrl());
        assertEquals("uploadBinaryUrl", wills.get(1).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("IHT", wills.get(1).getDocumentType().getTemplateName());
        assertEquals("Scanned", wills.get(2).getDocumentFileName());
        assertEquals("scanUrl", wills.get(2).getDocumentLink().getDocumentUrl());
        assertEquals("scanBinaryUrl", wills.get(2).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("will", wills.get(2).getDocumentType().getTemplateName());
    }

    @Test
    public void testSuccessfulFindWillForIntestacy() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.WILL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberWill = new CollectionMember(will);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMemberWill);

        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl")
                .documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        List<CollectionMember<ScannedDocument>> scannedDocumentsList = new ArrayList<>();
        scannedDocumentsList.add(collectionMemberScan);

        CaseData caseData = CaseData.builder()
            .boDocumentsUploaded(uploadDocumentsList)
            .scannedDocuments(scannedDocumentsList)
            .caseType(DocumentCaseType.INTESTACY.getCaseType())
            .build();

        List<Document> wills = findWillService.findWills(caseData);
        assertEquals(0, wills.size());
    }

    @Test
    public void shouldFindSelectedOrDefaultWill() {
        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl")
                .documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        List<CollectionMember<ScannedDocument>> scannedDocumentsList = new ArrayList<>();
        scannedDocumentsList.add(collectionMemberScan);

        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();

        CaseData caseData = CaseData.builder()
            .boDocumentsUploaded(uploadDocumentsList)
            .scannedDocuments(scannedDocumentsList)
            .caseType(DocumentCaseType.GOP.getCaseType())
            .build();

        List<Document> selectedWills = findWillService.findDefaultOrSelectedWills(caseData);
        assertEquals(1, selectedWills.size());
        assertEquals("Scanned", selectedWills.get(0).getDocumentFileName());

    }

    @Test
    public void shouldFindSelectedOrDefaultWills() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl")
                .documentBinaryUrl("uploadBinaryUrl").build())
            .documentType(DocumentType.WILL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberWill = new CollectionMember(will);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMemberWill);

        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl")
                .documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        List<CollectionMember<ScannedDocument>> scannedDocumentsList = new ArrayList<>();
        scannedDocumentsList.add(collectionMemberScan);

        List<CollectionMember<WillDocument>> willSelection = new ArrayList<>();
        WillDocument willDoc1 = WillDocument.builder()
            .documentSelected(Arrays.asList(YES))
            .documentDate("date1")
            .documentLabel("will1")
            .documentLink(DocumentLink.builder().documentFilename("file1").documentBinaryUrl("uploadBinaryUrl").build())
            .build();
        CollectionMember<WillDocument> will1 = new CollectionMember<WillDocument>(willDoc1);
        WillDocument willDoc2 = WillDocument.builder()
            .documentSelected(Arrays.asList(YES))
            .documentDate("date2")
            .documentLabel("will2")
            .documentLink(DocumentLink.builder().documentFilename("file2").documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<WillDocument> will2 = new CollectionMember<WillDocument>(willDoc2);
        WillDocument willDoc3 = WillDocument.builder()
            .documentSelected(Collections.emptyList())
            .documentDate("date3")
            .documentLabel("will3")
            .documentLink(DocumentLink.builder().documentFilename("file3").documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<WillDocument> will3 = new CollectionMember<WillDocument>(willDoc3);
        WillDocument willDoc4 = WillDocument.builder()
            .documentSelected(null)
            .documentDate("date4")
            .documentLabel("will4")
            .documentSelected(Collections.emptyList())
            .documentLink(DocumentLink.builder().documentFilename("file4").documentBinaryUrl("scanBinaryUrlOther")
                .build())
            .build();
        CollectionMember<WillDocument> will4 = new CollectionMember<WillDocument>(willDoc4);

        willSelection.add(will1);
        willSelection.add(will2);
        willSelection.add(will3);
        willSelection.add(will4);
        CaseData caseData = CaseData.builder()
            .boDocumentsUploaded(uploadDocumentsList)
            .scannedDocuments(scannedDocumentsList)
            .caseType(DocumentCaseType.GOP.getCaseType())
            .willSelection(willSelection)
            .build();

        List<Document> selectedWills = findWillService.findDefaultOrSelectedWills(caseData);
        assertEquals(2, selectedWills.size());
        assertEquals("uploadFileName", selectedWills.get(0).getDocumentFileName());
        assertEquals("Scanned", selectedWills.get(1).getDocumentFileName());

    }

}
