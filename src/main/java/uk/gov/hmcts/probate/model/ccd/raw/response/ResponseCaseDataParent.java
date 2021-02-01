package uk.gov.hmcts.probate.model.ccd.raw.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorp;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.OtherPartnerExecutorApplying;


import java.util.List;

@Jacksonized
@SuperBuilder
@Data
public class ResponseCaseDataParent {

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
    protected String titleAndClearingType;
    protected String trustCorpName;
    protected String actingTrustCorpName;
    protected String positionInTrustCorp;
    protected String additionalExecutorsTrustCorp;
    protected List<CollectionMember<AdditionalExecutorTrustCorp>> additionalExecutorsTrustCorpList;
    protected String lodgementAddress;
    protected String lodgementDate;
    protected String nameOfFirmNamedInWill;
    protected String otherPartnerExecutorName;
    protected String anyPartnersApplyingToActAsExecutor;
    protected String nameOfSucceededFirm;
    protected List<CollectionMember<OtherPartnerExecutorApplying>> otherPartnersApplyingAsExecutors;
    protected String solsForenames;
    protected String solsSurname;
    protected String solsSolicitorWillSignSOT;
}
