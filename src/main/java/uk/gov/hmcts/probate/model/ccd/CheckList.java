package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CheckList implements Serializable {
    private final String boExaminationChecklistQ1;
    private final String boExaminationChecklistQ2;
}
