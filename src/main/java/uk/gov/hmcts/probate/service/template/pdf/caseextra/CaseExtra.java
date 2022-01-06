package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CaseExtra {
    
    private final PA16FormCaseExtra pa16FormCaseExtra;
}
