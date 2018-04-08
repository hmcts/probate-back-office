package uk.gov.hmcts.probate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

public class Deceased implements Serializable {

    @NotNull(message = "{dobIsNull}")
    @JsonProperty("deceasedDob")
    private final LocalDate dateOfBirth;

    @NotNull(message = "{dodIsNull}")
    @JsonProperty("deceasedDod")
    private final LocalDate dateOfDeath;

    @JsonCreator
    public Deceased(@JsonProperty("deceasedDob") LocalDate dateOfBirth,
                    @JsonProperty("deceasedDod") LocalDate dateOfDeath) {
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

}
