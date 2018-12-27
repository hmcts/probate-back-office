package uk.gov.hmcts.probate.model.probateman;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
public class GrantApplication {

    @Id
    private Long id; //bigint,

    @Column(name = "PROBATE_NUMBER")
    private String probateNumber; //varchar(11),

    @Column(name = "PROBATE_VERSION")
    private Long probateVersion; //int,

    @Column(name = "DECEASED_ID")
    private Long deceasedId; //bigint,

    @Column(name = "DECEASED_FORENAMES")
    private String deceasedForenames; //varchar(50),

    @Column(name = "DECEASED_SURNAME")
    private String deceasedSurname; //varchar(50),

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth; //date,

    @Column(name = "DATE_OF_DEATH1")
    private LocalDate dateOfDeath1; //date,

    @Column(name = "DECEASED_ADDRESS")
    private String deceasedAddress; //varchar(500),

    @Column(name = "DECEASED_TEXT")
    private String deceasedText; //varchar(32000),

    @Column(name = "ALIAS_NAMES")
    private String aliasNames; //varchar(32000),

    @Column(name = "GRANT_APPLICATION_TEXT")
    private String grantApplicationText; //varchar(32000),

    @Column(name = "APPLICATION_EVENT_TEXT")
    private String applicationEventText; //varchar(32000),

    @Column(name = "OATH_TEXT")
    private String oathText; //varchar(32000),

    @Column(name = "EXECUTOR_TEXT")
    private String executorText; //varchar(32000),

    @Column(name = "OTHER_INFORMATION_TEXT")
    private String otherInformationText; //varchar(32000),

    @Column(name = "LINKED_DECEASED_IDS")
    private String linkedDeceasedIds; //varchar(32000),

    @Column(name = "CCD_CASE_NO")
    private String ccdCaseNo; //varchar(20),

    @Column(name = "DNM_IND")
    private String dnmInd; //varchar(1),

    @Column(name = "DECEASED_AGE_AT_DEATH")
    private Long deceasedAgeAtDeath; //int,

    @Column(name = "DECEASED_DEATH_TYPE")
    private String deceasedDeathType; //varchar(40),

    @Column(name = "DECEASED_DOMICILE")
    private String deceasedDomicile; //varchar(60),

    @Column(name = "DECEASED_DOMICILE_IN_WELSH")
    private String deceasedDomicileInWelsh; //varchar(10),

    @Column(name = "DECEASED_DOMICILE_WELSH")
    private String deceasedDomicileWelsh; //varchar(60),

    @Column(name = "DECEASED_HONOURS")
    private String deceasedHonours; //varchar(100),

    @Column(name = "DECEASED_SEX")
    private String deceasedSex; //varchar(1),

    @Column(name = "DECEASED_TITLE")
    private String deceasedTitle; //varchar(35),

    @Column(name = "APP_ADMIN_CLAUSE_LIMITATION")
    private String appAdminClauseLimitation; //varchar(100),

    @Column(name = "APP_ADMIN_CLAUSE_LIMITN_WELSH")
    private String appAdminClauseLimitnWelsh; //varchar(100),

    @Column(name = "APP_CASE_TYPE")
    private String appCaseType; //varchar(20),

    @Column(name = "APP_EXECUTOR_LIMITATION")
    private String appExecutorLimitation; //varchar(100),

    @Column(name = "APP_EXECUTOR_LIMITATION_WELSH")
    private String appExecutorLimitationWelsh; //varchar(100),

    @Column(name = "APP_RECEIVED_DATE")
    private LocalDate appReceivedDate; //date,

    @Column(name = "APPLICANT_ADDRESS")
    private String applicantAddress; //varchar(500),

    @Column(name = "APPLICANT_DX_EXCHANGE")
    private String applicantDxExchange; //varchar(25),

    @Column(name = "APPLICANT_DX_NUMBER")
    private String applicantDxNumber; //varchar(10),

    @Column(name = "APPLICANT_FORENAMES")
    private String applicantForenames; //varchar(50),

    @Column(name = "APPLICANT_HONOURS")
    private String applicantHonours; //varchar(100),

    @Column(name = "APPLICANT_SURNAME")
    private String applicantSurname; //varchar(50),

    @Column(name = "APPLICANT_TITLE")
    private String applicantTitle; //varchar(35),

    @Column(name = "GRANT_WELSH_LANGUAGE_IND")
    private String grantWelshLanguageInd; //boolean,

    @Column(name = "GRANT_WILL_TYPE")
    private String grantWillType; //varchar(200),

    @Column(name = "GRANT_WILL_TYPE_WELSH")
    private String grantWillTypeWelsh; //varchar(200),

    @Column(name = "EXCEPTED_ESTATE_IND")
    private String exceptedEstateInd; //varchar(1),

    @Column(name = "FILESLIP_SIGNAL")
    private String fileslipSignal; //boolean,

    @Column(name = "GRANT_APPLICANT_TYPE")
    private String grantApplicantType; //varchar(1),

    @Column(name = "GRANT_CONFIRMED_DATE")
    private LocalDate grantConfirmedDate; //date,

    @Column(name = "GRANT_ISSUED_DATE")
    private LocalDate grantIssuedDate; //date,

    @Column(name = "GRANT_ISSUED_SIGNAL")
    private String grantIssuedSignal; //boolean,

    @Column(name = "GRANT_LIMITATION")
    private String grantLimitation; //varchar(800),

    @Column(name = "GRANT_LIMITATION_WELSH")
    private String grantLimitationWelsh; //varchar(800),

    @Column(name = "GRANT_POWER_RESERVED")
    private String grantPowerReserved; //varchar(1),

    @Column(name = "GRANT_SOL_ID")
    private String grantSolId; //varchar(10),

    @Column(name = "GRANT_TYPE")
    private String grantType; //varchar(3),

    @Column(name = "GRANT_VERSION_DATE")
    private LocalDate grantVersionDate; //date,

    @Column(name = "GRANTEE1_ADDRESS")
    private String grantee1Address; //varchar(500),

    @Column(name = "GRANTEE1_FORENAMES")
    private String grantee1Forenames; //varchar(50),

    @Column(name = "GRANTEE1_HONOURS")
    private String grantee1Honours; //varchar(100),

    @Column(name = "GRANTEE1_SURNAME")
    private String grantee1Surname; //varchar(50),

    @Column(name = "GRANTEE1_TITLE")
    private String grantee1Title; //varchar(35),

    @Column(name = "GRANTEE2_ADDRESS")
    private String grantee2Address; //varchar(500),

    @Column(name = "GRANTEE2_FORENAMES")
    private String grantee2Forenames; //varchar(50),

    @Column(name = "GRANTEE2_HONOURS")
    private String grantee2Honours; //varchar(100),

    @Column(name = "GRANTEE2_SURNAME")
    private String grantee2Surname; //varchar(50),

    @Column(name = "GRANTEE2_TITLE")
    private String grantee2Title; //varchar(35),

    @Column(name = "GRANTEE3_ADDRESS")
    private String grantee3Address; //varchar(500),

    @Column(name = "GRANTEE3_FORENAMES")
    private String grantee3Forenames; //varchar(50),

    @Column(name = "GRANTEE3_HONOURS")
    private String grantee3Honours; //varchar(100),

    @Column(name = "GRANTEE3_SURNAME")
    private String grantee3Surname; //varchar(50),

    @Column(name = "GRANTEE3_TITLE")
    private String grantee3Title; //varchar(35),

    @Column(name = "GRANTEE4_ADDRESS")
    private String grantee4Address; //varchar(500),

    @Column(name = "GRANTEE4_FORENAMES")
    private String grantee4Forenames; //varchar(50),

    @Column(name = "GRANTEE4_HONOURS")
    private String grantee4Honours; //varchar(100),

    @Column(name = "GRANTEE4_SURNAME")
    private String grantee4Surname; //varchar(50),

    @Column(name = "GRANTEE4_TITLE")
    private String grantee4Title; //varchar(35),

    @Column(name = "GROSS_ESTATE_VALUE")
    private Long grossEstateValue; //bigint,

    @Column(name = "NET_ESTATE_VALUE")
    private Long netEstateValue; //bigint,

    @Column(name = "PLACE_OF_ORIGINAL_GRANT")
    private String placeOfOriginalGrant; //varchar(60),

    @Column(name = "PLACE_OF_ORIGINAL_GRANT_WELSH")
    private String placeOfOriginalGrantWelsh; //varchar(60),

    @Column(name = "POWER_RESERVED_WELSH")
    private String powerReservedWelsh; //varchar(1),

    @Column(name = "RESEAL_DATE")
    private LocalDate resealDate; //date,

    @Column(name = "SOLICITOR_REFERENCE")
    private String solicitorReference; //varchar(30)

}
