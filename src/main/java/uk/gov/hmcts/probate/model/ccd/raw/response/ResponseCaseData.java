package uk.gov.hmcts.probate.model.ccd.raw.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper=true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCaseData extends ResponseCaseDataParent{

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
    private List<CollectionMember<ParagraphDetail>> paragraphDetails = new ArrayList<>();
    private String bulkScanCaseReference;

    private final String grantDelayedNotificationDate;
    private final String grantStoppedDate;
    private String grantDelayedNotificationIdentified;
    private final String grantDelayedNotificationSent;
    private final String grantAwaitingDocumentationNotificationDate;
    private final String grantAwaitingDocumentatioNotificationSent;
    private final String pcqId;

    ResponseCaseData(String state, ApplicationType applicationType, String registryLocation, String deceasedDateOfDeath, String deceasedDateOfBirth, String deceasedForenames, String deceasedSurname, String solsSolicitorFirmName, SolsAddress solsSolicitorAddress, String solsSolicitorEmail,
                     String solsSolicitorPhoneNumber, String solsSOTName, String solsSOTForenames, String solsSOTSurname, String solsSOTJobTitle, String solsSolicitorAppReference, String ihtFormId, String solsSolicitorIsExec, String solsSolicitorIsMainApplicant, String solsSolicitorIsApplying,
                     String solsSolicitorNotApplyingReason, String solsWillType, String solsApplicantRelationshipToDeceased, String solsSpouseOrCivilRenouncing, String solsAdoptedEnglandOrWales, String solsMinorityInterest, String solsApplicantSiblings, String solsEntitledMinority, String solsDiedOrNotApplying, String solsResiduary, String solsResiduaryType, String solsLifeInterest, String willExists, String willAccessOriginal, String willHasCodicils, String willNumberOfCodicils, BigDecimal ihtNetValue, BigDecimal ihtGrossValue, String deceasedDomicileInEngWales, String extraCopiesOfGrant, String outsideUKGrantCopies, String applicationFee, String feeForUkCopies, String feeForNonUkCopies, String totalFee, String solsPaymentMethods, String solsFeeAccountNumber, String paymentReferenceNumber, DocumentLink solsLegalStatementDocument, DocumentLink statementOfTruthDocument, List<CollectionMember<Document>> probateDocumentsGenerated, List<CollectionMember<Document>> probateNotificationsGenerated, List<CollectionMember<UploadDocument>> boDocumentsUploaded, List<CollectionMember<CaseMatch>> caseMatches, String solsSOTNeedToUpdate, DocumentLink solsNextStepsDocument, String solsAdditionalInfo, String primaryApplicantForenames, String primaryApplicantSurname, String primaryApplicantEmailAddress, String primaryApplicantHasAlias, String primaryApplicantIsApplying, String solsPrimaryExecutorNotApplyingReason, String otherExecutorExists, String primaryApplicantAlias, String primaryApplicantSameWillName, String primaryApplicantAliasReason, String primaryApplicantOtherReason, String solsExecutorAliasNames, List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying, List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying, List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList, SolsAddress deceasedAddress, String deceasedAnyOtherNames, SolsAddress primaryApplicantAddress, List<CollectionMember<AliasName>> solsDeceasedAliasNamesList, List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList, String casePrinted, String boEmailDocsReceivedNotificationRequested, String boEmailGrantIssuedNotificationRequested, String boEmailDocsReceivedNotification, String boEmailGrantIssuedNotification, List<CollectionMember<StopReason>> boCaseStopReasonList, String boStopDetails, String boDeceasedTitle, String boDeceasedHonours, String boWillMessage, String boExecutorLimitation, String boAdminClauseLimitation, String boLimitationText, String ihtReferenceNumber, String ihtFormCompletedOnline, LegalStatement legalStatement, Declaration declaration, String deceasedMarriedAfterWillOrCodicilDate, String primaryApplicantPhoneNumber, String boExaminationChecklistQ2, String boExaminationChecklistQ1, String boExaminationChecklistRequestQA, List<CollectionMember<Payment>> payments, String applicationSubmittedDate, List<CollectionMember<ScannedDocument>> scannedDocuments, String evidenceHandled, String caseType, String paperForm, String languagePreferenceWelsh, String primaryApplicantSecondPhoneNumber, String primaryApplicantRelationshipToDeceased, String paRelationshipToDeceasedOther, String applicationFeePaperForm, String feeForCopiesPaperForm, String totalFeePaperForm, String paperPaymentMethod, String paymentReferenceNumberPaperform, String entitledToApply, String entitledToApplyOther, String notifiedApplicants, String foreignAsset, String foreignAssetEstateValue, String adopted, List<CollectionMember<AdoptedRelative>> adoptiveRelatives, String mentalCapacity, String courtOfProtection, String epaOrLpa, String epaRegistered, String domicilityCountry, List<CollectionMember<EstateItem>> ukEstate, String domicilityIHTCert, String applicationGrounds, String willDispose, String englishWill, String appointExec, String appointExecByDuties, String appointExecNo, String immovableEstate, String willDatedBeforeApril, String deceasedEnterMarriageOrCP, String deceasedMaritalStatus, String willsOutsideOfUK, String courtOfDecree, String dateOfMarriageOrCP, String dateOfDivorcedCPJudicially, String willGiftUnderEighteen, String applyingAsAnAttorney, List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyOnBehalfOfNameAndAddress, String spouseOrPartner, String childrenSurvived, String childrenOverEighteenSurvived, String childrenUnderEighteenSurvived, String childrenDied, String childrenDiedOverEighteen, String childrenDiedUnderEighteen, String parentsExistSurvived, String parentsExistOverEighteenSurvived, String parentsExistUnderEighteenSurvived, String wholeBloodNeicesAndNephews, String wholeBloodNeicesAndNephewsOverEighteen, String wholeBloodNeicesAndNephewsUnderEighteen, String wholeBloodSiblingsDied, String wholeBloodSiblingsDiedOverEighteen, String wholeBloodSiblingsDiedUnderEighteen, String wholeBloodSiblingsSurvived, String wholeBloodSiblingsSurvivedOverEighteen, String wholeBloodSiblingsSurvivedUnderEighteen, String halfBloodSiblingsDied, String halfBloodSiblingsDiedOverEighteen, String halfBloodSiblingsDiedUnderEighteen, String halfBloodSiblingsSurvived, String halfBloodSiblingsSurvivedOverEighteen, String halfBloodSiblingsSurvivedUnderEighteen, String grandparentsDied, String grandparentsDiedOverEighteen, String grandparentsDiedUnderEighteen, String halfBloodNeicesAndNephews, String halfBloodNeicesAndNephewsOverEighteen, String halfBloodNeicesAndNephewsUnderEighteen, String grandChildrenSurvived, String grandChildrenSurvivedOverEighteen, String grandChildrenSurvivedUnderEighteen, String wholeBloodUnclesAndAuntsDied, String wholeBloodUnclesAndAuntsDiedOverEighteen, String wholeBloodUnclesAndAuntsDiedUnderEighteen, String wholeBloodUnclesAndAuntsSurvived, String wholeBloodUnclesAndAuntsSurvivedOverEighteen, String wholeBloodUnclesAndAuntsSurvivedUnderEighteen, String halfBloodUnclesAndAuntsSurvived, String halfBloodUnclesAndAuntsSurvivedOverEighteen, String halfBloodUnclesAndAuntsSurvivedUnderEighteen, String halfBloodUnclesAndAuntsDied, String halfBloodUnclesAndAuntsDiedOverEighteen, String halfBloodUnclesAndAuntsDiedUnderEighteen, String halfBloodCousinsSurvived, String halfBloodCousinsSurvivedOverEighteen, String halfBloodCousinsSurvivedUnderEighteen, String wholeBloodCousinsSurvived, String wholeBloodCousinsSurvivedOverEighteen, String wholeBloodCousinsSurvivedUnderEighteen, String boSendToBulkPrint, String boSendToBulkPrintRequested, String grantIssuedDate, String dateOfDeathType, List<CollectionMember<CaseMatch>> legacySearchResultRows, String recordId, String legacyType, String legacyCaseViewUrl, String bulkPrintSendLetterId, String bulkPrintPdfSize, List<CollectionMember<BulkPrint>> bulkPrintId, String boCaveatStopNotificationRequested, String boCaveatStopNotification, String boCaseStopCaveatId, String boCaveatStopEmailNotificationRequested, String boCaveatStopEmailNotification, String boCaveatStopSendToBulkPrintRequested, String boCaveatStopSendToBulkPrint, String boEmailGrantReissuedNotificationRequested, String boEmailGrantReissuedNotification, String boGrantReissueSendToBulkPrint, String boGrantReissueSendToBulkPrintRequested, String orderNeeded, List<CollectionMember<Reissue>> reissueReason, String reissueDate, String reissueReasonNotation, String latestGrantReissueDate, String deceasedDivorcedInEnglandOrWales, String primaryApplicantAdoptionInEnglandOrWales, String deceasedSpouseNotApplyingReason, String deceasedOtherChildren, String allDeceasedChildrenOverEighteen, String anyDeceasedChildrenDieBeforeDeceased, String anyDeceasedGrandChildrenUnderEighteen, String deceasedAnyChildren, String deceasedHasAssetsOutsideUK, String solicitorIsMainApplicant, String boStopDetailsDeclarationParagraph, String boEmailRequestInfoNotificationRequested, String boEmailRequestInfoNotification, String boRequestInfoSendToBulkPrint, String boRequestInfoSendToBulkPrintRequested, String boAssembleLetterSendToBulkPrint, String boAssembleLetterSendToBulkPrintRequested, List<CollectionMember<ExecutorsApplyingNotification>> executorsApplyingNotifications, List<CollectionMember<Document>> probateSotDocumentsGenerated, Categories categories, DocumentLink previewLink, List<CollectionMember<ParagraphDetail>> paragraphDetails, String bulkScanCaseReference, String grantDelayedNotificationDate, String grantStoppedDate, String grantDelayedNotificationIdentified, String grantDelayedNotificationSent, String grantAwaitingDocumentationNotificationDate, String grantAwaitingDocumentatioNotificationSent, String pcqId) {
        this.state = state;
        this.applicationType = applicationType;
        this.registryLocation = registryLocation;
        this.deceasedDateOfDeath = deceasedDateOfDeath;
        this.deceasedDateOfBirth = deceasedDateOfBirth;
        this.deceasedForenames = deceasedForenames;
        this.deceasedSurname = deceasedSurname;
        this.solsSolicitorFirmName = solsSolicitorFirmName;
        this.solsSolicitorAddress = solsSolicitorAddress;
        this.solsSolicitorEmail = solsSolicitorEmail;
        this.solsSolicitorPhoneNumber = solsSolicitorPhoneNumber;
        this.solsSOTName = solsSOTName;
        this.solsSOTForenames = solsSOTForenames;
        this.solsSOTSurname = solsSOTSurname;
        this.solsSOTJobTitle = solsSOTJobTitle;
        this.solsSolicitorAppReference = solsSolicitorAppReference;
        this.ihtFormId = ihtFormId;
        this.solsSolicitorIsExec = solsSolicitorIsExec;
        this.solsSolicitorIsMainApplicant = solsSolicitorIsMainApplicant;
        this.solsSolicitorIsApplying = solsSolicitorIsApplying;
        this.solsSolicitorNotApplyingReason = solsSolicitorNotApplyingReason;
        this.solsWillType = solsWillType;
        this.solsApplicantRelationshipToDeceased = solsApplicantRelationshipToDeceased;
        this.solsSpouseOrCivilRenouncing = solsSpouseOrCivilRenouncing;
        this.solsAdoptedEnglandOrWales = solsAdoptedEnglandOrWales;
        this.solsMinorityInterest = solsMinorityInterest;
        this.solsApplicantSiblings = solsApplicantSiblings;
        this.solsEntitledMinority = solsEntitledMinority;
        this.solsDiedOrNotApplying = solsDiedOrNotApplying;
        this.solsResiduary = solsResiduary;
        this.solsResiduaryType = solsResiduaryType;
        this.solsLifeInterest = solsLifeInterest;
        this.willExists = willExists;
        this.willAccessOriginal = willAccessOriginal;
        this.willHasCodicils = willHasCodicils;
        this.willNumberOfCodicils = willNumberOfCodicils;
        this.ihtNetValue = ihtNetValue;
        this.ihtGrossValue = ihtGrossValue;
        this.deceasedDomicileInEngWales = deceasedDomicileInEngWales;
        this.extraCopiesOfGrant = extraCopiesOfGrant;
        this.outsideUKGrantCopies = outsideUKGrantCopies;
        this.applicationFee = applicationFee;
        this.feeForUkCopies = feeForUkCopies;
        this.feeForNonUkCopies = feeForNonUkCopies;
        this.totalFee = totalFee;
        this.solsPaymentMethods = solsPaymentMethods;
        this.solsFeeAccountNumber = solsFeeAccountNumber;
        this.paymentReferenceNumber = paymentReferenceNumber;
        this.solsLegalStatementDocument = solsLegalStatementDocument;
        this.statementOfTruthDocument = statementOfTruthDocument;
        this.probateDocumentsGenerated = probateDocumentsGenerated;
        this.probateNotificationsGenerated = probateNotificationsGenerated;
        this.boDocumentsUploaded = boDocumentsUploaded;
        this.caseMatches = caseMatches;
        this.solsSOTNeedToUpdate = solsSOTNeedToUpdate;
        this.solsNextStepsDocument = solsNextStepsDocument;
        this.solsAdditionalInfo = solsAdditionalInfo;
        this.primaryApplicantForenames = primaryApplicantForenames;
        this.primaryApplicantSurname = primaryApplicantSurname;
        this.primaryApplicantEmailAddress = primaryApplicantEmailAddress;
        this.primaryApplicantHasAlias = primaryApplicantHasAlias;
        this.primaryApplicantIsApplying = primaryApplicantIsApplying;
        this.solsPrimaryExecutorNotApplyingReason = solsPrimaryExecutorNotApplyingReason;
        this.otherExecutorExists = otherExecutorExists;
        this.primaryApplicantAlias = primaryApplicantAlias;
        this.primaryApplicantSameWillName = primaryApplicantSameWillName;
        this.primaryApplicantAliasReason = primaryApplicantAliasReason;
        this.primaryApplicantOtherReason = primaryApplicantOtherReason;
        this.solsExecutorAliasNames = solsExecutorAliasNames;
        this.additionalExecutorsApplying = additionalExecutorsApplying;
        this.additionalExecutorsNotApplying = additionalExecutorsNotApplying;
        this.solsAdditionalExecutorList = solsAdditionalExecutorList;
        this.deceasedAddress = deceasedAddress;
        this.deceasedAnyOtherNames = deceasedAnyOtherNames;
        this.primaryApplicantAddress = primaryApplicantAddress;
        this.solsDeceasedAliasNamesList = solsDeceasedAliasNamesList;
        this.deceasedAliasNamesList = deceasedAliasNamesList;
        this.casePrinted = casePrinted;
        this.boEmailDocsReceivedNotificationRequested = boEmailDocsReceivedNotificationRequested;
        this.boEmailGrantIssuedNotificationRequested = boEmailGrantIssuedNotificationRequested;
        this.boEmailDocsReceivedNotification = boEmailDocsReceivedNotification;
        this.boEmailGrantIssuedNotification = boEmailGrantIssuedNotification;
        this.boCaseStopReasonList = boCaseStopReasonList;
        this.boStopDetails = boStopDetails;
        this.boDeceasedTitle = boDeceasedTitle;
        this.boDeceasedHonours = boDeceasedHonours;
        this.boWillMessage = boWillMessage;
        this.boExecutorLimitation = boExecutorLimitation;
        this.boAdminClauseLimitation = boAdminClauseLimitation;
        this.boLimitationText = boLimitationText;
        this.ihtReferenceNumber = ihtReferenceNumber;
        this.ihtFormCompletedOnline = ihtFormCompletedOnline;
        this.legalStatement = legalStatement;
        this.declaration = declaration;
        this.deceasedMarriedAfterWillOrCodicilDate = deceasedMarriedAfterWillOrCodicilDate;
        this.primaryApplicantPhoneNumber = primaryApplicantPhoneNumber;
        this.boExaminationChecklistQ2 = boExaminationChecklistQ2;
        this.boExaminationChecklistQ1 = boExaminationChecklistQ1;
        this.boExaminationChecklistRequestQA = boExaminationChecklistRequestQA;
        this.payments = payments;
        this.applicationSubmittedDate = applicationSubmittedDate;
        this.scannedDocuments = scannedDocuments;
        this.evidenceHandled = evidenceHandled;
        this.caseType = caseType;
        this.paperForm = paperForm;
        this.languagePreferenceWelsh = languagePreferenceWelsh;
        this.primaryApplicantSecondPhoneNumber = primaryApplicantSecondPhoneNumber;
        this.primaryApplicantRelationshipToDeceased = primaryApplicantRelationshipToDeceased;
        this.paRelationshipToDeceasedOther = paRelationshipToDeceasedOther;
        this.applicationFeePaperForm = applicationFeePaperForm;
        this.feeForCopiesPaperForm = feeForCopiesPaperForm;
        this.totalFeePaperForm = totalFeePaperForm;
        this.paperPaymentMethod = paperPaymentMethod;
        this.paymentReferenceNumberPaperform = paymentReferenceNumberPaperform;
        this.entitledToApply = entitledToApply;
        this.entitledToApplyOther = entitledToApplyOther;
        this.notifiedApplicants = notifiedApplicants;
        this.foreignAsset = foreignAsset;
        this.foreignAssetEstateValue = foreignAssetEstateValue;
        this.adopted = adopted;
        this.adoptiveRelatives = adoptiveRelatives;
        this.mentalCapacity = mentalCapacity;
        this.courtOfProtection = courtOfProtection;
        this.epaOrLpa = epaOrLpa;
        this.epaRegistered = epaRegistered;
        this.domicilityCountry = domicilityCountry;
        this.ukEstate = ukEstate;
        this.domicilityIHTCert = domicilityIHTCert;
        this.applicationGrounds = applicationGrounds;
        this.willDispose = willDispose;
        this.englishWill = englishWill;
        this.appointExec = appointExec;
        this.appointExecByDuties = appointExecByDuties;
        this.appointExecNo = appointExecNo;
        this.immovableEstate = immovableEstate;
        this.willDatedBeforeApril = willDatedBeforeApril;
        this.deceasedEnterMarriageOrCP = deceasedEnterMarriageOrCP;
        this.deceasedMaritalStatus = deceasedMaritalStatus;
        this.willsOutsideOfUK = willsOutsideOfUK;
        this.courtOfDecree = courtOfDecree;
        this.dateOfMarriageOrCP = dateOfMarriageOrCP;
        this.dateOfDivorcedCPJudicially = dateOfDivorcedCPJudicially;
        this.willGiftUnderEighteen = willGiftUnderEighteen;
        this.applyingAsAnAttorney = applyingAsAnAttorney;
        this.attorneyOnBehalfOfNameAndAddress = attorneyOnBehalfOfNameAndAddress;
        this.spouseOrPartner = spouseOrPartner;
        this.childrenSurvived = childrenSurvived;
        this.childrenOverEighteenSurvived = childrenOverEighteenSurvived;
        this.childrenUnderEighteenSurvived = childrenUnderEighteenSurvived;
        this.childrenDied = childrenDied;
        this.childrenDiedOverEighteen = childrenDiedOverEighteen;
        this.childrenDiedUnderEighteen = childrenDiedUnderEighteen;
        this.parentsExistSurvived = parentsExistSurvived;
        this.parentsExistOverEighteenSurvived = parentsExistOverEighteenSurvived;
        this.parentsExistUnderEighteenSurvived = parentsExistUnderEighteenSurvived;
        this.wholeBloodNeicesAndNephews = wholeBloodNeicesAndNephews;
        this.wholeBloodNeicesAndNephewsOverEighteen = wholeBloodNeicesAndNephewsOverEighteen;
        this.wholeBloodNeicesAndNephewsUnderEighteen = wholeBloodNeicesAndNephewsUnderEighteen;
        this.wholeBloodSiblingsDied = wholeBloodSiblingsDied;
        this.wholeBloodSiblingsDiedOverEighteen = wholeBloodSiblingsDiedOverEighteen;
        this.wholeBloodSiblingsDiedUnderEighteen = wholeBloodSiblingsDiedUnderEighteen;
        this.wholeBloodSiblingsSurvived = wholeBloodSiblingsSurvived;
        this.wholeBloodSiblingsSurvivedOverEighteen = wholeBloodSiblingsSurvivedOverEighteen;
        this.wholeBloodSiblingsSurvivedUnderEighteen = wholeBloodSiblingsSurvivedUnderEighteen;
        this.halfBloodSiblingsDied = halfBloodSiblingsDied;
        this.halfBloodSiblingsDiedOverEighteen = halfBloodSiblingsDiedOverEighteen;
        this.halfBloodSiblingsDiedUnderEighteen = halfBloodSiblingsDiedUnderEighteen;
        this.halfBloodSiblingsSurvived = halfBloodSiblingsSurvived;
        this.halfBloodSiblingsSurvivedOverEighteen = halfBloodSiblingsSurvivedOverEighteen;
        this.halfBloodSiblingsSurvivedUnderEighteen = halfBloodSiblingsSurvivedUnderEighteen;
        this.grandparentsDied = grandparentsDied;
        this.grandparentsDiedOverEighteen = grandparentsDiedOverEighteen;
        this.grandparentsDiedUnderEighteen = grandparentsDiedUnderEighteen;
        this.halfBloodNeicesAndNephews = halfBloodNeicesAndNephews;
        this.halfBloodNeicesAndNephewsOverEighteen = halfBloodNeicesAndNephewsOverEighteen;
        this.halfBloodNeicesAndNephewsUnderEighteen = halfBloodNeicesAndNephewsUnderEighteen;
        this.grandChildrenSurvived = grandChildrenSurvived;
        this.grandChildrenSurvivedOverEighteen = grandChildrenSurvivedOverEighteen;
        this.grandChildrenSurvivedUnderEighteen = grandChildrenSurvivedUnderEighteen;
        this.wholeBloodUnclesAndAuntsDied = wholeBloodUnclesAndAuntsDied;
        this.wholeBloodUnclesAndAuntsDiedOverEighteen = wholeBloodUnclesAndAuntsDiedOverEighteen;
        this.wholeBloodUnclesAndAuntsDiedUnderEighteen = wholeBloodUnclesAndAuntsDiedUnderEighteen;
        this.wholeBloodUnclesAndAuntsSurvived = wholeBloodUnclesAndAuntsSurvived;
        this.wholeBloodUnclesAndAuntsSurvivedOverEighteen = wholeBloodUnclesAndAuntsSurvivedOverEighteen;
        this.wholeBloodUnclesAndAuntsSurvivedUnderEighteen = wholeBloodUnclesAndAuntsSurvivedUnderEighteen;
        this.halfBloodUnclesAndAuntsSurvived = halfBloodUnclesAndAuntsSurvived;
        this.halfBloodUnclesAndAuntsSurvivedOverEighteen = halfBloodUnclesAndAuntsSurvivedOverEighteen;
        this.halfBloodUnclesAndAuntsSurvivedUnderEighteen = halfBloodUnclesAndAuntsSurvivedUnderEighteen;
        this.halfBloodUnclesAndAuntsDied = halfBloodUnclesAndAuntsDied;
        this.halfBloodUnclesAndAuntsDiedOverEighteen = halfBloodUnclesAndAuntsDiedOverEighteen;
        this.halfBloodUnclesAndAuntsDiedUnderEighteen = halfBloodUnclesAndAuntsDiedUnderEighteen;
        this.halfBloodCousinsSurvived = halfBloodCousinsSurvived;
        this.halfBloodCousinsSurvivedOverEighteen = halfBloodCousinsSurvivedOverEighteen;
        this.halfBloodCousinsSurvivedUnderEighteen = halfBloodCousinsSurvivedUnderEighteen;
        this.wholeBloodCousinsSurvived = wholeBloodCousinsSurvived;
        this.wholeBloodCousinsSurvivedOverEighteen = wholeBloodCousinsSurvivedOverEighteen;
        this.wholeBloodCousinsSurvivedUnderEighteen = wholeBloodCousinsSurvivedUnderEighteen;
        this.boSendToBulkPrint = boSendToBulkPrint;
        this.boSendToBulkPrintRequested = boSendToBulkPrintRequested;
        this.grantIssuedDate = grantIssuedDate;
        this.dateOfDeathType = dateOfDeathType;
        this.legacySearchResultRows = legacySearchResultRows;
        this.recordId = recordId;
        this.legacyType = legacyType;
        this.legacyCaseViewUrl = legacyCaseViewUrl;
        this.bulkPrintSendLetterId = bulkPrintSendLetterId;
        this.bulkPrintPdfSize = bulkPrintPdfSize;
        this.bulkPrintId = bulkPrintId;
        this.boCaveatStopNotificationRequested = boCaveatStopNotificationRequested;
        this.boCaveatStopNotification = boCaveatStopNotification;
        this.boCaseStopCaveatId = boCaseStopCaveatId;
        this.boCaveatStopEmailNotificationRequested = boCaveatStopEmailNotificationRequested;
        this.boCaveatStopEmailNotification = boCaveatStopEmailNotification;
        this.boCaveatStopSendToBulkPrintRequested = boCaveatStopSendToBulkPrintRequested;
        this.boCaveatStopSendToBulkPrint = boCaveatStopSendToBulkPrint;
        this.boEmailGrantReissuedNotificationRequested = boEmailGrantReissuedNotificationRequested;
        this.boEmailGrantReissuedNotification = boEmailGrantReissuedNotification;
        this.boGrantReissueSendToBulkPrint = boGrantReissueSendToBulkPrint;
        this.boGrantReissueSendToBulkPrintRequested = boGrantReissueSendToBulkPrintRequested;
        this.orderNeeded = orderNeeded;
        this.reissueReason = reissueReason;
        this.reissueDate = reissueDate;
        this.reissueReasonNotation = reissueReasonNotation;
        this.latestGrantReissueDate = latestGrantReissueDate;
        this.deceasedDivorcedInEnglandOrWales = deceasedDivorcedInEnglandOrWales;
        this.primaryApplicantAdoptionInEnglandOrWales = primaryApplicantAdoptionInEnglandOrWales;
        this.deceasedSpouseNotApplyingReason = deceasedSpouseNotApplyingReason;
        this.deceasedOtherChildren = deceasedOtherChildren;
        this.allDeceasedChildrenOverEighteen = allDeceasedChildrenOverEighteen;
        this.anyDeceasedChildrenDieBeforeDeceased = anyDeceasedChildrenDieBeforeDeceased;
        this.anyDeceasedGrandChildrenUnderEighteen = anyDeceasedGrandChildrenUnderEighteen;
        this.deceasedAnyChildren = deceasedAnyChildren;
        this.deceasedHasAssetsOutsideUK = deceasedHasAssetsOutsideUK;
        this.solicitorIsMainApplicant = solicitorIsMainApplicant;
        this.boStopDetailsDeclarationParagraph = boStopDetailsDeclarationParagraph;
        this.boEmailRequestInfoNotificationRequested = boEmailRequestInfoNotificationRequested;
        this.boEmailRequestInfoNotification = boEmailRequestInfoNotification;
        this.boRequestInfoSendToBulkPrint = boRequestInfoSendToBulkPrint;
        this.boRequestInfoSendToBulkPrintRequested = boRequestInfoSendToBulkPrintRequested;
        this.boAssembleLetterSendToBulkPrint = boAssembleLetterSendToBulkPrint;
        this.boAssembleLetterSendToBulkPrintRequested = boAssembleLetterSendToBulkPrintRequested;
        this.executorsApplyingNotifications = executorsApplyingNotifications;
        this.probateSotDocumentsGenerated = probateSotDocumentsGenerated;
        this.categories = categories;
        this.previewLink = previewLink;
        this.paragraphDetails = paragraphDetails;
        this.bulkScanCaseReference = bulkScanCaseReference;
        this.grantDelayedNotificationDate = grantDelayedNotificationDate;
        this.grantStoppedDate = grantStoppedDate;
        this.grantDelayedNotificationIdentified = grantDelayedNotificationIdentified;
        this.grantDelayedNotificationSent = grantDelayedNotificationSent;
        this.grantAwaitingDocumentationNotificationDate = grantAwaitingDocumentationNotificationDate;
        this.grantAwaitingDocumentatioNotificationSent = grantAwaitingDocumentatioNotificationSent;
        this.pcqId = pcqId;

    }

    public static ResponseCaseDataBuilder builder() {
        return new ResponseCaseDataBuilder();
    }

    public static class ResponseCaseDataBuilder extends ResponseCaseDataParentBuilder {
        private String state;
        private ApplicationType applicationType;
        private String registryLocation;
        private String deceasedDateOfDeath;
        private String deceasedDateOfBirth;
        private String deceasedForenames;
        private String deceasedSurname;
        private String solsSolicitorFirmName;
        private SolsAddress solsSolicitorAddress;
        private String solsSolicitorEmail;
        private String solsSolicitorPhoneNumber;
        private String solsSOTName;
        private String solsSOTForenames;
        private String solsSOTSurname;
        private String solsSOTJobTitle;
        private String solsSolicitorAppReference;
        private String ihtFormId;
        private String solsSolicitorIsExec;
        private String solsSolicitorIsMainApplicant;
        private String solsSolicitorIsApplying;
        private String solsSolicitorNotApplyingReason;
        private String solsWillType;
        private String solsApplicantRelationshipToDeceased;
        private String solsSpouseOrCivilRenouncing;
        private String solsAdoptedEnglandOrWales;
        private String solsMinorityInterest;
        private String solsApplicantSiblings;
        private String solsEntitledMinority;
        private String solsDiedOrNotApplying;
        private String solsResiduary;
        private String solsResiduaryType;
        private String solsLifeInterest;
        private String willExists;
        private String willAccessOriginal;
        private String willHasCodicils;
        private String willNumberOfCodicils;
        private BigDecimal ihtNetValue;
        private BigDecimal ihtGrossValue;
        private String deceasedDomicileInEngWales;
        private String extraCopiesOfGrant;
        private String outsideUKGrantCopies;
        private String applicationFee;
        private String feeForUkCopies;
        private String feeForNonUkCopies;
        private String totalFee;
        private String solsPaymentMethods;
        private String solsFeeAccountNumber;
        private String paymentReferenceNumber;
        private DocumentLink solsLegalStatementDocument;
        private DocumentLink statementOfTruthDocument;
        private List<CollectionMember<Document>> probateDocumentsGenerated;
        private List<CollectionMember<Document>> probateNotificationsGenerated;
        private List<CollectionMember<UploadDocument>> boDocumentsUploaded;
        private List<CollectionMember<CaseMatch>> caseMatches;
        private String solsSOTNeedToUpdate;
        private DocumentLink solsNextStepsDocument;
        private String solsAdditionalInfo;
        private String primaryApplicantForenames;
        private String primaryApplicantSurname;
        private String primaryApplicantEmailAddress;
        private String primaryApplicantHasAlias;
        private String primaryApplicantIsApplying;
        private String solsPrimaryExecutorNotApplyingReason;
        private String otherExecutorExists;
        private String primaryApplicantAlias;
        private String primaryApplicantSameWillName;
        private String primaryApplicantAliasReason;
        private String primaryApplicantOtherReason;
        private String solsExecutorAliasNames;
        private List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying;
        private List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying;
        private List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList;
        private SolsAddress deceasedAddress;
        private String deceasedAnyOtherNames;
        private SolsAddress primaryApplicantAddress;
        private List<CollectionMember<AliasName>> solsDeceasedAliasNamesList;
        private List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList;
        private String casePrinted;
        private String boEmailDocsReceivedNotificationRequested;
        private String boEmailGrantIssuedNotificationRequested;
        private String boEmailDocsReceivedNotification;
        private String boEmailGrantIssuedNotification;
        private List<CollectionMember<StopReason>> boCaseStopReasonList;
        private String boStopDetails;
        private String boDeceasedTitle;
        private String boDeceasedHonours;
        private String boWillMessage;
        private String boExecutorLimitation;
        private String boAdminClauseLimitation;
        private String boLimitationText;
        private String ihtReferenceNumber;
        private String ihtFormCompletedOnline;
        private LegalStatement legalStatement;
        private Declaration declaration;
        private String deceasedMarriedAfterWillOrCodicilDate;
        private String primaryApplicantPhoneNumber;
        private String boExaminationChecklistQ2;
        private String boExaminationChecklistQ1;
        private String boExaminationChecklistRequestQA;
        private List<CollectionMember<Payment>> payments;
        private String applicationSubmittedDate;
        private List<CollectionMember<ScannedDocument>> scannedDocuments;
        private String evidenceHandled;
        private String caseType;
        private String paperForm;
        private String languagePreferenceWelsh;
        private String primaryApplicantSecondPhoneNumber;
        private String primaryApplicantRelationshipToDeceased;
        private String paRelationshipToDeceasedOther;
        private String applicationFeePaperForm;
        private String feeForCopiesPaperForm;
        private String totalFeePaperForm;
        private String paperPaymentMethod;
        private String paymentReferenceNumberPaperform;
        private String entitledToApply;
        private String entitledToApplyOther;
        private String notifiedApplicants;
        private String foreignAsset;
        private String foreignAssetEstateValue;
        private String adopted;
        private List<CollectionMember<AdoptedRelative>> adoptiveRelatives;
        private String mentalCapacity;
        private String courtOfProtection;
        private String epaOrLpa;
        private String epaRegistered;
        private String domicilityCountry;
        private List<CollectionMember<EstateItem>> ukEstate;
        private String domicilityIHTCert;
        private String applicationGrounds;
        private String willDispose;
        private String englishWill;
        private String appointExec;
        private String appointExecByDuties;
        private String appointExecNo;
        private String immovableEstate;
        private String willDatedBeforeApril;
        private String deceasedEnterMarriageOrCP;
        private String deceasedMaritalStatus;
        private String willsOutsideOfUK;
        private String courtOfDecree;
        private String dateOfMarriageOrCP;
        private String dateOfDivorcedCPJudicially;
        private String willGiftUnderEighteen;
        private String applyingAsAnAttorney;
        private List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyOnBehalfOfNameAndAddress;
        private String spouseOrPartner;
        private String childrenSurvived;
        private String childrenOverEighteenSurvived;
        private String childrenUnderEighteenSurvived;
        private String childrenDied;
        private String childrenDiedOverEighteen;
        private String childrenDiedUnderEighteen;
        private String parentsExistSurvived;
        private String parentsExistOverEighteenSurvived;
        private String parentsExistUnderEighteenSurvived;
        private String wholeBloodNeicesAndNephews;
        private String wholeBloodNeicesAndNephewsOverEighteen;
        private String wholeBloodNeicesAndNephewsUnderEighteen;
        private String wholeBloodSiblingsDied;
        private String wholeBloodSiblingsDiedOverEighteen;
        private String wholeBloodSiblingsDiedUnderEighteen;
        private String wholeBloodSiblingsSurvived;
        private String wholeBloodSiblingsSurvivedOverEighteen;
        private String wholeBloodSiblingsSurvivedUnderEighteen;
        private String halfBloodSiblingsDied;
        private String halfBloodSiblingsDiedOverEighteen;
        private String halfBloodSiblingsDiedUnderEighteen;
        private String halfBloodSiblingsSurvived;
        private String halfBloodSiblingsSurvivedOverEighteen;
        private String halfBloodSiblingsSurvivedUnderEighteen;
        private String grandparentsDied;
        private String grandparentsDiedOverEighteen;
        private String grandparentsDiedUnderEighteen;
        private String halfBloodNeicesAndNephews;
        private String halfBloodNeicesAndNephewsOverEighteen;
        private String halfBloodNeicesAndNephewsUnderEighteen;
        private String grandChildrenSurvived;
        private String grandChildrenSurvivedOverEighteen;
        private String grandChildrenSurvivedUnderEighteen;
        private String wholeBloodUnclesAndAuntsDied;
        private String wholeBloodUnclesAndAuntsDiedOverEighteen;
        private String wholeBloodUnclesAndAuntsDiedUnderEighteen;
        private String wholeBloodUnclesAndAuntsSurvived;
        private String wholeBloodUnclesAndAuntsSurvivedOverEighteen;
        private String wholeBloodUnclesAndAuntsSurvivedUnderEighteen;
        private String halfBloodUnclesAndAuntsSurvived;
        private String halfBloodUnclesAndAuntsSurvivedOverEighteen;
        private String halfBloodUnclesAndAuntsSurvivedUnderEighteen;
        private String halfBloodUnclesAndAuntsDied;
        private String halfBloodUnclesAndAuntsDiedOverEighteen;
        private String halfBloodUnclesAndAuntsDiedUnderEighteen;
        private String halfBloodCousinsSurvived;
        private String halfBloodCousinsSurvivedOverEighteen;
        private String halfBloodCousinsSurvivedUnderEighteen;
        private String wholeBloodCousinsSurvived;
        private String wholeBloodCousinsSurvivedOverEighteen;
        private String wholeBloodCousinsSurvivedUnderEighteen;
        private String boSendToBulkPrint;
        private String boSendToBulkPrintRequested;
        private String grantIssuedDate;
        private String dateOfDeathType;
        private List<CollectionMember<CaseMatch>> legacySearchResultRows;
        private String recordId;
        private String legacyType;
        private String legacyCaseViewUrl;
        private String bulkPrintSendLetterId;
        private String bulkPrintPdfSize;
        private List<CollectionMember<BulkPrint>> bulkPrintId;
        private String boCaveatStopNotificationRequested;
        private String boCaveatStopNotification;
        private String boCaseStopCaveatId;
        private String boCaveatStopEmailNotificationRequested;
        private String boCaveatStopEmailNotification;
        private String boCaveatStopSendToBulkPrintRequested;
        private String boCaveatStopSendToBulkPrint;
        private String boEmailGrantReissuedNotificationRequested;
        private String boEmailGrantReissuedNotification;
        private String boGrantReissueSendToBulkPrint;
        private String boGrantReissueSendToBulkPrintRequested;
        private String orderNeeded;
        private List<CollectionMember<Reissue>> reissueReason;
        private String reissueDate;
        private String reissueReasonNotation;
        private String latestGrantReissueDate;
        private String deceasedDivorcedInEnglandOrWales;
        private String primaryApplicantAdoptionInEnglandOrWales;
        private String deceasedSpouseNotApplyingReason;
        private String deceasedOtherChildren;
        private String allDeceasedChildrenOverEighteen;
        private String anyDeceasedChildrenDieBeforeDeceased;
        private String anyDeceasedGrandChildrenUnderEighteen;
        private String deceasedAnyChildren;
        private String deceasedHasAssetsOutsideUK;
        private String solicitorIsMainApplicant;
        private String boStopDetailsDeclarationParagraph;
        private String boEmailRequestInfoNotificationRequested;
        private String boEmailRequestInfoNotification;
        private String boRequestInfoSendToBulkPrint;
        private String boRequestInfoSendToBulkPrintRequested;
        private String boAssembleLetterSendToBulkPrint;
        private String boAssembleLetterSendToBulkPrintRequested;
        private List<CollectionMember<ExecutorsApplyingNotification>> executorsApplyingNotifications;
        private List<CollectionMember<Document>> probateSotDocumentsGenerated;
        private Categories categories;
        private DocumentLink previewLink;
        private List<CollectionMember<ParagraphDetail>> paragraphDetails;
        private String bulkScanCaseReference;
        private String grantDelayedNotificationDate;
        private String grantStoppedDate;
        private String grantDelayedNotificationIdentified;
        private String grantDelayedNotificationSent;
        private String grantAwaitingDocumentationNotificationDate;
        private String grantAwaitingDocumentatioNotificationSent;
        private String pcqId;

        ResponseCaseDataBuilder() {
        }

        public ResponseCaseDataBuilder state(String state) {
            this.state = state;
            return this;
        }

        public ResponseCaseDataBuilder applicationType(ApplicationType applicationType) {
            this.applicationType = applicationType;
            return this;
        }

        public ResponseCaseDataBuilder registryLocation(String registryLocation) {
            this.registryLocation = registryLocation;
            return this;
        }

        public ResponseCaseDataBuilder deceasedDateOfDeath(String deceasedDateOfDeath) {
            this.deceasedDateOfDeath = deceasedDateOfDeath;
            return this;
        }

        public ResponseCaseDataBuilder deceasedDateOfBirth(String deceasedDateOfBirth) {
            this.deceasedDateOfBirth = deceasedDateOfBirth;
            return this;
        }

        public ResponseCaseDataBuilder deceasedForenames(String deceasedForenames) {
            this.deceasedForenames = deceasedForenames;
            return this;
        }

        public ResponseCaseDataBuilder deceasedSurname(String deceasedSurname) {
            this.deceasedSurname = deceasedSurname;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorFirmName(String solsSolicitorFirmName) {
            this.solsSolicitorFirmName = solsSolicitorFirmName;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorAddress(SolsAddress solsSolicitorAddress) {
            this.solsSolicitorAddress = solsSolicitorAddress;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorEmail(String solsSolicitorEmail) {
            this.solsSolicitorEmail = solsSolicitorEmail;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorPhoneNumber(String solsSolicitorPhoneNumber) {
            this.solsSolicitorPhoneNumber = solsSolicitorPhoneNumber;
            return this;
        }

        public ResponseCaseDataBuilder solsSOTName(String solsSOTName) {
            this.solsSOTName = solsSOTName;
            return this;
        }

        public ResponseCaseDataBuilder solsSOTForenames(String solsSOTForenames) {
            this.solsSOTForenames = solsSOTForenames;
            return this;
        }

        public ResponseCaseDataBuilder solsSOTSurname(String solsSOTSurname) {
            this.solsSOTSurname = solsSOTSurname;
            return this;
        }

        public ResponseCaseDataBuilder solsSOTJobTitle(String solsSOTJobTitle) {
            this.solsSOTJobTitle = solsSOTJobTitle;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorAppReference(String solsSolicitorAppReference) {
            this.solsSolicitorAppReference = solsSolicitorAppReference;
            return this;
        }

        public ResponseCaseDataBuilder ihtFormId(String ihtFormId) {
            this.ihtFormId = ihtFormId;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorIsExec(String solsSolicitorIsExec) {
            this.solsSolicitorIsExec = solsSolicitorIsExec;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorIsMainApplicant(String solsSolicitorIsMainApplicant) {
            this.solsSolicitorIsMainApplicant = solsSolicitorIsMainApplicant;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorIsApplying(String solsSolicitorIsApplying) {
            this.solsSolicitorIsApplying = solsSolicitorIsApplying;
            return this;
        }

        public ResponseCaseDataBuilder solsSolicitorNotApplyingReason(String solsSolicitorNotApplyingReason) {
            this.solsSolicitorNotApplyingReason = solsSolicitorNotApplyingReason;
            return this;
        }

        public ResponseCaseDataBuilder solsWillType(String solsWillType) {
            this.solsWillType = solsWillType;
            return this;
        }

        public ResponseCaseDataBuilder solsApplicantRelationshipToDeceased(String solsApplicantRelationshipToDeceased) {
            this.solsApplicantRelationshipToDeceased = solsApplicantRelationshipToDeceased;
            return this;
        }

        public ResponseCaseDataBuilder solsSpouseOrCivilRenouncing(String solsSpouseOrCivilRenouncing) {
            this.solsSpouseOrCivilRenouncing = solsSpouseOrCivilRenouncing;
            return this;
        }

        public ResponseCaseDataBuilder solsAdoptedEnglandOrWales(String solsAdoptedEnglandOrWales) {
            this.solsAdoptedEnglandOrWales = solsAdoptedEnglandOrWales;
            return this;
        }

        public ResponseCaseDataBuilder solsMinorityInterest(String solsMinorityInterest) {
            this.solsMinorityInterest = solsMinorityInterest;
            return this;
        }

        public ResponseCaseDataBuilder solsApplicantSiblings(String solsApplicantSiblings) {
            this.solsApplicantSiblings = solsApplicantSiblings;
            return this;
        }

        public ResponseCaseDataBuilder solsEntitledMinority(String solsEntitledMinority) {
            this.solsEntitledMinority = solsEntitledMinority;
            return this;
        }

        public ResponseCaseDataBuilder solsDiedOrNotApplying(String solsDiedOrNotApplying) {
            this.solsDiedOrNotApplying = solsDiedOrNotApplying;
            return this;
        }

        public ResponseCaseDataBuilder solsResiduary(String solsResiduary) {
            this.solsResiduary = solsResiduary;
            return this;
        }

        public ResponseCaseDataBuilder solsResiduaryType(String solsResiduaryType) {
            this.solsResiduaryType = solsResiduaryType;
            return this;
        }

        public ResponseCaseDataBuilder solsLifeInterest(String solsLifeInterest) {
            this.solsLifeInterest = solsLifeInterest;
            return this;
        }

        public ResponseCaseDataBuilder willExists(String willExists) {
            this.willExists = willExists;
            return this;
        }

        public ResponseCaseDataBuilder willAccessOriginal(String willAccessOriginal) {
            this.willAccessOriginal = willAccessOriginal;
            return this;
        }

        public ResponseCaseDataBuilder willHasCodicils(String willHasCodicils) {
            this.willHasCodicils = willHasCodicils;
            return this;
        }

        public ResponseCaseDataBuilder willNumberOfCodicils(String willNumberOfCodicils) {
            this.willNumberOfCodicils = willNumberOfCodicils;
            return this;
        }

        public ResponseCaseDataBuilder ihtNetValue(BigDecimal ihtNetValue) {
            this.ihtNetValue = ihtNetValue;
            return this;
        }

        public ResponseCaseDataBuilder ihtGrossValue(BigDecimal ihtGrossValue) {
            this.ihtGrossValue = ihtGrossValue;
            return this;
        }

        public ResponseCaseDataBuilder deceasedDomicileInEngWales(String deceasedDomicileInEngWales) {
            this.deceasedDomicileInEngWales = deceasedDomicileInEngWales;
            return this;
        }

        public ResponseCaseDataBuilder extraCopiesOfGrant(String extraCopiesOfGrant) {
            this.extraCopiesOfGrant = extraCopiesOfGrant;
            return this;
        }

        public ResponseCaseDataBuilder outsideUKGrantCopies(String outsideUKGrantCopies) {
            this.outsideUKGrantCopies = outsideUKGrantCopies;
            return this;
        }

        public ResponseCaseDataBuilder applicationFee(String applicationFee) {
            this.applicationFee = applicationFee;
            return this;
        }

        public ResponseCaseDataBuilder feeForUkCopies(String feeForUkCopies) {
            this.feeForUkCopies = feeForUkCopies;
            return this;
        }

        public ResponseCaseDataBuilder feeForNonUkCopies(String feeForNonUkCopies) {
            this.feeForNonUkCopies = feeForNonUkCopies;
            return this;
        }

        public ResponseCaseDataBuilder totalFee(String totalFee) {
            this.totalFee = totalFee;
            return this;
        }

        public ResponseCaseDataBuilder solsPaymentMethods(String solsPaymentMethods) {
            this.solsPaymentMethods = solsPaymentMethods;
            return this;
        }

        public ResponseCaseDataBuilder solsFeeAccountNumber(String solsFeeAccountNumber) {
            this.solsFeeAccountNumber = solsFeeAccountNumber;
            return this;
        }

        public ResponseCaseDataBuilder paymentReferenceNumber(String paymentReferenceNumber) {
            this.paymentReferenceNumber = paymentReferenceNumber;
            return this;
        }

        public ResponseCaseDataBuilder solsLegalStatementDocument(DocumentLink solsLegalStatementDocument) {
            this.solsLegalStatementDocument = solsLegalStatementDocument;
            return this;
        }

        public ResponseCaseDataBuilder statementOfTruthDocument(DocumentLink statementOfTruthDocument) {
            this.statementOfTruthDocument = statementOfTruthDocument;
            return this;
        }

        public ResponseCaseDataBuilder probateDocumentsGenerated(List<CollectionMember<Document>> probateDocumentsGenerated) {
            this.probateDocumentsGenerated = probateDocumentsGenerated;
            return this;
        }

        public ResponseCaseDataBuilder probateNotificationsGenerated(List<CollectionMember<Document>> probateNotificationsGenerated) {
            this.probateNotificationsGenerated = probateNotificationsGenerated;
            return this;
        }

        public ResponseCaseDataBuilder boDocumentsUploaded(List<CollectionMember<UploadDocument>> boDocumentsUploaded) {
            this.boDocumentsUploaded = boDocumentsUploaded;
            return this;
        }

        public ResponseCaseDataBuilder caseMatches(List<CollectionMember<CaseMatch>> caseMatches) {
            this.caseMatches = caseMatches;
            return this;
        }

        public ResponseCaseDataBuilder solsSOTNeedToUpdate(String solsSOTNeedToUpdate) {
            this.solsSOTNeedToUpdate = solsSOTNeedToUpdate;
            return this;
        }

        public ResponseCaseDataBuilder solsNextStepsDocument(DocumentLink solsNextStepsDocument) {
            this.solsNextStepsDocument = solsNextStepsDocument;
            return this;
        }

        public ResponseCaseDataBuilder solsAdditionalInfo(String solsAdditionalInfo) {
            this.solsAdditionalInfo = solsAdditionalInfo;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantForenames(String primaryApplicantForenames) {
            this.primaryApplicantForenames = primaryApplicantForenames;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantSurname(String primaryApplicantSurname) {
            this.primaryApplicantSurname = primaryApplicantSurname;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantEmailAddress(String primaryApplicantEmailAddress) {
            this.primaryApplicantEmailAddress = primaryApplicantEmailAddress;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantHasAlias(String primaryApplicantHasAlias) {
            this.primaryApplicantHasAlias = primaryApplicantHasAlias;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantIsApplying(String primaryApplicantIsApplying) {
            this.primaryApplicantIsApplying = primaryApplicantIsApplying;
            return this;
        }

        public ResponseCaseDataBuilder solsPrimaryExecutorNotApplyingReason(String solsPrimaryExecutorNotApplyingReason) {
            this.solsPrimaryExecutorNotApplyingReason = solsPrimaryExecutorNotApplyingReason;
            return this;
        }

        public ResponseCaseDataBuilder otherExecutorExists(String otherExecutorExists) {
            this.otherExecutorExists = otherExecutorExists;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantAlias(String primaryApplicantAlias) {
            this.primaryApplicantAlias = primaryApplicantAlias;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantSameWillName(String primaryApplicantSameWillName) {
            this.primaryApplicantSameWillName = primaryApplicantSameWillName;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantAliasReason(String primaryApplicantAliasReason) {
            this.primaryApplicantAliasReason = primaryApplicantAliasReason;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantOtherReason(String primaryApplicantOtherReason) {
            this.primaryApplicantOtherReason = primaryApplicantOtherReason;
            return this;
        }

        public ResponseCaseDataBuilder solsExecutorAliasNames(String solsExecutorAliasNames) {
            this.solsExecutorAliasNames = solsExecutorAliasNames;
            return this;
        }

        public ResponseCaseDataBuilder additionalExecutorsApplying(List<CollectionMember<AdditionalExecutorApplying>> additionalExecutorsApplying) {
            this.additionalExecutorsApplying = additionalExecutorsApplying;
            return this;
        }

        public ResponseCaseDataBuilder additionalExecutorsNotApplying(List<CollectionMember<AdditionalExecutorNotApplying>> additionalExecutorsNotApplying) {
            this.additionalExecutorsNotApplying = additionalExecutorsNotApplying;
            return this;
        }

        public ResponseCaseDataBuilder solsAdditionalExecutorList(List<CollectionMember<AdditionalExecutor>> solsAdditionalExecutorList) {
            this.solsAdditionalExecutorList = solsAdditionalExecutorList;
            return this;
        }

        public ResponseCaseDataBuilder deceasedAddress(SolsAddress deceasedAddress) {
            this.deceasedAddress = deceasedAddress;
            return this;
        }

        public ResponseCaseDataBuilder deceasedAnyOtherNames(String deceasedAnyOtherNames) {
            this.deceasedAnyOtherNames = deceasedAnyOtherNames;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantAddress(SolsAddress primaryApplicantAddress) {
            this.primaryApplicantAddress = primaryApplicantAddress;
            return this;
        }

        public ResponseCaseDataBuilder solsDeceasedAliasNamesList(List<CollectionMember<AliasName>> solsDeceasedAliasNamesList) {
            this.solsDeceasedAliasNamesList = solsDeceasedAliasNamesList;
            return this;
        }

        public ResponseCaseDataBuilder deceasedAliasNamesList(List<CollectionMember<ProbateAliasName>> deceasedAliasNamesList) {
            this.deceasedAliasNamesList = deceasedAliasNamesList;
            return this;
        }

        public ResponseCaseDataBuilder casePrinted(String casePrinted) {
            this.casePrinted = casePrinted;
            return this;
        }

        public ResponseCaseDataBuilder boEmailDocsReceivedNotificationRequested(String boEmailDocsReceivedNotificationRequested) {
            this.boEmailDocsReceivedNotificationRequested = boEmailDocsReceivedNotificationRequested;
            return this;
        }

        public ResponseCaseDataBuilder boEmailGrantIssuedNotificationRequested(String boEmailGrantIssuedNotificationRequested) {
            this.boEmailGrantIssuedNotificationRequested = boEmailGrantIssuedNotificationRequested;
            return this;
        }

        public ResponseCaseDataBuilder boEmailDocsReceivedNotification(String boEmailDocsReceivedNotification) {
            this.boEmailDocsReceivedNotification = boEmailDocsReceivedNotification;
            return this;
        }

        public ResponseCaseDataBuilder boEmailGrantIssuedNotification(String boEmailGrantIssuedNotification) {
            this.boEmailGrantIssuedNotification = boEmailGrantIssuedNotification;
            return this;
        }

        public ResponseCaseDataBuilder boCaseStopReasonList(List<CollectionMember<StopReason>> boCaseStopReasonList) {
            this.boCaseStopReasonList = boCaseStopReasonList;
            return this;
        }

        public ResponseCaseDataBuilder boStopDetails(String boStopDetails) {
            this.boStopDetails = boStopDetails;
            return this;
        }

        public ResponseCaseDataBuilder boDeceasedTitle(String boDeceasedTitle) {
            this.boDeceasedTitle = boDeceasedTitle;
            return this;
        }

        public ResponseCaseDataBuilder boDeceasedHonours(String boDeceasedHonours) {
            this.boDeceasedHonours = boDeceasedHonours;
            return this;
        }

        public ResponseCaseDataBuilder boWillMessage(String boWillMessage) {
            this.boWillMessage = boWillMessage;
            return this;
        }

        public ResponseCaseDataBuilder boExecutorLimitation(String boExecutorLimitation) {
            this.boExecutorLimitation = boExecutorLimitation;
            return this;
        }

        public ResponseCaseDataBuilder boAdminClauseLimitation(String boAdminClauseLimitation) {
            this.boAdminClauseLimitation = boAdminClauseLimitation;
            return this;
        }

        public ResponseCaseDataBuilder boLimitationText(String boLimitationText) {
            this.boLimitationText = boLimitationText;
            return this;
        }

        public ResponseCaseDataBuilder ihtReferenceNumber(String ihtReferenceNumber) {
            this.ihtReferenceNumber = ihtReferenceNumber;
            return this;
        }

        public ResponseCaseDataBuilder ihtFormCompletedOnline(String ihtFormCompletedOnline) {
            this.ihtFormCompletedOnline = ihtFormCompletedOnline;
            return this;
        }

        public ResponseCaseDataBuilder legalStatement(LegalStatement legalStatement) {
            this.legalStatement = legalStatement;
            return this;
        }

        public ResponseCaseDataBuilder declaration(Declaration declaration) {
            this.declaration = declaration;
            return this;
        }

        public ResponseCaseDataBuilder deceasedMarriedAfterWillOrCodicilDate(String deceasedMarriedAfterWillOrCodicilDate) {
            this.deceasedMarriedAfterWillOrCodicilDate = deceasedMarriedAfterWillOrCodicilDate;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantPhoneNumber(String primaryApplicantPhoneNumber) {
            this.primaryApplicantPhoneNumber = primaryApplicantPhoneNumber;
            return this;
        }

        public ResponseCaseDataBuilder boExaminationChecklistQ2(String boExaminationChecklistQ2) {
            this.boExaminationChecklistQ2 = boExaminationChecklistQ2;
            return this;
        }

        public ResponseCaseDataBuilder boExaminationChecklistQ1(String boExaminationChecklistQ1) {
            this.boExaminationChecklistQ1 = boExaminationChecklistQ1;
            return this;
        }

        public ResponseCaseDataBuilder boExaminationChecklistRequestQA(String boExaminationChecklistRequestQA) {
            this.boExaminationChecklistRequestQA = boExaminationChecklistRequestQA;
            return this;
        }

        public ResponseCaseDataBuilder payments(List<CollectionMember<Payment>> payments) {
            this.payments = payments;
            return this;
        }

        public ResponseCaseDataBuilder applicationSubmittedDate(String applicationSubmittedDate) {
            this.applicationSubmittedDate = applicationSubmittedDate;
            return this;
        }

        public ResponseCaseDataBuilder scannedDocuments(List<CollectionMember<ScannedDocument>> scannedDocuments) {
            this.scannedDocuments = scannedDocuments;
            return this;
        }
        
        public ResponseCaseDataBuilder evidenceHandled(String evidenceHandled) {
            this.evidenceHandled = evidenceHandled;
            return this;
        }

        public ResponseCaseDataBuilder caseType(String caseType) {
            this.caseType = caseType;
            return this;
        }

        public ResponseCaseDataBuilder paperForm(String paperForm) {
            this.paperForm = paperForm;
            return this;
        }

        public ResponseCaseDataBuilder languagePreferenceWelsh(String languagePreferenceWelsh) {
            this.languagePreferenceWelsh = languagePreferenceWelsh;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantSecondPhoneNumber(String primaryApplicantSecondPhoneNumber) {
            this.primaryApplicantSecondPhoneNumber = primaryApplicantSecondPhoneNumber;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantRelationshipToDeceased(String primaryApplicantRelationshipToDeceased) {
            this.primaryApplicantRelationshipToDeceased = primaryApplicantRelationshipToDeceased;
            return this;
        }

        public ResponseCaseDataBuilder paRelationshipToDeceasedOther(String paRelationshipToDeceasedOther) {
            this.paRelationshipToDeceasedOther = paRelationshipToDeceasedOther;
            return this;
        }

        public ResponseCaseDataBuilder applicationFeePaperForm(String applicationFeePaperForm) {
            this.applicationFeePaperForm = applicationFeePaperForm;
            return this;
        }

        public ResponseCaseDataBuilder feeForCopiesPaperForm(String feeForCopiesPaperForm) {
            this.feeForCopiesPaperForm = feeForCopiesPaperForm;
            return this;
        }

        public ResponseCaseDataBuilder totalFeePaperForm(String totalFeePaperForm) {
            this.totalFeePaperForm = totalFeePaperForm;
            return this;
        }

        public ResponseCaseDataBuilder paperPaymentMethod(String paperPaymentMethod) {
            this.paperPaymentMethod = paperPaymentMethod;
            return this;
        }

        public ResponseCaseDataBuilder paymentReferenceNumberPaperform(String paymentReferenceNumberPaperform) {
            this.paymentReferenceNumberPaperform = paymentReferenceNumberPaperform;
            return this;
        }

        public ResponseCaseDataBuilder entitledToApply(String entitledToApply) {
            this.entitledToApply = entitledToApply;
            return this;
        }

        public ResponseCaseDataBuilder entitledToApplyOther(String entitledToApplyOther) {
            this.entitledToApplyOther = entitledToApplyOther;
            return this;
        }

        public ResponseCaseDataBuilder notifiedApplicants(String notifiedApplicants) {
            this.notifiedApplicants = notifiedApplicants;
            return this;
        }

        public ResponseCaseDataBuilder foreignAsset(String foreignAsset) {
            this.foreignAsset = foreignAsset;
            return this;
        }

        public ResponseCaseDataBuilder foreignAssetEstateValue(String foreignAssetEstateValue) {
            this.foreignAssetEstateValue = foreignAssetEstateValue;
            return this;
        }

        public ResponseCaseDataBuilder adopted(String adopted) {
            this.adopted = adopted;
            return this;
        }

        public ResponseCaseDataBuilder adoptiveRelatives(List<CollectionMember<AdoptedRelative>> adoptiveRelatives) {
            this.adoptiveRelatives = adoptiveRelatives;
            return this;
        }

        public ResponseCaseDataBuilder mentalCapacity(String mentalCapacity) {
            this.mentalCapacity = mentalCapacity;
            return this;
        }

        public ResponseCaseDataBuilder courtOfProtection(String courtOfProtection) {
            this.courtOfProtection = courtOfProtection;
            return this;
        }

        public ResponseCaseDataBuilder epaOrLpa(String epaOrLpa) {
            this.epaOrLpa = epaOrLpa;
            return this;
        }

        public ResponseCaseDataBuilder epaRegistered(String epaRegistered) {
            this.epaRegistered = epaRegistered;
            return this;
        }

        public ResponseCaseDataBuilder domicilityCountry(String domicilityCountry) {
            this.domicilityCountry = domicilityCountry;
            return this;
        }

        public ResponseCaseDataBuilder ukEstate(List<CollectionMember<EstateItem>> ukEstate) {
            this.ukEstate = ukEstate;
            return this;
        }

        public ResponseCaseDataBuilder domicilityIHTCert(String domicilityIHTCert) {
            this.domicilityIHTCert = domicilityIHTCert;
            return this;
        }

        public ResponseCaseDataBuilder applicationGrounds(String applicationGrounds) {
            this.applicationGrounds = applicationGrounds;
            return this;
        }

        public ResponseCaseDataBuilder willDispose(String willDispose) {
            this.willDispose = willDispose;
            return this;
        }

        public ResponseCaseDataBuilder englishWill(String englishWill) {
            this.englishWill = englishWill;
            return this;
        }

        public ResponseCaseDataBuilder appointExec(String appointExec) {
            this.appointExec = appointExec;
            return this;
        }

        public ResponseCaseDataBuilder appointExecByDuties(String appointExecByDuties) {
            this.appointExecByDuties = appointExecByDuties;
            return this;
        }

        public ResponseCaseDataBuilder appointExecNo(String appointExecNo) {
            this.appointExecNo = appointExecNo;
            return this;
        }

        public ResponseCaseDataBuilder immovableEstate(String immovableEstate) {
            this.immovableEstate = immovableEstate;
            return this;
        }

        public ResponseCaseDataBuilder willDatedBeforeApril(String willDatedBeforeApril) {
            this.willDatedBeforeApril = willDatedBeforeApril;
            return this;
        }

        public ResponseCaseDataBuilder deceasedEnterMarriageOrCP(String deceasedEnterMarriageOrCP) {
            this.deceasedEnterMarriageOrCP = deceasedEnterMarriageOrCP;
            return this;
        }

        public ResponseCaseDataBuilder deceasedMaritalStatus(String deceasedMaritalStatus) {
            this.deceasedMaritalStatus = deceasedMaritalStatus;
            return this;
        }

        public ResponseCaseDataBuilder willsOutsideOfUK(String willsOutsideOfUK) {
            this.willsOutsideOfUK = willsOutsideOfUK;
            return this;
        }

        public ResponseCaseDataBuilder courtOfDecree(String courtOfDecree) {
            this.courtOfDecree = courtOfDecree;
            return this;
        }

        public ResponseCaseDataBuilder dateOfMarriageOrCP(String dateOfMarriageOrCP) {
            this.dateOfMarriageOrCP = dateOfMarriageOrCP;
            return this;
        }

        public ResponseCaseDataBuilder dateOfDivorcedCPJudicially(String dateOfDivorcedCPJudicially) {
            this.dateOfDivorcedCPJudicially = dateOfDivorcedCPJudicially;
            return this;
        }

        public ResponseCaseDataBuilder willGiftUnderEighteen(String willGiftUnderEighteen) {
            this.willGiftUnderEighteen = willGiftUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder applyingAsAnAttorney(String applyingAsAnAttorney) {
            this.applyingAsAnAttorney = applyingAsAnAttorney;
            return this;
        }

        public ResponseCaseDataBuilder attorneyOnBehalfOfNameAndAddress(List<CollectionMember<AttorneyApplyingOnBehalfOf>> attorneyOnBehalfOfNameAndAddress) {
            this.attorneyOnBehalfOfNameAndAddress = attorneyOnBehalfOfNameAndAddress;
            return this;
        }

        public ResponseCaseDataBuilder spouseOrPartner(String spouseOrPartner) {
            this.spouseOrPartner = spouseOrPartner;
            return this;
        }

        public ResponseCaseDataBuilder childrenSurvived(String childrenSurvived) {
            this.childrenSurvived = childrenSurvived;
            return this;
        }

        public ResponseCaseDataBuilder childrenOverEighteenSurvived(String childrenOverEighteenSurvived) {
            this.childrenOverEighteenSurvived = childrenOverEighteenSurvived;
            return this;
        }

        public ResponseCaseDataBuilder childrenUnderEighteenSurvived(String childrenUnderEighteenSurvived) {
            this.childrenUnderEighteenSurvived = childrenUnderEighteenSurvived;
            return this;
        }

        public ResponseCaseDataBuilder childrenDied(String childrenDied) {
            this.childrenDied = childrenDied;
            return this;
        }

        public ResponseCaseDataBuilder childrenDiedOverEighteen(String childrenDiedOverEighteen) {
            this.childrenDiedOverEighteen = childrenDiedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder childrenDiedUnderEighteen(String childrenDiedUnderEighteen) {
            this.childrenDiedUnderEighteen = childrenDiedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder parentsExistSurvived(String parentsExistSurvived) {
            this.parentsExistSurvived = parentsExistSurvived;
            return this;
        }

        public ResponseCaseDataBuilder parentsExistOverEighteenSurvived(String parentsExistOverEighteenSurvived) {
            this.parentsExistOverEighteenSurvived = parentsExistOverEighteenSurvived;
            return this;
        }

        public ResponseCaseDataBuilder parentsExistUnderEighteenSurvived(String parentsExistUnderEighteenSurvived) {
            this.parentsExistUnderEighteenSurvived = parentsExistUnderEighteenSurvived;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodNeicesAndNephews(String wholeBloodNeicesAndNephews) {
            this.wholeBloodNeicesAndNephews = wholeBloodNeicesAndNephews;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodNeicesAndNephewsOverEighteen(String wholeBloodNeicesAndNephewsOverEighteen) {
            this.wholeBloodNeicesAndNephewsOverEighteen = wholeBloodNeicesAndNephewsOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodNeicesAndNephewsUnderEighteen(String wholeBloodNeicesAndNephewsUnderEighteen) {
            this.wholeBloodNeicesAndNephewsUnderEighteen = wholeBloodNeicesAndNephewsUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodSiblingsDied(String wholeBloodSiblingsDied) {
            this.wholeBloodSiblingsDied = wholeBloodSiblingsDied;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodSiblingsDiedOverEighteen(String wholeBloodSiblingsDiedOverEighteen) {
            this.wholeBloodSiblingsDiedOverEighteen = wholeBloodSiblingsDiedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodSiblingsDiedUnderEighteen(String wholeBloodSiblingsDiedUnderEighteen) {
            this.wholeBloodSiblingsDiedUnderEighteen = wholeBloodSiblingsDiedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodSiblingsSurvived(String wholeBloodSiblingsSurvived) {
            this.wholeBloodSiblingsSurvived = wholeBloodSiblingsSurvived;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodSiblingsSurvivedOverEighteen(String wholeBloodSiblingsSurvivedOverEighteen) {
            this.wholeBloodSiblingsSurvivedOverEighteen = wholeBloodSiblingsSurvivedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodSiblingsSurvivedUnderEighteen(String wholeBloodSiblingsSurvivedUnderEighteen) {
            this.wholeBloodSiblingsSurvivedUnderEighteen = wholeBloodSiblingsSurvivedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodSiblingsDied(String halfBloodSiblingsDied) {
            this.halfBloodSiblingsDied = halfBloodSiblingsDied;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodSiblingsDiedOverEighteen(String halfBloodSiblingsDiedOverEighteen) {
            this.halfBloodSiblingsDiedOverEighteen = halfBloodSiblingsDiedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodSiblingsDiedUnderEighteen(String halfBloodSiblingsDiedUnderEighteen) {
            this.halfBloodSiblingsDiedUnderEighteen = halfBloodSiblingsDiedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodSiblingsSurvived(String halfBloodSiblingsSurvived) {
            this.halfBloodSiblingsSurvived = halfBloodSiblingsSurvived;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodSiblingsSurvivedOverEighteen(String halfBloodSiblingsSurvivedOverEighteen) {
            this.halfBloodSiblingsSurvivedOverEighteen = halfBloodSiblingsSurvivedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodSiblingsSurvivedUnderEighteen(String halfBloodSiblingsSurvivedUnderEighteen) {
            this.halfBloodSiblingsSurvivedUnderEighteen = halfBloodSiblingsSurvivedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder grandparentsDied(String grandparentsDied) {
            this.grandparentsDied = grandparentsDied;
            return this;
        }

        public ResponseCaseDataBuilder grandparentsDiedOverEighteen(String grandparentsDiedOverEighteen) {
            this.grandparentsDiedOverEighteen = grandparentsDiedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder grandparentsDiedUnderEighteen(String grandparentsDiedUnderEighteen) {
            this.grandparentsDiedUnderEighteen = grandparentsDiedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodNeicesAndNephews(String halfBloodNeicesAndNephews) {
            this.halfBloodNeicesAndNephews = halfBloodNeicesAndNephews;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodNeicesAndNephewsOverEighteen(String halfBloodNeicesAndNephewsOverEighteen) {
            this.halfBloodNeicesAndNephewsOverEighteen = halfBloodNeicesAndNephewsOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodNeicesAndNephewsUnderEighteen(String halfBloodNeicesAndNephewsUnderEighteen) {
            this.halfBloodNeicesAndNephewsUnderEighteen = halfBloodNeicesAndNephewsUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder grandChildrenSurvived(String grandChildrenSurvived) {
            this.grandChildrenSurvived = grandChildrenSurvived;
            return this;
        }

        public ResponseCaseDataBuilder grandChildrenSurvivedOverEighteen(String grandChildrenSurvivedOverEighteen) {
            this.grandChildrenSurvivedOverEighteen = grandChildrenSurvivedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder grandChildrenSurvivedUnderEighteen(String grandChildrenSurvivedUnderEighteen) {
            this.grandChildrenSurvivedUnderEighteen = grandChildrenSurvivedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodUnclesAndAuntsDied(String wholeBloodUnclesAndAuntsDied) {
            this.wholeBloodUnclesAndAuntsDied = wholeBloodUnclesAndAuntsDied;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodUnclesAndAuntsDiedOverEighteen(String wholeBloodUnclesAndAuntsDiedOverEighteen) {
            this.wholeBloodUnclesAndAuntsDiedOverEighteen = wholeBloodUnclesAndAuntsDiedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodUnclesAndAuntsDiedUnderEighteen(String wholeBloodUnclesAndAuntsDiedUnderEighteen) {
            this.wholeBloodUnclesAndAuntsDiedUnderEighteen = wholeBloodUnclesAndAuntsDiedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodUnclesAndAuntsSurvived(String wholeBloodUnclesAndAuntsSurvived) {
            this.wholeBloodUnclesAndAuntsSurvived = wholeBloodUnclesAndAuntsSurvived;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodUnclesAndAuntsSurvivedOverEighteen(String wholeBloodUnclesAndAuntsSurvivedOverEighteen) {
            this.wholeBloodUnclesAndAuntsSurvivedOverEighteen = wholeBloodUnclesAndAuntsSurvivedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodUnclesAndAuntsSurvivedUnderEighteen(String wholeBloodUnclesAndAuntsSurvivedUnderEighteen) {
            this.wholeBloodUnclesAndAuntsSurvivedUnderEighteen = wholeBloodUnclesAndAuntsSurvivedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodUnclesAndAuntsSurvived(String halfBloodUnclesAndAuntsSurvived) {
            this.halfBloodUnclesAndAuntsSurvived = halfBloodUnclesAndAuntsSurvived;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodUnclesAndAuntsSurvivedOverEighteen(String halfBloodUnclesAndAuntsSurvivedOverEighteen) {
            this.halfBloodUnclesAndAuntsSurvivedOverEighteen = halfBloodUnclesAndAuntsSurvivedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodUnclesAndAuntsSurvivedUnderEighteen(String halfBloodUnclesAndAuntsSurvivedUnderEighteen) {
            this.halfBloodUnclesAndAuntsSurvivedUnderEighteen = halfBloodUnclesAndAuntsSurvivedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodUnclesAndAuntsDied(String halfBloodUnclesAndAuntsDied) {
            this.halfBloodUnclesAndAuntsDied = halfBloodUnclesAndAuntsDied;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodUnclesAndAuntsDiedOverEighteen(String halfBloodUnclesAndAuntsDiedOverEighteen) {
            this.halfBloodUnclesAndAuntsDiedOverEighteen = halfBloodUnclesAndAuntsDiedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodUnclesAndAuntsDiedUnderEighteen(String halfBloodUnclesAndAuntsDiedUnderEighteen) {
            this.halfBloodUnclesAndAuntsDiedUnderEighteen = halfBloodUnclesAndAuntsDiedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodCousinsSurvived(String halfBloodCousinsSurvived) {
            this.halfBloodCousinsSurvived = halfBloodCousinsSurvived;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodCousinsSurvivedOverEighteen(String halfBloodCousinsSurvivedOverEighteen) {
            this.halfBloodCousinsSurvivedOverEighteen = halfBloodCousinsSurvivedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder halfBloodCousinsSurvivedUnderEighteen(String halfBloodCousinsSurvivedUnderEighteen) {
            this.halfBloodCousinsSurvivedUnderEighteen = halfBloodCousinsSurvivedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodCousinsSurvived(String wholeBloodCousinsSurvived) {
            this.wholeBloodCousinsSurvived = wholeBloodCousinsSurvived;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodCousinsSurvivedOverEighteen(String wholeBloodCousinsSurvivedOverEighteen) {
            this.wholeBloodCousinsSurvivedOverEighteen = wholeBloodCousinsSurvivedOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder wholeBloodCousinsSurvivedUnderEighteen(String wholeBloodCousinsSurvivedUnderEighteen) {
            this.wholeBloodCousinsSurvivedUnderEighteen = wholeBloodCousinsSurvivedUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder boSendToBulkPrint(String boSendToBulkPrint) {
            this.boSendToBulkPrint = boSendToBulkPrint;
            return this;
        }

        public ResponseCaseDataBuilder boSendToBulkPrintRequested(String boSendToBulkPrintRequested) {
            this.boSendToBulkPrintRequested = boSendToBulkPrintRequested;
            return this;
        }

        public ResponseCaseDataBuilder grantIssuedDate(String grantIssuedDate) {
            this.grantIssuedDate = grantIssuedDate;
            return this;
        }

        public ResponseCaseDataBuilder dateOfDeathType(String dateOfDeathType) {
            this.dateOfDeathType = dateOfDeathType;
            return this;
        }

        public ResponseCaseDataBuilder legacySearchResultRows(List<CollectionMember<CaseMatch>> legacySearchResultRows) {
            this.legacySearchResultRows = legacySearchResultRows;
            return this;
        }

        public ResponseCaseDataBuilder recordId(String recordId) {
            this.recordId = recordId;
            return this;
        }

        public ResponseCaseDataBuilder legacyType(String legacyType) {
            this.legacyType = legacyType;
            return this;
        }

        public ResponseCaseDataBuilder legacyCaseViewUrl(String legacyCaseViewUrl) {
            this.legacyCaseViewUrl = legacyCaseViewUrl;
            return this;
        }

        public ResponseCaseDataBuilder bulkPrintSendLetterId(String bulkPrintSendLetterId) {
            this.bulkPrintSendLetterId = bulkPrintSendLetterId;
            return this;
        }

        public ResponseCaseDataBuilder bulkPrintPdfSize(String bulkPrintPdfSize) {
            this.bulkPrintPdfSize = bulkPrintPdfSize;
            return this;
        }

        public ResponseCaseDataBuilder bulkPrintId(List<CollectionMember<BulkPrint>> bulkPrintId) {
            this.bulkPrintId = bulkPrintId;
            return this;
        }

        public ResponseCaseDataBuilder boCaveatStopNotificationRequested(String boCaveatStopNotificationRequested) {
            this.boCaveatStopNotificationRequested = boCaveatStopNotificationRequested;
            return this;
        }

        public ResponseCaseDataBuilder boCaveatStopNotification(String boCaveatStopNotification) {
            this.boCaveatStopNotification = boCaveatStopNotification;
            return this;
        }

        public ResponseCaseDataBuilder boCaseStopCaveatId(String boCaseStopCaveatId) {
            this.boCaseStopCaveatId = boCaseStopCaveatId;
            return this;
        }

        public ResponseCaseDataBuilder boCaveatStopEmailNotificationRequested(String boCaveatStopEmailNotificationRequested) {
            this.boCaveatStopEmailNotificationRequested = boCaveatStopEmailNotificationRequested;
            return this;
        }

        public ResponseCaseDataBuilder boCaveatStopEmailNotification(String boCaveatStopEmailNotification) {
            this.boCaveatStopEmailNotification = boCaveatStopEmailNotification;
            return this;
        }

        public ResponseCaseDataBuilder boCaveatStopSendToBulkPrintRequested(String boCaveatStopSendToBulkPrintRequested) {
            this.boCaveatStopSendToBulkPrintRequested = boCaveatStopSendToBulkPrintRequested;
            return this;
        }

        public ResponseCaseDataBuilder boCaveatStopSendToBulkPrint(String boCaveatStopSendToBulkPrint) {
            this.boCaveatStopSendToBulkPrint = boCaveatStopSendToBulkPrint;
            return this;
        }

        public ResponseCaseDataBuilder boEmailGrantReissuedNotificationRequested(String boEmailGrantReissuedNotificationRequested) {
            this.boEmailGrantReissuedNotificationRequested = boEmailGrantReissuedNotificationRequested;
            return this;
        }

        public ResponseCaseDataBuilder boEmailGrantReissuedNotification(String boEmailGrantReissuedNotification) {
            this.boEmailGrantReissuedNotification = boEmailGrantReissuedNotification;
            return this;
        }

        public ResponseCaseDataBuilder boGrantReissueSendToBulkPrint(String boGrantReissueSendToBulkPrint) {
            this.boGrantReissueSendToBulkPrint = boGrantReissueSendToBulkPrint;
            return this;
        }

        public ResponseCaseDataBuilder boGrantReissueSendToBulkPrintRequested(String boGrantReissueSendToBulkPrintRequested) {
            this.boGrantReissueSendToBulkPrintRequested = boGrantReissueSendToBulkPrintRequested;
            return this;
        }

        public ResponseCaseDataBuilder orderNeeded(String orderNeeded) {
            this.orderNeeded = orderNeeded;
            return this;
        }

        public ResponseCaseDataBuilder reissueReason(List<CollectionMember<Reissue>> reissueReason) {
            this.reissueReason = reissueReason;
            return this;
        }

        public ResponseCaseDataBuilder reissueDate(String reissueDate) {
            this.reissueDate = reissueDate;
            return this;
        }

        public ResponseCaseDataBuilder reissueReasonNotation(String reissueReasonNotation) {
            this.reissueReasonNotation = reissueReasonNotation;
            return this;
        }

        public ResponseCaseDataBuilder latestGrantReissueDate(String latestGrantReissueDate) {
            this.latestGrantReissueDate = latestGrantReissueDate;
            return this;
        }

        public ResponseCaseDataBuilder deceasedDivorcedInEnglandOrWales(String deceasedDivorcedInEnglandOrWales) {
            this.deceasedDivorcedInEnglandOrWales = deceasedDivorcedInEnglandOrWales;
            return this;
        }

        public ResponseCaseDataBuilder primaryApplicantAdoptionInEnglandOrWales(String primaryApplicantAdoptionInEnglandOrWales) {
            this.primaryApplicantAdoptionInEnglandOrWales = primaryApplicantAdoptionInEnglandOrWales;
            return this;
        }

        public ResponseCaseDataBuilder deceasedSpouseNotApplyingReason(String deceasedSpouseNotApplyingReason) {
            this.deceasedSpouseNotApplyingReason = deceasedSpouseNotApplyingReason;
            return this;
        }

        public ResponseCaseDataBuilder deceasedOtherChildren(String deceasedOtherChildren) {
            this.deceasedOtherChildren = deceasedOtherChildren;
            return this;
        }

        public ResponseCaseDataBuilder allDeceasedChildrenOverEighteen(String allDeceasedChildrenOverEighteen) {
            this.allDeceasedChildrenOverEighteen = allDeceasedChildrenOverEighteen;
            return this;
        }

        public ResponseCaseDataBuilder anyDeceasedChildrenDieBeforeDeceased(String anyDeceasedChildrenDieBeforeDeceased) {
            this.anyDeceasedChildrenDieBeforeDeceased = anyDeceasedChildrenDieBeforeDeceased;
            return this;
        }

        public ResponseCaseDataBuilder anyDeceasedGrandChildrenUnderEighteen(String anyDeceasedGrandChildrenUnderEighteen) {
            this.anyDeceasedGrandChildrenUnderEighteen = anyDeceasedGrandChildrenUnderEighteen;
            return this;
        }

        public ResponseCaseDataBuilder deceasedAnyChildren(String deceasedAnyChildren) {
            this.deceasedAnyChildren = deceasedAnyChildren;
            return this;
        }

        public ResponseCaseDataBuilder deceasedHasAssetsOutsideUK(String deceasedHasAssetsOutsideUK) {
            this.deceasedHasAssetsOutsideUK = deceasedHasAssetsOutsideUK;
            return this;
        }

        public ResponseCaseDataBuilder solicitorIsMainApplicant(String solicitorIsMainApplicant) {
            this.solicitorIsMainApplicant = solicitorIsMainApplicant;
            return this;
        }

        public ResponseCaseDataBuilder boStopDetailsDeclarationParagraph(String boStopDetailsDeclarationParagraph) {
            this.boStopDetailsDeclarationParagraph = boStopDetailsDeclarationParagraph;
            return this;
        }

        public ResponseCaseDataBuilder boEmailRequestInfoNotificationRequested(String boEmailRequestInfoNotificationRequested) {
            this.boEmailRequestInfoNotificationRequested = boEmailRequestInfoNotificationRequested;
            return this;
        }

        public ResponseCaseDataBuilder boEmailRequestInfoNotification(String boEmailRequestInfoNotification) {
            this.boEmailRequestInfoNotification = boEmailRequestInfoNotification;
            return this;
        }

        public ResponseCaseDataBuilder boRequestInfoSendToBulkPrint(String boRequestInfoSendToBulkPrint) {
            this.boRequestInfoSendToBulkPrint = boRequestInfoSendToBulkPrint;
            return this;
        }

        public ResponseCaseDataBuilder boRequestInfoSendToBulkPrintRequested(String boRequestInfoSendToBulkPrintRequested) {
            this.boRequestInfoSendToBulkPrintRequested = boRequestInfoSendToBulkPrintRequested;
            return this;
        }

        public ResponseCaseDataBuilder boAssembleLetterSendToBulkPrint(String boAssembleLetterSendToBulkPrint) {
            this.boAssembleLetterSendToBulkPrint = boAssembleLetterSendToBulkPrint;
            return this;
        }

        public ResponseCaseDataBuilder boAssembleLetterSendToBulkPrintRequested(String boAssembleLetterSendToBulkPrintRequested) {
            this.boAssembleLetterSendToBulkPrintRequested = boAssembleLetterSendToBulkPrintRequested;
            return this;
        }

        public ResponseCaseDataBuilder executorsApplyingNotifications(List<CollectionMember<ExecutorsApplyingNotification>> executorsApplyingNotifications) {
            this.executorsApplyingNotifications = executorsApplyingNotifications;
            return this;
        }

        public ResponseCaseDataBuilder probateSotDocumentsGenerated(List<CollectionMember<Document>> probateSotDocumentsGenerated) {
            this.probateSotDocumentsGenerated = probateSotDocumentsGenerated;
            return this;
        }

        public ResponseCaseDataBuilder categories(Categories categories) {
            this.categories = categories;
            return this;
        }

        public ResponseCaseDataBuilder previewLink(DocumentLink previewLink) {
            this.previewLink = previewLink;
            return this;
        }

        public ResponseCaseDataBuilder paragraphDetails(List<CollectionMember<ParagraphDetail>> paragraphDetails) {
            this.paragraphDetails = paragraphDetails;
            return this;
        }

        public ResponseCaseDataBuilder bulkScanCaseReference(String bulkScanCaseReference) {
            this.bulkScanCaseReference = bulkScanCaseReference;
            return this;
        }

        public ResponseCaseDataBuilder grantDelayedNotificationDate(String grantDelayedNotificationDate) {
            this.grantDelayedNotificationDate = grantDelayedNotificationDate;
            return this;
        }

        public ResponseCaseDataBuilder grantStoppedDate(String grantStoppedDate) {
            this.grantStoppedDate = grantStoppedDate;
            return this;
        }

        public ResponseCaseDataBuilder grantDelayedNotificationIdentified(String grantDelayedNotificationIdentified) {
            this.grantDelayedNotificationIdentified = grantDelayedNotificationIdentified;
            return this;
        }

        public ResponseCaseDataBuilder grantDelayedNotificationSent(String grantDelayedNotificationSent) {
            this.grantDelayedNotificationSent = grantDelayedNotificationSent;
            return this;
        }

        public ResponseCaseDataBuilder grantAwaitingDocumentationNotificationDate(String grantAwaitingDocumentationNotificationDate) {
            this.grantAwaitingDocumentationNotificationDate = grantAwaitingDocumentationNotificationDate;
            return this;
        }

        public ResponseCaseDataBuilder grantAwaitingDocumentatioNotificationSent(String grantAwaitingDocumentatioNotificationSent) {
            this.grantAwaitingDocumentatioNotificationSent = grantAwaitingDocumentatioNotificationSent;
            return this;
        }

        public ResponseCaseDataBuilder pcqId(String pcqId) {
            this.pcqId = pcqId;
            return this;
        }

        public ResponseCaseDataBuilder reprintDocument(DynamicList reprintDocument) {
            this.reprintDocument = reprintDocument;
            return this;
        }

        public ResponseCaseDataBuilder reprintNumberOfCopies(String reprintNumberOfCopies) {
            this.reprintNumberOfCopies = reprintNumberOfCopies;
            return this;
        }

        public ResponseCaseDataBuilder solsAmendLegalStatmentSelect(DynamicList solsAmendLegalStatmentSelect) {
            this.solsAmendLegalStatmentSelect = solsAmendLegalStatmentSelect;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder ihtNetValueField(String ihtNetValueField) {
            this.ihtNetValueField = ihtNetValueField;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder ihtGrossValueField(String ihtGrossValueField) {
            this.ihtGrossValueField = ihtGrossValueField;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder deceasedDiedEngOrWales(String deceasedDiedEngOrWales) {
            this.deceasedDiedEngOrWales = deceasedDiedEngOrWales;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder deceasedDeathCertificate(String deceasedDeathCertificate) {
            this.deceasedDeathCertificate = deceasedDeathCertificate;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder deceasedForeignDeathCertInEnglish(String deceasedForeignDeathCertInEnglish) {
            this.deceasedForeignDeathCertInEnglish = deceasedForeignDeathCertInEnglish;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder deceasedForeignDeathCertTranslation(String deceasedForeignDeathCertTranslation) {
            this.deceasedForeignDeathCertTranslation = deceasedForeignDeathCertTranslation;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder numberOfExecutors(Long numberOfExecutors) {
            this.numberOfExecutors = numberOfExecutors;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder numberOfApplicants(Long numberOfApplicants) {
            this.numberOfApplicants = numberOfApplicants;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder legalDeclarationJson(String legalDeclarationJson) {
            this.legalDeclarationJson = legalDeclarationJson;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder checkAnswersSummaryJson(String checkAnswersSummaryJson) {
            this.checkAnswersSummaryJson = checkAnswersSummaryJson;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder registryAddress(String registryAddress) {
            this.registryAddress = registryAddress;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder registryEmailAddress(String registryEmailAddress) {
            this.registryEmailAddress = registryEmailAddress;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder registrySequenceNumber(String registrySequenceNumber) {
            this.registrySequenceNumber = registrySequenceNumber;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder dispenseWithNotice(String dispenseWithNotice) {
            this.dispenseWithNotice = dispenseWithNotice;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder titleAndClearingType(String titleAndClearingType) {
            this.titleAndClearingType = titleAndClearingType;
            return this;
        }

        @Override
        public ResponseCaseDataBuilder declarationCheckbox(String declarationCheckbox) {
            this.declarationCheckbox = declarationCheckbox;
            return this;
        }

        public ResponseCaseData build() {
            ResponseCaseData responseCaseData = new ResponseCaseData(state, applicationType, registryLocation, deceasedDateOfDeath, deceasedDateOfBirth, deceasedForenames, deceasedSurname, solsSolicitorFirmName, solsSolicitorAddress, solsSolicitorEmail, solsSolicitorPhoneNumber, solsSOTName,
                solsSOTForenames, solsSOTSurname, solsSOTJobTitle, solsSolicitorAppReference, ihtFormId, solsSolicitorIsExec, solsSolicitorIsMainApplicant, solsSolicitorIsApplying, solsSolicitorNotApplyingReason, solsWillType, solsApplicantRelationshipToDeceased, solsSpouseOrCivilRenouncing,
                solsAdoptedEnglandOrWales, solsMinorityInterest, solsApplicantSiblings, solsEntitledMinority, solsDiedOrNotApplying, solsResiduary, solsResiduaryType, solsLifeInterest, willExists, willAccessOriginal, willHasCodicils, willNumberOfCodicils, ihtNetValue, ihtGrossValue,
                deceasedDomicileInEngWales, extraCopiesOfGrant, outsideUKGrantCopies, applicationFee, feeForUkCopies, feeForNonUkCopies, totalFee, solsPaymentMethods, solsFeeAccountNumber, paymentReferenceNumber, solsLegalStatementDocument, statementOfTruthDocument, probateDocumentsGenerated,
                probateNotificationsGenerated, boDocumentsUploaded, caseMatches, solsSOTNeedToUpdate, solsNextStepsDocument, solsAdditionalInfo, primaryApplicantForenames, primaryApplicantSurname, primaryApplicantEmailAddress, primaryApplicantHasAlias, primaryApplicantIsApplying, solsPrimaryExecutorNotApplyingReason, otherExecutorExists, primaryApplicantAlias, primaryApplicantSameWillName, primaryApplicantAliasReason, primaryApplicantOtherReason, solsExecutorAliasNames, additionalExecutorsApplying, additionalExecutorsNotApplying, solsAdditionalExecutorList, deceasedAddress, deceasedAnyOtherNames, primaryApplicantAddress, solsDeceasedAliasNamesList, deceasedAliasNamesList, casePrinted, boEmailDocsReceivedNotificationRequested, boEmailGrantIssuedNotificationRequested, boEmailDocsReceivedNotification, boEmailGrantIssuedNotification, boCaseStopReasonList, boStopDetails, boDeceasedTitle, boDeceasedHonours, boWillMessage, boExecutorLimitation, boAdminClauseLimitation, boLimitationText, ihtReferenceNumber, ihtFormCompletedOnline, legalStatement, declaration, deceasedMarriedAfterWillOrCodicilDate, primaryApplicantPhoneNumber, boExaminationChecklistQ2, boExaminationChecklistQ1, boExaminationChecklistRequestQA, payments, applicationSubmittedDate, scannedDocuments, evidenceHandled, caseType, paperForm, languagePreferenceWelsh, primaryApplicantSecondPhoneNumber, primaryApplicantRelationshipToDeceased, paRelationshipToDeceasedOther, applicationFeePaperForm, feeForCopiesPaperForm, totalFeePaperForm, paperPaymentMethod, paymentReferenceNumberPaperform, entitledToApply, entitledToApplyOther, notifiedApplicants, foreignAsset, foreignAssetEstateValue, adopted, adoptiveRelatives, mentalCapacity, courtOfProtection, epaOrLpa, epaRegistered, domicilityCountry, ukEstate, domicilityIHTCert, applicationGrounds, willDispose, englishWill, appointExec, appointExecByDuties, appointExecNo, immovableEstate, willDatedBeforeApril, deceasedEnterMarriageOrCP, deceasedMaritalStatus, willsOutsideOfUK, courtOfDecree, dateOfMarriageOrCP, dateOfDivorcedCPJudicially, willGiftUnderEighteen, applyingAsAnAttorney, attorneyOnBehalfOfNameAndAddress, spouseOrPartner, childrenSurvived, childrenOverEighteenSurvived, childrenUnderEighteenSurvived, childrenDied, childrenDiedOverEighteen, childrenDiedUnderEighteen, parentsExistSurvived, parentsExistOverEighteenSurvived, parentsExistUnderEighteenSurvived, wholeBloodNeicesAndNephews, wholeBloodNeicesAndNephewsOverEighteen, wholeBloodNeicesAndNephewsUnderEighteen, wholeBloodSiblingsDied, wholeBloodSiblingsDiedOverEighteen, wholeBloodSiblingsDiedUnderEighteen, wholeBloodSiblingsSurvived, wholeBloodSiblingsSurvivedOverEighteen, wholeBloodSiblingsSurvivedUnderEighteen, halfBloodSiblingsDied, halfBloodSiblingsDiedOverEighteen, halfBloodSiblingsDiedUnderEighteen, halfBloodSiblingsSurvived, halfBloodSiblingsSurvivedOverEighteen, halfBloodSiblingsSurvivedUnderEighteen, grandparentsDied, grandparentsDiedOverEighteen, grandparentsDiedUnderEighteen, halfBloodNeicesAndNephews, halfBloodNeicesAndNephewsOverEighteen, halfBloodNeicesAndNephewsUnderEighteen, grandChildrenSurvived, grandChildrenSurvivedOverEighteen, grandChildrenSurvivedUnderEighteen, wholeBloodUnclesAndAuntsDied, wholeBloodUnclesAndAuntsDiedOverEighteen, wholeBloodUnclesAndAuntsDiedUnderEighteen, wholeBloodUnclesAndAuntsSurvived, wholeBloodUnclesAndAuntsSurvivedOverEighteen, wholeBloodUnclesAndAuntsSurvivedUnderEighteen, halfBloodUnclesAndAuntsSurvived, halfBloodUnclesAndAuntsSurvivedOverEighteen, halfBloodUnclesAndAuntsSurvivedUnderEighteen, halfBloodUnclesAndAuntsDied, halfBloodUnclesAndAuntsDiedOverEighteen, halfBloodUnclesAndAuntsDiedUnderEighteen, halfBloodCousinsSurvived, halfBloodCousinsSurvivedOverEighteen, halfBloodCousinsSurvivedUnderEighteen, wholeBloodCousinsSurvived, wholeBloodCousinsSurvivedOverEighteen, wholeBloodCousinsSurvivedUnderEighteen, boSendToBulkPrint, boSendToBulkPrintRequested, grantIssuedDate, dateOfDeathType, legacySearchResultRows, recordId, legacyType, legacyCaseViewUrl, bulkPrintSendLetterId, bulkPrintPdfSize, bulkPrintId, boCaveatStopNotificationRequested, boCaveatStopNotification, boCaseStopCaveatId, boCaveatStopEmailNotificationRequested, boCaveatStopEmailNotification, boCaveatStopSendToBulkPrintRequested, boCaveatStopSendToBulkPrint, boEmailGrantReissuedNotificationRequested, boEmailGrantReissuedNotification, boGrantReissueSendToBulkPrint, boGrantReissueSendToBulkPrintRequested, orderNeeded, reissueReason, reissueDate, reissueReasonNotation, latestGrantReissueDate, deceasedDivorcedInEnglandOrWales, primaryApplicantAdoptionInEnglandOrWales, deceasedSpouseNotApplyingReason, deceasedOtherChildren, allDeceasedChildrenOverEighteen, anyDeceasedChildrenDieBeforeDeceased, anyDeceasedGrandChildrenUnderEighteen, deceasedAnyChildren, deceasedHasAssetsOutsideUK, solicitorIsMainApplicant, boStopDetailsDeclarationParagraph, boEmailRequestInfoNotificationRequested, boEmailRequestInfoNotification, boRequestInfoSendToBulkPrint, boRequestInfoSendToBulkPrintRequested, boAssembleLetterSendToBulkPrint, boAssembleLetterSendToBulkPrintRequested, executorsApplyingNotifications, probateSotDocumentsGenerated, categories, previewLink, paragraphDetails, bulkScanCaseReference, grantDelayedNotificationDate, grantStoppedDate, grantDelayedNotificationIdentified, grantDelayedNotificationSent, grantAwaitingDocumentationNotificationDate, grantAwaitingDocumentatioNotificationSent, pcqId);
            ResponseCaseDataParent responseCaseDataParent = new ResponseCaseDataParent(reprintDocument, reprintNumberOfCopies, solsAmendLegalStatmentSelect, declarationCheckbox, ihtGrossValueField, ihtNetValueField, deceasedDiedEngOrWales, deceasedDeathCertificate, deceasedForeignDeathCertInEnglish, deceasedForeignDeathCertTranslation, numberOfExecutors, numberOfApplicants, legalDeclarationJson,
                checkAnswersSummaryJson, registryAddress,
                registryEmailAddress, registrySequenceNumber, dispenseWithNotice, titleAndClearingType);
            
            responseCaseData.reprintDocument = responseCaseDataParent.reprintDocument;
            responseCaseData.reprintNumberOfCopies = responseCaseDataParent.reprintNumberOfCopies;
            responseCaseData.solsAmendLegalStatmentSelect = responseCaseDataParent.solsAmendLegalStatmentSelect;
            responseCaseData.declarationCheckbox = responseCaseDataParent.declarationCheckbox;
            responseCaseData.ihtGrossValueField = responseCaseDataParent.ihtGrossValueField;
            responseCaseData.ihtNetValueField = responseCaseDataParent.ihtNetValueField;
            responseCaseData.deceasedDiedEngOrWales = responseCaseDataParent.deceasedDiedEngOrWales;
            responseCaseData.deceasedDeathCertificate = responseCaseDataParent.deceasedDeathCertificate;
            responseCaseData.deceasedForeignDeathCertInEnglish = responseCaseDataParent.deceasedForeignDeathCertInEnglish;
            responseCaseData.deceasedForeignDeathCertTranslation = responseCaseDataParent.deceasedForeignDeathCertTranslation;
            responseCaseData.numberOfExecutors = responseCaseDataParent.numberOfExecutors;
            responseCaseData.numberOfApplicants = responseCaseDataParent.numberOfApplicants;
            responseCaseData.legalDeclarationJson = responseCaseDataParent.legalDeclarationJson;
            responseCaseData.checkAnswersSummaryJson = responseCaseDataParent.checkAnswersSummaryJson;
            responseCaseData.registryAddress = responseCaseDataParent.registryAddress;
            responseCaseData.registryEmailAddress = responseCaseDataParent.registryEmailAddress;
            responseCaseData.registrySequenceNumber = responseCaseDataParent.registrySequenceNumber;
            responseCaseData.dispenseWithNotice = responseCaseDataParent.dispenseWithNotice;
            responseCaseData.titleAndClearingType = responseCaseDataParent.titleAndClearingType;
            return  responseCaseData;
        }
    }
}
