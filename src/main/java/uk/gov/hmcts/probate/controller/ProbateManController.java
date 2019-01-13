package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.criterion.CaseMatchingCriteria;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.service.CaseMatchingService;
import uk.gov.hmcts.probate.service.ProbateManService;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProbateManController {

    private final ProbateManService probateManService;
    private static final List<CaseType> GRANT_MATCH_TYPES = Arrays.asList(LEGACY);
    private final CaseMatchingService caseMatchingService;

    @PostMapping(path = "/probateManTypes/{probateManType}/cases/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CaseDetails> saveGrantApplicationToCcd(@PathVariable("probateManType") ProbateManType probateManType,
                                                                 @PathVariable("id") String id) {
        log.info("Performing legacy case save");
        return ResponseEntity.ok(probateManService.saveToCcd(Long.parseLong(id), probateManType));
    }

    @PostMapping(path = "/legacy/search", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> legacySearch(@RequestBody CallbackRequest callbackRequest,
                                                         HttpServletRequest request) {
        log.info("Performing legacy case search");

        CaseMatchingCriteria caseMatchingCriteria = CaseMatchingCriteria.of(callbackRequest.getCaseDetails());

        List<CaseMatch> caseMatches = new ArrayList<>();
        caseMatches.addAll(caseMatchingService.findCrossMatches(GRANT_MATCH_TYPES, caseMatchingCriteria));

        List<CollectionMember<CaseMatch>> caseMatchesList = new ArrayList();

        caseMatches.forEach(match -> caseMatchesList.add(new CollectionMember<CaseMatch>(null, match)));

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
        CaseData data = callbackRequest.getCaseDetails().getData();
        List<CollectionMember<CaseMatch>> rows = data.getLegacySearchResultRows();

        rows.stream().map(CollectionMember::getValue)
                .filter(row -> "YES".equalsIgnoreCase(row.getDoImport()))
                //.filter(row -> LegacyCaseType.GRANT_OF_REPRESENTATION.getName().equalsIgnoreCase(row.getType()))
                .forEach(row -> importRow(row));
        ResponseCaseData responseCaseData = ResponseCaseData.builder()
                .legacySearchResultRows(rows)
                .build();

        CallbackResponse callbackResponse = CallbackResponse.builder()
                .data(responseCaseData)
                .build();
        return ResponseEntity.ok(callbackResponse);
    }

    private void importRow(CaseMatch row) {
        String legacyCaseTypeName = row.getType();
        LegacyCaseType legacyCaseType = LegacyCaseType.getByLegacyCaseTypeName(legacyCaseTypeName);
        String id = row.getId();
        log.info("Importing legacy case into ccd for legacyCaseType=" + legacyCaseTypeName + ", with id=" + id);
        ProbateManType probateManType = ProbateManType.getByLegacyCaseType(legacyCaseType);
        probateManService.saveToCcd(Long.parseLong(id), probateManType);

    }
}
