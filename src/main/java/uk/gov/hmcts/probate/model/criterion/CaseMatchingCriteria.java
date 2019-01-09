package uk.gov.hmcts.probate.model.criterion;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Builder
@Data
public class CaseMatchingCriteria {
    private final Long id;
    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String deceasedAliases;
    private final String deceasedDateOfBirth;
    private final String deceasedDateOfDeath;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    public static CaseMatchingCriteria of(CaseDetails caseDetails) {
        return CaseMatchingCriteria.builder()
                .id(caseDetails.getId())
                .deceasedForenames(caseDetails.getData().getDeceasedForenames())
                .deceasedSurname(caseDetails.getData().getDeceasedSurname())
                .deceasedAliases(caseDetails.getData().getDeceasedFullName())
                .deceasedDateOfBirth(getDateFormatted(caseDetails.getData().getDeceasedDateOfBirth()))
                .deceasedDateOfDeath(getDateFormatted(caseDetails.getData().getDeceasedDateOfDeath()))
                .build();
    }

    public static CaseMatchingCriteria of(CaveatDetails caveatDetails) {
        return CaseMatchingCriteria.builder()
                .id(caveatDetails.getId())
                .deceasedForenames(caveatDetails.getData().getDeceasedForenames())
                .deceasedSurname(caveatDetails.getData().getDeceasedSurname())
                .deceasedAliases(caveatDetails.getData().getDeceasedFullName())
                .deceasedDateOfBirth(getDateFormatted(caveatDetails.getData().getDeceasedDateOfBirth()))
                .deceasedDateOfDeath(getDateFormatted(caveatDetails.getData().getDeceasedDateOfDeath()))
                .build();
    }

    public static CaseMatchingCriteria of(StandingSearchDetails standingSearchDetails) {
        return CaseMatchingCriteria.builder()
                .id(standingSearchDetails.getId())
                .deceasedForenames(standingSearchDetails.getData().getDeceasedForenames())
                .deceasedSurname(standingSearchDetails.getData().getDeceasedSurname())
                .deceasedAliases(standingSearchDetails.getData().getDeceasedFullName())
                .deceasedDateOfBirth(getDateFormatted(standingSearchDetails.getData().getDeceasedDateOfBirth()))
                .deceasedDateOfDeath(getDateFormatted(standingSearchDetails.getData().getDeceasedDateOfDeath()))
                .build();
    }

    private static String getDateFormatted(LocalDate date) {
        if (date == null) {
            return "";
        }

        return date.format(dateTimeFormatter);
    }
}
