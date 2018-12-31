package uk.gov.hmcts.probate.model.ccd.grantapplication.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.time.LocalDate;

@JsonDeserialize(builder = GrantApplicationData.GrantApplicationDataBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GrantApplicationData {

    private static final String DATE_FORMAT = "yyyy-MM-dd";


    private String primaryApplicantForenames;

    private String primaryApplicantSurname;

    private String deceasedForenames;

    private String deceasedSurname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDate deceasedDateOfBirth;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDate deceasedDateOfDeath;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class GrantApplicationDataBuilder {
    }
}
