package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ScannedDocument {

    private final String controlNumber;

    private final String fileName;

    private final String type;

    private final String scannedDate;

    private final DocumentLink url;
    
    private final String exceptionRecordReference;
}
