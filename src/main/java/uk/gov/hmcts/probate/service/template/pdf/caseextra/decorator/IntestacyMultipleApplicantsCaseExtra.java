package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class IntestacyMultipleApplicantsCaseExtra {
    private List<String> englishCoApplicantDescriptions;
    private List<String> welshCoApplicantDescriptions;
}
