package uk.gov.hmcts.probate.model.probateman;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class WillLodgement extends ProbateManModel {

    @Id
    private Long id;// bigint

    @Column(name = "RK_NUMBER")
    private String rkNumber;// varchar(11)

    @Column(name = "PROBATE_NUMBER")
    private String probateNumber;// varchar(11)

    @Column(name = "PROBATE_VERSION")
    private Long probateVersion;// int

    @Column(name = "DECEASED_ID")
    private Long deceasedId;// bigint,

    @Column(name = "DECEASED_FORENAMES")
    private String deceasedForenames;// varchar(50)

    @Column(name = "DECEASED_SURNAME")
    private String deceasedSurname;// varchar(30)

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;// date

    @Column(name = "DATE_OF_DEATH1")
    private LocalDate dateOfDeath1;// date

    @Column(name = "ALIAS_NAMES")
    private String aliasNames;// varchar(32000)

    @Column(name = "RECORD_KEEPERS_TEXT")
    private String recordKeepersText;// varchar(32000)

    @Column(name = "CCD_CASE_NO")
    private String ccdCaseNo;// varchar(20)

    @Column(name = "DNM_IND")
    private String dnmInd;// varchar(1)
}
