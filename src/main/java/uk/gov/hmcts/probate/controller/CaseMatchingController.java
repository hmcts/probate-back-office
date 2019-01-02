package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.CaseType.CAVEAT;
import static uk.gov.hmcts.probate.model.CaseType.GRANT_OF_REPRESENTATION;
import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@RequiredArgsConstructor
@RequestMapping(value = "/case-matching", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class CaseMatchingController {

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    private final CaseMatchingService caseMatchingService;

    private static final List<CaseType> GRANT_MATCH_TYPES = Arrays.asList(GRANT_OF_REPRESENTATION, CAVEAT, LEGACY);
    private static final List<CaseType> CAVEAT_MATCH_TYPES = Arrays.asList(GRANT_OF_REPRESENTATION, CAVEAT, LEGACY);

    @PostMapping(path = "/search")
    public ResponseEntity<CallbackResponse> search(@RequestBody CallbackRequest request) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(request.getCaseDetails());

        List<CaseMatch> caseMatches = caseMatchingService.findCrossMatches(GRANT_MATCH_TYPES, caseMatchingCriteria);

        return ResponseEntity.ok(callbackResponseTransformer.addMatches(request, caseMatches));
    }

    @PostMapping(path = "/search-from-caveat-flow")
    public ResponseEntity<CaveatCallbackResponse> searchFromCaveatFlow(@RequestBody CaveatCallbackRequest request) {
        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(request.getCaveatDetails());

        List<CaseMatch> caseMatches = caseMatchingService.findCrossMatches(CAVEAT_MATCH_TYPES, caseMatchingCriteria);

        return ResponseEntity.ok(caveatCallbackResponseTransformer.addMatches(request, caseMatches));
    }
}
