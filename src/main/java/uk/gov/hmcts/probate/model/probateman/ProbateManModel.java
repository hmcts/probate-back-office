package uk.gov.hmcts.probate.model.probateman;

import lombok.Data;

import javax.persistence.Column;

@Data
public abstract class ProbateManModel {

    @Column(name = "DNM_IND")
    private String dnmInd; //varchar(1), DO NOT MATCH flag

    @Column(name = "CCD_CASE_NO")
    private String ccdCaseNo; //varchar(20),

}
