package uk.gov.hmcts.probate.model.ccd.raw.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.*;
import uk.gov.hmcts.probate.model.caseaccess.OrganisationPolicy;

import java.time.LocalDate;
import java.util.List;

@Jacksonized
@SuperBuilder
@Data
public class ResponseCaseDataParent {

    protected final String schemaVersion;
    protected final DynamicList reprintDocument;
    protected final String reprintNumberOfCopies;
    protected final DynamicList solsAmendLegalStatmentSelect;
    protected List<CollectionMember<BulkScanEnvelope>> bulkScanEnvelopes;
    protected final String declarationCheckbox;
    protected final String ihtGrossValueField;
    protected final String ihtNetValueField;
    protected final String deceasedForeignDeathCertTranslation;
    protected final String deceasedDiedEngOrWales;
    protected final String deceasedForeignDeathCertInEnglish;
    protected final String deceasedDeathCertificate;
    protected final Long numberOfExecutors;
    protected final Long numberOfApplicants;
    protected final String legalDeclarationJson;
    protected final String checkAnswersSummaryJson;
    protected final String registryEmailAddress;
    protected final String registryAddress;
    protected final String registrySequenceNumber;
    protected final String dispenseWithNotice;
    protected final String dispenseWithNoticeLeaveGiven;
    protected final LocalDate dispenseWithNoticeLeaveGivenDate;
    protected final String dispenseWithNoticeOverview;
    protected final String dispenseWithNoticeSupportingDocs;
    protected final List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeOtherExecsList;
    protected final String titleAndClearingType;
    protected final String trustCorpName;
    protected SolsAddress trustCorpAddress;
    protected final List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList;
    protected final String lodgementDate;
    protected final String isSolThePrimaryApplicant;
    protected final String lodgementAddress;
    protected final String nameOfFirmNamedInWill;
    protected SolsAddress addressOfFirmNamedInWill;
    protected final String nameOfSucceededFirm;
    protected SolsAddress addressOfSucceededFirm;
    protected final String anyOtherApplyingPartners;
    protected final String anyOtherApplyingPartnersTrustCorp;
    protected final List<CollectionMember<AdditionalExecutorPartners>> otherPartnersApplyingAsExecutors;
    protected final String morePartnersHoldingPowerReserved;
    protected final String probatePractitionersPositionInTrust;
    protected final String solsForenames;
    protected final String solsSolicitorWillSignSOT;
    protected final String solsSurname;
    protected final List<String> whoSharesInCompanyProfits;
    protected String solsIdentifiedNotApplyingExecs;
    protected String solsIdentifiedApplyingExecs;
    protected String solsIdentifiedNotApplyingExecsCcdCopy;
    protected String solsIdentifiedApplyingExecsCcdCopy;
    protected final String solsReviewSOTConfirm;
    protected final String solsReviewSOTConfirmCheckbox1Names;
    protected final String solsReviewSOTConfirmCheckbox2Names;
    protected final String taskList;
    protected final String escalatedDate;
    protected final String caseWorkerEscalationDate;
    protected final String resolveCaseWorkerEscalationDate;
    protected final String authenticatedDate;
    protected final String iht217;
    protected final String noOriginalWillAccessReason;
    protected final LocalDate originalWillSignedDate;
    protected final List<CollectionMember<CodicilAddedDate>> codicilAddedDateList;
    protected final String furtherEvidenceForApplication;
    protected DynamicList solsPBANumber;
    protected String solsPBAPaymentReference;
    protected String solsOrgHasPBAs;
    protected String solsNeedsPBAPayment;
    protected OrganisationPolicy applicantOrganisationPolicy;
    protected LocalDate lastEvidenceAddedDate;
    protected String documentUploadedAfterCaseStopped;
    protected String serviceRequestReference;
    protected String paymentTaken;
    protected String applicationSubmittedBy;
    protected DynamicRadioList ihtFormsReported;
}
