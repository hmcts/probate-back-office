package uk.gov.hmcts.probate.service.documentmanagement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.DemoInstanceToggleService;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentManagementRequestBuilder {

    private static final String CLASSIFICATION_PRIVATE_PARAMETER = "PRIVATE";
    private static final String JURISDICTION = "PROBATE";

    private final DemoInstanceToggleService demoInstanceToggleService;

    public DocumentUploadRequest perpareDocumentUploadRequest(EvidenceManagementFileUpload file,
                                                                     DocumentType documentType) {
        MultipartFile multipartFile = ByteArrayMultipartFile.builder()
            .content(file.getBytes())
            .contentType(file.getContentType())
            .name(file.getFileName())
            .build();

        List<MultipartFile> multipartFileList = Arrays.asList(multipartFile);
        return new DocumentUploadRequest(CLASSIFICATION_PRIVATE_PARAMETER,
            getCcdCaseType(documentType), JURISDICTION, multipartFileList);

    }

    public DocumentUploadRequest perpareDocumentUploadRequestForCitizen(List<MultipartFile> multipartFileList,
                                                                        DocumentType documentType) {
        return new DocumentUploadRequest(CLASSIFICATION_PRIVATE_PARAMETER,
            getCcdCaseType(documentType), JURISDICTION, multipartFileList);

    }

    private String getCcdCaseType(DocumentType documentType) {
        switch (documentType) {
            case CAVEAT_COVERSHEET:
            case CAVEAT_RAISED:
            case CAVEAT_STOPPED:
            case CAVEAT_EXTENDED:
            case CAVEAT_WITHDRAWN:
                return CcdCaseType.CAVEAT.getName();
            case WILL_LODGEMENT_DEPOSIT_RECEIPT:
                return CcdCaseType.WILL_LODGEMENT.getName();
            default:
                return demoInstanceToggleService.getCcdCaseType().getName();
        }
    }

}
