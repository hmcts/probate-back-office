package uk.gov.hmcts.probate.service;

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
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl").documentBinaryUrl(
                "uploadBinaryUrl").build())
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
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl").documentBinaryUrl("scanBinaryUrl").build())
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
        assertEquals("Scanned", wills.get(1).getDocumentFileName());
        assertEquals("scanUrl", wills.get(1).getDocumentLink().getDocumentUrl());
        assertEquals("scanBinaryUrl", wills.get(1).getDocumentLink().getDocumentBinaryUrl());
    }

    @Test
    public void testSuccessfulFindWillForNonWillUploadAndScansForGOP() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl").documentBinaryUrl(
                "uploadBinaryUrl").build())
            .documentType(DocumentType.WILL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberWill = new CollectionMember(will);
        UploadDocument email = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl").documentBinaryUrl(
                "uploadBinaryUrl").build())
            .documentType(DocumentType.EMAIL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberEmaill = new CollectionMember(email);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMemberWill);
        uploadDocumentsList.add(collectionMemberEmaill);

        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl").documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        ScannedDocument scanNonWill = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("somethingelse")
            .url(DocumentLink.builder().documentFilename("scanFileName1").documentUrl("scanUrl1").documentBinaryUrl("scanBinaryUrl1").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScanNonWill = new CollectionMember(scanNonWill);
        ScannedDocument scanNonOther = ScannedDocument.builder()
            .fileName("Scanned")
            .type("NotOther")
            .subtype("somethingelse")
            .url(DocumentLink.builder().documentFilename("scanFileName1").documentUrl("scanUrl1").documentBinaryUrl("scanBinaryUrl1").build())
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
        assertEquals(2, wills.size());
        assertEquals("uploadFileName", wills.get(0).getDocumentFileName());
        assertEquals("uploadUrl", wills.get(0).getDocumentLink().getDocumentUrl());
        assertEquals("uploadBinaryUrl", wills.get(0).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("Scanned", wills.get(1).getDocumentFileName());
        assertEquals("scanUrl", wills.get(1).getDocumentLink().getDocumentUrl());
        assertEquals("scanBinaryUrl", wills.get(1).getDocumentLink().getDocumentBinaryUrl());
    }

    @Test
    public void testSuccessfulFindWillForAdmonWill() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl").documentBinaryUrl(
                "uploadBinaryUrl").build())
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
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl").documentBinaryUrl("scanBinaryUrl").build())
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
        assertEquals("Scanned", wills.get(1).getDocumentFileName());
        assertEquals("scanUrl", wills.get(1).getDocumentLink().getDocumentUrl());
        assertEquals("scanBinaryUrl", wills.get(1).getDocumentLink().getDocumentBinaryUrl());
    }

    @Test
    public void testSuccessfulFindWillForNonWillUploadAndScansForAdmonWill() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl").documentBinaryUrl(
                "uploadBinaryUrl").build())
            .documentType(DocumentType.WILL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberWill = new CollectionMember(will);
        UploadDocument email = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl").documentBinaryUrl(
                "uploadBinaryUrl").build())
            .documentType(DocumentType.EMAIL)
            .comment("")
            .build();
        CollectionMember<UploadDocument> collectionMemberEmaill = new CollectionMember(email);
        List<CollectionMember<UploadDocument>> uploadDocumentsList = new ArrayList<>();
        uploadDocumentsList.add(collectionMemberWill);
        uploadDocumentsList.add(collectionMemberEmaill);

        ScannedDocument scan = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("will")
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl").documentBinaryUrl("scanBinaryUrl").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScan = new CollectionMember(scan);
        ScannedDocument scanNonWill = ScannedDocument.builder()
            .fileName("Scanned")
            .type("other")
            .subtype("somethingelse")
            .url(DocumentLink.builder().documentFilename("scanFileName1").documentUrl("scanUrl1").documentBinaryUrl("scanBinaryUrl1").build())
            .build();
        CollectionMember<ScannedDocument> collectionMemberScanNonWill = new CollectionMember(scanNonWill);
        ScannedDocument scanNonOther = ScannedDocument.builder()
            .fileName("Scanned")
            .type("NotOther")
            .subtype("somethingelse")
            .url(DocumentLink.builder().documentFilename("scanFileName1").documentUrl("scanUrl1").documentBinaryUrl("scanBinaryUrl1").build())
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
        assertEquals(2, wills.size());
        assertEquals("uploadFileName", wills.get(0).getDocumentFileName());
        assertEquals("uploadUrl", wills.get(0).getDocumentLink().getDocumentUrl());
        assertEquals("uploadBinaryUrl", wills.get(0).getDocumentLink().getDocumentBinaryUrl());
        assertEquals("Scanned", wills.get(1).getDocumentFileName());
        assertEquals("scanUrl", wills.get(1).getDocumentLink().getDocumentUrl());
        assertEquals("scanBinaryUrl", wills.get(1).getDocumentLink().getDocumentBinaryUrl());
    }

    @Test
    public void testSuccessfulFindWillForIntestacy() {
        UploadDocument will = UploadDocument.builder()
            .documentLink(DocumentLink.builder().documentFilename("uploadFileName").documentUrl("uploadUrl").documentBinaryUrl(
                "uploadBinaryUrl").build())
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
            .url(DocumentLink.builder().documentFilename("scanFileName").documentUrl("scanUrl").documentBinaryUrl("scanBinaryUrl").build())
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

}
