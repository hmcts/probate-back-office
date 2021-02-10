package uk.gov.hmcts.probate.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.WillDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.DocumentType.IHT;
import static uk.gov.hmcts.probate.model.DocumentType.OTHER;
import static uk.gov.hmcts.probate.model.DocumentType.WILL;

@Service
@Slf4j
@AllArgsConstructor
public class OrderWillsService {

    private static List<String> willOrder = Arrays.asList(WILL.getTemplateName(), IHT.getTemplateName(), OTHER.getTemplateName());
    private static DateTimeFormatter willDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<CollectionMember<WillDocument>> orderWillDocuments(List<Document> documents) {
        Comparator comparator = Comparator.comparing((Document doc)->willOrder.indexOf(doc.getDocumentType().getTemplateName()))
            .thenComparing(doc->(doc.getDocumentDateAdded() == null ? LocalDate.now() :doc.getDocumentDateAdded()))
            .thenComparing(doc->doc.getDocumentFileName());

        Collections.sort(documents, comparator);
        
        return buildWillDocumentsCollection(documents);
    }

    private List<CollectionMember<WillDocument>>  buildWillDocumentsCollection(List<Document> documents) {
        List<CollectionMember<WillDocument>> willCollection = new ArrayList<>();
        for (Document document : documents) {
            willCollection.add(buildWillDocumentCollectionMember(document));
        }
        return willCollection;
    }

    private CollectionMember<WillDocument> buildWillDocumentCollectionMember(Document document) {
        WillDocument willDocument = WillDocument.builder()
            .documentDate(willDateFormatter.format(document.getDocumentDateAdded()))
            .documentLabel(document.getDocumentType().getTemplateName())
            .documentLink(document.getDocumentLink())
            .document(document)
            .build();
        return new CollectionMember<>(willDocument);
    }
}
