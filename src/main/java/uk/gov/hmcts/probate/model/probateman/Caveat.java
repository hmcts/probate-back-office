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
@Table(name = "CAVEATS_FLAT")
@EqualsAndHashCode(callSuper = true)
public class Caveat extends ProbateManModel {

    @Id
    private Long id;

    @Column(name = "CAVEAT_NUMBER")
    private String caveatNumber;

    @Column(name = "PROBATE_NUMBER")
    private String probateNumber;

    @Column(name = "REGISTRY_NAME")
    private String registryName;

    @Column(name = "SUBREGISTRY_NAME")
    private String subregistryName;

    @Column(name = "REGISTRY_CODE")
    private Long registryCode;

    @Column(name = "DECEASED_ID")
    private Long deceasedId;

    @Column(name = "DECEASED_SURNAME")
    private String deceasedSurname;

    @Column(name = "DECEASED_FORENAMES")
    private String deceasedForenames;

    @Column(name = "PROBATE_VERSION")
    private Long probateVersion;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "DATE_OF_DEATH1")
    private LocalDate dateOfDeath;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "DATE_OF_DEATH2")
    private LocalDate dateOfDeath2;

    @Column(name = "ALIAS_NAMES")
    private String aliasNames;

    @Column(name = "CAVEAT_TYPE")
    private String caveatType;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "CAVEAT_DATE_OF_ENTRY")
    private LocalDate caveatDateOfEntry;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "CAV_DATE_LAST_EXTENDED")
    private LocalDate cavDateLastExtended;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Column(name = "CAV_EXPIRY_DATE")
    private LocalDate cavExpiryDate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
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
}
