package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchData;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchDetails;
import uk.gov.hmcts.probate.model.ccd.standingsearch.response.ResponseStandingSearchData;
import uk.gov.hmcts.probate.model.ccd.standingsearch.response.StandingSearchCallbackResponse;

import java.time.format.DateTimeFormatter;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;

@Component
@RequiredArgsConstructor
public class StandingSearchCallbackResponseTransformer {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ApplicationType DEFAULT_APPLICATION_TYPE = PERSONAL;
    private static final String DEFAULT_REGISTRY_LOCATION = "Leeds";

    public StandingSearchCallbackResponse transform(StandingSearchCallbackRequest callbackRequest) {
        ResponseStandingSearchData responseStandingSearchData = getResponseStandingSearchtData(callbackRequest.getStandingSearchDetails())
                .build();

        return transformResponse(responseStandingSearchData);
    }

    private StandingSearchCallbackResponse transformResponse(ResponseStandingSearchData responseStandingSearchData) {
        return StandingSearchCallbackResponse.builder().responseStandingSearchData(responseStandingSearchData).build();
    }

    private ResponseStandingSearchData.ResponseStandingSearchDataBuilder getResponseStandingSearchtData(StandingSearchDetails standingSearchDetails) {
        StandingSearchData standingSearchData = standingSearchDetails.getStandingSearchData();

        return ResponseStandingSearchData.builder()

                .applicationType(ofNullable(standingSearchData.getApplicationType()).orElse(DEFAULT_APPLICATION_TYPE))
                .registryLocation(ofNullable(standingSearchData.getRegistryLocation()).orElse(DEFAULT_REGISTRY_LOCATION))
                .deceasedForenames(standingSearchData.getDeceasedForenames())
                .deceasedSurname(standingSearchData.getDeceasedSurname())
                .deceasedDateOfDeath(dateTimeFormatter.format(standingSearchData.getDeceasedDateOfDeath()))
                .deceasedDateOfBirth(dateTimeFormatter.format(standingSearchData.getDeceasedDateOfBirth()))
                .deceasedAnyOtherNames(standingSearchData.getDeceasedAnyOtherNames())
                .deceasedFullAliasNameList(standingSearchData.getDeceasedFullAliasNameList())
                .deceasedAddress(standingSearchData.getDeceasedAddress())

                .applicantForenames(standingSearchData.getApplicantForenames())
                .applicantSurname(standingSearchData.getApplicantSurname())
                .applicantEmailAddress(standingSearchData.getApplicantEmailAddress())
                .applicantAddress(standingSearchData.getApplicantAddress())

                .numberOfCopies(standingSearchData.getNumberOfCopies());
    }
}
