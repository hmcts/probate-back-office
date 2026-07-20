package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
public class RegistrarDirection {

    @CCD(label = "Direction added")
    private final LocalDateTime addedDateTime;
    @CCD(
            label = "Registrar decision",
            typeOverride = FieldType.FixedRadioList,
            typeParameterOverride = "registrarsDecisionType"
    )
    private final String decision;
    @CCD(label = "Further information", typeOverride = FieldType.TextArea)
    private final String furtherInformation;

}
