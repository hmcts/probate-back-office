package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.bulkscan.type.InputEnvelope;
import uk.gov.hmcts.bulkscan.type.InputScannableItem;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.exceptionrecord.InputScannedDoc;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;
import uk.gov.hmcts.reform.probate.model.ProbateDocumentLink;
import uk.gov.hmcts.reform.probate.model.ScannedDocument;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class ScannedDocumentMapper {

    public CollectionMember<ScannedDocument> toCaseDoc(
            InputScannedDoc exceptionRecordDoc,
            String exceptionRecordReference
    ) {
        if (exceptionRecordDoc == null) {
            return null;
        } else {
            ProbateDocumentLink exceptionRecordDocumentLink = ProbateDocumentLink.builder()
                    .documentUrl(exceptionRecordDoc.url.getDocumentUrl())
                    .documentBinaryUrl(exceptionRecordDoc.url.getDocumentBinaryUrl())
                    .documentFilename(exceptionRecordDoc.url.getDocumentFilename())
                    .build();
            return new CollectionMember<>(null, new ScannedDocument(
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

    public CollectionMember<ScannedDocument> toCaseDoc(
            InputScannableItem scannableItem,
            UploadResponse uploadResponse,
            String caseNumber,
            Instant deliveryDate
    ) {
        ProbateDocumentLink exceptionRecordDocumentLink = ProbateDocumentLink.builder()
                .documentUrl(uploadResponse.getDocuments().get(0).links.self.href)
                .documentBinaryUrl(uploadResponse.getDocuments().get(0).links.binary.href)
                .documentFilename(uploadResponse.getDocuments().get(0).originalDocumentName)
                .build();
        return new CollectionMember<>(null, new ScannedDocument(
                scannableItem.documentControlNumber,
                scannableItem.fileName,
                scannableItem.documentType.toString(),
                scannableItem.documentSubtype,
                LocalDateTime.ofInstant(scannableItem.scanningDate, ZoneOffset.UTC),
                exceptionRecordDocumentLink,
                caseNumber,
                LocalDateTime.ofInstant(deliveryDate, ZoneOffset.UTC)
        ));
    }

    public CollectionMember<ScannedDocument> toCaseDoc(
            InputScannableItem scannableItem,
            UploadResponse uploadResponse,
            InputEnvelope inputEnvelope
    ) {
        ProbateDocumentLink exceptionRecordDocumentLink = ProbateDocumentLink.builder()
                .documentUrl(uploadResponse.getDocuments().get(0).links.self.href)
                .documentBinaryUrl(uploadResponse.getDocuments().get(0).links.binary.href)
                .documentFilename(uploadResponse.getDocuments().get(0).originalDocumentName)
                .build();
        return new CollectionMember<>(null, new ScannedDocument(
                scannableItem.documentControlNumber,
                scannableItem.fileName,
                scannableItem.documentType.toString(),
                scannableItem.documentSubtype,
                LocalDateTime.ofInstant(scannableItem.scanningDate, ZoneOffset.UTC),
                exceptionRecordDocumentLink,
                inputEnvelope.caseNumber,
                LocalDateTime.ofInstant(inputEnvelope.deliveryDate, ZoneOffset.UTC)
        ));
    }

    public uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument>
    updateCaseDoc(InputScannedDoc exceptionRecordDoc, String exceptionRecordReference) {
        if (exceptionRecordDoc == null) {
            return null;
        } else {
            DocumentLink exceptionRecordDocumentLink = DocumentLink.builder()
                    .documentUrl(exceptionRecordDoc.url.getDocumentUrl())
                    .documentBinaryUrl(exceptionRecordDoc.url.getDocumentBinaryUrl())
                    .documentFilename(exceptionRecordDoc.url.getDocumentFilename())
                    .build();
            return new uk.gov.hmcts.probate.model.ccd.raw.CollectionMember<>(
                    new uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument(
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
