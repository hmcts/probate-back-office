package uk.gov.hmcts.probate.transformer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.Constants.CAVEAT_LIFESPAN;

@Component
@RequiredArgsConstructor
public class CaveatCallbackResponseTransformer {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final ApplicationType DEFAULT_APPLICATION_TYPE = PERSONAL;
    private static final String DEFAULT_REGISTRY_LOCATION = "Leeds";

    public CaveatCallbackResponse caveatRaised(CaveatCallbackRequest caveatCallbackRequest) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaveatDetails();

        ResponseCaveatData responseCaveatData = getResponseCaveatData(caveatDetails)
                .cavExpiryDate(dateTimeFormatter.format(LocalDate.now().plusMonths(CAVEAT_LIFESPAN)))
                .build();

        return transformResponse(responseCaveatData);
    }

    public CaveatCallbackResponse generalMessage(CaveatCallbackRequest caveatCallbackRequest, Document document) {
        CaveatDetails caveatDetails = caveatCallbackRequest.getCaveatDetails();

        caveatDetails.getCaveatData().getDocumentsGenerated().add(new CollectionMember<>(null, document));

        ResponseCaveatData responseCaveatData = getResponseCaveatData(caveatDetails)
                .cavMessageContent("")
                .build();

        return transformResponse(responseCaveatData);
    }

    public CaveatCallbackResponse transform(CaveatCallbackRequest callbackRequest) {
        ResponseCaveatData responseCaveatData = getResponseCaveatData(callbackRequest.getCaveatDetails())
                .build();

        return transformResponse(responseCaveatData);
    }

    private CaveatCallbackResponse transformResponse(ResponseCaveatData responseCaveatData) {
        return CaveatCallbackResponse.builder().caveatData(responseCaveatData).build();
    }

    private ResponseCaveatData.ResponseCaveatDataBuilder getResponseCaveatData(CaveatDetails caveatDetails) {
        CaveatData caveatData = caveatDetails.getCaveatData();

        return ResponseCaveatData.builder()

                .cavApplicationType(ofNullable(caveatData.getApplicationType()).orElse(DEFAULT_APPLICATION_TYPE))
                .cavRegistryLocation(ofNullable(caveatData.getRegistryLocation()).orElse(DEFAULT_REGISTRY_LOCATION))
                .cavDeceasedForenames(caveatData.getDeceasedForenames())
                .cavDeceasedSurname(caveatData.getDeceasedSurname())
                .cavDeceasedDateOfDeath(dateTimeFormatter.format(caveatData.getDeceasedDateOfDeath()))
                .cavDeceasedDateOfBirth(dateTimeFormatter.format(caveatData.getCavDeceasedDateOfBirth()))
                .cavDeceasedAnyOtherNames(caveatData.getDeceasedAnyOtherNames())
                .cavDeceasedFullAliasNameList(caveatData.getDeceasedFullAliasNameList())
                .cavDeceasedAddress(caveatData.getDeceasedAddress())

                .cavCaveatorForenames(caveatData.getCaveatorForenames())
                .cavCaveatorSurname(caveatData.getCaveatorSurname())
                .cavCaveatorEmailAddress(caveatData.getCaveatorEmailAddress())
                .cavCaveatorAddress(caveatData.getCaveatorAddress())

                .cavMessageContent(caveatData.getMessageContent())

                .cavDocumentsUploaded(caveatData.getDocumentsUploaded())
                .cavDocumentsGenerated(caveatData.getDocumentsGenerated());
    }
}
