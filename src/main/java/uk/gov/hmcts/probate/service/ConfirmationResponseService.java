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
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_207_TEXT_WELSH;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.template.MarkdownTemplate.STOP_BODY;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;

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
    private String paymentInfo = null;
    private String paymentSummary = null;

    private String paymentInfoWelsh = null;
    private String paymentSummaryWelsh = null;

    public AfterSubmitCallbackResponse getNextStepsConfirmation(CaveatData caveatData, Long id) {
        return getStopConfirmationUsingMarkdown(generateNextStepsBodyMarkdown(caveatData, id));
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
            String[] reasonsText = reasonText.split(":");
            Map<String, String> keyValue = new HashMap<>();
            keyValue.put("{{reason}}", reasonsText[0]);
            keyValue.put("{{reasonWelsh}}", reasonsText[1]);
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

    private TemplateResponse generateNextStepsBodyMarkdown(CaveatData caveatData, Long id) {
        Map<String, String> keyValue = new HashMap<>();
        keyValue.put("{{solicitorReference}}", caveatData.getSolsSolicitorAppReference());
        String caseSubmissionDate = "";
        if (caveatData.getApplicationSubmittedDate() != null) {
            caseSubmissionDate = caveatData.getApplicationSubmittedDate().format(formatter);
        }
        keyValue.put("{{caseSubmissionDate}}", caseSubmissionDate);
        keyValue.put("{{caseReference}}", id.toString());

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
        keyValue.put("{{paymentReferenceNumber}}", ccdData.getFee().getSolsPBAPaymentReference());
        keyValue.put("{{paymentAmount}}", getAmountAsString(ccdData.getFee().getAmount()));
        keyValue.put("{{applicationFee}}", getAmountAsString(ccdData.getFee().getApplicationFee()));
        keyValue.put("{{feeForUkCopies}}", getOptionalAmountAsString(ccdData.getFee().getFeeForUkCopies()));
        keyValue.put("{{feeForNonUkCopies}}", getOptionalAmountAsString(ccdData.getFee().getFeeForNonUkCopies()));
        keyValue.put("{{caseRef}}", ccdData.getCaseId().toString());
        keyValue.put("{{originalWill}}", getWillLabel(caseData));
        keyValue.put("{{originalWillWelsh}}", getWillLabelWelsh(caseData));

        String additionalInfo = ccdData.getSolsAdditionalInfo();
        String additionalInfoWelsh = ccdData.getSolsAdditionalInfo();
        if (Strings.isNullOrEmpty(additionalInfo)) {
            additionalInfo = "None provided";
            additionalInfoWelsh = "Ni ddarparwyd un";
        }

        if (ccdData.getFee().getAmount().compareTo(BigDecimal.ZERO) > 0) {
            paymentSummary = "**Application fee** &pound;" + getAmountAsString(ccdData.getFee().getApplicationFee())
                    + "\n\n" + "**Fee for additional UK copies** &pound;" + getOptionalAmountAsString(ccdData.getFee()
                    .getFeeForUkCopies()) + "\n\n"
                    + "**Fee for certified copies** &pound;" + getOptionalAmountAsString(ccdData.getFee()
                    .getFeeForNonUkCopies()) + "\n\n"
                    + "**Fee amount** &pound;" + getAmountAsString(ccdData.getFee().getAmount()) + "\n\n"
                    + "**Customer application reference** " + ccdData.getFee().getSolsPBAPaymentReference();

            paymentInfo = "**You must complete payment next**\n"
                    + "\n" + "Go to the Service Request tab on your case details\n"
                    + "\n" + "Complete the payment process\n";

            paymentSummaryWelsh = "**Ffi gwneud cais** &pound;"
                    + getAmountAsString(ccdData.getFee().getApplicationFee())
                    + "\n\n" + "**Ffi am gopïau ychwanegol i'w defnyddio yn y DU** &pound;"
                    + getOptionalAmountAsString(ccdData.getFee().getFeeForUkCopies()) + "\n\n"
                    + "**Ffi am gopïau ardystiedig** &pound;" + getOptionalAmountAsString(ccdData.getFee()
                    .getFeeForNonUkCopies()) + "\n\n"
                    + "**Swm y ffi** &pound;" + getAmountAsString(ccdData.getFee().getAmount()) + "\n\n"
                    + "**Cyfeirnod cais y cwsmer** " + ccdData.getFee().getSolsPBAPaymentReference();

            paymentInfoWelsh = "**Mae'n rhaid i chi gwblhau'r broses dalu nesaf**\n\n"
                     + "Ewch i'r tab Cais am Daliad ym manylion eich achos\n\n"
                     + "Cwblhewch y broses dalu\n";
        } else {
            paymentSummary = "Not applicable";
            paymentInfo = "";
            paymentSummaryWelsh = "Amherthnasol";
            paymentInfoWelsh = "";
        }

        String legalPhotocopy = "";
        String legalPhotocopyWelsh = "";
        if (hasNoLegalStatmentBeenUploaded(ccdData)) {
            legalPhotocopy = format("*   %s", PageTextConstants.DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY);
            legalPhotocopyWelsh = format("*   %s", PageTextConstants.DOCUMENT_LEGAL_STATEMENT_PHOTOCOPY_WELSH);
        }
        keyValue.put("{{legalPhotocopy}}", legalPhotocopy);
        keyValue.put("{{legalPhotocopyWelsh}}", legalPhotocopyWelsh);
        keyValue.put("{{ihtText}}", getIhtText(ccdData));
        keyValue.put("{{ihtTextWelsh}}", getIhtTextWelsh(ccdData));
        keyValue.put("{{ihtForm}}", getIhtForm(ccdData));
        keyValue.put("{{ihtFormWelsh}}", getIhtFormWelsh(ccdData));
        keyValue.put("{{additionalInfo}}", additionalInfo);
        keyValue.put("{{additionalInfoWelsh}}", additionalInfoWelsh);
        keyValue.put("{{paymentSummary}}", paymentSummary);
        keyValue.put("{{paymentInfo}}", paymentInfo);
        keyValue.put("{{paymentSummaryWelsh}}", paymentSummaryWelsh);
        keyValue.put("{{paymentInfoWelsh}}", paymentInfoWelsh);
        keyValue.put("{{pa14form}}", getPA14FormLabel(caseData));
        keyValue.put("{{pa14formWelsh}}", getPA14FormLabelWelsh(caseData));
        keyValue.put("{{pa15form}}", getPA15FormLabel(caseData));
        keyValue.put("{{pa15formWelsh}}", getPA15FormLabelWelsh(caseData));
        keyValue.put("{{pa16form}}", getPA16FormLabel(caseData));
        keyValue.put("{{pa16formWelsh}}", getPA16FormLabelWelsh(caseData));
        keyValue.put("{{pa17form}}", getPA17FormLabel(caseData));
        keyValue.put("{{pa17formWelsh}}", getPA17FormLabelWelsh(caseData));
        keyValue.put("{{admonWillRenunciation}}", getAdmonWillRenunciationFormLabel(ccdData));
        keyValue.put("{{admonWillRenunciationWelsh}}", getAdmonWillRenunciationFormLabelWelsh(ccdData));
        keyValue.put("{{tcResolutionLodgedWithApp}}", getTcResolutionFormLabel(ccdData));
        keyValue.put("{{tcResolutionLodgedWithAppWelsh}}", getTcResolutionFormLabelWelsh(ccdData));
        keyValue.put("{{authenticatedTranslation}}", getAuthenticatedTranslationLabel(ccdData));
        keyValue.put("{{authenticatedTranslationWelsh}}", getAuthenticatedTranslationLabelWelsh(ccdData));
        keyValue.put("{{dispenseWithNoticeSupportingDocs}}",
                getDispenseWithNoticeSupportDocsLabelAndText(ccdData));
        keyValue.put("{{dispenseWithNoticeSupportingDocsWelsh}}",
                getDispenseWithNoticeSupportDocsLabelAndTextWelsh(ccdData));
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
        if (ihtFormValue != null && !ihtFormValue.contentEquals(IHT400421_VALUE)
                && !ihtFormValue.contentEquals(IHT400_VALUE) && !ihtFormValue.contentEquals(NOT_APPLICABLE_VALUE)) {
            if (YES.equals(ccdData.getIht217())) {
                ihtForm = "IHT205 and IHT217";
            } else {
                ihtForm = ccdData.getIht().getFormName();
            }
        }

        return ihtForm;
    }

    private String getIhtFormWelsh(CCDData ccdData) {
        String ihtFormValue = ccdData.getIht().getFormName();
        String ihtFormWelsh = "";
        if (ihtFormValue != null && !ihtFormValue.contentEquals(IHT400421_VALUE)
                && !ihtFormValue.contentEquals(IHT400_VALUE) && !ihtFormValue.contentEquals(NOT_APPLICABLE_VALUE)) {
            if (YES.equals(ccdData.getIht217())) {
                ihtFormWelsh = "IHT205 ac IHT217";
            } else {
                ihtFormWelsh = ccdData.getIht().getFormName();
            }
        }

        return ihtFormWelsh;
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
        } else if (!ihtFormValue.contentEquals(IHT400421_VALUE) && !ihtFormValue.contentEquals(IHT400_VALUE)
                && !ihtFormValue.contentEquals(NOT_APPLICABLE_VALUE)) {
            ihtText = "\n*   the inheritance tax form ";
        }

        return ihtText;
    }

    private String getIhtTextWelsh(CCDData ccdData) {
        String ihtFormValue = ccdData.getIht().getFormName();
        String ihtTextWelsh = "";
        if (ihtFormValue == null) {
            CaseData caseData = CaseData.builder()
                    .ihtFormEstateValuesCompleted(ccdData.getIht().getIhtFormEstateValuesCompleted())
                    .ihtFormEstate(ccdData.getIht().getIhtFormEstate())
                    .build();
            if (ihtEstate207BusinessRule.isApplicable(caseData)) {
                ihtTextWelsh = "\n*   " + IHT_ESTATE_207_TEXT_WELSH;
            }
        } else if (!ihtFormValue.contentEquals(IHT400421_VALUE) && !ihtFormValue.contentEquals(IHT400_VALUE)
                && !ihtFormValue.contentEquals(NOT_APPLICABLE_VALUE)) {
            ihtTextWelsh = "\n*   ffurflen Treth Etifeddiant ";
        }

        return ihtTextWelsh;
    }

    private String getPA14FormLabel(CaseData caseData) {
        return markdownDecoratorService.getPA14FormLabel(caseData,false);
    }

    private String getPA14FormLabelWelsh(CaseData caseData) {
        return markdownDecoratorService.getPA14FormLabel(caseData, true);
    }

    private String getPA15FormLabel(CaseData caseData) {
        return markdownDecoratorService.getPA15FormLabel(caseData, false);
    }

    private String getPA15FormLabelWelsh(CaseData caseData) {
        return markdownDecoratorService.getPA15FormLabel(caseData, true);
    }

    private String getPA16FormLabel(CaseData caseData) {
        return markdownDecoratorService.getPA16FormLabel(caseData, false);
    }

    private String getPA16FormLabelWelsh(CaseData caseData) {
        return markdownDecoratorService.getPA16FormLabel(caseData, true);
    }

    private String getPA17FormLabel(CaseData caseData) {
        return markdownDecoratorService.getPA17FormLabel(caseData, false);
    }

    private String getPA17FormLabelWelsh(CaseData caseData) {
        return markdownDecoratorService.getPA17FormLabel(caseData, true);
    }

    private String getWillLabel(CaseData caseData) {
        return markdownDecoratorService.getWillLabel(caseData);
    }

    private String getWillLabelWelsh(CaseData caseData) {
        return markdownDecoratorService.getWillLabelWelsh(caseData);
    }

    private String getAdmonWillRenunciationFormLabel(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
            .solsWillType(ccdData.getSolsWillType())
            .build();
        return markdownDecoratorService.getAdmonWillRenunciationFormLabel(caseData, false);
    }

    private String getAdmonWillRenunciationFormLabelWelsh(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
                .solsWillType(ccdData.getSolsWillType())
                .build();
        return markdownDecoratorService.getAdmonWillRenunciationFormLabel(caseData, true);
    }

    private String getTcResolutionFormLabel(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
                .titleAndClearingType(ccdData.getTitleAndClearingType())
                .build();
        return markdownDecoratorService.getTcResolutionFormLabel(caseData, false);
    }

    private String getTcResolutionFormLabelWelsh(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
                .titleAndClearingType(ccdData.getTitleAndClearingType())
                .build();
        return markdownDecoratorService.getTcResolutionFormLabel(caseData, true);
    }

    private String getAuthenticatedTranslationLabel(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
            .englishWill(ccdData.getEnglishWill())
            .build();
        return markdownDecoratorService.getAuthenticatedTranslationLabel(caseData, false);
    }

    private String getAuthenticatedTranslationLabelWelsh(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
                .englishWill(ccdData.getEnglishWill())
                .build();
        return markdownDecoratorService.getAuthenticatedTranslationLabel(caseData, true);
    }

    private String getDispenseWithNoticeSupportDocsLabelAndText(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
                .dispenseWithNotice(ccdData.getDispenseWithNotice())
                .dispenseWithNoticeSupportingDocs(ccdData.getDispenseWithNoticeSupportingDocs())
                .build();
        return markdownDecoratorService.getDispenseWithNoticeSupportDocsLabelAndList(caseData, false);
    }

    private String getDispenseWithNoticeSupportDocsLabelAndTextWelsh(CCDData ccdData) {
        CaseData caseData = CaseData.builder()
                .dispenseWithNotice(ccdData.getDispenseWithNotice())
                .dispenseWithNoticeSupportingDocs(ccdData.getDispenseWithNoticeSupportingDocs())
                .build();
        return markdownDecoratorService.getDispenseWithNoticeSupportDocsLabelAndList(caseData, true);
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
