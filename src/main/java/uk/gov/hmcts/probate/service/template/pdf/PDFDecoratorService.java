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
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.CaseExtraDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.RemovePenceDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorCoversheetPDFDecorator;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator.SolicitorLegalStatementPDFDecorator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.AD_COLLIGENDA_BONA_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_REISSUE_DRAFT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.INTESTACY_GRANT_DRAFT;
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
    private final RemovePenceDecorator removePenceDecorator;
    private final CaseExtraDecorator caseExtraDecorator;

    @Autowired
    public PDFDecoratorService(ObjectMapper objectMapper, 
                               SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator,
                               SolicitorLegalStatementPDFDecorator solicitorLegalStatementPDFDecorator,
                               RemovePenceDecorator removePenceDecorator, CaseExtraDecorator caseExtraDecorator) {
        this.objectMapper = objectMapper.copy();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalNumberSerializer());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.objectMapper.setDateFormat(df);
        this.objectMapper.registerModule(module);
        
        this.solicitorCoversheetPDFDecorator = solicitorCoversheetPDFDecorator;
        this.solicitorLegalStatementPDFDecorator = solicitorLegalStatementPDFDecorator;
        this.removePenceDecorator = removePenceDecorator;
        this.caseExtraDecorator = caseExtraDecorator;
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
        boolean legalStatement = documentType.equals(LEGAL_STATEMENT_PROBATE_TRUST_CORPS)
            || documentType.equals(LEGAL_STATEMENT_PROBATE)
            || documentType.equals(LEGAL_STATEMENT_INTESTACY)
            || documentType.equals(LEGAL_STATEMENT_ADMON);
        if (data instanceof CallbackRequest) {
            if (documentType.equals(SOLICITOR_COVERSHEET)) {
                CaseData caseData = ((CallbackRequest) data).getCaseDetails().getData();
                updatedJson = solicitorCoversheetPDFDecorator.decorate(caseData);
            } else if (legalStatement) {
                CaseData caseData = ((CallbackRequest) data).getCaseDetails().getData();
                updatedJson = caseExtraDecorator.combineDecorations(solicitorLegalStatementPDFDecorator
                        .decorate(caseData), removePenceDecorator.decorate(caseData, documentType));
            } else if (documentType.equals(DIGITAL_GRANT_REISSUE_DRAFT)
                    || documentType.equals(DIGITAL_GRANT_REISSUE)
                    || documentType.equals(DIGITAL_GRANT)
                    || documentType.equals(DIGITAL_GRANT_DRAFT)
                    || documentType.equals(INTESTACY_GRANT)
                    || documentType.equals(INTESTACY_GRANT_DRAFT)
                    || documentType.equals(ADMON_WILL_GRANT)
                    || documentType.equals(ADMON_WILL_GRANT_DRAFT)
                    || documentType.equals(AD_COLLIGENDA_BONA_GRANT)
                    || documentType.equals(AD_COLLIGENDA_BONA_GRANT_DRAFT)
            ) {
                CaseData caseData = ((CallbackRequest) data).getCaseDetails().getData();
                updatedJson = removePenceDecorator.decorate(caseData, documentType);
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
