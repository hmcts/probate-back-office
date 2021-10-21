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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static uk.gov.hmcts.probate.model.DocumentType.SOLICITOR_COVERSHEET;

@Slf4j
@Component
public class PDFDecoratorService {
    private final ObjectMapper objectMapper;
    private final SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator;

    @Autowired
    public PDFDecoratorService(ObjectMapper objectMapper, 
                               SolicitorCoversheetPDFDecorator solicitorCoversheetPDFDecorator) {
        this.objectMapper = objectMapper.copy();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalNumberSerializer());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.objectMapper.setDateFormat(df);
        this.objectMapper.registerModule(module);
        
        this.solicitorCoversheetPDFDecorator = solicitorCoversheetPDFDecorator;
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
        String updatedJson = dataJson;
        if (data instanceof CallbackRequest && documentType.equals(SOLICITOR_COVERSHEET)) {
            CaseData caseData = ((CallbackRequest) data).getCaseDetails().getData();
            updatedJson = solicitorCoversheetPDFDecorator.decorate(updatedJson, caseData);
        }

        System.out.println("updatedJson:" + updatedJson);
        return updatedJson;
    }
}
