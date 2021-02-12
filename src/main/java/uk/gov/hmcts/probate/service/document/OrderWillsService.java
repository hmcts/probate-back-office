package uk.gov.hmcts.probate.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.WillDocument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static uk.gov.hmcts.probate.model.DocumentType.IHT;
import static uk.gov.hmcts.probate.model.DocumentType.OTHER;
import static uk.gov.hmcts.probate.model.DocumentType.WILL;

@Service
@Slf4j
@AllArgsConstructor
public class OrderWillsService {

    private static final String DOCUMENT_TYPE_WILL_NAME = "Will";
    private static final String DOCUMENT_TYPE_CODICIL_NAME = "Codicil";
    private static final String DOCUMENT_TYPE_OTHER_NAME = "Other";

    private static List<String> willOrder = Arrays.asList(WILL.getTemplateName(), IHT.getTemplateName(),
        OTHER.getTemplateName());
    private static DateTimeFormatter willDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<CollectionMember<WillDocument>> orderWillDocuments(List<Document> documents) {
        Comparator<Document> comparator = Comparator.comparing((Document doc) -> willOrder.indexOf(
            doc.getDocumentType().getTemplateName()))
            .thenComparing(doc -> (doc.getDocumentDateAdded() == null ? LocalDate.now() : doc.getDocumentDateAdded()))
            .thenComparing(doc -> doc.getDocumentFileName());

        Collections.sort(documents, comparator);

        return buildWillDocumentsCollection(documents);
    }

    private List<CollectionMember<WillDocument>> buildWillDocumentsCollection(List<Document> documents) {
        List<CollectionMember<WillDocument>> willCollection = new ArrayList<>();
        for (Document document : documents) {
            willCollection.add(buildWillDocumentCollectionMember(document));
        }
        return willCollection;
    }

    private CollectionMember<WillDocument> buildWillDocumentCollectionMember(Document document) {
        WillDocument willDocument = WillDocument.builder()
            .documentDate(document.getDocumentDateAdded() == null ? null :
                willDateFormatter.format(document.getDocumentDateAdded()))
            .documentLabel(reformatDocumentLabel(document.getDocumentType()))
            .documentLink(document.getDocumentLink())
            .build();
        return new CollectionMember<>(willDocument);
    }

    private String reformatDocumentLabel(DocumentType templateName) {
        String formatted = "";
        switch (templateName) {
            case WILL:
                formatted = DOCUMENT_TYPE_WILL_NAME;
                break;
            case IHT:
                formatted = DOCUMENT_TYPE_CODICIL_NAME;
                break;
            case OTHER:
                formatted = DOCUMENT_TYPE_OTHER_NAME;
                break;
            default:
                throw new BadRequestException("Document type not allowed for Order Will");
        }

        return formatted;
    }
}
