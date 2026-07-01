package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CaveatDataTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailValidationRule;
import uk.gov.hmcts.probate.validator.NocEmailAddressNotifyValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRule;
import uk.gov.hmcts.probate.validator.ValidationRuleCaveats;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.NO;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventValidationService {

    private final CCDDataTransformer ccdBeanTransformer;
    private final CaveatDataTransformer caveatDataTransformer;

    static final String SEP_DATE_BEFORE_DOB_EN = "WIP Date of divorce, dissolution or judicial separation"
            + " must be after the date of birth";
    static final String SEP_DATE_BEFORE_DOB_CY = "WIP (NEEDS TRANSLATION) Date of divorce, dissolution or"
            + " judicial separation must be after the date of birth";

    static final String SEP_DATE_AFTER_DOD_EN = "Date of divorce, dissolution or judicial separation must be"
            + " before the date of death";
    static final String SEP_DATE_AFTER_DOD_CY = "Rhaid i ddyddiad yr ysgariad, diddymiad neu ymwahaniad"
            + " cyfreithiol fod cyn dyddiad y farwolaeth";

    static final String DIV_DISS_OUTSIDE_ENG_WALES_EN = "You cannot use the online service if the divorce or"
            + " dissolution took place outside of England and Wales. You should apply by post using Form PA1A instead.";
    static final String DIV_DISS_OUTSIDE_ENG_WALES_CY = "Ni allwch ddefnyddio'r gwasanaeth ar-lein os"
            + " digwyddodd yr ysgariad neu'r diddymiad y tu allan i Gymru a Lloegr. Dylech wneud cais drwy'r post gan"
            + " ddefnyddio Ffurflen PA1A yn lle hynny.";

    static final String SEPARATION_OUTSIDE_ENG_WALES_EN = "You cannot use the online service if the judicial"
            + " separation took place outside of England and  Wales. You should apply by post using Form PA1A instead.";
    static final String SEPARATION_OUTSIDE_ENG_WALES_CY = "Ni allwch ddefnyddio'r gwasanaeth ar-lein os"
            + " digwyddodd yr ymwahaniad cyfreithiol y tu allan i Gymru a Lloegr. Dylech wneud cais drwy'r post gan"
            + " ddefnyddio Ffurflen PA1A yn lle hynny.";

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

    public CallbackResponse validateNocEmail(CaseData caseData,
                                             NocEmailAddressNotifyValidationRule nocEmailAddressNotifyValidationRule) {
        String solicitorEmail = getRemovedSolicitorEmail(caseData);
        List<FieldErrorResponse> businessErrors = nocEmailAddressNotifyValidationRule
                .validate(caseData.getApplicationType(), solicitorEmail);
        return CallbackResponse.builder()
                .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                .build();
    }

    private String getRemovedSolicitorEmail(CaseData caseData) {
        return caseData.getRemovedRepresentative() != null
                ? caseData.getRemovedRepresentative().getSolicitorEmail() : null;
    }

    public CaveatCallbackResponse validateCaveatNocEmail(CaveatData caveatData,
                                             NocEmailAddressNotifyValidationRule nocEmailAddressNotifyValidationRule) {
        String solicitorEmail = getCaveatRemovedSolicitorEmail(caveatData);
        List<FieldErrorResponse> businessErrors = nocEmailAddressNotifyValidationRule
                .validate(caveatData.getApplicationType(), solicitorEmail);
        return CaveatCallbackResponse.builder()
                .errors(businessErrors.stream().map(FieldErrorResponse::getMessage).collect(Collectors.toList()))
                .build();
    }

    private String getCaveatRemovedSolicitorEmail(CaveatData caveatData) {
        return caveatData.getRemovedRepresentative() != null
                ? caveatData.getRemovedRepresentative().getSolicitorEmail() : null;
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

    public List<String> generateErrorsSepDateBounds(final CaseData caseData) {
        final List<String> errors = new ArrayList<>();

        final LocalDate dob = caseData.getDeceasedDateOfBirth();
        final LocalDate dod = caseData.getDeceasedDateOfDeath();
        final String sepDateStr = caseData.getDateOfDivorcedCPJudicially();

        if (! StringUtils.isBlank(sepDateStr)) {
            final LocalDate sepDate = LocalDate.parse(sepDateStr);
            if (sepDate.isBefore(dob)) {
                errors.add(SEP_DATE_BEFORE_DOB_EN);
                errors.add(SEP_DATE_BEFORE_DOB_CY);
            }
            if (sepDate.isAfter(dod)) {
                errors.add(SEP_DATE_AFTER_DOD_EN);
                errors.add(SEP_DATE_AFTER_DOD_CY);
            }
        }
        return errors;
    }

    public List<String> generateErrorsSepOutsideEngWales(final CaseData caseData) {
        final List<String> errors = new ArrayList<>();

        final String separationInEngWal = caseData.getDeceasedDivorcedInEnglandOrWales();
        final String decMaritalStatus = caseData.getDeceasedMaritalStatus();

        if (NO.equals(separationInEngWal)) {
            if ("divorcedCivilPartnership".equals(decMaritalStatus)) {
                errors.add(DIV_DISS_OUTSIDE_ENG_WALES_EN);
                errors.add(DIV_DISS_OUTSIDE_ENG_WALES_CY);
            }
            if ("judicially".equals(decMaritalStatus)) {
                errors.add(SEPARATION_OUTSIDE_ENG_WALES_EN);
                errors.add(SEPARATION_OUTSIDE_ENG_WALES_CY);
            }
        }

        return errors;
    }

}
