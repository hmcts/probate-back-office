package uk.gov.hmcts.probate.model.ccd.raw;

import joptsimple.internal.Strings;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Grantee {

    private final String fullName;
    private final List<String> address;

    public String getFirstName() {
        if (fullName.trim().split("\\w+").length > 1) {
            return fullName.substring(0, fullName.lastIndexOf(' '));
        }
        return fullName;
    }

    public String getLastName() {
        if (fullName.trim().split("\\w+").length > 1) {
            return fullName.substring(fullName.lastIndexOf(' ') + 1);
        }
        return Strings.EMPTY;
    }
}