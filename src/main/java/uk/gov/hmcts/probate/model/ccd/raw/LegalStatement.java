package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
public class LegalStatement {
    @CCD(label = "Intro")
    private final String intro;
    @CCD(label = "Applicant")
    private final String applicant;
    @CCD(label = "Deceased")
    private final String deceased;
    @CCD(label = "Deceased other names")
    private final String deceasedOtherNames;
    @CCD(label = "Deceased estate value")
    private final String deceasedEstateValue;
    @CCD(label = "Excepted estate confirmation")
    private final String deceasedEstateValueExceptedEstateConfirmation;
    @CCD(label = "Deceased estate land")
    private final String deceasedEstateLand;
    @CCD(
            label = "Executors not applying",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "LegalStatementExecutorNotApplying"
    )
    private final List<LegalStatementExecutorsNotApplying> executorsNotApplying;
    @CCD(
            label = "Executors applying",
            typeOverride = FieldType.Collection,
            typeParameterOverride = "LegalStatementExecutorApplying"
    )
    private final List<LegalStatementExecutorsApplying> executorsApplying;

}