package uk.gov.hmcts.probate.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.probate.controller.validation.SolicitorApplicationSubmittedEventGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Data
@Controller
public class NextStepsController {

    private static final Logger log = LoggerFactory.getLogger(NextStepsController.class);

    private final CCDDataTransformer ccdBeanTransformer;
    private final ConfirmationResponseService confirmationResponseService;

    @PostMapping(path = "/nextsteps", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<AfterSubmitCallbackResponse> getNextSteps(
        @Validated(SolicitorApplicationSubmittedEventGroup.class) @RequestBody CallbackRequest callbackRequest,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CCDData ccdData = ccdBeanTransformer.transform(callbackRequest);
        AfterSubmitCallbackResponse afterSubmitCallbackResponse = confirmationResponseService
            .getNextStepsConfirmation(ccdData);

        return ResponseEntity.ok(afterSubmitCallbackResponse);
    }
}
