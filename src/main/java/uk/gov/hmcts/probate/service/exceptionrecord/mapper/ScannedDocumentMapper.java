package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.probate.model.exceptionrecord.InputScannedDoc;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentLink;
import uk.gov.hmcts.reform.probate.model.ScannedDocument;

@Component
public class ScannedDocumentMapper {

    /**
     * Converts document in Exception Record model to document in Case model.
     */
    public CollectionMember<ScannedDocument> toCaseDoc(
            InputScannedDoc exceptionRecordDoc,
            String exceptionRecordReference
    ) {
        if (exceptionRecordDoc == null) {
            return null;
        } else {
            ProbateDocumentLink exceptionRecordDocumentLink = ProbateDocumentLink.builder()
                    .documentUrl(exceptionRecordDoc.url)
                    .documentFilename(exceptionRecordDoc.fileName)
                    .build();
            return new CollectionMember<ScannedDocument>(null, new ScannedDocument(
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
