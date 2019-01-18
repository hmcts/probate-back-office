package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.ResponseWillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.ResponseWillLodgementData.ResponseWillLodgementDataBuilder;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.WillLodgementCallbackResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;

@Component
@RequiredArgsConstructor
public class WillLodgementCallbackResponseTransformer {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ApplicationType DEFAULT_APPLICATION_TYPE = PERSONAL;
    private static final String DEFAULT_REGISTRY_LOCATION = "Leeds";

    public WillLodgementCallbackResponse transform(WillLodgementCallbackRequest callbackRequest) {
        ResponseWillLodgementData responseWillLodgementData = getResponseWillLodgementData(callbackRequest.getCaseDetails())
                .build();

        return transformResponse(responseWillLodgementData);
    }

    private WillLodgementCallbackResponse transformResponse(ResponseWillLodgementData responseWillLodgementData) {
        return WillLodgementCallbackResponse.builder().responseWillLodgementData(responseWillLodgementData).build();
    }

    private ResponseWillLodgementDataBuilder getResponseWillLodgementData(WillLodgementDetails willLodgementDetails) {
        WillLodgementData willLodgementData = willLodgementDetails.getData();

        return ResponseWillLodgementData.builder()

                .applicationType(ofNullable(willLodgementData.getApplicationType()).orElse(DEFAULT_APPLICATION_TYPE))
                .registryLocation(ofNullable(willLodgementData.getRegistryLocation()).orElse(DEFAULT_REGISTRY_LOCATION))

                .lodgementType(willLodgementData.getLodgementType())
                .lodgedDate(dateTimeFormatter.format(willLodgementData.getLodgedDate()))
                .willDate(dateTimeFormatter.format(willLodgementData.getWillDate()))
                .codicilDate(transformToString(willLodgementData.getCodicilDate()))
                .numberOfCodicils(transformToString(willLodgementData.getNumberOfCodicils()))
                .jointWill(willLodgementData.getJointWill())

                .deceasedForenames(willLodgementData.getDeceasedForenames())
                .deceasedSurname(willLodgementData.getDeceasedSurname())
                .deceasedGender(willLodgementData.getDeceasedGender())
                .deceasedDateOfBirth(transformToString(willLodgementData.getDeceasedDateOfBirth()))
                .deceasedDateOfDeath(dateTimeFormatter.format(willLodgementData.getDeceasedDateOfDeath()))
                .deceasedTypeOfDeath(willLodgementData.getDeceasedTypeOfDeath())
                .deceasedAnyOtherNames(willLodgementData.getDeceasedAnyOtherNames())
                .deceasedFullAliasNameList(willLodgementData.getDeceasedFullAliasNameList())
                .deceasedAddress(willLodgementData.getDeceasedAddress())
                .deceasedEmailAddress(willLodgementData.getDeceasedEmailAddress())

                .executorTitle(willLodgementData.getExecutorTitle())
                .executorForenames(willLodgementData.getExecutorForenames())
                .executorSurname(willLodgementData.getExecutorSurname())
                .executorAddress(willLodgementData.getExecutorAddress())
                .executorEmailAddress(willLodgementData.getExecutorEmailAddress())
                .additionalExecutorList(willLodgementData.getAdditionalExecutorList())

                .withdrawalReason(willLodgementData.getWithdrawalReason())
                .documentsGenerated(willLodgementData.getDocumentsGenerated())
                .documentsUploaded(willLodgementData.getDocumentsUploaded());
    }

    private String transformToString(Long longValue) {
        return ofNullable(longValue)
                .map(String::valueOf)
                .orElse(null);
    }

    private String transformToString(LocalDate dateValue) {
        return ofNullable(dateValue)
                .map(String::valueOf)
                .orElse(null);
    }
}
