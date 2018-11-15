package uk.gov.hmcts.probate.model.ccd.raw.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdoptedRelatives;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AttorneyApplyingOnBehalf;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Declaration;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.EstateItems;
import uk.gov.hmcts.probate.model.ccd.raw.LegalStatement;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocuments;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCaseData {

    private final String state;
    private final ApplicationType applicationType;
    private final String registryLocation;

    private final String deceasedDateOfDeath;
    private final String deceasedDateOfBirth;
    private final String deceasedForenames;
    private final String deceasedSurname;
    private final String solsSolicitorFirmName;
    private final String solsSolicitorFirmPostcode;
    private final String solsSolicitorEmail;
    private final String solsSolicitorPhoneNumber;
    private final String solsSOTName;
    private final String solsSOTJobTitle;
    private final String solsSolicitorAppReference;
    private final String ihtFormId;
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
    private final List<CollectionMember<Document>> probateDocumentsGenerated;
    private final List<CollectionMember<Document>> probateNotificationsGenerated;
    private final List<CollectionMember<UploadDocument>> boDocumentsUploaded;
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
    //Todo remove PA specific attr
    private final String primaryApplicantPhoneNumber;

    private final String boExaminationChecklistQ2;
    private final String boExaminationChecklistQ1;
    private final String boExaminationChecklistRequestQA;

    private final List<CollectionMember<Payment>> payments;

    private final String applicationSubmittedDate;
    private final List<CollectionMember<ScannedDocuments>> scannedDocuments;
    private final String evidenceHandled;


    private final String caseType;
    private final String paperForm;

    //paper form case creator fields
    private final String primaryApplicantSecondaryPhoneNumber;
    private final String primaryApplicantRelationshipToDeceased;
    private final String paRelationshipToDeceasedOther;
    private final String deceasedMartialStatus;
    private final String willDatedBeforeApril;
    private final String deceasedEnterMarriageOrCP;
    private final String dateOfMarriageOrCP;
    private final String dateOfDivorcedCPJudicially;
    private final String willsOutsideOfUK;
    private final String courtOfDecree;
    private final String willGiftUnderEighteen;
    private final String applyingAsAnAttorney;
    private final List<CollectionMember<AttorneyApplyingOnBehalf>> attorneyOnBehalfOfNameAndAddress;
    private final String mentalCapacity;
    private final String courtOfProtection;
    private final String epaOrLpa;
    private final String epaRegistered;
    private final String domicilityCountry;
    private final List<CollectionMember<EstateItems>> ukEstate;
    private final String domicilityIHTCert;
    private final String entitledToApply;
    private final String entitledToApplyOther;
    private final String notifiedApplicants;
    private final String foreignAsset;
    private final String foreignAssetEstateValue;
    private final String legallyAdopted;
    private final List<CollectionMember<AdoptedRelatives>> legallyAdoptiveRelatives;

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
    private final String wholeBloodSiblingSurvived;
    private final String wholeBloodSiblingSurvivedOverEighteen;
    private final String wholeBloodSiblingSurvivedUnderEighteen;
    private final String wholeBloodSiblingDied;
    private final String wholeBloodSiblingDiedOverEighteen;
    private final String wholeBloodSiblingDiedUnderEighteen;
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
    private final String wholeBloodUncleAndAuntsSurvived;
    private final String wholeBloodUncleAndAuntsSurvivedOverEighteen;
    private final String wholeBloodUncleAndAuntsSurvivedUnderEighteen;
    private final String wholeBloodUncleAndAuntsDied;
    private final String wholeBloodUncleAndAuntsDiedOverEighteen;
    private final String wholeBloodUncleAndAuntsDiedUnderEighteen;
    private final String wholeBloodCousinsSurvived;
    private final String wholeBloodCousinsSurvivedOverEighteen;
    private final String wholeBloodCousinsSurvivedUnderEighteen;
    private final String halfBloodUncleAndAuntsSurvived;
    private final String halfBloodUncleAndAuntsSurvivedOverEighteen;
    private final String halfBloodUncleAndAuntsSurvivedUnderEighteen;
    private final String halfBloodUncleAndAuntsDied;
    private final String halfBloodUncleAndAuntsDiedOverEighteen;
    private final String halfBloodUncleAndAuntsDiedUnderEighteen;
    private final String halfBloodCousinsSurvived;
    private final String halfBloodCousinsSurvivedOverEighteen;
    private final String halfBloodCousinsSurvivedUnderEighteen;
    private final String applicationFeePaperForm;
    private final String feeForCopiesPaperForm;
    private final String totalFeePaperForm;
    private final String paperPaymentMethod;
    private final String paymentReferenceNumberPaperform;

}
