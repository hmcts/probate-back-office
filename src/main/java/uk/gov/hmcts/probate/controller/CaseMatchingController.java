package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.standingsearch.response.StandingSearchCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.willlodgement.request.WillLodgementCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.willlodgement.response.WillLodgementCallbackResponse;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.service.LegacyImportService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.StandingSearchCallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.WillLodgementCallbackResponseTransformer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@RequiredArgsConstructor
@RequestMapping(value = "/case-matching", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class CaseMatchingController {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    private final StandingSearchCallbackResponseTransformer standingSearchCallbackResponseTransformer;
    private final WillLodgementCallbackResponseTransformer willLodgementCallbackResponseTransformer;
    private final CaseMatchingService caseMatchingService;
    private final LegacyImportService legacyImportService;
    private final BusinessValidationMessageService businessValidationMessageService;


    @PostMapping(path = "/search-from-grant-flow")
    public ResponseEntity<CallbackResponse> search(@RequestBody CallbackRequest request) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(request.getCaseDetails());

        List<CaseMatch> caseMatches = caseMatchingService.findCrossMatches(CaseType.getAll(), caseMatchingCriteria);

        return ResponseEntity.ok(callbackResponseTransformer.addMatches(request, caseMatches));
    }

    @PostMapping(path = "/search-from-caveat-flow")
    public ResponseEntity<CaveatCallbackResponse> searchFromCaveatFlow(@RequestBody CaveatCallbackRequest request) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(request.getCaseDetails());

        List<CaseMatch> caseMatches = caseMatchingService.findCrossMatches(CaseType.getAll(), caseMatchingCriteria);

        return ResponseEntity.ok(caveatCallbackResponseTransformer.addMatches(request, caseMatches));
    }

    @PostMapping(path = "/search-from-standing-search-flow")
    public ResponseEntity<StandingSearchCallbackResponse> searchFromStandingSearchFlow(
            @RequestBody StandingSearchCallbackRequest request) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(request.getCaseDetails());

        List<CaseMatch> caseMatches = caseMatchingService.findCrossMatches(CaseType.getAll(), caseMatchingCriteria);

        return ResponseEntity.ok(standingSearchCallbackResponseTransformer.addMatches(request, caseMatches));
    }

    @PostMapping(path = "/search-from-will-lodgement-flow")
    public ResponseEntity<WillLodgementCallbackResponse> searchFromWillLodgementFlow(
            @RequestBody WillLodgementCallbackRequest request) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(request.getCaseDetails());

        List<CaseMatch> caseMatches = caseMatchingService.findCrossMatches(CaseType.getAll(), caseMatchingCriteria);

        return ResponseEntity.ok(willLodgementCallbackResponseTransformer.addMatches(request, caseMatches));
    }

    @PostMapping(path = "/import-legacy-from-grant-flow", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> doImportFromGrant(@RequestBody CallbackRequest callbackRequest,
                                                     HttpServletRequest request) {

        List<FieldErrorResponse> errors = validateLegacySearchImportRows(callbackRequest.getCaseDetails().getData().getCaseMatches());
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(CallbackResponse.builder()
                    .errors(errors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                    .build());
        }


        log.info("Performing import-legacy-from-grant-flow");
        List<CaseMatch> rows = legacyImportService
                .importLegacyRows(callbackRequest.getCaseDetails().getData().getCaseMatches());

        return ResponseEntity.ok(callbackResponseTransformer.addMatches(callbackRequest, rows));
    }

    @PostMapping(path = "/import-legacy-from-caveat-flow", consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CaveatCallbackResponse> doImportFromCaveat(@RequestBody CaveatCallbackRequest callbackRequest,
                                                     HttpServletRequest request) {

        List<FieldErrorResponse> errors = validateLegacySearchImportRows(callbackRequest.getCaseDetails().getData().getCaseMatches());
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(CaveatCallbackResponse.builder()
                    .errors(errors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                    .build());
        }

        log.info("import-legacy-from-caveat-flow");
        List<CaseMatch> rows = legacyImportService
                .importLegacyRows(callbackRequest.getCaseDetails().getData().getCaseMatches());

        return ResponseEntity.ok(caveatCallbackResponseTransformer.addMatches(callbackRequest, rows));
    }

    @PostMapping(path = "/import-legacy-from-standing-search-flow",
            consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<StandingSearchCallbackResponse> doImportFromStandingSearch(
            @RequestBody StandingSearchCallbackRequest callbackRequest, HttpServletRequest request) {

        List<FieldErrorResponse> errors = validateLegacySearchImportRows(callbackRequest.getCaseDetails().getData().getCaseMatches());
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(StandingSearchCallbackResponse.builder()
                    .errors(errors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                    .build());
        }

        log.info("import-legacy-from-standing-search-flow");
        List<CaseMatch> rows = legacyImportService
                .importLegacyRows(callbackRequest.getCaseDetails().getData().getCaseMatches());

        return ResponseEntity.ok(standingSearchCallbackResponseTransformer.addMatches(callbackRequest, rows));
    }

    @PostMapping(path = "/import-legacy-from-will-lodgement-flow",
            consumes = APPLICATION_JSON_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<WillLodgementCallbackResponse> doImportFromWillLodgement(
            @RequestBody WillLodgementCallbackRequest callbackRequest, HttpServletRequest request) {

        List<FieldErrorResponse> errors = validateLegacySearchImportRows(callbackRequest.getCaseDetails().getData().getCaseMatches());
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(WillLodgementCallbackResponse.builder()
                    .errors(errors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                    .build());
        }

        log.info("import-legacy-from-will-lodgement-flow");
        List<CaseMatch> rows = legacyImportService
                .importLegacyRows(callbackRequest.getCaseDetails().getData().getCaseMatches());

        return ResponseEntity.ok(willLodgementCallbackResponseTransformer.addMatches(callbackRequest, rows));
    }

    private List<FieldErrorResponse> validateLegacySearchImportRows(List<CollectionMember<CaseMatch>> caseMatches) {
        List<FieldErrorResponse> errors = new ArrayList<>();
        if (!legacyImportService.areLegacyRowsValidToImport(caseMatches)) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, "onlySingleImportAllowed"));
        }

        return errors;
    }


}
