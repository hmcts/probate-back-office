package uk.gov.hmcts.probate.model.probateman;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@JsonNaming(RegularCaseNamingStrategy.class)
@Entity
@Data
@Table(name = "STANDING_SEARCHES_FLAT")
@EqualsAndHashCode(callSuper = true)
public class StandingSearch extends ProbateManModel {

    @Id
    private Long id; //bigint,

    @Column(name = "SS_NUMBER")
    private String ssNumber; //varchar(11),

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

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth; //date,

    @Column(name = "DECEASED_ADDRESS")
    private String deceasedAddress; //varchar(500),

    @Column(name = "APPLICANT_ADDRESS")
    private String applicantAddress; //varchar(500),

    @Column(name = "REGISTRY_REG_LOCATION_CODE")
    private Long registryRegLocationCode; //int,

    @Column(name = "SS_APPLICANT_FORENAME")
    private String ssApplicantForename; //varchar(30),

    @Column(name = "SS_APPLICANT_SURNAME")
    private String ssApplicantSurname; //varchar(50),

    @Column(name = "SS_APPLICANT_HONOURS")
    private String ssApplicantHonours; //varchar(100),

    @Column(name = "SS_APPLICANT_TITLE")
    private String ssApplicantTitle; //varchar(35),

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "DATE_OF_DEATH1")
    private LocalDate dateOfDeath1; //date,

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "DATE_OF_DEATH2")
    private LocalDate dateOfDeath2; //date,

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "SS_DATE_LAST_EXTENDED")
    private LocalDate ssDateLastExtended; //date,

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "SS_DATE_OF_ENTRY")
    private LocalDate ssDateOfEntry; //date,

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "SS_DATE_OF_EXPIRY")
    private LocalDate ssDateOfExpiry; //date,

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "SS_WITHDRAWN_DATE")
    private LocalDate ssWithdrawnDate; //date,

    @Column(name = "STANDING_SEARCH_TEXT")
    private String standingSearchText; //varchar(32000),

    @Column(name = "ALIAS_NAMES")
    private String aliasNames; //varchar(32000)

}
