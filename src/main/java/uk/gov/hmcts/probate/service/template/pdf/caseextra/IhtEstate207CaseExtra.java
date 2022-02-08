package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class IhtEstate207CaseExtra {

    private final String showIhtEstate;
    private final String ihtEstate207Text;

}
