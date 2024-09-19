package uk.gov.hmcts.probate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.PrepareNocCaveatService;
import uk.gov.hmcts.probate.service.PrepareNocService;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.NocEmailAddressNotifyValidationRule;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.service.notify.NotificationClientException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.State.NOC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/noc")
public class NoticeOfChangeController {
    private final PrepareNocService prepareNocService;

    private final PrepareNocCaveatService prepareNocCaveatService;
    private final EventValidationService eventValidationService;
    private final NotificationService notificationService;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;
    private final NocEmailAddressNotifyValidationRule nocEmailAddressNotifyValidationRule;


    @PostMapping(path = "/apply-decision", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(description = "About to submit NoC Request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Callback processed.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AboutToStartOrSubmitCallbackResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)})
    @SecurityRequirement(name = "Bearer Authentication")
    public AboutToStartOrSubmitCallbackResponse applyDecision(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authorisation,
            @RequestBody CallbackRequest callbackRequest) {
        log.info("Apply Decision - " + callbackRequest.getCaseDetails().getId().toString());
        return prepareNocService.applyDecision(callbackRequest, authorisation);
    }

    @PostMapping(path = "/caveat-apply-decision", consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @Operation(description = "About to submit NoC Request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Callback processed.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AboutToStartOrSubmitCallbackResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)})
    @SecurityRequirement(name = "Bearer Authentication")
    public AboutToStartOrSubmitCallbackResponse applyDecisionCaveat(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @Parameter(hidden = true) String authorisation,
            @RequestBody CallbackRequest callbackRequest) {
        log.info("Apply Decision - " + callbackRequest.getCaseDetails().getId().toString());
        return prepareNocCaveatService.applyDecision(callbackRequest, authorisation);
    }

    @PostMapping(path = "/caveat-noc-notification", produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CaveatCallbackResponse> sendNOCEmailNotification(
            @RequestBody CaveatCallbackRequest callbackRequest) throws NotificationClientException {
        log.info("Preparing to send email notification for NOC");
        CaveatDetails caveatDetails = callbackRequest.getCaseDetails();
        CaveatData caveatData = caveatDetails.getData();
        CaveatCallbackResponse response;

        List<Document> documents = new ArrayList<>();
        response = eventValidationService.validateCaveatNocEmail(caveatData, nocEmailAddressNotifyValidationRule);
        if (response.getErrors().isEmpty() && !isFirstNOCOnPaperForm(caveatData)) {
            log.info("Initiate call to notify Solicitor for case id {} ",
                    callbackRequest.getCaseDetails().getId());
            Document nocSentEmail = notificationService.sendCaveatNocEmail(NOC, caveatDetails);
            documents.add(nocSentEmail);
            log.info("Successful response from notify for case id {} ",
                    callbackRequest.getCaseDetails().getId());
            response = caveatCallbackResponseTransformer.addNocDocuments(callbackRequest, documents);
        } else {
            log.info("No email sent or document returned to case: {}", caveatDetails.getId());
        }
        return ResponseEntity.ok(response);
    }

    private boolean isFirstNOCOnPaperForm(CaveatData caveatData) {
        return YES.equals(caveatData.getPaperForm())
                && caveatData.getChangeOfRepresentatives().size() == 1;
    }
}
