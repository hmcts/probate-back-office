package uk.gov.hmcts.probate.model.exceptionrecord;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class InputScannedDoc {

    public final String type;
    public final String subtype;
    public final String url;
    public final String controlNumber;
    public final String fileName;
    public final LocalDateTime scannedDate;
    public final LocalDateTime deliveryDate;

    public InputScannedDoc(
            @JsonProperty("type") String type,
            @JsonProperty("subtype") String subtype,
            @JsonProperty("url") String url,
            @JsonProperty("control_number") String controlNumber,
            @JsonProperty("file_name") String fileName,
            @JsonProperty("scanned_date") LocalDateTime scannedDate,
            @JsonProperty("delivery_date") LocalDateTime deliveryDate
    ) {
        this.type = type;
        this.subtype = subtype;
        this.url = url;
        this.controlNumber = controlNumber;
        this.fileName = fileName;
        this.scannedDate = scannedDate;
        this.deliveryDate = deliveryDate;
    }
}