package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.Alias;
import uk.gov.hmcts.probate.model.Constants;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Data
@Builder
@AllArgsConstructor
public class ProbateAliasName {

    @JsonProperty(value = "Forenames")
    private final String forenames;

    @JsonProperty(value = "LastName")
    private final String lastName;

    @JsonProperty(value = "AppearOnGrant")
    private final String appearOnGrant;

    public static ProbateAliasName createFromAlias(Alias alias) {
        return new ProbateAliasName(alias.getFirstName(), alias.getLastName(), YES);
    }

}
