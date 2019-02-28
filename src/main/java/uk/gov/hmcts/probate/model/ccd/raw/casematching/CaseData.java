package uk.gov.hmcts.probate.model.ccd.raw.casematching;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class CaseData {
    private final String legacyId;
    private final String ccdCaseId;
    private final String legacyType;
    private final String deceasedForenames;
    private final String deceasedSurname;
    private final LocalDate deceasedDateOfDeath;
    private final LocalDate deceasedDateOfBirth;
    private final SolsAddress deceasedAddress;
    private final List<CollectionMember<AliasName>> solsDeceasedAliasNamesList;

    @JsonProperty("legacy_case_type")
    private final String legacyCaseType;

    @JsonProperty(value = "alias_names")
    private final String legacySearchAliasNames;

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }
}