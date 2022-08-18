package uk.gov.hmcts.probate.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.IhtEstate207BusinessRule;
import uk.gov.hmcts.probate.businessrule.NoDocumentsRequiredBusinessRule;
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
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.AfterSubmitCallbackResponse;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.template.markdown.MarkdownDecoratorService;
import uk.gov.hmcts.probate.service.template.markdown.MarkdownSubstitutionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.template.MarkdownTemplate.STOP_BODY;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

@Component
@RequiredArgsConstructor
public class ConfirmationResponseService {

    private static final String CAVEAT_APPLICATION_FEE = "3.00";
    private final MessageResourceService messageResourceService;
    private final MarkdownSubstitutionService markdownSubstitutionService;
    private final MarkdownDecoratorService markdownDecoratorService;
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
    private final IhtEstate207BusinessRule ihtEstate207BusinessRule;
    private final NoDocumentsRequiredBusinessRule noDocumentsRequiredBusinessRule;
    @Value("${markdown.templatesDirectory}")
    private String templatesDirectory;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AfterSubmitCallbackResponse getNextStepsConfirmation(CaveatData caveatData) {
        return getStopConfirmationUsingMarkdown(generateNextStepsBodyMarkdown(caveatData));
    }

    public AfterSubmitCallbackResponse getNextStepsConfirmation(CCDData ccdData, CaseData caseData) {
        return getStopConfirmationUsingMarkdown(generateNextStepsBodyMarkdown(ccdData, caseData));
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

        return markdownSubstitutionService
            .generatePage(templatesDirectory, MarkdownTemplate.CAVEAT_NEXT_STEPS, keyValue);
    }

    private TemplateResponse generateNextStepsBodyMarkdown(CCDData ccdData, CaseData caseData) {
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
        keyValue.put("{{paymentAmount}}", getAmountAsString(ccdData.getFee().getAmount()));
        keyValue.put("{{applicationFee}}", getAmountAsString(ccdData.getFee().getApplicationFee()));
        keyValue.put("{{feeForUkCopies}}", getOptionalAmountAsString(ccdData.getFee().getFeeForUkCopies()));
        keyValue.put("{{feeForNonUkCopies}}", getOptionalAmountAsString(ccdData.getFee().getFeeForNonUkCopies()));
        keyValue.put("{{solsSolicitorAppReference}}", ccdData.getFee().getSolsSolicitorAppReference());
        keyValue.put("{{caseRef}}", ccdData.getCaseId().toString());
        keyValue.put("{{originalWill}}", getWillLabel(caseData));

        String additionalInfo = ccdData.getSolsAdditionalInfo();
        if (Strings.isNullOrEmpty(additionalInfo)) {
            additionalInfo = "None provided";
        }

        String legalPhotocopy = "";
        if (hasNoLegalStatmentBeenUploaded(ccdData)) {
            legalPhotocopy = format("*   %s", PageTextConstants.DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY);
        }
        keyValue.put("{{legalPhotocopy}}", legalPhotocopy);
        keyValue.put("{{ihtText}}", getIhtText(ccdData));
        keyValue.put("{{ihtForm}}", getIhtForm(ccdData));
        keyValue.put("{{additionalInfo}}", additionalInfo);
        keyValue.put("{{pa14form}}", getPA14FormLabel(caseData));
        keyValue.put("{{pa15form}}", getPA15FormLabel(caseData));
        keyValue.put("{{pa16form}}", getPA16FormLabel(caseData));
        keyValue.put("{{pa17form}}", getPA17FormLabel(caseData));
        keyValue.put("{{admonWillRenunciation}}", getAdmonWillRenunciationFormLabel(ccdData));
        keyValue.put("{{tcResolutionLodgedWithApp}}", getTcResolutionFormLabel(ccdData));
        keyValue.put("{{authenticatedTranslation}}", getAuthenticatedTranslationLabel(ccdData));
        keyValue.put("{{dispenseWithNoticeSupportingDocs}}", getDispenseWithNoticeSupportDocsLabelAndText(ccdData));
        MarkdownTemplate template;
        if (noDocumentsRequiredBusinessRule.isApplicable(caseData)) {
            template = MarkdownTemplate.NEXT_STEPS_NO_DOCUMENTS_REQUIRED;
        } else {
            template = MarkdownTemplate.NEXT_STEPS;
        }
        return markdownSubstitutionService.generatePage(templatesDirectory, template, keyValue);
    }

    private String getIhtForm(CCDData ccdData) {
        String ihtFormValue = ccdData.getIht().getFormName();
        String ihtForm = "";
        if (ihtFormValue != null && !ihtFormValue.contentEquals(IHT400421_VALUE)) {
            if (YES.equals(ccdData.getIht217())) {
                ihtForm = "IHT205 and IHT217";
            } else {
                ihtForm = ccdData.getIht().getFormName();
            }
        }

        return ihtForm;
    }

    private String getIhtText(CCDData ccdData) {
        String ihtFormValue = ccdData.getIht().getFormName();
        String ihtText = "";
        if (ihtFormValue == null) {
            CaseData caseData = CaseData.builder()
                .ihtFormEstateValuesCompleted(ccdData.getIht().getIhtFormEstateValuesCompleted())
                .ihtFormEstate(ccdData.getIht().getIhtFormEstate())
                .build();
            if (ihtEstate207BusinessRule.isApplicable(caseData)) {
                ihtText = "\n*   " + IHT_ESTATE_207_TEXT;
            }
        } else if (!ihtFormValue.contentEquals(IHT400421_VALUE)) {
            ihtText = "\n*   the inheritance tax form ";
        }

        return ihtText;
    }

    private String getPA14FormLabel(CaseData caseData) {
        return markdownDecoratorService.getPA14FormLabel(caseData);
    }

    private String getPA15FormLabel(CaseData caseData) {
        return markdownDecoratorService.getPA15FormLabel(caseData);
    }

    private String getPA16FormLabel(CaseData caseData) {
        return markdownDecoratorService.getPA16FormLabel(caseData);
    }

    private String getPA17FormLabel(CaseData caseData) {
        return markdownDecoratorService.getPA17FormLabel(caseData);
    }

    private String getWillLabel(CaseData caseData) {
        return markdownDecoratorService.getWillLabel(caseData);
    }

    private String getAdmonWillRenunciationFormLabel(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
            .solsWillType(ccdData.getSolsWillType())
            .build();
        return markdownDecoratorService.getAdmonWillRenunciationFormLabel(caseData);
    }

    private String getTcResolutionFormLabel(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
                .titleAndClearingType(ccdData.getTitleAndClearingType())
                .build();
        return markdownDecoratorService.getTcResolutionFormLabel(caseData);
    }

    private String getAuthenticatedTranslationLabel(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
            .englishWill(ccdData.getEnglishWill())
            .build();
        return markdownDecoratorService.getAuthenticatedTranslationLabel(caseData);
    }

    private String getDispenseWithNoticeSupportDocsLabelAndText(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
                .dispenseWithNotice(ccdData.getDispenseWithNotice())
                .dispenseWithNoticeSupportingDocs(ccdData.getDispenseWithNoticeSupportingDocs())
                .build();
        return markdownDecoratorService.getDispenseWithNoticeSupportDocsLabelAndList(caseData);
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

    private String getOptionalAmountAsString(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return getAmountAsString(amount);
    }

    private String getAmountAsString(BigDecimal amount) {
        return amount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).toString();
    }
}
