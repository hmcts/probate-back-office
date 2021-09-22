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

    private final List<String> selectedLimitationTypes = new ArrayList();
    private final List<String> willMessageLimitations = new ArrayList();
    private final List<String> grantLimitations = new ArrayList();

}
