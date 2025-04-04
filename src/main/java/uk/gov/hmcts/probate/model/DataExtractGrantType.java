package uk.gov.hmcts.probate.model;

import static uk.gov.hmcts.probate.model.Constants.EXTRACT_AD_COLLIGENDA_BONA;
import static uk.gov.hmcts.probate.model.Constants.EXTRACT_ADMINISTRATION;
import static uk.gov.hmcts.probate.model.Constants.EXTRACT_ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.EXTRACT_PROBATE;

public enum DataExtractGrantType {
    gop(EXTRACT_PROBATE),
    intestacy(EXTRACT_ADMINISTRATION),
    admonWill(EXTRACT_ADMON_WILL),
    adColligendaBona(EXTRACT_AD_COLLIGENDA_BONA);

    private String caseTypeItem;

    DataExtractGrantType(String caseType) {
        this.caseTypeItem = caseType;
    }

    public String getCaseTypeMapped() {
        return caseTypeItem;
    }
}
