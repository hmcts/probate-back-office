package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScannedDocument {

    private final String controlNumber;

    private final String fileName;

    private final String type;

    private final String scannedDate;

    private final String url;

    private final String exceptionRecordReference;
}
