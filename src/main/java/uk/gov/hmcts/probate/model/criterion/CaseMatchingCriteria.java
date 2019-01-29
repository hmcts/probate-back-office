package uk.gov.hmcts.probate.model.criterion;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Builder
@Data
public class CaseMatchingCriteria {
    private final Long id;
    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String deceasedFullName;
    private final String deceasedDateOfBirth;
    private final String deceasedDateOfDeath;
    private final LocalDate deceasedDateOfDeathRaw;
    private final LocalDate deceasedDateOfBirthRaw;
    private final List<String> deceasedAliases;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    public static CaseMatchingCriteria of(CaseDetails caseDetails) {
        CaseData data = caseDetails.getData();

        return CaseMatchingCriteria.builder()
                .id(caseDetails.getId())
                .deceasedForenames(data.getDeceasedForenames())
                .deceasedSurname(data.getDeceasedSurname())
                .deceasedFullName(data.getDeceasedFullName())
                .deceasedAliases(Optional.ofNullable(data.getSolsDeceasedAliasNamesList()).orElse(emptyList()).stream()
                        .map(CollectionMember::getValue)
                        .map(AliasName::getSolsAliasname)
                        .collect(Collectors.toList()))
                .deceasedDateOfBirth(getDateFormatted(data.getDeceasedDateOfBirth()))
                .deceasedDateOfBirthRaw(data.getDeceasedDateOfBirth())
                .deceasedDateOfDeath(getDateFormatted(data.getDeceasedDateOfDeath()))
                .deceasedDateOfDeathRaw(data.getDeceasedDateOfDeath())
                .build();
    }

    public static CaseMatchingCriteria of(CaveatDetails caveatDetails) {
        CaveatData data = caveatDetails.getData();

        return CaseMatchingCriteria.builder()
                .id(caveatDetails.getId())
                .deceasedForenames(data.getDeceasedForenames())
                .deceasedSurname(data.getDeceasedSurname())
                .deceasedFullName(data.getDeceasedFullName())
                .deceasedAliases(Optional.ofNullable(data.getDeceasedFullAliasNameList()).orElse(emptyList()).stream()
                        .map(CollectionMember::getValue)
                        .map(ProbateFullAliasName::getFullAliasName)
                        .collect(Collectors.toList()))
                .deceasedDateOfBirth(getDateFormatted(data.getDeceasedDateOfBirth()))
                .deceasedDateOfBirthRaw(data.getDeceasedDateOfBirth())
                .deceasedDateOfDeath(getDateFormatted(data.getDeceasedDateOfDeath()))
                .deceasedDateOfDeathRaw(data.getDeceasedDateOfDeath())
                .build();
    }

    public static CaseMatchingCriteria of(StandingSearchDetails standingSearchDetails) {
        StandingSearchData data = standingSearchDetails.getData();

        return CaseMatchingCriteria.builder()
                .id(standingSearchDetails.getId())
                .deceasedForenames(data.getDeceasedForenames())
                .deceasedSurname(data.getDeceasedSurname())
                .deceasedFullName(data.getDeceasedFullName())
                .deceasedAliases(Optional.ofNullable(data.getDeceasedFullAliasNameList()).orElse(emptyList()).stream()
                        .map(CollectionMember::getValue)
                        .map(ProbateFullAliasName::getFullAliasName)
                        .collect(Collectors.toList()))
                .deceasedDateOfBirth(getDateFormatted(data.getDeceasedDateOfBirth()))
                .deceasedDateOfBirthRaw(data.getDeceasedDateOfBirth())
                .deceasedDateOfDeath(getDateFormatted(data.getDeceasedDateOfDeath()))
                .deceasedDateOfDeathRaw(data.getDeceasedDateOfDeath())
                .build();
    }

    public static CaseMatchingCriteria of(WillLodgementDetails willLodgementDetails) {
        WillLodgementData data = willLodgementDetails.getData();

        return CaseMatchingCriteria.builder()
                .id(willLodgementDetails.getId())
                .deceasedForenames(data.getDeceasedForenames())
                .deceasedSurname(data.getDeceasedSurname())
                .deceasedFullName(data.getDeceasedFullName())
                .deceasedAliases(Optional.ofNullable(data.getDeceasedFullAliasNameList()).orElse(emptyList()).stream()
                        .map(CollectionMember::getValue)
                        .map(ProbateFullAliasName::getFullAliasName)
                        .collect(Collectors.toList()))
                .deceasedDateOfBirth(getDateFormatted(data.getDeceasedDateOfBirth()))
                .deceasedDateOfBirthRaw(data.getDeceasedDateOfBirth())
                .deceasedDateOfDeath(getDateFormatted(data.getDeceasedDateOfDeath()))
                .deceasedDateOfDeathRaw(data.getDeceasedDateOfDeath())
                .build();
    }

    private static String getDateFormatted(LocalDate date) {
        if (date == null) {
            return LocalDate.now().plusYears(1L).format(dateTimeFormatter);
        }

        return date.format(dateTimeFormatter);
    }
}
