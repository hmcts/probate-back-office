package uk.gov.hmcts.probate.service.template.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalNumberSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorCoversheetPDFDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorLegalStatementPDFDecorator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_ADMON;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_INTESTACY;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE;
import static uk.gov.hmcts.probate.model.DocumentType.LEGAL_STATEMENT_PROBATE_TRUST_CORPS;
import static uk.gov.hmcts.probate.model.DocumentType.SOLICITOR_COVERSHEET;

@Slf4j
@Component
public class PDFDecoratorService {
    private static final String CASE_EXTRAS_KEY = "\"case_extras\"";
    private final ObjectMapper objectMapper;
    private final SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator;
    private final SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecorator;

    @Autowired
    public PDFDecoratorService(ObjectMapper objectMapper, 
                               SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator,
                               SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecorator) {
        this.objectMapper = objectMapper.copy();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalNumberSerializer());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.objectMapper.setDateFormat(df);
        this.objectMapper.registerModule(module);
        
        this.solicitorCoversheetPDFDecorator = solicitorCoversheetPDFDecorator;
        this.solicitorLegalStatementPDFDecorator = solicitorLegalStatementPDFDecorator;
    }

    public String decorate(Object data, DocumentType documentType) {
        String dataJson = "";
        try {
            dataJson = objectMapper.writeValueAsString(data);
            dataJson = addExtraCaseData(dataJson, data, documentType);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        }
        
        return dataJson;
    }

    private String addExtraCaseData(String dataJson, Object data, DocumentType documentType) {
        String updatedJson = "";
        if (data instanceof CallbackRequest) {
            if (documentType.equals(SOLICITOR_COVERSHEET)) {
                CaseData caseData = ((CallbackRequest) data).getCaseDetails().getData();
                updatedJson = solicitorCoversheetPDFDecorator.decorate(caseData);
            } else if (documentType.equals(LEGAL_STATEMENT_PROBATE_TRUST_CORPS) 
                || documentType.equals(LEGAL_STATEMENT_PROBATE)
                || documentType.equals(LEGAL_STATEMENT_INTESTACY)
                || documentType.equals(LEGAL_STATEMENT_ADMON) 
            ) {
                CaseData caseData = ((CallbackRequest) data).getCaseDetails().getData();
                updatedJson = solicitorLegalStatementPDFDecorator.decorate(caseData);
            }
        }

        return mergeCaseExtrasJson(dataJson, updatedJson);
    }

    private String mergeCaseExtrasJson(String caseJson, String caseExtrasJson) {
        String before = caseJson.substring(0, caseJson.lastIndexOf("}"));
        String emptyCaseExtrasJson = "{}";
        if (!caseExtrasJson.isEmpty()) {
            emptyCaseExtrasJson = caseExtrasJson;
        }
        return before + "," + CASE_EXTRAS_KEY + ":" + emptyCaseExtrasJson + "}";
    }
}
