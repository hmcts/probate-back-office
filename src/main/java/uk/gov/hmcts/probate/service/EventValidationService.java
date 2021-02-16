package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.payments.PaymentResponse;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CaveatDataTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.CreditAccountPaymentValidationRule;
import uk.gov.hmcts.probate.validator.EmailValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRuleCaveats;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventValidationService {

    private final CCDDataTransformer ccdBeanTransformer;
    private final CaveatDataTransformer caveatDataTransformer;

    public List<FieldErrorResponse> validate(CCDData form, List<? extends ValidationRule> rules) {
        return rules.stream()
            .map(rule -> rule.validate(form))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    public CallbackResponse validateRequest(CallbackRequest callbackRequest,
                                             List<? extends ValidationRule> rules) {

        CCDData ccdData = ccdBeanTransformer.transform(callbackRequest);

        List<FieldErrorResponse> businessErrors = validate(ccdData, rules);

        return CallbackResponse.builder()
                .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                .build();
    }

    public List<FieldErrorResponse> validateEmail(CCDData form, List<? extends EmailValidationRule> rules) {
        return rules.stream()
                .map(rule -> rule.validate(form))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public CallbackResponse validateEmailRequest(CallbackRequest callbackRequest,
                                            List<? extends EmailValidationRule> rules) {

        CCDData ccdData = ccdBeanTransformer.transformEmail(callbackRequest);

        List<FieldErrorResponse> businessErrors = validateEmail(ccdData, rules);

        return CallbackResponse.builder()
                .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                .build();
    }

    public CaveatCallbackResponse validateCaveatRequest(CaveatCallbackRequest callbackRequest,
                                                  List<? extends ValidationRuleCaveats> rules) {

        CaveatData caveatData = caveatDataTransformer.transformCaveats(callbackRequest);

        List<FieldErrorResponse> businessErrors = validateCaveat(caveatData, rules);

        return CaveatCallbackResponse.builder()
                .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                .build();
    }

    private List<FieldErrorResponse> validateCaveat(CaveatData form, List<? extends ValidationRuleCaveats> rules) {
        return rules.stream()
                .map(rule -> (rule).validate(form))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<FieldErrorResponse> validateBulkPrint(CCDData form, List<? extends BulkPrintValidationRule> rules) {
        return rules.stream()
                .map(rule -> rule.validate(form))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public CallbackResponse validateBulkPrintResponse(String letterId,
                                                      List<? extends BulkPrintValidationRule> rules) {

        CCDData ccdData = ccdBeanTransformer.transformBulkPrint(letterId);
        List<FieldErrorResponse> businessErrors = validateBulkPrint(ccdData, rules);
        return CallbackResponse.builder()
                .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                .build();
    }

    public CaveatCallbackResponse validateCaveatBulkPrintResponse(String letterId,
                                                      List<? extends BulkPrintValidationRule> rules) {

        CCDData ccdData = ccdBeanTransformer.transformBulkPrint(letterId);
        List<FieldErrorResponse> businessErrors = validateBulkPrint(ccdData, rules);
        return CaveatCallbackResponse.builder()
                .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                .build();
    }

    public CallbackResponse validatePaymentResponse(CaseDetails caseDetails, PaymentResponse paymentResponse, 
            CreditAccountPaymentValidationRule creditAccountPaymentValidationRule) {
        List<FieldErrorResponse> businessErrors = creditAccountPaymentValidationRule
            .validate(caseDetails, paymentResponse);
        return CallbackResponse.builder()
            .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
            .build();
    }
}
