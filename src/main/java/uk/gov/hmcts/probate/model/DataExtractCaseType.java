package uk.gov.hmcts.probate.model;

import static uk.gov.hmcts.probate.model.Constants.ADMINISTRATION;
import static uk.gov.hmcts.probate.model.Constants.ADMON_WILL;
import static uk.gov.hmcts.probate.model.Constants.PROBATE;

public enum DataExtractCaseType {
    gop(PROBATE),
    intestacy(ADMINISTRATION),
    admonWill(ADMON_WILL);

    private String caseTypeItem;

    DataExtractCaseType(String caseType) {
        this.caseTypeItem = caseType;
    }

    public String getCaseTypeMapped() {
        return caseTypeItem;
    }
}
