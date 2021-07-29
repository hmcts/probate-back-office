package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.controller.validation.AmendCaseDetailsGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationAdmonGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationCreatedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationIntestacyGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationProbateGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationReviewedGroup;
import uk.gov.hmcts.probate.controller.validation.ApplicationUpdatedGroup;
import uk.gov.hmcts.probate.controller.validation.NextStepsConfirmationGroup;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.Reissue;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdoptedRelative;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AttorneyApplyingOnBehalfOf;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.BulkScanEnvelope;
import uk.gov.hmcts.probate.model.ccd.raw.Categories;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.Declaration;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.LegalStatement;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
@Data
public class CaseData extends CaseDataParent {

    // Tasklist update
    private final String taskList;

    // Not final as field set in CaseDataTransformer
    // EVENT = solicitorCreateApplication
    @NotBlank(groups = {ApplicationCreatedGroup.class},
        message = "{solsSolicitorFirmNameIsNull}")
    private String solsSolicitorFirmName;

    @Valid
    private final SolsAddress solsSolicitorAddress;

    @NotBlank(groups = {ApplicationCreatedGroup.class}, message = "{solsSolicitorAppReferenceIsNull}")
    private final String solsSolicitorAppReference;

    private final String solsSolicitorEmail;

    private final String solsSolicitorPhoneNumber;

    @NotBlank(groups = {ApplicationCreatedGroup.class}, message = "{solsSolicitorIsExecIsNull}")
    private final String solsSolicitorIsExec;

    private String solsSolicitorIsApplying;

    private String solsSolicitorNotApplyingReason;

    // EVENT = solicitorUpdateApplication
    private final String willDispose;

    private final String englishWill;

    private final String appointExec;

    private final String appointExecByDuties;

    private final String appointExecNo;

    private final String immovableEstate;

    // This is an old schema (prior to 2.0.0) attribute so it should be not blank for
    // an amend of these, but for new trust corp this field is no longer needed & not part of the schema
    // @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{applicationGroundsIsNull}")
    private final String applicationGrounds;

    // Not final as field set in CaseDataTransformer
    @NotBlank(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class},
        message = "{deceasedForenameIsNull}")
    private String deceasedForenames;

    // Not final as field set in CaseDataTransformer
    @NotBlank(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class},
        message = "{deceasedSurnameIsNull}")
    private String deceasedSurname;

    @NotNull(groups = {ApplicationProbateGroup.class, ApplicationIntestacyGroup.class, ApplicationAdmonGroup.class,
        ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{dodIsNull}")
    private final LocalDate deceasedDateOfDeath;

    private final LocalDate currentDate = LocalDate.now();

    private final String currentDateFormatted = convertDate(currentDate);

    @Getter(lazy = true)
    private final String deceasedDateOfDeathFormatted = convertDate(deceasedDateOfDeath);

    @NotNull(groups = {ApplicationProbateGroup.class, ApplicationIntestacyGroup.class, ApplicationAdmonGroup.class,
        ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{dobIsNull}")
    private final LocalDate deceasedDateOfBirth;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedDomicileInEngWalesIsNull}")
    private final String deceasedDomicileInEngWales;

    @NotNull(groups = {ApplicationUpdatedGroup.class, AmendCaseDetailsGroup.class}, message = "{deceasedAddressIsNull}")
    private final SolsAddress deceasedAddress;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{deceasedAnyOtherNamesIsNull}")
    private final String deceasedAnyOtherNames;

    private final List<CollectionMember<AliasName>> solsDeceasedAliasNamesList;

    @NotBlank(groups = {ApplicationUpdatedGroup.class}, message = "{ihtFormIdIsNull}")
    private final String ihtFormId;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{ihtNetIsNull}")
    @DecimalMin(groups = {ApplicationUpdatedGroup.class}, value = "0.0", message = "{ihtNetNegative}")
    private final BigDecimal ihtNetValue;

    @NotNull(groups = {ApplicationUpdatedGroup.class}, message = "{ihtGrossIsNull}")
    @DecimalMin(groups = {ApplicationUpdatedGroup.class}, value = "0.0", message = "{ihtGrossNegative}")
    private final BigDecimal ihtGrossValue;

    @NotBlank(groups = {ApplicationUpdatedGroup.class,
        ApplicationProbateGroup.class,
        ApplicationIntestacyGroup.class,
        ApplicationAdmonGroup.class}, message = "{solsWillTypeIsNull}")
    private final String solsWillType;

    // EVENT = solicitorUpdateProbate and Admon
    @NotBlank(groups = {ApplicationProbateGroup.class, ApplicationAdmonGroup.class}, message = "{willAsOriginalIsNull}")
    private final String willAccessOriginal;

    @NotBlank(groups = {ApplicationProbateGroup.class,
        ApplicationAdmonGroup.class}, message = "{willNumberOfCodicilsIsNull}")
    private final String willHasCodicils;

    private final String willNumberOfCodicils;

    @NotBlank(groups = {ApplicationAdmonGroup.class}, message = "{solsEntitledMinorityIsNull}")
    private final String solsEntitledMinority;

    @NotBlank(groups = {ApplicationAdmonGroup.class}, message = "{solsDiedOrNotApplyingIsNull}")
    private final String solsDiedOrNotApplying;

    @NotBlank(groups = {ApplicationAdmonGroup.class}, message = "{solsResiduaryIsNull}")
    private final String solsResiduary;

    private final String solsResiduaryType;

    @NotBlank(groups = {ApplicationAdmonGroup.class}, message = "{solsLifeInterestIsNull}")
    private final String solsLifeInterest;

    @NotBlank(groups = {ApplicationIntestacyGroup.class, ApplicationAdmonGroup.class},
        message = "{primaryApplicantForenamesIsNull}")
    private String primaryApplicantForenames;

    @NotBlank(groups = {ApplicationIntestacyGroup.class, ApplicationAdmonGroup.class},
        message = "{primaryApplicantSurnameIsNull}")
    private String primaryApplicantSurname;

    private String primaryApplicantHasAlias;

    private final String solsExecutorAliasNames;

    @NotBlank(groups = {ApplicationIntestacyGroup.class}, message = "{primaryApplicantIsApplyingIsNull}")
    private String primaryApplicantIsApplying;

    private String solsPrimaryExecutorNotApplyingReason;

    @NotNull(groups = {ApplicationAdmonGroup.class,
        ApplicationIntestacyGroup.class}, message = "{primaryApplicantAddressIsNull}")
    private SolsAddress primaryApplicantAddress;

    @NotBlank(groups = {ApplicationAdmonGroup.class,
        ApplicationIntestacyGroup.class}, message = "{primaryApplicantEmailAddressIsNull}")
    private String primaryApplicantEmailAddress;

    @NotBlank(groups = {ApplicationProbateGroup.class}, message = "{otherExecutorExistsIsNull}")
    private final String otherExecutorExists;

    private final List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;

    private final String solsAdditionalInfo;

    // EVENT = solicitorUpdateIntestacy
    @NotBlank(groups = {ApplicationProbateGroup.class,
        ApplicationAdmonGroup.class,
        ApplicationIntestacyGroup.class}, message = "{willExistsIsNull}")
    private final String willExists;

    @NotNull(groups = {ApplicationIntestacyGroup.class}, message = "{deceasedMaritalStatusIsNull}")
    private final String deceasedMaritalStatus;

    @NotBlank(groups = {ApplicationIntestacyGroup.class}, message = "{solsApplicantRelationshipToDeceasedIsNull}")
    private final String solsApplicantRelationshipToDeceased;

    private final String solsSpouseOrCivilRenouncing;

    private final String solsAdoptedEnglandOrWales;

    @NotBlank(groups = {ApplicationIntestacyGroup.class}, message = "{solsMinorityInterestIsNull}")
    private final String solsMinorityInterest;

    private final String solsApplicantSiblings;

    private final String boEmailDocsReceivedNotificationRequested;

    private final String boEmailDocsReceivedNotification;

    private final String boEmailGrantIssuedNotificationRequested;

    private final String boEmailGrantIssuedNotification;

    @Builder.Default
    private final String boSendToBulkPrint = YES;

    private final String boSendToBulkPrintRequested;

    //EVENT = review
    private final DocumentLink solsLegalStatementDocument;

    private final DocumentLink statementOfTruthDocument;

    private final DocumentLink solsCoversheetDocument;

    @Builder.Default
    private final List<CollectionMember<Document>> probateDocumentsGenerated = new ArrayList<>();

    @Builder.Default
    private final List<CollectionMember<Document>> probateNotificationsGenerated = new ArrayList<>();

    @Builder.Default
    private final List<CollectionMember<CaseMatch>> caseMatches = new ArrayList<>();

    private final List<CollectionMember<UploadDocument>> boDocumentsUploaded;

    @NotNull(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTNeedToUpdateIsNull}")
    private final String solsSOTNeedToUpdate;

    private final DocumentLink solsLegalStatementUpload;

    private final LocalDate solsIHT400Date;

    private final String solsSOTName;

    @NotBlank(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTForenamesIsNull}")
    private final String solsSOTForenames;

    @NotBlank(groups = {ApplicationReviewedGroup.class}, message = "{solsSOTSurnameIsNull}")
    private final String solsSOTSurname;

    private final String solsSOTJobTitle;

    private final String solsReviewSOTConfirm;

    private final String solsReviewSOTConfirmCheckbox1Names;

    private final String solsReviewSOTConfirmCheckbox2Names;

    @Min(value = 0, groups = {ApplicationReviewedGroup.class, AmendCaseDetailsGroup.class}, message = 
        "{extraCopiesOfGrantIsNegative}")
    private final Long extraCopiesOfGrant;

    @Min(value = 0, groups = {ApplicationReviewedGroup.class, AmendCaseDetailsGroup.class}, message = 
        "{outsideUKGrantCopiesIsNegative}")
    private final Long outsideUKGrantCopies;

    private final String solsPaymentMethods;

    private final String solsFeeAccountNumber;

    private final String casePrinted;

    private final List<CollectionMember<StopReason>> boCaseStopReasonList;

    private final String boStopDetails;

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
    private final ApplicationType applicationType;
    private final String registryLocation;
    private final String paymentReferenceNumber;
    private final Declaration declaration;
    private final LegalStatement legalStatement;
    private final String deceasedMarriedAfterWillOrCodicilDate;
    private final List<CollectionMember<ProbateAliasName>> deceasedAliasNameList;
    private String primaryApplicantPhoneNumber;
    // EVENT = Amend case details
    private final String boDeceasedTitle;
    private final String boDeceasedHonours;
    private final String boWillMessage;
    private final String boExecutorLimitation;
    private final String boAdminClauseLimitation;
    private final String boLimitationText;
    private final List<CollectionMember<Payment>> payments;
    private final String boExaminationChecklistQ1;
    private final String boExaminationChecklistQ2;
    private final String boExaminationChecklistRequestQA;
    private final String applicationSubmittedDate;
    private final List<CollectionMember<ScannedDocument>> scannedDocuments;
    private final String evidenceHandled;
    private final String caseType;
    private final String paperForm;
    private final String languagePreferenceWelsh;
    private String primaryApplicantAlias;
    private final String primaryApplicantAliasReason;
    private final String primaryApplicantOtherReason;
    private final String primaryApplicantSameWillName;
    //paper form case creator fields
    private final String primaryApplicantSecondPhoneNumber;
    private final String primaryApplicantRelationshipToDeceased;
    private final String paRelationshipToDeceasedOther;
    private final String willDatedBeforeApril;
    private final String deceasedEnterMarriageOrCP;
    private final String dateOfMarriageOrCP;
    private final String dateOfDivorcedCPJudicially;
    private final String willsOutsideOfUK;
    private final String courtOfDecree;
    private final String willGiftUnderEighteen;
    private final String applyingAsAnAttorney;
    private final List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyOnBehalfOfNameAndAddress;
    private final String mentalCapacity;
    private final String courtOfProtection;
    private final String epaOrLpa;
    private final String epaRegistered;
    private final String domicilityCountry;
    private final List<CollectionMember<EstateItem>> ukEstate;
    private final String domicilityIHTCert;
    private final String entitledToApply;
    private final String entitledToApplyOther;
    private final String notifiedApplicants;
    private final String foreignAsset;
    private final String foreignAssetEstateValue;
    private final String adopted;
    private final List<CollectionMember<AdoptedRelative>> adoptiveRelatives;
    private final String spouseOrPartner;
    private final String childrenSurvived;
    private final String childrenOverEighteenSurvived;
    private final String childrenUnderEighteenSurvived;
    private final String childrenDied;
    private final String childrenDiedOverEighteen;
    private final String childrenDiedUnderEighteen;
    private final String grandChildrenSurvived;
    private final String grandChildrenSurvivedOverEighteen;
    private final String grandChildrenSurvivedUnderEighteen;
    private final String parentsExistSurvived;
    private final String parentsExistOverEighteenSurvived;
    private final String parentsExistUnderEighteenSurvived;
    private final String wholeBloodSiblingsSurvived;
    private final String wholeBloodSiblingsSurvivedOverEighteen;
    private final String wholeBloodSiblingsSurvivedUnderEighteen;
    private final String wholeBloodSiblingsDied;
    private final String wholeBloodSiblingsDiedOverEighteen;
    private final String wholeBloodSiblingsDiedUnderEighteen;
    private final String wholeBloodNeicesAndNephews;
    private final String wholeBloodNeicesAndNephewsOverEighteen;
    private final String wholeBloodNeicesAndNephewsUnderEighteen;
    private final String halfBloodSiblingsSurvived;
    private final String halfBloodSiblingsSurvivedOverEighteen;
    private final String halfBloodSiblingsSurvivedUnderEighteen;
    private final String halfBloodSiblingsDied;
    private final String halfBloodSiblingsDiedOverEighteen;
    private final String halfBloodSiblingsDiedUnderEighteen;
    private final String halfBloodNeicesAndNephews;
    private final String halfBloodNeicesAndNephewsOverEighteen;
    private final String halfBloodNeicesAndNephewsUnderEighteen;
    private final String grandparentsDied;
    private final String grandparentsDiedOverEighteen;
    private final String grandparentsDiedUnderEighteen;
    private final String wholeBloodUnclesAndAuntsSurvived;
    private final String wholeBloodUnclesAndAuntsSurvivedOverEighteen;
    private final String wholeBloodUnclesAndAuntsSurvivedUnderEighteen;
    private final String wholeBloodUnclesAndAuntsDied;
    private final String wholeBloodUnclesAndAuntsDiedOverEighteen;
    private final String wholeBloodUnclesAndAuntsDiedUnderEighteen;
    private final String wholeBloodCousinsSurvived;
    private final String wholeBloodCousinsSurvivedOverEighteen;
    private final String wholeBloodCousinsSurvivedUnderEighteen;
    private final String halfBloodUnclesAndAuntsSurvived;
    private final String halfBloodUnclesAndAuntsSurvivedOverEighteen;
    private final String halfBloodUnclesAndAuntsSurvivedUnderEighteen;
    private final String halfBloodUnclesAndAuntsDied;
    private final String halfBloodUnclesAndAuntsDiedOverEighteen;
    private final String halfBloodUnclesAndAuntsDiedUnderEighteen;
    private final String halfBloodCousinsSurvived;
    private final String halfBloodCousinsSurvivedOverEighteen;
    private final String halfBloodCousinsSurvivedUnderEighteen;
    private final String applicationFeePaperForm;
    private final String feeForCopiesPaperForm;
    private final String totalFeePaperForm;
    private final String paperPaymentMethod;
    private final String paymentReferenceNumberPaperform;
    private final String bulkPrintSendLetterId;
    private final String bulkPrintPdfSize;
    private final String dateOfDeathType;
    private final String resolveStopState;
    private final String orderNeeded;
    private final List<CollectionMember<Reissue>> reissueReason;
    private final String reissueReasonNotation;
    private final String latestGrantReissueDate;
    private final String boStopDetailsDeclarationParagraph;
    private final List<CollectionMember<ExecutorsApplyingNotification>> executorsApplyingNotifications;
    private final List<CollectionMember<CaseMatch>> legacySearchResultRows;
    private final String recordId;
    private final String legacyId;
    private final String legacyType;
    private final String legacyCaseViewUrl;
    private final String boCaveatStopNotificationRequested;
    private final String boCaveatStopNotification;
    private final String boCaseStopCaveatId;
    private final String boCaveatStopEmailNotificationRequested;
    private final String boCaveatStopEmailNotification;
    private final String boCaveatStopSendToBulkPrintRequested;
    private final String boEmailGrantReIssuedNotificationRequested;
    private final String boEmailGrantReissuedNotification;
    @Builder.Default
    private final String boCaveatStopSendToBulkPrint = YES;
    @Builder.Default
    private final String boGrantReissueSendToBulkPrint = YES;
    private final String boGrantReissueSendToBulkPrintRequested;
    private final String deceasedDivorcedInEnglandOrWales;
    private final String primaryApplicantAdoptionInEnglandOrWales;
    private final String deceasedSpouseNotApplyingReason;
    private final String deceasedOtherChildren;
    private final String allDeceasedChildrenOverEighteen;
    private final String anyDeceasedChildrenDieBeforeDeceased;
    private final String anyDeceasedGrandChildrenUnderEighteen;
    private final String deceasedAnyChildren;
    private final String deceasedHasAssetsOutsideUK;
    private final String boEmailRequestInfoNotificationRequested;
    @Builder.Default
    private final List<CollectionMember<Document>> probateSotDocumentsGenerated = new ArrayList<>();
    private final Categories categories;
    private final DocumentLink previewLink;
    private final String boEmailRequestInfoNotification;
    @Builder.Default
    private final String boRequestInfoSendToBulkPrint = YES;
    private final String boRequestInfoSendToBulkPrintRequested;
    @Builder.Default
    private final String boAssembleLetterSendToBulkPrint = YES;
    private final String boAssembleLetterSendToBulkPrintRequested;
    @JsonProperty(value = "executorsApplying")
    private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying;
    @JsonProperty(value = "executorsNotApplying")
    private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying;
    private List<CollectionMember<AdditionalExecutorApplying>> executorsApplyingLegalStatement;
    private List<CollectionMember<AdditionalExecutorNotApplying>> executorsNotApplyingLegalStatement;
    @Builder.Default
    private List<CollectionMember<BulkPrint>> bulkPrintId = new ArrayList<>();
    @Builder.Default
    private List<CollectionMember<ParagraphDetail>> paragraphDetails = new ArrayList<>();
    private String bulkScanCaseReference;
    private LocalDate grantDelayedNotificationDate;
    private LocalDate grantStoppedDate;
    private LocalDate escalatedDate;
    private String grantDelayedNotificationIdentified;
    private String grantDelayedNotificationSent;
    private LocalDate grantAwaitingDocumentationNotificationDate;
    private String grantAwaitingDocumentatioNotificationSent;
    private String pcqId;
    @Builder.Default
    private final List<CollectionMember<BulkScanEnvelope>> bulkScanEnvelopes = new ArrayList<>();
    private DynamicList reprintDocument;
    private String reprintNumberOfCopies;
    private DynamicList solsAmendLegalStatmentSelect;
    private String declarationCheckbox;
    private String ihtGrossValueField;
    private String ihtNetValueField;
    private Long numberOfExecutors;
    private Long numberOfApplicants;
    private String legalDeclarationJson;
    private String checkAnswersSummaryJson;
    private String registryAddress;
    private String registryEmailAddress;
    private String caseHandedOffToLegacySite;
    private final List<CollectionMember<DeathRecord>> deathRecords;

    // @Getter(lazy = true)
    // private final String reissueDateFormatted = convertDate(reissueDate);

    public boolean isPrimaryApplicantApplying() {
        return YES.equals(primaryApplicantIsApplying);
    }

    public boolean isPrimaryApplicantNotApplying() {
        return NO.equals(primaryApplicantIsApplying);
    }

    public String getDeceasedFullName() {
        return String.join(" ", deceasedForenames, deceasedSurname);
    }

    public String getPrimaryApplicantFullName() {
        return String.join(" ", primaryApplicantForenames, primaryApplicantSurname);
    }

    public String getValueForEmailNotifications(String emailNotifications) {
        return emailNotifications != null ? emailNotifications : getDefaultValueForEmailNotifications();
    }

    public String getDefaultValueForEmailNotifications() {
        return (primaryApplicantEmailAddress == null || primaryApplicantEmailAddress.isEmpty())
            && (solsSolicitorEmail == null || solsSolicitorEmail.isEmpty()) ? NO : YES;
    }

    public String getValueForCaveatStopEmailNotification() {
        return getBoCaveatStopEmailNotification() != null ? getBoCaveatStopEmailNotification() :
            getDefaultValueForCaveatStopEmailNotification();
    }

    public String getDefaultValueForCaveatStopEmailNotification() {
        return primaryApplicantEmailAddress == null || primaryApplicantEmailAddress.isEmpty() ? NO : YES;
    }

    public boolean isDocsReceivedEmailNotificationRequested() {
        return YES.equals(getValueForEmailNotifications(getBoEmailDocsReceivedNotification()));
    }

    public boolean isGrantIssuedEmailNotificationRequested() {
        return YES.equals(getValueForEmailNotifications(getBoEmailGrantIssuedNotification()));
    }

    public boolean isGrantReissuedEmailNotificationRequested() {
        return YES.equals(getValueForEmailNotifications(getBoEmailGrantReissuedNotification()));
    }

    public boolean isBoEmailRequestInfoNotificationRequested() {
        return YES.equals(getValueForEmailNotifications(getBoEmailRequestInfoNotification()));
    }

    public boolean isCaveatStopEmailNotificationRequested() {
        return YES.equals(getValueForCaveatStopEmailNotification());
    }

    public boolean isSendForBulkPrintingRequested() {
        return YES.equals(getBoSendToBulkPrint());
    }

    public boolean isSendForBulkPrintingRequestedGrantReIssued() {
        return YES.equals(getBoGrantReissueSendToBulkPrint());
    }

    public boolean isCaveatStopNotificationRequested() {
        return YES.equals(getBoCaveatStopNotification());
    }

    public boolean isCaveatStopSendToBulkPrintRequested() {
        return YES.equals(getBoCaveatStopSendToBulkPrint());
    }

    public boolean isBoRequestInfoSendToBulkPrintRequested() {
        return YES.equals(getBoRequestInfoSendToBulkPrint());
    }

    public boolean isBoAssembleLetterSendToBulkPrintRequested() {
        return YES.equals(getBoAssembleLetterSendToBulkPrint());
    }

    public LanguagePreference getLanguagePreference() {
        return getLanguagePreferenceWelsh() != null && YES.equals(getLanguagePreferenceWelsh())
            ? LanguagePreference.WELSH : LanguagePreference.ENGLISH;
    }

    public boolean isLanguagePreferenceWelsh() {
        return YES.equals(getLanguagePreferenceWelsh());
    }

}