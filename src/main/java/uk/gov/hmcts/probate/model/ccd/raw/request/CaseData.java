package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.hmcts.probate.controller.validation.AmendCaseDetailsGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationReviewedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.controller.validation.NextStepsConfirmationGroup;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    @NotBlank(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class},
            message = "{deceasedForenameIsNull}")
    private final String deceasedForenames;

    @NotBlank(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class},
            message = "{deceasedSurnameIsNull}")
    private final String deceasedSurname;

    @NotNull(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{dodIsNull}")
    private final LocalDate deceasedDateOfDeath;

    private final LocalDate currentDate = LocalDate.now();

    private final String currentDateFormatted = convertDate(currentDate);

    @Getter(lazy = true)
    private final String deceasedDateOfDeathFormatted = convertDate(deceasedDateOfDeath);

    @NotNull(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{dobIsNull}")
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

    @NotNull(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{deceasedAddressIsNull}")
    private final SolsAddress deceasedAddress;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedAnyOtherNamesIsNull}")
    private final String deceasedAnyOtherNames;

    private final List<CollectionMember<AliasName>> solsDeceasedAliasNamesList;

    private final List<CollectionMember<ProbateAliasName>> boDeceasedAliasNamesList;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{solsIHTFormIdIsNull}")
    private final String solsIHTFormId;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{ihtNetIsNull}")
    @DecimalMin(groups = {ApplicationUpdatedGroup.class}, value = "0.0", message = "{ihtNetNegative}")
    private final BigDecimal ihtNetValue;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{ihtGrossIsNull}")
    @DecimalMin(groups = {ApplicationUpdatedGroup.class}, value = "0.0", message = "{ihtGrossNegative}")
    private final BigDecimal ihtGrossValue;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantForenamesIsNull}")
    private final String primaryApplicantForenames;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantSurnameIsNull}")
    private final String primaryApplicantSurname;

    private final String primaryApplicantEmailAddress;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantHasAliasIsNull}")
    private final String primaryApplicantHasAlias;

    private final String solsExecutorAliasNames;

    private final String solsExecutorAliasFirstNames;

    private final String solsExecutorAliasSurnames;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantIsApplyingIsNull}")
    private final String primaryApplicantIsApplying;

    private final String solsPrimaryExecutorNotApplyingReason;

    private final SolsAddress primaryApplicantAddress;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{otherExecutorExistsIsNull}")
    private final String otherExecutorExists;

    private final List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;

    private final String solsAdditionalInfo;

    private final String boEmailDocsReceivedNotificationRequested;

    @Getter(lazy = true)
    private final String boEmailDocsReceivedNotification = YES;

    private final String boEmailGrantIssuedNotificationRequested;

    @Getter(lazy = true)
    private final String boEmailGrantIssuedNotification = YES;

    //EVENT = review
    private final DocumentLink solsLegalStatementDocument;

    private final List<CollectionMember<Document>> probateDocumentsGenerated = new ArrayList<>();

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

    private final String casePrinted;

    private final List<CollectionMember<StopReason>> boCaseStopReasonList;

    private final String ihtReferenceNumber;

    private final String ihtFormCompletedOnline;


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

    @JsonProperty(value = "executorsApplying")
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying;
    @JsonProperty(value = "executorsNotApplying")
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying;

    private final ApplicationType applicationType;

    private final String registryLocation;

    // EVENT = Amend case details
    private final String boDeceasedTitle;

    private final String boDeceasedHonours;

    private final String boWillMessage;

    private final String boExecutorLimitation;

    private final String boAdminClauseLimitation;

    private final String boLimitationText;

    @Getter(lazy = true)
    private final List<CollectionMember<AdditionalExecutor>> executorsApplyingForLegalStatement = getAllExecutors(true);

    @Getter(lazy = true)
    private final List<CollectionMember<AdditionalExecutor>> executorsNotApplyingForLegalStatement = getAllExecutors(false);

    public boolean isPrimaryApplicantApplying() {
        return YES.equals(primaryApplicantIsApplying);
    }

    private boolean isPrimaryApplicantNotApplying() {
        return NO.equals(primaryApplicantIsApplying);
    }

    private List<CollectionMember<AdditionalExecutor>> getAllExecutors(boolean applying) {
        List<CollectionMember<AdditionalExecutor>> totalExecutors = new ArrayList<>();
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

            CollectionMember<AdditionalExecutor> primaryAdditionalExecutors = new CollectionMember<>(null, primaryExecutor);
            totalExecutors.add(primaryAdditionalExecutors);
        }

        if (getSolsAdditionalExecutorList() != null) {
            totalExecutors.addAll(getSolsAdditionalExecutorList());
        }

        return totalExecutors.stream().filter(ex -> isApplying(ex, applying)).collect(Collectors.toList());
    }

    private boolean isApplying(CollectionMember<AdditionalExecutor> ex, boolean applying) {
        if (ex == null || ex.getValue() == null || ex.getValue().getAdditionalApplying() == null) {
            return false;
        }

        return ex.getValue().getAdditionalApplying().equals(applying ? YES : NO);
    }

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getPrimaryApplicantFullName() {
        return String.join(" ", primaryApplicantForenames, primaryApplicantSurname);
    }

    public boolean isDocsReceivedEmailNotificationRequested() {
        return YES.equals(getBoEmailDocsReceivedNotification());
    }

    public boolean isGrantIssuedEmailNotificationRequested() {
        return YES.equals(getBoEmailGrantIssuedNotification());
    }

    private String convertDate(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMMM yyyy");
        try {
            Date date = originalFormat.parse(dateToConvert.toString());
            String formattedDate = targetFormat.format(date);
            int day = Integer.parseInt(formattedDate.substring(0, 2));
            switch (day) {
                case 1:
                case 21:
                case 31:
                    return day + "st " + formattedDate.substring(3);

                case 2:
                case 22:
                    return day + "nd " + formattedDate.substring(3);

                case 3:
                case 23:
                    return day + "rd " + formattedDate.substring(3);

                default:
                    return day + "th " + formattedDate.substring(3);
            }
        } catch (ParseException ex) {
            ex.getMessage();
            return null;
        }
    }
}