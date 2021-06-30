package uk.gov.hmcts.probate.service.template.printservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.template.DocumentResponse;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;

@Service
@RequiredArgsConstructor
public class PrintService {

    private static final String TEMPLATE_CASE_DETAILS_SOL = "caseDetailsSOL.html";
    private static final String TEMPLATE_CASE_DETAILS_PA = "caseDetailsPA.html";
    private static final String TEMPLATE_PROBATE_MAN_LEGACY_CASE = "probateManLegacyCase.html";

    private static final String DOCUMENT_NAME = "Print Case Details";
    private static final String DOCUMENT_TYPE = "HTML";
    private final AppInsights appInsights;
    private final FileSystemResourceService fileSystemResourceService;
    @Value("${printservice.templatesDirectory}")
    private String templatesDirectory;
    @Value("${printservice.host}")
    private String printServiceHost;
    @Value("${printservice.path}")
    private String printServicePath;

    public String getSolicitorCaseDetailsTemplateForPrintService() {
        return getFileAsString(TEMPLATE_CASE_DETAILS_SOL);
    }

    public String getPACaseDetailsTemplateForPrintService() {
        return getFileAsString(TEMPLATE_CASE_DETAILS_PA);
    }

    public String getProbateManLegacyCase() {
        return getFileAsString(TEMPLATE_PROBATE_MAN_LEGACY_CASE);
    }

    private String getFileAsString(String fileName) {
        return fileSystemResourceService.getFileFromResourceAsString(templatesDirectory + fileName);
    }

    public List<DocumentResponse> getAllDocuments(CaseDetails caseDetails) {
        Long caseId = caseDetails.getId();
        String applicationTypeCode = caseDetails.getData().getApplicationType().getCode();
        String urlTemplate = printServiceHost + printServicePath + applicationTypeCode;
        String url = String.format(urlTemplate, caseId);

        appInsights.trackEvent(REQUEST_SENT.toString(), appInsights.trackingMap("url", url));

        DocumentResponse documentResponse = new DocumentResponse(DOCUMENT_NAME, DOCUMENT_TYPE, url);

        return Collections.singletonList(documentResponse);
    }
}
