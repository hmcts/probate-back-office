package uk.gov.hmcts.probate.model.ccd;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class CCDData implements Serializable {

    private final String solicitorReference;
    private final Solicitor solicitor;
    private final Deceased deceased;
    private final InheritanceTax iht;
    private final Fee fee;
    private final String solsAdditionalInfo;
    private final LocalDate caseSubmissionDate;
    private final List<Executor> executors;
    private final CheckList checkList;
}
