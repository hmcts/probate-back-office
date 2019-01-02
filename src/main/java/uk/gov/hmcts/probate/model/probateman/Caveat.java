package uk.gov.hmcts.probate.model.probateman;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "CAVEATS_FLAT")
@EqualsAndHashCode(callSuper = false)
public class Caveat extends ProbateManModel {

    @Id
    private Long id;

    @Column(name = "CAVEAT_NUMBER")
    private String caveatNumber;

    @Column(name = "PROBATE_NUMBER")
    private String probateNumber;

    @Column(name = "PROBATE_VERSION")
    private Long probateVersion;

    @Column(name = "DECEASED_ID")
    private Long deceasedId;

    @Column(name = "DECEASED_FORENAMES")
    private String deceasedForenames;

    @Column(name = "DECEASED_SURNAME")
    private String deceasedSurname;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "DATE_OF_DEATH1")
    private LocalDate dateOfDeath;

    @Column(name = "ALIAS_NAMES")
    private String aliasNames;

    @Column(name = "CCD_CASE_NO")
    private String ccdCaseNo;

    @Column(name = "CAVEAT_TYPE")
    private String caveatType;

    @Column(name = "CAVEAT_DATE_OF_ENTRY")
    private LocalDate caveatDateOfEntry;

    @Column(name = "CAV_DATE_LAST_EXTENDED")
    private LocalDate cavDateLastExtended;

    @Column(name = "CAV_EXPIRY_DATE")
    private LocalDate cavExpiryDate;

    @Column(name = "CAV_WITHDRAWN_DATE")
    private LocalDate cavWithDrawnDate;

    @Column(name = "CAVEATOR_TITLE")
    private String caveatorTitle;

    @Column(name = "CAVEATOR_HONOURS")
    private String caveatorHonours;

    @Column(name = "CAVEATOR_FORENAMES")
    private String caveatorForenames;

    @Column(name = "CAVEATOR_SURNAME")
    private String caveatorSurname;

    @Column(name = "CAV_SOLICITOR_NAME")
    private String cavSolicitorName;

    @Column(name = "CAV_SERVICE_ADDRESS")
    private String cavServiceAddress;

    @Column(name = "CAV_DX_NUMBER")
    private String cavDxNumber;

    @Column(name = "CAV_DX_EXCHANGE")
    private String cavDxExchange;

    @Column(name = "CAVEAT_TEXT")
    private String caveatText;

    @Column(name = "CAVEAT_EVENT_TEXT")
    private String caveatEventText;

    @Column(name = "DNM_IND")
    private String dnmInd;
}
