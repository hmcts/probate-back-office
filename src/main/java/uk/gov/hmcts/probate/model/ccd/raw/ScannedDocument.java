package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
@AllArgsConstructor
public class ScannedDocument {

    @CCD(label = "Document control number")
    private final String controlNumber;

    @CCD(label = "Document name")
    private final String fileName;

    @CCD(label = "Document type", typeOverride = FieldType.FixedList, typeParameterOverride = "ScannedDocumentType")
    private final String type;
    
    @CCD(label = "Document Subtype")
    private final String subtype;

    @CCD(label = "Scanned date")
    private final LocalDateTime scannedDate;

    @CCD(label = "Document Url", typeOverride = FieldType.Document)
    private final DocumentLink url;
    
    @CCD(label = "Exception record reference")
    private final String exceptionRecordReference;

    @CCD(label = "Delivery date")
    private final LocalDateTime deliveryDate;
}
