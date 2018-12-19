package uk.gov.hmcts.probate.model.criterion;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.time.LocalDate;

@Builder
@Data
public class CaseMatchingCriteria {
    private final Long id;
    private final String deceasedForenames;
    private final String deceasedSurname;
    private final LocalDate deceasedDateOfDeath;

    public static CaseMatchingCriteria of(CaseDetails caseDetails) {
        return CaseMatchingCriteria.builder()
                .id(caseDetails.getId())
                .deceasedForenames(caseDetails.getData().getDeceasedForenames())
                .deceasedSurname(caseDetails.getData().getDeceasedSurname())
                .deceasedDateOfDeath(caseDetails.getData().getDeceasedDateOfDeath())
                .build();
    }

    public static CaseMatchingCriteria of(CaveatDetails caveatDetails) {
        return CaseMatchingCriteria.builder()
                .id(caveatDetails.getId())
                .deceasedForenames(caveatDetails.getCaveatData().getDeceasedForenames())
                .deceasedSurname(caveatDetails.getCaveatData().getDeceasedSurname())
                .deceasedDateOfDeath(caveatDetails.getCaveatData().getDeceasedDateOfDeath())
                .build();
    }
}
