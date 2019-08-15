package uk.gov.hmcts.probate.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.changerule.DomicilityRule;
import uk.gov.hmcts.probate.changerule.ExecutorsRule;
import uk.gov.hmcts.probate.changerule.MinorityRule;
import uk.gov.hmcts.probate.changerule.MultipleClaimsRule;
import uk.gov.hmcts.probate.changerule.NoOriginalWillRule;
import uk.gov.hmcts.probate.changerule.ChangeRule;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.template.markdown.MarkdownSubstitutionService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.template.MarkdownTemplate.STOP_BODY;

@Component
@RequiredArgsConstructor
public class ConfirmationResponseService {

    private static final String REASON_FOR_NOT_APPLYING_RENUNCIATION = "Renunciation";
    private static final String REASON_FOR_NOT_APPLYING_DIED_BEFORE = "DiedBefore";
    private static final String REASON_FOR_NOT_APPLYING_DIED_AFTER = "DiedAfter";
    private static final String IHT_400421 = "IHT400421";
    private static final String GRANT_TYPE_PROBATE = "WillLeft";
    private static final String GRANT_TYPE_INTESTACY = "NoWill";


    @Value("${markdown.templatesDirectory}")
    private String templatesDirectory;

    private final MessageResourceService messageResourceService;

    private final MarkdownSubstitutionService markdownSubstitutionService;
    private final NoOriginalWillRule noOriginalWillRule;
    private final DomicilityRule domicilityConfirmationResponseRule;
    private final ExecutorsRule executorsConfirmationResponseRule;
    private final MinorityRule minorityConfirmationResponseRule;
    private final MultipleClaimsRule multipleClaimsConfirmationResponseRule;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AfterSubmitCallbackResponse getStopConfirmation(CallbackRequest callbackRequest) {
        return getStopConfirmationUsingMarkdown(generateStopBodyMarkdown(callbackRequest.getCaseDetails().getData()));
    }

    public AfterSubmitCallbackResponse getNextStepsConfirmation(CCDData ccdData) {
        return getStopConfirmationUsingMarkdown(generateNextStepsBodyMarkdown(ccdData));
    }

    private TemplateResponse generateStopBodyMarkdown(CaseData caseData) {

        Optional<TemplateResponse> response = getStopBodyMarkdown(caseData, domicilityConfirmationResponseRule, STOP_BODY);
        if (response.isPresent()) {
            return response.get();
        }

        if (!GRANT_TYPE_INTESTACY.equals(caseData.getSolsWillType())) {
            response = getStopBodyMarkdown(caseData, noOriginalWillRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }

            response = getStopBodyMarkdown(caseData, multipleClaimsConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }
        }


        if (GRANT_TYPE_PROBATE.equals(caseData.getSolsWillType())) {
            response = getStopBodyMarkdown(caseData, executorsConfirmationResponseRule, STOP_BODY);
            if (response.isPresent()) {
                return response.get();
            }
        }

        if (GRANT_TYPE_INTESTACY.equals(caseData.getSolsWillType())) {
            response = getStopBodyMarkdown(caseData, minorityConfirmationResponseRule, STOP_BODY);
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
        keyValue.put("{{solsSolicitorAddress.addressLine1}}", ccdData.getSolicitor().getFirmAddress().getAddressLine1());
        keyValue.put("{{solsSolicitorAddress.addressLine2}}", ccdData.getSolicitor().getFirmAddress().getAddressLine2());
        keyValue.put("{{solsSolicitorAddress.addressLine3}}", ccdData.getSolicitor().getFirmAddress().getAddressLine3());
        keyValue.put("{{solsSolicitorAddress.postTown}}", ccdData.getSolicitor().getFirmAddress().getPostTown());
        keyValue.put("{{solsSolicitorAddress.county}}", ccdData.getSolicitor().getFirmAddress().getCounty());
        keyValue.put("{{solsSolicitorAddress.postCode}}", ccdData.getSolicitor().getFirmAddress().getPostCode());
        keyValue.put("{{solsSolicitorAddress.country}}", ccdData.getSolicitor().getFirmAddress().getCounty());
        keyValue.put("{{solicitorName}}", ccdData.getSolicitor().getFullname());
        keyValue.put("{{solicitorJobRole}}", ccdData.getSolicitor().getJobRole());
        keyValue.put("{{deceasedFirstname}}", ccdData.getDeceased().getFirstname());
        keyValue.put("{{deceasedLastname}}", ccdData.getDeceased().getLastname());
        keyValue.put("{{deceasedDateOfDeath}}", ccdData.getDeceased().getDateOfDeath().format(formatter));
        keyValue.put("{{ihtForm}}", ccdData.getIht().getFormName());
        keyValue.put("{{paymentMethod}}", ccdData.getFee().getPaymentMethod());
        keyValue.put("{{paymentAmount}}", getAmountAsString(ccdData.getFee().getAmount()));
        keyValue.put("{{applicationFee}}", getAmountAsString(ccdData.getFee().getApplicationFee()));
        keyValue.put("{{feeForUkCopies}}", getOptionalAmountAsString(ccdData.getFee().getFeeForUkCopies()));
        keyValue.put("{{feeForNonUkCopies}}", getOptionalAmountAsString(ccdData.getFee().getFeeForNonUkCopies()));
        keyValue.put("{{solsPaymentReferenceNumber}}", ccdData.getFee().getPaymentReferenceNumber());

        String additionalInfo = ccdData.getSolsAdditionalInfo();
        if (Strings.isNullOrEmpty(additionalInfo)) {
            additionalInfo = "None provided";
        }

        String ihtFormValue = ccdData.getIht().getFormName();
        String iht400 = "";
        if (ihtFormValue.contentEquals(IHT_400421)) {
            iht400 = "*   the stamped (receipted) IHT 421 with this application\n";
        }
        keyValue.put("{{iht400}}", iht400);
        keyValue.put("{{additionalInfo}}", additionalInfo);
        keyValue.put("{{renouncingExecutors}}", getRenouncingExecutors(ccdData.getExecutors()));
        keyValue.put("{{deadExecutors}}", getDeadExecutors(ccdData.getExecutors()));

        return markdownSubstitutionService.generatePage(templatesDirectory, MarkdownTemplate.NEXT_STEPS, keyValue);
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
        return amount.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).toString();
    }
}
