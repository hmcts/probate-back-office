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
@Table(name = "WILLS_FLAT")
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

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;// date

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "DATE_OF_DEATH1")
    private LocalDate dateOfDeath1;// date

    @Column(name = "ALIAS_NAMES")
    private String aliasNames;// varchar(32000)

    @Column(name = "RECORD_KEEPERS_TEXT")
    private String recordKeepersText;// varchar(32000)

}
