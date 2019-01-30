package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;

import java.io.Serializable;

@EqualsAndHashCode(of = {"id"})
@Data
@Builder
public class CaseMatch implements Serializable {
    private final String id;
    private final String ccdCaseId;
    private final String fullName;
    private final String aliases;
    private final String dob;
    private final String dod;
    private final String postcode;
    private final String valid;
    private final String comment;
    private final String type;
    private CaseLink caseLink;
    private final String legacyCaseViewUrl;
    private final String doImport;

}
