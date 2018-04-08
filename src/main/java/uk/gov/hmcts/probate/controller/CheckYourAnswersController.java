package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.controller.validation.SolAddEstateDetailsEventGroup;
import uk.gov.hmcts.probate.controller.validation.SolCheckYourAnswers;
import uk.gov.hmcts.probate.controller.validation.SolExecutorDetailsUpdated;
import uk.gov.hmcts.probate.controller.validation.SolReviewLegalStatement;
import uk.gov.hmcts.probate.controller.validation.SolicitorAddWillDetailsGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.template.PDFServiceTemplate;
import uk.gov.hmcts.probate.service.StateChangeService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Data
@RestController
@RequestMapping("/checkYourAnswers")
public class CheckYourAnswersController {

    private static final Logger log = LoggerFactory.getLogger(CheckYourAnswersController.class);

    private final CallbackResponseTransformer callbackResponseTransformer;
    private final PDFManagementService pdfManagementService;
    private final ObjectMapper objectMapper;
    private final StateChangeService stateChangeService;

    @PostMapping(path = "/beforeLegalStatement", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> checkBeforeLegalStatement(
        @Validated({SolicitorAddWillDetailsGroup.class,
            SolAddEstateDetailsEventGroup.class,
            SolExecutorDetailsUpdated.class,
            SolCheckYourAnswers.class}) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        Optional<String> newState = stateChangeService.getChangedStateForCheckYourAnswers(callbackRequest.getCaseDetails().getData());
        CallbackResponse response = callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, newState);

        if (Strings.isNullOrEmpty(response.getData().getState())) {
            PDFServiceTemplate pdfServiceTemplate = PDFServiceTemplate.LEGAL_STATEMENT;
            String json = objectMapper.writeValueAsString(callbackRequest);
            CCDDocument document = pdfManagementService.generateAndUpload(pdfServiceTemplate, json);
            response = callbackResponseTransformer.transform(callbackRequest, pdfServiceTemplate, document);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/beforeStatementOfTruth", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> checkBeforeStatementOfTruth(
        @Validated({SolicitorAddWillDetailsGroup.class,
            SolAddEstateDetailsEventGroup.class,
            SolExecutorDetailsUpdated.class,
            SolReviewLegalStatement.class}) @RequestBody CallbackRequest callbackRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        Optional<String> newState = stateChangeService.getChangedStateForStatementOfTruth(callbackRequest.getCaseDetails().getData());
        CallbackResponse response = callbackResponseTransformer.transformWithConditionalStateChange(callbackRequest, newState);

        return ResponseEntity.ok(response);
    }
}
