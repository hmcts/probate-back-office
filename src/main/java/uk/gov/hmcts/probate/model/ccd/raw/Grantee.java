package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Grantee {

    private final String firstName;
    private final String lastName;
    private final List<String> address;
}
