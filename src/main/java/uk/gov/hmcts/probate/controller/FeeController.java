package uk.gov.hmcts.probate.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.controller.validation.FeeGroup;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Data
@RestController
public class FeeController {

    private static final Logger log = LoggerFactory.getLogger(FeeController.class);

    private final FeeService feeService;
    private final CCDDataTransformer ccdBeanTransformer;
    private final CallbackResponseTransformer callbackResponseTransformer;

    @PostMapping(path = "/fee", consumes = APPLICATION_JSON_UTF8_VALUE, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> getFee(@Validated(FeeGroup.class)
                                                   @RequestBody CallbackRequest callbackRequest,
                                                   BindingResult bindingResult) {

        log.debug("POST /fee: {}", callbackRequest);

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid payload", bindingResult);
        }

        CCDData ccdData = ccdBeanTransformer.transform(callbackRequest);

        FeeServiceResponse feeServiceResponse = feeService.getTotalFee(
            ccdData.getIht().getNetValueInPounds(),
            ccdData.getFee().getExtraCopiesOfGrant(),
            ccdData.getFee().getOutsideUKGrantCopies());

        CallbackResponse callbackResponse = callbackResponseTransformer.transform(callbackRequest, feeServiceResponse);

        return ResponseEntity.ok(callbackResponse);
    }
}
