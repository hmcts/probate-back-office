package uk.gov.hmcts.probate.model.ccd.raw.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

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
    protected final String dispenseWithNoticeLeaveGivenDate;
    protected final String dispenseWithNoticeOverview;
    protected final String dispenseWithNoticeSupportingDocs;
    protected final List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeOtherExecsList;
    protected final String titleAndClearingType;
    protected final String titleAndClearingTypeNoT;
    protected final String trustCorpName;
    protected SolsAddress trustCorpAddress;
    protected final List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList;
    protected final String lodgementDate;
    protected final String lodgementAddress;
    protected final String nameOfFirmNamedInWill;
    protected final String nameOfSucceededFirm;
    protected final List<CollectionMember<AdditionalExecutorPartners>> otherPartnersApplyingAsExecutors;
    protected final String morePartnersHoldingPowerReserved;
    protected final String solsForenames;
    protected final String solsSolicitorWillSignSOT;
    protected final String solsSurname;
    protected final String soleTraderOrLimitedCompany;
    protected final List<String> whoSharesInCompanyProfits;
    protected final String solsIdentifiedNotApplyingExecs;
    protected final String solsIdentifiedApplyingExecs;
    protected final String solsReviewSOTConfirm;
    protected final String solsReviewSOTConfirmCheckbox1Names;
    protected final String solsReviewSOTConfirmCheckbox2Names;
    protected final String taskList;
    protected final String escalatedDate;
    protected final String authenticatedDate;
    protected final String iht217;
    protected final String noOriginalWillAccessReason;
    protected final LocalDate originalWillSignedDate;
    protected final List<CollectionMember<CodicilAddedDate>> codicilAddedDateList;
}
