package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.DocumentType;

@Data
@Builder
public class WillDocument {

    private final String willLabel;
    private final String willDate;
    private final DocumentLink willDocumentLink;
}
