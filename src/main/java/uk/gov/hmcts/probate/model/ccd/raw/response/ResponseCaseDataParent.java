package uk.gov.hmcts.probate.model.ccd.raw.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.BulkScanEnvelope;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;

import java.util.List;

@Jacksonized
@SuperBuilder
@Data
public class ResponseCaseDataParent {

    protected DynamicList reprintDocument;

    protected String reprintNumberOfCopies;

    protected DynamicList solsAmendLegalStatmentSelect;

    protected List<CollectionMember<BulkScanEnvelope>> bulkScanEnvelopes;

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
    protected String taskList;
    protected String escalatedDate;
    protected String authenticatedDate;
    protected String iht217;
    protected DynamicList solsPBANumber;
    protected String solsPBAPaymentReference;
    protected String solsOrgHasPBAs;
    protected String solsNeedsPBAPayment;
    protected String automatedProcess;
}