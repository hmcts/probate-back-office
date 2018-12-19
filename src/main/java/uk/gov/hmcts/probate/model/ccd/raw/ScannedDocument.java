package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ScannedDocument {

    private final String controlNumber;

    private final String fileName;

    private final String type;

    private final LocalDateTime scannedDate;

    private final DocumentLink url;
    
    private final String exceptionRecordReference;
}
