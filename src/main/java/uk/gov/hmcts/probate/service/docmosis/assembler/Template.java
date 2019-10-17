package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Template {
    private final String value;
}
