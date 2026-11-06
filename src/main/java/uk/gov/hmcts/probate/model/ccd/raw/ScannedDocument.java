package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ScannedDocument {

    private final String controlNumber;

    private final String fileName;

    private final String type;

    private final String subtype;

    private final String finalWill;

    private final LocalDateTime scannedDate;

    private final DocumentLink url;

    private final String exceptionRecordReference;

    private final LocalDateTime deliveryDate;

    public boolean isFinalWill() {
        return YES.equalsIgnoreCase(finalWill);
    }
}
