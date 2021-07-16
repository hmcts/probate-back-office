package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;

import java.time.LocalDate;

@Jacksonized
@SuperBuilder
@Data
public class CaseDataParent {

    protected String registrySequenceNumber;
    protected final String deceasedDeathCertificate;
    protected final String deceasedDiedEngOrWales;
    protected final String deceasedForeignDeathCertInEnglish;
    protected final String deceasedForeignDeathCertTranslation;
    protected final String iht217;
    protected final String automatedProcess;

    @Getter
    protected LocalDate authenticatedDate;
    private final DynamicList solsPBANumber;
    private final String solsPBAPaymentReference;
    private final String solsOrgHasPBAs;
    private final String solsNeedsPBAPayment;

}
