package uk.gov.hmcts.probate.model.ccd.raw.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;


import java.util.List;

@Jacksonized
@SuperBuilder
@Data
public class ResponseCaseDataParent {

    protected String schemaVersion;
    // A second copy of schemaVersion, holding exactly the same value.
    // Needed due to ccd quirks/RI to allow its use in FieldShowCondition for multiple pages for same event
    protected String schemaVersionCcdCopy;
    protected DynamicList reprintDocument;
    protected String reprintNumberOfCopies;
    protected DynamicList solsAmendLegalStatmentSelect;
    protected String declarationCheckbox;
    protected String ihtGrossValueField;
    protected String ihtNetValueField;
    protected String deceasedForeignDeathCertTranslation;
    protected String deceasedForeignDeathCertInEnglish;
    protected String deceasedDiedEngOrWales;
    protected String deceasedDeathCertificate;
    protected Long numberOfExecutors;
    protected Long numberOfApplicants;
    protected String legalDeclarationJson;
    protected String checkAnswersSummaryJson;
    protected String registryAddress;
    protected String registryEmailAddress;
    protected String registrySequenceNumber;
    protected String dispenseWithNotice;
    protected String dispenseWithNoticeLeaveGiven;
    protected String dispenseWithNoticeLeaveGivenDate;
    protected String dispenseWithNoticeOverview;
    protected String dispenseWithNoticeSupportingDocs;
    protected List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeOtherExecsList;
    protected String titleAndClearingType;
    protected String titleAndClearingTypeNoT;
    protected String trustCorpName;
    protected SolsAddress trustCorpAddress;
    protected List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList;
    protected String lodgementAddress;
    protected String lodgementDate;
    protected String nameOfFirmNamedInWill;
    protected String nameOfSucceededFirm;
    protected List<CollectionMember<AdditionalExecutorPartners>> otherPartnersApplyingAsExecutors;
    protected String solsForenames;
    protected String solsSurname;
    protected String solsSolicitorWillSignSOT;

}
