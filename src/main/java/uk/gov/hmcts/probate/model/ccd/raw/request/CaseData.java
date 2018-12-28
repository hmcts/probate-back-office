package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.probate.controller.validation.AmendCaseDetailsGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationReviewedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.controller.validation.NextStepsConfirmationGroup;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdoptedRelative;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AttorneyApplyingOnBehalfOf;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Declaration;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.LegalStatement;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
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

@JsonDeserialize(builder = CaseData.CaseDataBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CaseData {

    // EVENT = solicitorCreateApplication
    @NotBlank(groups = {ApplicationCreatedGroup.class},
        message = "{solsSolicitorFirmNameIsNull}")
    private String solsSolicitorFirmName;

    @NotBlank(groups = {ApplicationCreatedGroup.class},
        message = "{solsSolicitorFirmPostcodeIsNull}")
    private String solsSolicitorFirmPostcode;

    @NotBlank(groups = {ApplicationCreatedGroup.class}, message = "{solsSolicitorAppReferenceIsNull}")
    private String solsSolicitorAppReference;

    private String solsSolicitorEmail;

    private String solsSolicitorPhoneNumber;

    // EVENT = solicitorUpdateApplication
    @NotBlank(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class},
        message = "{deceasedForenameIsNull}")
    private String deceasedForenames;

    @NotBlank(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class},
        message = "{deceasedSurnameIsNull}")
    private String deceasedSurname;

    @JsonProperty("legacy_case_type")
    private String legacyCaseType;

    @NotNull(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{dodIsNull}")
    private LocalDate deceasedDateOfDeath;

    private LocalDate currentDate = LocalDate.now();

    private String currentDateFormatted = convertDate(currentDate);

    @Getter(lazy = true)
    private final String deceasedDateOfDeathFormatted = convertDate(deceasedDateOfDeath);

    @NotNull(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{dobIsNull}")
    private LocalDate deceasedDateOfBirth;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{willExistsIsNull}")
    private String willExists;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{willAsOriginalIsNull}")
    private String willAccessOriginal;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{willNumberOfCodicilsIsNull}")
    private String willHasCodicils;

    private String willNumberOfCodicils;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedDomicileInEngWalesIsNull}")
    private String deceasedDomicileInEngWales;

    @NotNull(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{deceasedAddressIsNull}")
    private SolsAddress deceasedAddress;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedAnyOtherNamesIsNull}")
    private String deceasedAnyOtherNames;

    private List<CollectionMember<AliasName>> solsDeceasedAliasNamesList;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{ihtFormIdIsNull}")
    private String ihtFormId;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{ihtNetIsNull}")
    @DecimalMin(groups = {ApplicationUpdatedGroup.class}, value = "0.0", message = "{ihtNetNegative}")
    private BigDecimal ihtNetValue;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{ihtGrossIsNull}")
    @DecimalMin(groups = {ApplicationUpdatedGroup.class}, value = "0.0", message = "{ihtGrossNegative}")
    private BigDecimal ihtGrossValue;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantForenamesIsNull}")
    private String primaryApplicantForenames;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantSurnameIsNull}")
    private String primaryApplicantSurname;

    private String primaryApplicantEmailAddress;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantHasAliasIsNull}")
    private String primaryApplicantHasAlias;

    private String primaryApplicantAlias;

    private String primaryApplicantAliasReason;

    private String primaryApplicantOtherReason;

    private String solsExecutorAliasNames;

    private String primaryApplicantSameWillName;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{primaryApplicantIsApplyingIsNull}")
    private String primaryApplicantIsApplying;

    private String solsPrimaryExecutorNotApplyingReason;

    private SolsAddress primaryApplicantAddress;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{otherExecutorExistsIsNull}")
    private String otherExecutorExists;

    private List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;

    private String solsAdditionalInfo;

    private String boEmailDocsReceivedNotificationRequested;

    @SuppressWarnings("squid:S1170")
    @Builder.Default
    private String boEmailDocsReceivedNotification = YES;

    private String boEmailGrantIssuedNotificationRequested;

    @SuppressWarnings("squid:S1170")
    @Builder.Default
    private String boEmailGrantIssuedNotification = YES;

    //EVENT = review
    private DocumentLink solsLegalStatementDocument;

    @Builder.Default
    private List<CollectionMember<Document>> probateDocumentsGenerated = new ArrayList<>();

    @Builder.Default
    private List<CollectionMember<Document>> probateNotificationsGenerated = new ArrayList<>();

    @Builder.Default
    private List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

    private List<CollectionMember<UploadDocument>> boDocumentsUploaded;

    @NotNull(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTNeedToUpdateIsNull}")
    private String solsSOTNeedToUpdate;

    @NotBlank(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTNameIsNull}")
    private String solsSOTName;

    @NotBlank(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTJobTitleIsNull}")
    private String solsSOTJobTitle;

    private Long extraCopiesOfGrant;

    private Long outsideUKGrantCopies;

    @NotNull(groups = {ApplicationReviewedGroup.class}, message = "{solicitorPaymentMethodIsNull}")
    private String solsPaymentMethods;

    private String solsFeeAccountNumber;

    private String casePrinted;

    private List<CollectionMember<StopReason>> boCaseStopReasonList;

    private String boStopDetails;

    private String ihtReferenceNumber;

    private String ihtFormCompletedOnline;


    //next steps
    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{applicationFeeIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{applicationFeeNegative}")
    private BigDecimal applicationFee;

    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{feeForUkCopiesIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{feeForUkCopiesNegative}")
    private BigDecimal feeForUkCopies;

    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{feeForNonUkCopiesIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{feeForNonUkCopiesNegative}")
    private BigDecimal feeForNonUkCopies;

    @NotNull(groups = {NextStepsConfirmationGroup.class}, message = "{totalFeeIsNull}")
    @DecimalMin(groups = {NextStepsConfirmationGroup.class}, value = "0.0", message = "{totalFeeNegative}")
    private BigDecimal totalFee;

    @JsonProperty(value = "executorsApplying")
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying;
    @JsonProperty(value = "executorsNotApplying")
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying;

    private ApplicationType applicationType;

    private String registryLocation;

    private String paymentReferenceNumber;

    private Declaration declaration;

    private LegalStatement legalStatement;

    private String deceasedMarriedAfterWillOrCodicilDate;

    private List<CollectionMember<ProbateAliasName>> deceasedAliasNameList;

    private String primaryApplicantPhoneNumber;

    // EVENT = Amend case details
    private String boDeceasedTitle;

    private String boDeceasedHonours;

    private String boWillMessage;

    private String boExecutorLimitation;

    private String boAdminClauseLimitation;

    private String boLimitationText;

    private List<CollectionMember<Payment>> payments;

    private String boExaminationChecklistQ1;

    private String boExaminationChecklistQ2;

    private String boExaminationChecklistRequestQA;

    private String applicationSubmittedDate;

    private List<CollectionMember<ScannedDocument>> scannedDocuments;
    private String evidenceHandled;

    private String caseType;
    private String paperForm;

    //paper form case creator fields
    private String primaryApplicantSecondPhoneNumber;
    private String primaryApplicantRelationshipToDeceased;
    private String paRelationshipToDeceasedOther;
    private String deceasedMartialStatus;
    private String willDatedBeforeApril;
    private String deceasedEnterMarriageOrCP;
    private String dateOfMarriageOrCP;
    private String dateOfDivorcedCPJudicially;
    private String willsOutsideOfUK;
    private String courtOfDecree;
    private String willGiftUnderEighteen;
    private String applyingAsAnAttorney;
    private List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyOnBehalfOfNameAndAddress;
    private String mentalCapacity;
    private String courtOfProtection;
    private String epaOrLpa;
    private String epaRegistered;
    private String domicilityCountry;
    private List<CollectionMember<EstateItem>> ukEstateItems;
    private String domicilityIHTCert;
    private String entitledToApply;
    private String entitledToApplyOther;
    private String notifiedApplicants;
    private String foreignAsset;
    private String foreignAssetEstateValue;
    private String adopted;
    private List<CollectionMember<AdoptedRelative>> adoptiveRelatives;

    private String spouseOrPartner;
    private String childrenSurvived;
    private String childrenOverEighteenSurvived;
    private String childrenUnderEighteenSurvived;
    private String childrenDied;
    private String childrenDiedOverEighteen;
    private String childrenDiedUnderEighteen;
    private String grandChildrenSurvived;
    private String grandChildrenSurvivedOverEighteen;
    private String grandChildrenSurvivedUnderEighteen;
    private String parentsExistSurvived;
    private String parentsExistOverEighteenSurvived;
    private String parentsExistUnderEighteenSurvived;
    private String wholeBloodSiblingsSurvived;
    private String wholeBloodSiblingsSurvivedOverEighteen;
    private String wholeBloodSiblingsSurvivedUnderEighteen;
    private String wholeBloodSiblingsDied;
    private String wholeBloodSiblingsDiedOverEighteen;
    private String wholeBloodSiblingsDiedUnderEighteen;
    private String wholeBloodNeicesAndNephews;
    private String wholeBloodNeicesAndNephewsOverEighteen;
    private String wholeBloodNeicesAndNephewsUnderEighteen;
    private String halfBloodSiblingsSurvived;
    private String halfBloodSiblingsSurvivedOverEighteen;
    private String halfBloodSiblingsSurvivedUnderEighteen;
    private String halfBloodSiblingsDied;
    private String halfBloodSiblingsDiedOverEighteen;
    private String halfBloodSiblingsDiedUnderEighteen;
    private String halfBloodNeicesAndNephews;
    private String halfBloodNeicesAndNephewsOverEighteen;
    private String halfBloodNeicesAndNephewsUnderEighteen;
    private String grandparentsDied;
    private String grandparentsDiedOverEighteen;
    private String grandparentsDiedUnderEighteen;
    private String wholeBloodUnclesAndAuntsSurvived;
    private String wholeBloodUnclesAndAuntsSurvivedOverEighteen;
    private String wholeBloodUnclesAndAuntsSurvivedUnderEighteen;
    private String wholeBloodUnclesAndAuntsDied;
    private String wholeBloodUnclesAndAuntsDiedOverEighteen;
    private String wholeBloodUnclesAndAuntsDiedUnderEighteen;
    private String wholeBloodCousinsSurvived;
    private String wholeBloodCousinsSurvivedOverEighteen;
    private String wholeBloodCousinsSurvivedUnderEighteen;
    private String halfBloodUnclesAndAuntsSurvived;
    private String halfBloodUnclesAndAuntsSurvivedOverEighteen;
    private String halfBloodUnclesAndAuntsSurvivedUnderEighteen;
    private String halfBloodUnclesAndAuntsDied;
    private String halfBloodUnclesAndAuntsDiedOverEighteen;
    private String halfBloodUnclesAndAuntsDiedUnderEighteen;
    private String halfBloodCousinsSurvived;
    private String halfBloodCousinsSurvivedOverEighteen;
    private String halfBloodCousinsSurvivedUnderEighteen;
    private String applicationFeePaperForm;
    private String feeForCopiesPaperForm;
    private String totalFeePaperForm;
    private String paperPaymentMethod;
    private String paymentReferenceNumberPaperform;


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

    @JsonPOJOBuilder(withPrefix = "")
    public static final class CaseDataBuilder {
    }
}