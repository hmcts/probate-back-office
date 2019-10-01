package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.exceptionrecord.InputScannedDoc;
import uk.gov.hmcts.probate.model.exceptionrecord.Item;

@Component
public class ScannedDocumentMapper {

    /**
     * Converts document in Exception Record model to document in Case model.
     */
    public Item<ScannedDocument> toCaseDoc(
            InputScannedDoc exceptionRecordDoc,
            String exceptionRecordReference
    ) {
        if (exceptionRecordDoc == null) {
            return null;
        } else {
            DocumentLink exceptionRecordDocumentLink = DocumentLink.builder()
                    .documentUrl(exceptionRecordDoc.url)
                    .documentFilename(exceptionRecordDoc.fileName)
                    .build();
            return new Item<>(new ScannedDocument(
                    exceptionRecordDoc.controlNumber,
                    exceptionRecordDoc.fileName,
                    exceptionRecordDoc.type,
                    exceptionRecordDoc.subtype,
                    exceptionRecordDoc.scannedDate,
                    exceptionRecordDocumentLink,
                    exceptionRecordReference,
                    exceptionRecordDoc.deliveryDate
            ));
        }
    }
}
