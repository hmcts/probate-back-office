package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class PA14FormCaseExtra {

    @JsonProperty(value = "showPa15Form")
    private final String showPa14Form;
    @JsonProperty(value = "pa15FormPoints")
    private final List<NotApplyingExecutorFormPoint> notApplyingExecutorFormPoints;

}
