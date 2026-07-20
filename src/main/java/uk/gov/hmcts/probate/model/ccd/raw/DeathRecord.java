package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.Alias;

import java.time.LocalDate;
import java.util.List;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
public class DeathRecord {
    @CCD(label = "System Number")
    private final Integer systemNumber;
    @CCD(label = "Name")
    private final String name;
    @CCD(label = "Date of birth")
    private final LocalDate dateOfBirth;
    @CCD(label = "Sex")
    private final String sex;
    @CCD(label = "Address")
    private final String address;
    @CCD(label = "Date of death")
    private final LocalDate dateOfDeath;
    @CCD(label = "Aliases", typeOverride = FieldType.Collection, typeParameterOverride = "Aliases")
    private final List<CollectionMember<Alias>> aliases;
    @CCD(label = "Is match valid?", typeOverride = FieldType.YesOrNo)
    private final String valid;
}
