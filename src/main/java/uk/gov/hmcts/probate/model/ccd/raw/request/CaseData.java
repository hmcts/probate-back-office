package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.hmcts.probate.controller.validation.FeeGroup;
import uk.gov.hmcts.probate.controller.validation.SolAddDeceasedDetailsEventGroup;
import uk.gov.hmcts.probate.controller.validation.SolAddEstateDetailsEventGroup;
import uk.gov.hmcts.probate.controller.validation.SolAddFirmDetailsEventGroup;
import uk.gov.hmcts.probate.controller.validation.SolCheckYourAnswers;
import uk.gov.hmcts.probate.controller.validation.SolExecutorDetailsUpdated;
import uk.gov.hmcts.probate.controller.validation.SolReviewLegalStatement;
import uk.gov.hmcts.probate.controller.validation.SolicitorAddWillDetailsGroup;
import uk.gov.hmcts.probate.controller.validation.SolicitorApplicationSubmittedEventGroup;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutors;
import uk.gov.hmcts.probate.model.ccd.raw.AliasNames;
import uk.gov.hmcts.probate.model.ccd.raw.CCDDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class CaseData {

    private static final String YES_VALUE = "Yes";
    private static final String NO_VALUE = "No";

    // EVENT = solicitorCreateApplication
    @NotBlank(groups = {SolAddFirmDetailsEventGroup.class, SolicitorApplicationSubmittedEventGroup.class},
        message = "{solsSolicitorFirmNameIsNull}")
    private final String solsSolicitorFirmName;

    @NotBlank(groups = {SolAddFirmDetailsEventGroup.class, SolicitorApplicationSubmittedEventGroup.class},
        message = "{solsSolicitorFirmPostcodeIsNull}")
    private final String solsSolicitorFirmPostcode;

    // EVENT = solicitorAddDeceasedDetails
    @NotBlank(groups = {SolAddDeceasedDetailsEventGroup.class, SolicitorApplicationSubmittedEventGroup.class},
        message = "{deceasedForenameIsNull}")
    private final String deceasedForenames;

    @NotBlank(groups = {SolAddDeceasedDetailsEventGroup.class, SolicitorApplicationSubmittedEventGroup.class},
        message = "{deceasedSurnameIsNull}")
    private final String deceasedSurname;

    @NotNull(groups = {SolAddDeceasedDetailsEventGroup.class, SolicitorApplicationSubmittedEventGroup.class,
        SolCheckYourAnswers.class}, message = "{dodIsNull}")
    private final LocalDate deceasedDateOfDeath;

    @NotNull(groups = {SolAddDeceasedDetailsEventGroup.class, SolicitorApplicationSubmittedEventGroup.class,
        SolCheckYourAnswers.class}, message = "{dobIsNull}")
    private final LocalDate deceasedDateOfBirth;

    private final String solsSolicitorAppReference;

    @NotBlank(groups = {SolicitorApplicationSubmittedEventGroup.class}, message = "{solsSOTNameIsNull}")
    private final String solsSOTName;

    @NotBlank(groups = {SolicitorApplicationSubmittedEventGroup.class}, message = "{solsSOTJobTitleIsNull}")
    private final String solsSOTJobTitle;

    @NotBlank(groups = {SolicitorApplicationSubmittedEventGroup.class}, message = "{solsIHTFormIdIsNull}")
    private final String solsIHTFormId;

    @NotBlank(groups = {SolicitorAddWillDetailsGroup.class}, message = "{willExistsIsNull}")
    private final String willExists;

    @NotBlank(groups = {SolicitorAddWillDetailsGroup.class}, message = "{willAsOriginalIsNull}")
    private final String willAccessOriginal;

    @NotBlank(groups = {SolicitorAddWillDetailsGroup.class}, message = "{willNumberOfCodicilsIsNull}")
    private final String willHasCodicils;

    private final String willNumberOfCodicils;

    // EVENT = SolAddDeceasedEstateDetails
    @NotNull(groups = {SolAddEstateDetailsEventGroup.class, SolExecutorDetailsUpdated.class, FeeGroup.class},
        message = "{ihtNetIsNull}")
    @DecimalMin(groups = {SolAddEstateDetailsEventGroup.class, SolExecutorDetailsUpdated.class, FeeGroup.class},
        value = "0.0", message = "{ihtNetNegative}")
    private final Float ihtNetValue;

    @NotNull(groups = {SolAddEstateDetailsEventGroup.class, SolExecutorDetailsUpdated.class, FeeGroup.class},
        message = "{ihtGrossIsNull}")
    @DecimalMin(groups = {SolAddEstateDetailsEventGroup.class, SolExecutorDetailsUpdated.class, FeeGroup.class},
        value = "0.0", message = "{ihtGrossNegative}")
    private final Float ihtGrossValue;

    @NotBlank(groups = {SolAddEstateDetailsEventGroup.class}, message = "{deceasedDomicileInEngWalesIsNull}")
    private final String deceasedDomicileInEngWales;

    //EVENT solicitorAddExecutorDetails
    @NotBlank(groups = {SolExecutorDetailsUpdated.class}, message = "{primaryApplicantForenamesIsNull}")
    private final String primaryApplicantForenames;

    @NotBlank(groups = {SolExecutorDetailsUpdated.class}, message = "{primaryApplicantSurnameIsNull}")
    private final String primaryApplicantSurname;

    @NotBlank(groups = {SolExecutorDetailsUpdated.class}, message = "{primaryApplicantHasAliasIsNull}")
    private final String primaryApplicantHasAlias;

    @NotBlank(groups = {SolExecutorDetailsUpdated.class}, message = "{primaryApplicantIsApplyingIsNull}")
    private final String primaryApplicantIsApplying;

    @NotBlank(groups = {SolExecutorDetailsUpdated.class}, message = "{otherExecutorExistsIsNull}")
    private final String otherExecutorExists;

    private final String solsExecutorAliasNames;

    private final String solsPrimaryExecutorNotApplyingReason;

    private final List<AdditionalExecutors> solsAdditionalExecutorList;

    @Valid
    @NotNull(groups = {SolExecutorDetailsUpdated.class, SolAddEstateDetailsEventGroup.class}, message = "{deceasedAddressIsNull}")
    private final SolsAddress deceasedAddress;

    @NotNull(groups = {SolExecutorDetailsUpdated.class}, message = "{deceasedAnyOtherNamesIsNull}")
    private final String deceasedAnyOtherNames;

    @NotNull(groups = {SolExecutorDetailsUpdated.class}, message = "{primaryApplicantAddressIsNull}")
    private final SolsAddress primaryApplicantAddress;

    private final List<AliasNames> solsDeceasedAliasNamesList;

    @NotNull(groups = {SolCheckYourAnswers.class}, message = "{solsCYANeedToUpdateIsNull}")
    private final String solsCYANeedToUpdate;

    private final String solsCYAStateTransition;

    //EVENT = review legal statement
    private final CCDDocument solsLegalStatementDocument;

    @NotNull(groups = {SolReviewLegalStatement.class}, message = "{solsSOTNeedToUpdateIsNull}")
    private final String solsSOTNeedToUpdate;

    private final String solsSOTStateTransition;

    //EVENT = solicitorPaySubmit
    private final Long extraCopiesOfGrant;

    private final Long outsideUKGrantCopies;

    @NotNull(groups = {FeeGroup.class, SolicitorApplicationSubmittedEventGroup.class}, message = "{solicitorPaymentMethodIsNull}")
    private final String solsPaymentMethods;

    @NotNull(groups = {SolicitorApplicationSubmittedEventGroup.class}, message = "{applicationFeeIsNull}")
    @DecimalMin(groups = {SolicitorApplicationSubmittedEventGroup.class}, value = "0.0", message = "{applicationFeeNegative}")
    private final BigDecimal applicationFee;

    @NotNull(groups = {SolicitorApplicationSubmittedEventGroup.class}, message = "{feeForUkCopiesIsNull}")
    @DecimalMin(groups = {SolicitorApplicationSubmittedEventGroup.class}, value = "0.0", message = "{feeForUkCopiesNegative}")
    private final BigDecimal feeForUkCopies;

    @NotNull(groups = {SolicitorApplicationSubmittedEventGroup.class}, message = "{feeForNonUkCopiesIsNull}")
    @DecimalMin(groups = {SolicitorApplicationSubmittedEventGroup.class}, value = "0.0", message = "{feeForNonUkCopiesNegative}")
    private final BigDecimal feeForNonUkCopies;

    @NotNull(groups = {SolicitorApplicationSubmittedEventGroup.class}, message = "{totalFeeIsNull}")
    @DecimalMin(groups = {SolicitorApplicationSubmittedEventGroup.class}, value = "0.0", message = "{totalFeeNegative}")
    private final BigDecimal totalFee;

    private final String solsFeeAccountNumber;

    //EVENT solicitorNextSteps
    private final String solsAdditionalInfo;


    public void setExecutorsApplying(List<AdditionalExecutors> executorsApplying) {

    }

    public void setExecutorsNotApplying(List<AdditionalExecutors> executorsNotApplying) {

    }

    public List<AdditionalExecutors> getExecutorsApplying() {

        return getAllExecutors(true);
    }

    public List<AdditionalExecutors> getExecutorsNotApplying() {

        return getAllExecutors(false);
    }

    public boolean isPrimaryApplicantApplying() {
        return YES_VALUE.equals(primaryApplicantIsApplying);
    }

    private List<AdditionalExecutors> getAllExecutors(boolean applying) {
        List<AdditionalExecutors> totalExecutors = new ArrayList<>();
        AdditionalExecutor primaryExecutor = AdditionalExecutor.builder()
            .additionalExecForenames(getPrimaryApplicantForenames())
            .additionalExecLastname(getPrimaryApplicantSurname())
            .additionalApplying(YES_VALUE)
            .additionalExecAddress(getPrimaryApplicantAddress())
            .additionalExecNameOnWill(getPrimaryApplicantHasAlias())
            .additionalExecAliasNameOnWill(getSolsExecutorAliasNames())
            .additionalExecReasonNotApplying(getSolsPrimaryExecutorNotApplyingReason())
            .build();

        AdditionalExecutors primaryAdditionalExecutors = AdditionalExecutors.builder()
            .additionalExecutor(primaryExecutor)
            .build();

        if (getSolsAdditionalExecutorList() != null) {
            totalExecutors.addAll(getSolsAdditionalExecutorList());
        }

        totalExecutors.add(primaryAdditionalExecutors);

        return totalExecutors.stream().filter(ex -> isApplying(ex, applying)).collect(Collectors.toList());
    }

    private boolean isApplying(AdditionalExecutors ex, boolean applying) {
        return (ex != null
            && ex.getAdditionalExecutor() != null
            && ex.getAdditionalExecutor().getAdditionalApplying() != null
            && ex.getAdditionalExecutor().getAdditionalApplying().equals(applying ? YES_VALUE : NO_VALUE));
    }

}
