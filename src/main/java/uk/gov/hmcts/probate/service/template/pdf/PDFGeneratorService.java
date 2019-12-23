package uk.gov.hmcts.probate.service.template.pdf;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.config.documents.WelshMonthTranslation;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.probate.service.docmosis.DocmosisPdfGenerationService;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class PDFGeneratorService {
    public static final String TEMPLATE_EXTENSION = ".html";
    private final FileSystemResourceService fileSystemResourceService;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final AppInsights appInsights;
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final ObjectMapper objectMapper;
    private final PDFServiceClient pdfServiceClient;
    private final DocmosisPdfGenerationService docmosisPdfGenerationService;
    private final WelshMonthTranslation welshMonthTranslation;

    public EvidenceManagementFileUpload generatePdf(DocumentType documentType, String pdfGenerationData) {
        byte[] postResult;
        try {
            postResult = generateFromHtml(documentType.getTemplateName(), pdfGenerationData);
        } catch (IOException | PDFServiceClientException e) {
            log.error(e.getMessage(), e);
            throw new ClientException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        return new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, postResult);
    }

    public EvidenceManagementFileUpload generateDocmosisDocumentFrom(String templateName, Map<String, Object>
        placeholders) {
        final String DECEASED_DATE_OF_DEATH ="deceasedDateOfDeath";
        final String DECEASED_DATE_OF_DEATH_IN_WELSH ="deceasedDateOfDeathInWelsh";
        final String GRANT_ISSUED_DATE ="grantIssuedDate";
        final String GRANT_ISSUED_DATE_IN_WELSH ="grantIssuedDateInWelsh";
        byte[] postResult;
        try {
            if(placeholders.get(DECEASED_DATE_OF_DEATH) !=null){
                String deceasedDate  =  (String) placeholders.get(DECEASED_DATE_OF_DEATH);
                placeholders.put(DECEASED_DATE_OF_DEATH_IN_WELSH, convertDateInWelsh(LocalDate.parse(deceasedDate)));
            }
            String grantIssuedDate  = (String) placeholders.get(GRANT_ISSUED_DATE);
            if(grantIssuedDate == null){
                LocalDate _grantIssuedDate = LocalDate.now();
                placeholders.put(GRANT_ISSUED_DATE,dateTimeFormatter.format(_grantIssuedDate));
                placeholders.put(GRANT_ISSUED_DATE_IN_WELSH,convertDateInWelsh(_grantIssuedDate));
            } else {
                placeholders.put(GRANT_ISSUED_DATE_IN_WELSH, convertDateInWelsh(LocalDate.parse(grantIssuedDate)));
            }
            postResult = docmosisPdfGenerationService.generateDocFrom(templateName, placeholders);
        } catch (PDFServiceClientException e) {
            log.error(e.getMessage(), e);
            throw new ClientException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        return new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, postResult);
    }

    private byte[] generateFromHtml(String templateName, String pdfGenerationData) throws IOException {
        String templatePath = pdfServiceConfiguration.getTemplatesDirectory() + templateName + TEMPLATE_EXTENSION;
        String templateAsString = fileSystemResourceService.getFileFromResourceAsString(templatePath);

        Map<String, Object> paramMap = asMap(pdfGenerationData);
        appInsights.trackEvent(REQUEST_SENT, pdfServiceConfiguration.getUrl());

        return pdfServiceClient.generateFromHtml(templateAsString.getBytes(), paramMap);
    }

    private Map<String, Object> asMap(String placeholderValues) throws IOException {
        return objectMapper.readValue(placeholderValues, new TypeReference<HashMap<String, Object>>() {
        });
    }

    protected String convertDateInWelsh(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        String month = welshMonthTranslation.getMonths().get(dateToConvert.getMonth().getValue());
        return (dateToConvert.getDayOfMonth()+" "+ month+ " " + dateToConvert.getYear());
    }
}
