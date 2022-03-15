package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BadRequestException;

@Slf4j
@Component
@AllArgsConstructor
public class CaseExtraDecorator {
    private final ObjectMapper objectMapper;

    public String decorate(Object data) {
        String dataJson = "";
        try {
            dataJson = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException(e.getMessage());
        }
        return dataJson;
    }

    public String combineDecorations(String origDecor, String newDecor) {
        String all = origDecor + newDecor;
        return all.replaceAll("}\\{", ",");
    }

}
