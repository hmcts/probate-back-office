package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationReviewedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.controller.validation.NextStepsConfirmationGroup;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.AliasNames;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Builder
@Data
public class CaseData {

    // EVENT = solicitorCreateApplication
    @NotBlank(groups = {ApplicationCreatedGroup.class},
            message = "{solsSolicitorFirmNameIsNull}")
    private final String solsSolicitorFirmName;

    @NotBlank(groups = {ApplicationCreatedGroup.class},
            message = "{solsSolicitorFirmPostcodeIsNull}")
    private final String solsSolicitorFirmPostcode;

    @NotBlank(groups = {ApplicationCreatedGroup.class}, message = "{solsSolicitorAppReferenceIsNull}")
    private final String solsSolicitorAppReference;

    private final String solsSolicitorEmail;

    private final String solsSolicitorPhoneNumber;

    // EVENT = solicitorUpdateApplication
    @NotBlank(groups = {ApplicationUpdatedGroup.class},
            message = "{deceasedForenameIsNull}")
    private final String deceasedForenames;

    @NotBlank(groups = {ApplicationUpdatedGroup.class},
            message = "{deceasedSurnameIsNull}")
    private final String deceasedSurname;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{dodIsNull}")
    private final LocalDate deceasedDateOfDeath;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{dobIsNull}")
    private final LocalDate deceasedDateOfBirth;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{willExistsIsNull}")
    private final String willExists;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{willAsOriginalIsNull}")
    private final String willAccessOriginal;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{willNumberOfCodicilsIsNull}")
    private final String willHasCodicils;

    private final String willNumberOfCodicils;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedDomicileInEngWalesIsNull}")
    private final String deceasedDomicileInEngWales;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedAddressIsNull}")
    private final SolsAddress deceasedAddress;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedAnyOtherNamesIsNull}")
    private final String deceasedAnyOtherNames;

    private final List<AliasNames> solsDeceasedAliasNamesList;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{solsIHTFormIdIsNull}")
    private final String solsIHTFormId;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{ihtNetIsNull}")
    @DecimalMin(groups = {ApplicationUpdatedGroup.class}, value = "0.0", message = "{ihtNetNegative}")
    private final Float ihtNetValue;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{ihtGrossIsNull}")
    @DecimalMin(groups = {ApplicationUpdatedGroup.class}, value = "0.0", message = "{ihtGrossNegative}")
    private final Float ihtGrossValue;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantForenamesIsNull}")
    private final String primaryApplicantForenames;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantSurnameIsNull}")
    private final String primaryApplicantSurname;

    private final String primaryApplicantEmailAddress;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantHasAliasIsNull}")
    private final String primaryApplicantHasAlias;

    private final String solsExecutorAliasNames;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantIsApplyingIsNull}")
    private final String primaryApplicantIsApplying;

    private final String solsPrimaryExecutorNotApplyingReason;

    private final SolsAddress primaryApplicantAddress;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{otherExecutorExistsIsNull}")
    private final String otherExecutorExists;

    private final List<AdditionalExecutors> solsAdditionalExecutorList;

    private final String solsAdditionalInfo;

    private final String boEmailDocsReceivedNotification;

    private final String boEmailGrantIssuedNotification;

    //EVENT = review
    private final CCDDocument solsLegalStatementDocument;

    @NotNull(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTNeedToUpdateIsNull}")
    private final String solsSOTNeedToUpdate;

    @NotBlank(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTNameIsNull}")
    private final String solsSOTName;

    @NotBlank(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTJobTitleIsNull}")
    private final String solsSOTJobTitle;

    private final Long extraCopiesOfGrant;

    private final Long outsideUKGrantCopies;

    @NotNull(groups = {ApplicationReviewedGroup.class}, message = "{solicitorPaymentMethodIsNull}")
    private final String solsPaymentMethods;

    private final String solsFeeAccountNumber;

    //next steps
    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{applicationFeeIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{applicationFeeNegative}")
    private final BigDecimal applicationFee;

    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{feeForUkCopiesIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{feeForUkCopiesNegative}")
    private final BigDecimal feeForUkCopies;

    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{feeForNonUkCopiesIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{feeForNonUkCopiesNegative}")
    private final BigDecimal feeForNonUkCopies;

    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{totalFeeIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{totalFeeNegative}")
    private final BigDecimal totalFee;

    private List<AdditionalExecutors> executorsApplying;

    private List<AdditionalExecutors> executorsNotApplying;

    private final ApplicationType applicationType;

    private final String registryLocation;

    public List<AdditionalExecutors> getExecutorsApplying() {

        return getAllExecutors(true);
    }

    public List<AdditionalExecutors> getExecutorsNotApplying() {

        return getAllExecutors(false);
    }

    public boolean isPrimaryApplicantApplying() {
        return YES.equals(primaryApplicantIsApplying);
    }

    private boolean isPrimaryApplicantNotApplying() {
        return NO.equals(primaryApplicantIsApplying);
    }

    private List<AdditionalExecutors> getAllExecutors(boolean applying) {
        List<AdditionalExecutors> totalExecutors = new ArrayList<>();
        if ((applying && isPrimaryApplicantApplying())
                || (!applying && isPrimaryApplicantNotApplying())) {
            AdditionalExecutor primaryExecutor = AdditionalExecutor.builder()
                    .additionalExecForenames(getPrimaryApplicantForenames())
                    .additionalExecLastname(getPrimaryApplicantSurname())
                    .additionalApplying(getPrimaryApplicantIsApplying())
                    .additionalExecAddress(getPrimaryApplicantAddress())
                    .additionalExecNameOnWill(getPrimaryApplicantHasAlias())
                    .additionalExecAliasNameOnWill(getSolsExecutorAliasNames())
                    .additionalExecReasonNotApplying(getSolsPrimaryExecutorNotApplyingReason())
                    .build();

            AdditionalExecutors primaryAdditionalExecutors = AdditionalExecutors.builder()
                    .additionalExecutor(primaryExecutor)
                    .build();
            totalExecutors.add(primaryAdditionalExecutors);
        }

        if (getSolsAdditionalExecutorList() != null) {
            totalExecutors.addAll(getSolsAdditionalExecutorList());
        }

        return totalExecutors.stream().filter(ex -> isApplying(ex, applying)).collect(Collectors.toList());
    }

    private boolean isApplying(AdditionalExecutors ex, boolean applying) {
        if (ex == null || ex.getAdditionalExecutor() == null || ex.getAdditionalExecutor().getAdditionalApplying() == null) {
            return false;
        }

        return ex.getAdditionalExecutor().getAdditionalApplying().equals(applying ? YES : NO);
    }

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getPrimaryApplicantFullName() {
        return String.join(" ", primaryApplicantForenames, primaryApplicantSurname);
    }

    public boolean isDocsReceivedEmailNotificationRequested() {
        return YES.equals(boEmailDocsReceivedNotification);
    }

    public boolean isGrantIssuedEmailNotificationRequested() {
        return YES.equals(boEmailGrantIssuedNotification);
    }
}
