package uk.gov.hmcts.probate.model.ccd.raw.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ExecutorsApplyingNotification;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.Reissue;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdoptedRelative;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AttorneyApplyingOnBehalfOf;
import uk.gov.hmcts.probate.model.ccd.raw.BulkPrint;
import uk.gov.hmcts.probate.model.ccd.raw.Categories;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.Declaration;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItem;
import uk.gov.hmcts.probate.model.ccd.raw.LegalStatement;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
@Data
public class ResponseCaseData extends ResponseCaseDataParent {

    private final String state;
    private final ApplicationType applicationType;
    private final String registryLocation;

    private final String deceasedDateOfDeath;
    private final String deceasedDateOfBirth;
    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String solsSolicitorFirmName;
    private final SolsAddress solsSolicitorAddress;
    private final String solsSolicitorEmail;
    private final String solsSolicitorPhoneNumber;
    private final String solsSOTName;
    private final String solsSOTForenames;
    private final String solsSOTSurname;
    private final String solsSOTJobTitle;
    private final String solsSolicitorAppReference;
    private final String ihtFormId;
    private final String solsSolicitorIsExec;
    private final String solsSolicitorIsMainApplicant;
    private final String solsSolicitorIsApplying;
    private final String solsSolicitorNotApplyingReason;
    private final String solsWillType;
    private final String solsApplicantRelationshipToDeceased;
    private final String solsSpouseOrCivilRenouncing;
    private final String solsAdoptedEnglandOrWales;
    private final String solsMinorityInterest;
    private final String solsApplicantSiblings;
    private final String solsEntitledMinority;
    private final String solsDiedOrNotApplying;
    private final String solsResiduary;
    private final String solsResiduaryType;
    private final String solsLifeInterest;
    private final LocalDate solsIHT400Date;
    private final String willExists;
    private final String willAccessOriginal;
    private final String willHasCodicils;
    private final String willNumberOfCodicils;
    private final BigDecimal ihtNetValue;
    private final BigDecimal ihtGrossValue;
    private final String deceasedDomicileInEngWales;
    private final String extraCopiesOfGrant;
    private final String outsideUKGrantCopies;
    private final String applicationFee;
    private final String feeForUkCopies;
    private final String feeForNonUkCopies;
    private final String totalFee;
    private final String solsPaymentMethods;
    private final String solsFeeAccountNumber;
    private final String paymentReferenceNumber;
    private final DocumentLink solsLegalStatementDocument;
    private final DocumentLink statementOfTruthDocument;
    private final DocumentLink solsCoversheetDocument;
    private final List<CollectionMember<Document>> probateDocumentsGenerated;
    private final List<CollectionMember<Document>> probateNotificationsGenerated;
    private final List<CollectionMember<UploadDocument>> boDocumentsUploaded;
    private final List<CollectionMember<CaseMatch>> caseMatches;
    private final String solsSOTNeedToUpdate;
    private final DocumentLink solsNextStepsDocument;
    private final String solsAdditionalInfo;
    private final String primaryApplicantForenames;
    private final String primaryApplicantSurname;
    private final String primaryApplicantEmailAddress;
    private final String primaryApplicantHasAlias;
    private final String primaryApplicantIsApplying;
    private final String solsPrimaryExecutorNotApplyingReason;
    private final String otherExecutorExists;
    private final String primaryApplicantAlias;
    private final String primaryApplicantSameWillName;
    private final String primaryApplicantAliasReason;
    private final String primaryApplicantOtherReason;
    private final String solsExecutorAliasNames;
    @JsonProperty(value = "executorsApplying")
    private final List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying;
    @JsonProperty(value = "executorsNotApplying")
    private final List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying;
    private final List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;
    private final SolsAddress deceasedAddress;
    private final String deceasedAnyOtherNames;
    private final SolsAddress primaryApplicantAddress;
    private final List<CollectionMember<AliasName>> solsDeceasedAliasNamesList;
    private final List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList;
    private final String casePrinted;
    private final String boEmailDocsReceivedNotificationRequested;
    private final String boEmailGrantIssuedNotificationRequested;
    private final String boEmailDocsReceivedNotification;
    private final String boEmailGrantIssuedNotification;
    private final List<CollectionMember<StopReason>> boCaseStopReasonList;
    private final String boStopDetails;
    private final String boDeceasedTitle;
    private final String boDeceasedHonours;

    private final String boWillMessage;
    private final String boExecutorLimitation;
    private final String boAdminClauseLimitation;
    private final String boLimitationText;
    private final String ihtReferenceNumber;
    private final String ihtFormCompletedOnline;

    private final LegalStatement legalStatement;
    private final Declaration declaration;
    private final String deceasedMarriedAfterWillOrCodicilDate;
    private final String primaryApplicantPhoneNumber;

    private final String boExaminationChecklistQ2;
    private final String boExaminationChecklistQ1;
    private final String boExaminationChecklistRequestQA;

    private final List<CollectionMember<Payment>> payments;

    private final String applicationSubmittedDate;
    private final List<CollectionMember<ScannedDocument>> scannedDocuments;
    private final String evidenceHandled;

    private final String caseType;
    private final String paperForm;
    private final String languagePreferenceWelsh;

    //paper form case creator fields
    private final String primaryApplicantSecondPhoneNumber;
    private final String primaryApplicantRelationshipToDeceased;
    private final String paRelationshipToDeceasedOther;
    private final String applicationFeePaperForm;
    private final String feeForCopiesPaperForm;
    private final String totalFeePaperForm;
    private final String paperPaymentMethod;
    private final String paymentReferenceNumberPaperform;
    private final String entitledToApply;
    private final String entitledToApplyOther;
    private final String notifiedApplicants;
    private final String foreignAsset;
    private final String foreignAssetEstateValue;
    private final String adopted;
    private final List<CollectionMember<AdoptedRelative>> adoptiveRelatives;
    private final String mentalCapacity;
    private final String courtOfProtection;
    private final String epaOrLpa;
    private final String epaRegistered;
    private final String domicilityCountry;
    private final List<CollectionMember<EstateItem>> ukEstate;
    private final String domicilityIHTCert;
    private final String applicationGrounds;
    private final String willDispose;
    private final String englishWill;
    private final String appointExec;

    private final String appointExecByDuties;
    private final String appointExecNo;
    private final String immovableEstate;
    private final String willDatedBeforeApril;
    private final String deceasedEnterMarriageOrCP;
    private final String deceasedMaritalStatus;
    private final String willsOutsideOfUK;
    private final String courtOfDecree;
    private final String dateOfMarriageOrCP;
    private final String dateOfDivorcedCPJudicially;
    private final String willGiftUnderEighteen;
    private final String applyingAsAnAttorney;
    private final List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyOnBehalfOfNameAndAddress;

    private final String spouseOrPartner;
    private final String childrenSurvived;
    private final String childrenOverEighteenSurvived;
    private final String childrenUnderEighteenSurvived;
    private final String childrenDied;
    private final String childrenDiedOverEighteen;
    private final String childrenDiedUnderEighteen;
    private final String parentsExistSurvived;
    private final String parentsExistOverEighteenSurvived;
    private final String parentsExistUnderEighteenSurvived;
    private final String wholeBloodNeicesAndNephews;
    private final String wholeBloodNeicesAndNephewsOverEighteen;
    private final String wholeBloodNeicesAndNephewsUnderEighteen;
    private final String wholeBloodSiblingsDied;
    private final String wholeBloodSiblingsDiedOverEighteen;
    private final String wholeBloodSiblingsDiedUnderEighteen;
    private final String wholeBloodSiblingsSurvived;
    private final String wholeBloodSiblingsSurvivedOverEighteen;
    private final String wholeBloodSiblingsSurvivedUnderEighteen;
    private final String halfBloodSiblingsDied;
    private final String halfBloodSiblingsDiedOverEighteen;
    private final String halfBloodSiblingsDiedUnderEighteen;
    private final String halfBloodSiblingsSurvived;
    private final String halfBloodSiblingsSurvivedOverEighteen;
    private final String halfBloodSiblingsSurvivedUnderEighteen;
    private final String grandparentsDied;
    private final String grandparentsDiedOverEighteen;
    private final String grandparentsDiedUnderEighteen;
    private final String halfBloodNeicesAndNephews;
    private final String halfBloodNeicesAndNephewsOverEighteen;
    private final String halfBloodNeicesAndNephewsUnderEighteen;
    private final String grandChildrenSurvived;
    private final String grandChildrenSurvivedOverEighteen;
    private final String grandChildrenSurvivedUnderEighteen;
    private final String wholeBloodUnclesAndAuntsDied;
    private final String wholeBloodUnclesAndAuntsDiedOverEighteen;
    private final String wholeBloodUnclesAndAuntsDiedUnderEighteen;
    private final String wholeBloodUnclesAndAuntsSurvived;
    private final String wholeBloodUnclesAndAuntsSurvivedOverEighteen;
    private final String wholeBloodUnclesAndAuntsSurvivedUnderEighteen;
    private final String halfBloodUnclesAndAuntsSurvived;
    private final String halfBloodUnclesAndAuntsSurvivedOverEighteen;
    private final String halfBloodUnclesAndAuntsSurvivedUnderEighteen;
    private final String halfBloodUnclesAndAuntsDied;
    private final String halfBloodUnclesAndAuntsDiedOverEighteen;
    private final String halfBloodUnclesAndAuntsDiedUnderEighteen;
    private final String halfBloodCousinsSurvived;
    private final String halfBloodCousinsSurvivedOverEighteen;
    private final String halfBloodCousinsSurvivedUnderEighteen;
    private final String wholeBloodCousinsSurvived;
    private final String wholeBloodCousinsSurvivedOverEighteen;
    private final String wholeBloodCousinsSurvivedUnderEighteen;

    private final String boSendToBulkPrint;
    private final String boSendToBulkPrintRequested;
    private final String grantIssuedDate;
    private final String dateOfDeathType;

    private final List<CollectionMember<CaseMatch>> legacySearchResultRows;

    private final String recordId;
    private final String legacyType;
    private final String legacyCaseViewUrl;

    private final String bulkPrintSendLetterId;
    private final String bulkPrintPdfSize;
    private final List<CollectionMember<BulkPrint>> bulkPrintId;

    private final String boCaveatStopNotificationRequested;
    private final String boCaveatStopNotification;
    private final String boCaseStopCaveatId;
    private final String boCaveatStopEmailNotificationRequested;
    private final String boCaveatStopEmailNotification;
    private final String boCaveatStopSendToBulkPrintRequested;
    private final String boCaveatStopSendToBulkPrint;
    private final String boEmailGrantReissuedNotificationRequested;
    private final String boEmailGrantReissuedNotification;
    private final String boGrantReissueSendToBulkPrint;
    private final String boGrantReissueSendToBulkPrintRequested;
    private final String orderNeeded;
    private final List<CollectionMember<Reissue>> reissueReason;
    private final String reissueDate;
    private final String reissueReasonNotation;
    private final String latestGrantReissueDate;

    private final String deceasedDivorcedInEnglandOrWales;
    private final String primaryApplicantAdoptionInEnglandOrWales;
    private final String deceasedSpouseNotApplyingReason;
    private final String deceasedOtherChildren;
    private final String allDeceasedChildrenOverEighteen;
    private final String anyDeceasedChildrenDieBeforeDeceased;
    private final String anyDeceasedGrandChildrenUnderEighteen;
    private final String deceasedAnyChildren;
    private final String deceasedHasAssetsOutsideUK;
    private final String solicitorIsMainApplicant;

    private final String boStopDetailsDeclarationParagraph;
    private final String boEmailRequestInfoNotificationRequested;
    private final String boEmailRequestInfoNotification;
    private final String boRequestInfoSendToBulkPrint;
    private final String boRequestInfoSendToBulkPrintRequested;
    private final String boAssembleLetterSendToBulkPrint;
    private final String boAssembleLetterSendToBulkPrintRequested;
    private final List<CollectionMember<ExecutorsApplyingNotification>> executorsApplyingNotifications;
    private final List<CollectionMember<Document>> probateSotDocumentsGenerated;

    private final Categories categories;
    private final DocumentLink previewLink;
    private final String grantDelayedNotificationDate;
    private final String grantStoppedDate;
    private final String grantDelayedNotificationSent;
    private final String grantAwaitingDocumentationNotificationDate;
    private final String grantAwaitingDocumentatioNotificationSent;
    private final String pcqId;
    @Builder.Default
    private List<CollectionMember<ParagraphDetail>> paragraphDetails = new ArrayList<>();
    private String bulkScanCaseReference;
    private String grantDelayedNotificationIdentified;
    private String caseHandedOffToLegacySite;
    private List<CollectionMember<DeathRecord>> deathRecords;
    private final Integer deathRecordSystemNumber;
    private DeathRecord deathRecord;
}
