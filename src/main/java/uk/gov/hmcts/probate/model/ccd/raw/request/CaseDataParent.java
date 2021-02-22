package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.time.LocalDate;
import java.util.List;

@Jacksonized
@SuperBuilder
@Data
public class CaseDataParent {

    protected String schemaVersion;
    // A second copy of schemaVersion, holding exactly the same value.
    // Needed due to ccd quirks/RI to allow its use in FieldShowCondition for multiple pages for same event
    protected String schemaVersionCcdCopy;
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
    protected String trustCorpAddress;
    protected List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList;
    protected String lodgementAddress;
    protected LocalDate lodgementDate;
    protected String nameOfFirmNamedInWill;
    protected String nameOfSucceededFirm;
    protected List<CollectionMember<AdditionalExecutorPartners>> otherPartnersApplyingAsExecutors;
    protected String morePartnersHoldingPowerReserved;
    protected String dispenseWithNoticeLeaveGiven;
    protected String dispenseWithNoticeLeaveGivenDate;
    protected String dispenseWithNoticeOverview;
    protected String dispenseWithNoticeSupportingDocs;
    protected List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeOtherExecsList;

}
