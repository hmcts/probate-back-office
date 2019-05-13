package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData.ResponseCaveatDataBuilder;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;
import static uk.gov.hmcts.probate.model.DocumentType.CAVEAT;

@Component
@RequiredArgsConstructor
public class CaveatCallbackResponseTransformer {

    private final DocumentTransformer documentTransformer;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ApplicationType DEFAULT_APPLICATION_TYPE = PERSONAL;
    private static final String DEFAULT_REGISTRY_LOCATION = "Leeds";

    public CaveatCallbackResponse caveatRaised(CaveatCallbackRequest caveatCallbackRequest, List<Document> documents, String letterId) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        documents.forEach(document -> documentTransformer.addDocument(caveatCallbackRequest, document));
        ResponseCaveatDataBuilder responseCaveatDataBuilder = getResponseCaveatData(caveatDetails);

        if (documentTransformer.hasDocumentWithType(documents, CAVEAT)) {
            responseCaveatDataBuilder
                    .sendToBulkPrintRequested(caveatCallbackRequest.getCaseDetails().getData().getSendToBulkPrint())
                    .bulkPrintSendLetterId(letterId)
                    .build();

        }
        responseCaveatDataBuilder
                .expiryDate(dateTimeFormatter.format(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)))
                .caveatRaisedEmailNotificationRequested(
                        caveatCallbackRequest.getCaseDetails().getData().getCaveatRaisedEmailNotification())
                .build();

        return transformResponse(responseCaveatDataBuilder.build());
    }

    public CaveatCallbackResponse defaultCaveatValues(CaveatCallbackRequest caveatCallbackRequest) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        ResponseCaveatData responseCaveatData = getResponseCaveatData(caveatDetails)
                .caveatRaisedEmailNotificationRequested(caveatCallbackRequest.getCaseDetails().getData().getCaveatRaisedEmailNotification())
                .build();

        return transformResponse(responseCaveatData);
    }

    public CaveatCallbackResponse generalMessage(CaveatCallbackRequest caveatCallbackRequest, Document document) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaseDetails();

        caveatDetails.getData().getDocumentsGenerated().add(new CollectionMember<>(null, document));

        ResponseCaveatData responseCaveatData = getResponseCaveatData(caveatDetails)
                .messageContent("")
                .build();

        return transformResponse(responseCaveatData);
    }

    public CaveatCallbackResponse transform(CaveatCallbackRequest callbackRequest) {
        ResponseCaveatData responseCaveatData = getResponseCaveatData(callbackRequest.getCaseDetails())
                .build();

        return transformResponse(responseCaveatData);
    }

    public CaveatCallbackResponse addMatches(CaveatCallbackRequest request, List<CaseMatch> newMatches) {
        List<CollectionMember<CaseMatch>> storedMatches = request.getCaseDetails().getData().getCaseMatches();

        // Removing case matches that have been already added
        storedMatches.stream()
                .map(CollectionMember::getValue).forEach(newMatches::remove);

        storedMatches.addAll(newMatches.stream().map(CollectionMember::new).collect(Collectors.toList()));

        storedMatches.sort(Comparator.comparingInt(m -> ofNullable(m.getValue().getValid()).orElse("").length()));

        ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = getResponseCaveatData(request.getCaseDetails());

        return transformResponse(responseCaseDataBuilder.build());
    }

    private CaveatCallbackResponse transformResponse(ResponseCaveatData responseCaveatData) {
        return CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
    }

    private ResponseCaveatDataBuilder getResponseCaveatData(CaveatDetails caveatDetails) {
        CaveatData caveatData = caveatDetails.getData();

        return ResponseCaveatData.builder()

                .applicationType(ofNullable(caveatData.getApplicationType()).orElse(DEFAULT_APPLICATION_TYPE))
                .registryLocation(ofNullable(caveatData.getRegistryLocation()).orElse(DEFAULT_REGISTRY_LOCATION))
                .deceasedForenames(caveatData.getDeceasedForenames())
                .deceasedSurname(caveatData.getDeceasedSurname())
                .deceasedDateOfDeath(dateTimeFormatter.format(caveatData.getDeceasedDateOfDeath()))
                .deceasedDateOfBirth(transformToString(caveatData.getDeceasedDateOfBirth()))
                .deceasedAnyOtherNames(caveatData.getDeceasedAnyOtherNames())
                .deceasedFullAliasNameList(caveatData.getDeceasedFullAliasNameList())
                .deceasedAddress(caveatData.getDeceasedAddress())

                .caveatorForenames(caveatData.getCaveatorForenames())
                .caveatorSurname(caveatData.getCaveatorSurname())
                .caveatorEmailAddress(caveatData.getCaveatorEmailAddress())
                .caveatorAddress(caveatData.getCaveatorAddress())

                .caseMatches(caveatData.getCaseMatches())

                .expiryDate(transformToString(caveatData.getExpiryDate()))
                .messageContent(caveatData.getMessageContent())
                .caveatReopenReason(caveatData.getCaveatReopenReason())

                .documentsUploaded(caveatData.getDocumentsUploaded())
                .documentsGenerated(caveatData.getDocumentsGenerated())
                .recordId(caveatData.getRecordId())
                .legacyCaseViewUrl(caveatData.getLegacyCaseViewUrl())
                .legacyType(caveatData.getLegacyType())

                .caveatRaisedEmailNotification(caveatData.getCaveatRaisedEmailNotification())
                .sendToBulkPrint(caveatData.getSendToBulkPrint());
    }

    private String transformToString(LocalDate dateValue) {
        return ofNullable(dateValue)
                .map(String::valueOf)
                .orElse(null);
    }
}
