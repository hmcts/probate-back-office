package uk.gov.hmcts.probate.model.ccd.raw.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Declaration;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.LegalStatement;
import uk.gov.hmcts.probate.model.ccd.raw.Payment;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;

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
    
}
