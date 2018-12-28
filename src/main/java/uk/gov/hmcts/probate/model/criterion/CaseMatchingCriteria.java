package uk.gov.hmcts.probate.model.criterion;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchDetails;

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
                .deceasedForenames(caveatDetails.getData().getDeceasedForenames())
                .deceasedSurname(caveatDetails.getData().getDeceasedSurname())
                .deceasedDateOfDeath(caveatDetails.getData().getDeceasedDateOfDeath())
                .build();
    }

    public static CaseMatchingCriteria of(StandingSearchDetails standingSearchDetails) {
        return CaseMatchingCriteria.builder()
                .id(standingSearchDetails.getId())
                .deceasedForenames(standingSearchDetails.getData().getDeceasedForenames())
                .deceasedSurname(standingSearchDetails.getData().getDeceasedSurname())
                .deceasedDateOfDeath(standingSearchDetails.getData().getDeceasedDateOfDeath())
                .build();
    }
}
