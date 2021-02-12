package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementDetails;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.ResponseWillLodgementData;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.ResponseWillLodgementData.ResponseWillLodgementDataBuilder;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.WillLodgementCallbackResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.Constants.DATE_OF_DEATH_TYPE_DEFAULT;

@Component
@RequiredArgsConstructor
public class WillLodgementCallbackResponseTransformer {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ApplicationType DEFAULT_APPLICATION_TYPE = PERSONAL;
    private static final String DEFAULT_REGISTRY_LOCATION = "Leeds";
    private final DocumentTransformer documentTransformer;

    public WillLodgementCallbackResponse transform(WillLodgementCallbackRequest callbackRequest) {
        ResponseWillLodgementData responseWillLodgementData =
            getResponseWillLodgementData(callbackRequest.getCaseDetails())
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
            .lodgedDate(transformToString(willLodgementData.getLodgedDate()))
            .willDate(transformToString(willLodgementData.getWillDate()))
            .codicilDate(transformToString(willLodgementData.getCodicilDate()))
            .numberOfCodicils(transformToString(willLodgementData.getNumberOfCodicils()))
            .jointWill(willLodgementData.getJointWill())

            .deceasedForenames(willLodgementData.getDeceasedForenames())
            .deceasedSurname(willLodgementData.getDeceasedSurname())
            .deceasedGender(willLodgementData.getDeceasedGender())
            .deceasedDateOfBirth(transformToString(willLodgementData.getDeceasedDateOfBirth()))
            .deceasedDateOfDeath(transformToString(willLodgementData.getDeceasedDateOfDeath()))
            .deceasedTypeOfDeath(
                ofNullable(willLodgementData.getDeceasedTypeOfDeath()).orElse(DATE_OF_DEATH_TYPE_DEFAULT))
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

            .caseMatches(willLodgementData.getCaseMatches())

            .withdrawalReason(willLodgementData.getWithdrawalReason())
            .documentsGenerated(willLodgementData.getDocumentsGenerated())
            .documentsUploaded(willLodgementData.getDocumentsUploaded())

            .recordId(willLodgementData.getRecordId())
            .legacyCaseViewUrl(willLodgementData.getLegacyCaseViewUrl())
            .legacyType(willLodgementData.getLegacyType());

    }

    public WillLodgementCallbackResponse addMatches(WillLodgementCallbackRequest request, List<CaseMatch> newMatches) {
        List<CollectionMember<CaseMatch>> storedMatches = request.getCaseDetails().getData().getCaseMatches();

        // Removing case matches that have been already added
        storedMatches.stream()
            .map(CollectionMember::getValue).forEach(newMatches::remove);

        storedMatches.addAll(newMatches.stream().map(CollectionMember::new).collect(Collectors.toList()));

        storedMatches.sort(Comparator.comparingInt(m -> ofNullable(m.getValue().getValid()).orElse("").length()));

        ResponseWillLodgementDataBuilder responseCaseDataBuilder =
            getResponseWillLodgementData(request.getCaseDetails());

        return transformResponse(responseCaseDataBuilder.build());
    }

    public WillLodgementCallbackResponse addDocuments(WillLodgementCallbackRequest callbackRequest,
                                                      List<Document> documents) {
        documents.forEach(document -> documentTransformer.addDocument(callbackRequest, document));

        ResponseWillLodgementData.ResponseWillLodgementDataBuilder responseWillLodgementData =
            getResponseWillLodgementData(callbackRequest.getCaseDetails());
        return transformResponse(responseWillLodgementData.build());
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
