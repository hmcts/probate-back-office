package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.probate.model.ProbateDocument;

@Data
@Builder
public class WillDocument {

    private final String documentSelected;
    private final String documentLabel;
    private final String documentDate;
    private final DocumentLink documentLink;
    
}
