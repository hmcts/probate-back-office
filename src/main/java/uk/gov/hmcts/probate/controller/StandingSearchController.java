package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.standingsearch.response.StandingSearchCallbackResponse;
import uk.gov.hmcts.probate.service.CcdSupplementaryDataService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.transformer.StandingSearchCallbackResponseTransformer;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RequestMapping(value = "/standing-search", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class StandingSearchController {

    private final StandingSearchCallbackResponseTransformer standingSearchCallbackResponseTransformer;
    private final DocumentGeneratorService documentGeneratorService;
    private final CcdSupplementaryDataService ccdSupplementaryDataService;

    @PostMapping(path = "/create")
    public ResponseEntity<StandingSearchCallbackResponse> createStandingSearch(
        @RequestBody StandingSearchCallbackRequest callbackRequest) {

        StandingSearchCallbackResponse callbackResponse =
            standingSearchCallbackResponseTransformer.standingSearchCreated(callbackRequest);

        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/supplementaryData", consumes = APPLICATION_JSON_VALUE,
            produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<StandingSearchCallbackResponse> setSupplementaryData(
            @RequestBody StandingSearchCallbackRequest callbackRequest) {

        ccdSupplementaryDataService.submitSupplementaryDataToCcd(
                callbackRequest.getCaseDetails().getId().toString());
        StandingSearchCallbackResponse callbackResponse =
                StandingSearchCallbackResponse.builder().build();
        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/setup-for-permanent-removal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandingSearchCallbackResponse> setupForPermanentRemovalStandingSearch(
            @RequestBody StandingSearchCallbackRequest callbackRequest) {
        return ResponseEntity.ok(standingSearchCallbackResponseTransformer
                .setupOriginalDocumentsForRemoval(callbackRequest));
    }

    @PostMapping(path = "/permanently-delete-removed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandingSearchCallbackResponse> permanentlyDeleteRemovedStandingSearch(
            @RequestBody StandingSearchCallbackRequest callbackRequest) {
        documentGeneratorService.permanentlyDeleteRemovedDocumentsForStandingSearch(callbackRequest);
        return ResponseEntity.ok(standingSearchCallbackResponseTransformer.transform(callbackRequest));
    }

}
