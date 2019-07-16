package uk.gov.hmcts.probate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/error", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
@RestController
public class BulkScanningController {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @PostMapping(path = "/attach-scanned-docs", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CallbackResponse> displayAttachScanDocErrorIfUsedFromUI(@RequestBody CallbackRequest callbackRequest) {
        String[] args = {callbackRequest.getCaseDetails().getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage("errorAttachScannedDocs", args, Locale.UK);
        throw new BusinessValidationException(userMessage,
                "User should not call attach scanned docs for case: " + callbackRequest.getCaseDetails().getId());
    }

}
