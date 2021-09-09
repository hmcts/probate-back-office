package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Limitations {

    private final List<String> selectedLimitations = new ArrayList();
    private final List<String> willSelectedLimitations = new ArrayList();
    private final List<String> grantSelectedLimitations = new ArrayList();
    private final List<String> admonWillSelectedLimitations = new ArrayList();
    private final List<String> admonSelectedLimitations = new ArrayList();

    @JsonIgnore
    public List<List<String>> getAllSelectedLimitations() {
        return Arrays.asList(willSelectedLimitations, grantSelectedLimitations, admonWillSelectedLimitations
            , admonSelectedLimitations);
    }
}
