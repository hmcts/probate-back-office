package uk.gov.hmcts.probate.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.changerule.ApplicantSiblingsRule;
import uk.gov.hmcts.probate.changerule.ChangeRule;
import uk.gov.hmcts.probate.changerule.DiedOrNotApplyingRule;
import uk.gov.hmcts.probate.changerule.EntitledMinorityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.ImmovableEstateRule;
import uk.gov.hmcts.probate.changerule.LifeInterestRule;
import uk.gov.hmcts.probate.changerule.MinorityInterestRule;
import uk.gov.hmcts.probate.changerule.RenouncingRule;
import uk.gov.hmcts.probate.changerule.ResiduaryRule;
import uk.gov.hmcts.probate.changerule.SolsExecutorRule;
import uk.gov.hmcts.probate.changerule.SpouseOrCivilRule;
import uk.gov.hmcts.probate.model.PageTextConstants;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.template.markdown.MarkdownSubstitutionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.template.MarkdownTemplate.STOP_BODY;

@Component
@RequiredArgsConstructor
public class ConfirmationResponseService {

    static final String PAYMENT_METHOD_VALUE_FEE_ACCOUNT = "fee account";
    static final String PAYMENT_REFERENCE_CHEQUE = "Cheque (payable to 'HM Courts & Tribunals Service')";
    private static final String REASON_FOR_NOT_APPLYING_RENUNCIATION = "Renunciation";
    private static final String REASON_FOR_NOT_APPLYING_DIED_BEFORE = "DiedBefore";
    private static final String REASON_FOR_NOT_APPLYING_DIED_AFTER = "DiedAfter";
    private static final String IHT_400421 = "IHT400421";
    private static final String CAVEAT_APPLICATION_FEE = "3.00";
    public static final String NO_PAYMENT_NEEDED = "No payment needed";
    public static final String PARM_PAYMENT_METHOD = "{{paymentMethod}}";
    public static final String PARM_SELECTED_PBA = "{{selectedPBA}}";
    public static final String PARM_PAYMENT_REFERENCE_NUMBER = "{{paymentReferenceNumber}}";
    private final MessageResourceService messageResourceService;
    private final MarkdownSubstitutionService markdownSubstitutionService;
    private final ApplicantSiblingsRule applicantSiblingsConfirmationResponseRule;
    private final DiedOrNotApplyingRule diedOrNotApplyingRule;
    private final EntitledMinorityRule entitledMinorityRule;
    private final ExecutorsRule executorsConfirmationResponseRule;
    private final ImmovableEstateRule immovableEstateRule;
    private final LifeInterestRule lifeInterestRule;
    private final MinorityInterestRule minorityInterestConfirmationResponseRule;
    private final RenouncingRule renouncingConfirmationResponseRule;
    private final ResiduaryRule residuaryRule;
    private final SolsExecutorRule solsExecutorConfirmationResponseRule;
    private final SpouseOrCivilRule spouseOrCivilConfirmationResponseRule;
    @Value("${markdown.templatesDirectory}")
    private String templatesDirectory;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AfterSubmitCallbackResponse getNextStepsConfirmation(CaveatData caveatData) {
        return getStopConfirmationUsingMarkdown(generateNextStepsBodyMarkdown(caveatData));
    }

    public AfterSubmitCallbackResponse getNextStepsConfirmation(CCDData ccdData) {
        return getStopConfirmationUsingMarkdown(generateNextStepsBodyMarkdown(ccdData));
    }

    public AfterSubmitCallbackResponse getStopConfirmation(CallbackRequest callbackRequest) {
        return getStopConfirmationUsingMarkdown(generateStopBodyMarkdown(callbackRequest.getCaseDetails().getData()));
    }

    private TemplateResponse generateStopBodyMarkdown(CaseData caseData) {

        Optional<TemplateResponse> response = Optional.of(new TemplateResponse(null));

        if (GRANT_TYPE_PROBATE.equals(caseData.getSolsWillType())) {
            response = getStopBodyMarkdown(caseData, executorsConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }
        }

        if (GRANT_TYPE_INTESTACY.equals(caseData.getSolsWillType())) {
            response = getStopBodyMarkdown(caseData, minorityInterestConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, immovableEstateRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, applicantSiblingsConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, renouncingConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, solsExecutorConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, spouseOrCivilConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }
        }

        if (GRANT_TYPE_ADMON.equals(caseData.getSolsWillType())) {
            response = getStopBodyMarkdown(caseData, immovableEstateRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, diedOrNotApplyingRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, entitledMinorityRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, lifeInterestRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, residuaryRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, solsExecutorConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }
        }

        return response.orElseGet(() -> new TemplateResponse(null));
    }

    private Optional<TemplateResponse> getStopBodyMarkdown(CaseData caseData,
                                                           ChangeRule changeRule,
                                                           MarkdownTemplate template) {
        if (changeRule.isChangeNeeded(caseData)) {
            String messageKey = changeRule.getConfirmationBodyMessageKey();
            String reasonText = messageResourceService.getMessage(messageKey);
            Map<String, String> keyValue = new HashMap<>();
            keyValue.put("{{reason}}", reasonText);
            return Optional.of(markdownSubstitutionService.generatePage(templatesDirectory, template, keyValue));
        }

        return Optional.empty();
    }

    private AfterSubmitCallbackResponse getStopConfirmationUsingMarkdown(TemplateResponse templateResponse) {
        return AfterSubmitCallbackResponse.builder()
            .confirmationHeader(null)
            .confirmationBody(templateResponse.getTemplate())
            .build();
    }

    private TemplateResponse generateNextStepsBodyMarkdown(CaveatData caveatData) {
        Map<String, String> keyValue = new HashMap<>();
        keyValue.put("{{solicitorReference}}", caveatData.getSolsSolicitorAppReference());
        String caseSubmissionDate = "";
        if (caveatData.getApplicationSubmittedDate() != null) {
            caseSubmissionDate = caveatData.getApplicationSubmittedDate().format(formatter);
        }
        keyValue.put("{{caseSubmissionDate}}", caseSubmissionDate);
        keyValue.put("{{applicationFee}}", CAVEAT_APPLICATION_FEE);
        keyValue.put(PARM_PAYMENT_METHOD, caveatData.getSolsPaymentMethods());
        keyValue.put(PARM_SELECTED_PBA, caveatData.getSolsPBANumber().getValue().getCode());
        keyValue.put(PARM_PAYMENT_REFERENCE_NUMBER, getPaymentReference(caveatData));

        return markdownSubstitutionService
            .generatePage(templatesDirectory, MarkdownTemplate.CAVEAT_NEXT_STEPS, keyValue);
    }

    private TemplateResponse generateNextStepsBodyMarkdown(CCDData ccdData) {
        Map<String, String> keyValue = new HashMap<>();
        keyValue.put("{{solicitorReference}}", ccdData.getSolicitorReference());
        String caseSubmissionDate = "";
        if (ccdData.getCaseSubmissionDate() != null) {
            caseSubmissionDate = ccdData.getCaseSubmissionDate().format(formatter);
        }
        keyValue.put("{{caseSubmissionDate}}", caseSubmissionDate);
        keyValue.put("{{solsSolicitorFirmName}}", ccdData.getSolicitor().getFirmName());
        keyValue.put("{{solsSolicitorAddress}}", createAddressValueString(ccdData.getSolicitor().getFirmAddress()));
        keyValue
            .put("{{solsSolicitorAddress.addressLine1}}", ccdData.getSolicitor().getFirmAddress().getAddressLine1());
        keyValue
            .put("{{solsSolicitorAddress.addressLine2}}", ccdData.getSolicitor().getFirmAddress().getAddressLine2());
        keyValue
            .put("{{solsSolicitorAddress.addressLine3}}", ccdData.getSolicitor().getFirmAddress().getAddressLine3());
        keyValue.put("{{solsSolicitorAddress.postTown}}", ccdData.getSolicitor().getFirmAddress().getPostTown());
        keyValue.put("{{solsSolicitorAddress.county}}", ccdData.getSolicitor().getFirmAddress().getCounty());
        keyValue.put("{{solsSolicitorAddress.postCode}}", ccdData.getSolicitor().getFirmAddress().getPostCode());
        keyValue.put("{{solsSolicitorAddress.country}}", ccdData.getSolicitor().getFirmAddress().getCounty());
        keyValue.put("{{solicitorName}}", ccdData.getSolicitor().getFullname());
        keyValue.put("{{solicitorJobRole}}", ccdData.getSolicitor().getJobRole());
        keyValue.put("{{deceasedFirstname}}", ccdData.getDeceased().getFirstname());
        keyValue.put("{{deceasedLastname}}", ccdData.getDeceased().getLastname());
        keyValue.put("{{deceasedDateOfDeath}}", ccdData.getDeceased().getDateOfDeath().format(formatter));
        if (ccdData.getFee().getPaymentMethod() != null) {
            keyValue.put(PARM_PAYMENT_METHOD, ccdData.getFee().getPaymentMethod());
            keyValue.put(PARM_SELECTED_PBA, ccdData.getFee().getSolsPBANumber());
            keyValue.put(PARM_PAYMENT_REFERENCE_NUMBER, getPaymentReference(ccdData));
        } else {
            keyValue.put(PARM_PAYMENT_METHOD, NO_PAYMENT_NEEDED);
            keyValue.put(PARM_SELECTED_PBA, NO_PAYMENT_NEEDED);
            keyValue.put(PARM_PAYMENT_REFERENCE_NUMBER, NO_PAYMENT_NEEDED);
        }
        keyValue.put("{{paymentAmount}}", getAmountAsString(ccdData.getFee().getAmount()));
        keyValue.put("{{applicationFee}}", getAmountAsString(ccdData.getFee().getApplicationFee()));
        keyValue.put("{{feeForUkCopies}}", getOptionalAmountAsString(ccdData.getFee().getFeeForUkCopies()));
        keyValue.put("{{feeForNonUkCopies}}", getOptionalAmountAsString(ccdData.getFee().getFeeForNonUkCopies()));
        keyValue.put("{{paymentReferenceNumber}}", getPaymentReference(ccdData));
        keyValue.put("{{caseRef}}", ccdData.getCaseId().toString());

        String solsWillType = ccdData.getSolsWillType();
        String originalWill = "\n*   the original will";
        if (solsWillType.equals(GRANT_TYPE_INTESTACY)) {
            originalWill = "";
        } else if ("Yes".equals(ccdData.getWillHasCodicils())) {
            originalWill = "\n*   the original will and any codicils";
        }
        
        keyValue.put("{{originalWill}}", originalWill);
        
        String additionalInfo = ccdData.getSolsAdditionalInfo();
        if (Strings.isNullOrEmpty(additionalInfo)) {
            additionalInfo = "None provided";
        }

        String ihtFormValue = ccdData.getIht().getFormName();
        String ihtText = "";
        String ihtForm = "";
        if (!ihtFormValue.contentEquals(IHT_400421)) {
            ihtText = "\n*   the inheritance tax form ";
            if ("Yes".equals(ccdData.getIht217())) {
                ihtForm = "IHT205 and IHT217";
            } else {
                ihtForm = ccdData.getIht().getFormName();
            }
        }

        String legalPhotocopy = "";
        if (hasNoLegalStatmentBeenUploaded(ccdData)) {
            legalPhotocopy = format("*   %s", PageTextConstants.DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY);
        }
        keyValue.put("{{legalPhotocopy}}", legalPhotocopy);
        keyValue.put("{{ihtText}}", ihtText);
        keyValue.put("{{ihtForm}}", ihtForm);
        keyValue.put("{{additionalInfo}}", additionalInfo);
        keyValue.put("{{renouncingExecutors}}", getRenouncingExecutors(ccdData.getExecutors()));
        keyValue.put("{{deadExecutors}}", getDeadExecutors(ccdData.getExecutors()));

        return markdownSubstitutionService.generatePage(templatesDirectory, MarkdownTemplate.NEXT_STEPS, keyValue);
    }

    boolean hasNoLegalStatmentBeenUploaded(CCDData ccdData) {
        return !ccdData.isHasUploadedLegalStatement();
    } 
    
    private String createAddressValueString(SolsAddress address) {
        StringBuilder solsSolicitorAddress = new StringBuilder();
        return solsSolicitorAddress.append(defaultString(address.getAddressLine1()))
            .append(defaultString(address.getAddressLine2()))
            .append(defaultString(address.getAddressLine3()))
            .append(defaultString(address.getPostTown()))
            .append(defaultString(address.getCounty()))
            .append(defaultString(address.getPostCode()))
            .append(defaultString(address.getCountry()))
            .toString();
    }

    private String defaultString(String value) {
        return value == null ? "" : value + ", ";
    }

    private String getRenouncingExecutors(List<Executor> executors) {
        String renouncingExecutors = executors.stream()
            .filter(executor -> !executor.isApplying())
            .filter(executor -> REASON_FOR_NOT_APPLYING_RENUNCIATION.equals(executor.getReasonNotApplying()))
            .map(executor -> "*   renunciation form for " + executor.getForename() + " " + executor.getLastname())
            .collect(Collectors.joining("\n"));
        return !StringUtils.isEmpty(renouncingExecutors) ? renouncingExecutors + "\n" : renouncingExecutors;
    }

    private String getDeadExecutors(List<Executor> executors) {
        String deadExecutors = executors.stream()
            .filter(executor -> !executor.isApplying())
            .filter(executor -> REASON_FOR_NOT_APPLYING_DIED_BEFORE.equals(executor.getReasonNotApplying())
                || REASON_FOR_NOT_APPLYING_DIED_AFTER.equals(executor.getReasonNotApplying()))
            .map(executor -> "*   death certificate for " + executor.getForename() + " " + executor.getLastname())
            .collect(Collectors.joining("\n"));
        return !StringUtils.isEmpty(deadExecutors) ? deadExecutors + "\n" : deadExecutors;
    }

    private String getOptionalAmountAsString(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return getAmountAsString(amount);
    }

    private String getAmountAsString(BigDecimal amount) {
        return amount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).toString();
    }

    private String getPaymentReference(CCDData ccdData) {
        if (PAYMENT_METHOD_VALUE_FEE_ACCOUNT.equals(ccdData.getFee().getPaymentMethod())) {
            return ccdData.getFee().getSolsPBAPaymentReference();
        } else {
            return PAYMENT_REFERENCE_CHEQUE;
        }
    }

    private String getPaymentReference(CaveatData caveatData) {
        if (PAYMENT_METHOD_VALUE_FEE_ACCOUNT.equals(caveatData.getSolsPaymentMethods())) {
            return caveatData.getSolsPBAPaymentReference();
        } else {
            return PAYMENT_REFERENCE_CHEQUE;
        }
    }
}
