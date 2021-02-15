package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WillDocument {

    private final List<String> documentSelected;
    private final String documentLabel;
    private final String documentDate;
    private final DocumentLink documentLink;
    
}
