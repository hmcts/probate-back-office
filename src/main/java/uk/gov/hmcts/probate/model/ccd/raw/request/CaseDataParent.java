package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.controller.validation.ApplicationReviewedGroup;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;

import javax.validation.constraints.NotNull;

@Jacksonized
@SuperBuilder
@Data
public class CaseDataParent {

    protected String registrySequenceNumber;
    protected final String deceasedDeathCertificate;
    protected final String deceasedDiedEngOrWales;
    protected final String deceasedForeignDeathCertInEnglish;
    protected final String deceasedForeignDeathCertTranslation;

    @NotNull(groups = {ApplicationReviewedGroup.class}, message = "{solsPBANumberIsNull}")
    private final DynamicList solsPBANumber;

    @NotNull(groups = {ApplicationReviewedGroup.class}, message = "{solsPBAPaymentReferenceIsNull}")
    private final String solsPBAPaymentReference;

    private final String solsOrgHasPBAs;

}
