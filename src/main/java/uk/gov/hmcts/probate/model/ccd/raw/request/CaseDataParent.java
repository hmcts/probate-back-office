package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.solicitorexecutors.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.time.LocalDate;
import java.util.List;

@Jacksonized
@SuperBuilder
@Data
public class CaseDataParent {

    protected String schemaVersion;
    protected String registrySequenceNumber;
    protected final String deceasedDeathCertificate;
    protected final String deceasedDiedEngOrWales;
    protected final String deceasedForeignDeathCertInEnglish;
    protected final String deceasedForeignDeathCertTranslation;
    protected String solsForenames;
    protected String solsSurname;
    protected String solsSolicitorWillSignSOT;
    protected String dispenseWithNotice;
    protected String titleAndClearingType;
    protected String titleAndClearingTypeNoT;
    protected String trustCorpName;
    protected List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList;
    protected String lodgementAddress;
    protected LocalDate lodgementDate;
    protected String nameOfFirmNamedInWill;
    protected String nameOfSucceededFirm;
    protected List<CollectionMember<AdditionalExecutorPartners>> otherPartnersApplyingAsExecutors;
    protected String dispenseWithNoticeLeaveGiven;
    protected String dispenseWithNoticeLeaveGivenDate;
    protected String dispenseWithNoticeOverview;
    protected String dispenseWithNoticeSupportingDocs;
    protected List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> powerReservedExecutorList;
    protected String soleTraderOrLimitedCompany;
    protected String whoSharesInCompanyProfits;

}
