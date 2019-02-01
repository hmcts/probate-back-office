package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.probateman.ProbateManCaseResponse;
import uk.gov.hmcts.probate.model.probateman.ProbateManModel;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;
import uk.gov.hmcts.probate.service.LegacyImportService;
import uk.gov.hmcts.probate.service.LegacySearchService;
import uk.gov.hmcts.probate.service.ProbateManService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProbateManController {

    private final ProbateManService probateManService;
    private final LegacySearchService legacySearchService;
    private final LegacyImportService legacyImportService;
    private final BusinessValidationMessageService businessValidationMessageService;
    private static final String SUBMISSION_NOT_ALLOWED = "Submission not allowed";

    @GetMapping(path = "/probateManTypes/{probateManType}/cases/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProbateManCaseResponse> saveGrantApplicationToCcd(@PathVariable("probateManType") ProbateManType probateManType,
                                                                            @PathVariable("id") String id) {
        ProbateManModel probateManModel = probateManService.getProbateManModel(Long.parseLong(id), probateManType);
        return ResponseEntity.ok(ProbateManCaseResponse.builder().probateManCase(probateManModel).build());
    }

    @PostMapping(path = "/legacy/search", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> legacySearch(@RequestBody CallbackRequest callbackRequest,
                                                         HttpServletRequest request) {
        log.info("Performing legacy case search");
        List<CollectionMember<CaseMatch>> caseMatchesList = legacySearchService.findLegacyCaseMatches(callbackRequest.getCaseDetails());

        ResponseCaseData responseCaseData = ResponseCaseData.builder()
                .legacySearchResultRows(caseMatchesList)
                .build();

        CallbackResponse callbackResponse = CallbackResponse.builder()
                .data(responseCaseData)
                .build();
        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/legacy/doImport", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> doImport(@RequestBody CallbackRequest callbackRequest,
                                                     HttpServletRequest request) {

        log.info("Performing legacy case import");
        List<CaseMatch> rows = legacyImportService.importLegacyRows(callbackRequest.getCaseDetails().getData().getLegacySearchResultRows());

        ResponseCaseData responseCaseData = ResponseCaseData.builder()
                .legacySearchResultRows(rows.stream().map(row -> new CollectionMember<CaseMatch>(row)).collect(Collectors.toList()))
                .build();

        CallbackResponse callbackResponse = CallbackResponse.builder()
                .data(responseCaseData)
                .build();
        return ResponseEntity.ok(callbackResponse);
    }

    @PostMapping(path = "/legacy/resetSearch", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> resetSearch(@RequestBody CallbackRequest callbackRequest,
                                                        BindingResult bindingResult,
                                                        HttpServletRequest request) {

        log.info("submitting legacy search - invalid action");
        List<String> errors = Arrays.asList(businessValidationMessageService
                .generateError(SUBMISSION_NOT_ALLOWED, "legacyCaseSubmissionNotAllowed").getMessage());
        CallbackResponse callbackResponse = CallbackResponse.builder()
                .errors(errors)
                .build();

        return ResponseEntity.ok(callbackResponse);
    }

}
