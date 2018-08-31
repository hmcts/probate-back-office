package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LegalStatement {
    private final String intro;
    private final String applicant;
    private final String deceased;
    private final String deceasedOtherNames;
    private final String deceasedEstateValue;
    private final String deceasedEstateLand;
    private final List<LegalStatementExecutorsNotApplying> executorsNotApplying;
    private final List<LegalStatementExecutorsApplying> executorsApplying;

}