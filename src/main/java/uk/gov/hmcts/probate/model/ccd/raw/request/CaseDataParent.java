package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@SuperBuilder
@Data
public class CaseDataParent {

    protected String registrySequenceNumber;
    protected String dispenseWithNotice;
    protected String titleAndClearingType;
    protected final String deceasedDeathCertificate;
    protected final String deceasedDiedEngOrWales;
    protected final String deceasedForeignDeathCertInEnglish;
    protected final String deceasedForeignDeathCertTranslation;

}
