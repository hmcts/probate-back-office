package uk.gov.hmcts.probate.service.template.printservice;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.PrintTemplateApplicationType;
import uk.gov.hmcts.probate.model.template.DocumentResponse;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.util.Collections;
import java.util.List;

@Data
@Service
public class PrintService {
    private static final Logger log = LoggerFactory.getLogger(PrintService.class);

    private static final String TEMPLATE_CASE_DETAILS_SOL = "caseDetailsSOL.html";
    private static final String TEMPLATE_CASE_DETAILS_PA = "caseDetailsPA.html";

    private static final String DOCUMENT_NAME = "Print Case Details";
    private static final String DOCUMENT_TYPE = "HTML";

    @Value("${printservice.templatesDirectory}")
    private String templatesDirectory;

    @Value("${printservice.host}")
    private String printServiceHost;

    @Value("${printservice.path}")
    private String printServicePath;

    private final FileSystemResourceService fileSystemResourceService;

    public String getSolicitorCaseDetailsTemplateForPrintService() {
        return getFileAsString(TEMPLATE_CASE_DETAILS_SOL);
    }

    public String getPACaseDetailsTemplateForPrintService() {
        return getFileAsString(TEMPLATE_CASE_DETAILS_PA);
    }

    private String getFileAsString(String fileName) {
        return fileSystemResourceService.getFileFromResourceAsString(templatesDirectory + fileName);
    }

    public List<DocumentResponse> getAllDocuments(CaseDetails caseDetails) {
        Long caseId = caseDetails.getId();
        String type = PrintTemplateApplicationType.valueOf(caseDetails.getData().getApplicationType().toUpperCase())
                .getPrintType();

        String urlTemplate = printServiceHost + printServicePath + type;
        String url = String.format(urlTemplate, caseId);

        DocumentResponse documentResponse = new DocumentResponse(DOCUMENT_NAME, DOCUMENT_TYPE, url);

        return Collections.singletonList(documentResponse);
    }
}
