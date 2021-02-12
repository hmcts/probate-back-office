package uk.gov.hmcts.probate.service.document;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.WillDocument;

import javax.validation.UnexpectedTypeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderWillsServiceTest {
    @InjectMocks
    private OrderWillsService orderWillsService;
    private static DateTimeFormatter willDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void shouldOrderWills() {
        Document will1 = buildDocument("will1", "2005-02-01", DocumentType.WILL);
        Document will2 = buildDocument("will2", "2010-01-01", DocumentType.WILL);
        Document other1 = buildDocument("other1", "2001-01-01", DocumentType.OTHER);
        Document other2 = buildDocument("other2", "2009-02-01", DocumentType.OTHER);
        Document codicil1 = buildDocument("codicil1", "2001-05-01", DocumentType.IHT);
        Document codicil2 = buildDocument("codicil2", "2019-12-31", DocumentType.IHT);
        Document codicil3 = buildDocument("codicil3", null, DocumentType.IHT);
        
        List<Document> documentList = new ArrayList<>();
        documentList.add(codicil3);
        documentList.add(other1);
        documentList.add(codicil2);
        documentList.add(other2);
        documentList.add(will1);
        documentList.add(codicil1);
        documentList.add(will2);
        List<CollectionMember<WillDocument>> willDocuments = orderWillsService.orderWillDocuments(documentList);

        assertWillDocument(willDocuments.get(0), "Will", "2005-02-01", "binaryurl-will1");
        assertWillDocument(willDocuments.get(1), "Will", "2010-01-01", "binaryurl-will2");
        assertWillDocument(willDocuments.get(2), "Codicil", "2001-05-01", "binaryurl-codicil1");
        assertWillDocument(willDocuments.get(3), "Codicil", "2019-12-31", "binaryurl-codicil2");
        assertWillDocument(willDocuments.get(4), "Codicil", null, "binaryurl-codicil3");
        assertWillDocument(willDocuments.get(5), "Other", "2001-01-01", "binaryurl-other1");
        assertWillDocument(willDocuments.get(6), "Other", "2009-02-01", "binaryurl-other2");
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotRecogniseDocumentType() {
        Document will1 = buildDocument("will1", "2005-02-01", DocumentType.WILL);
        Document will2 = buildDocument("will2", "2010-01-01", DocumentType.DIGITAL_GRANT);

        List<Document> documentList = new ArrayList<>();
        documentList.add(will1);
        documentList.add(will2);
        orderWillsService.orderWillDocuments(documentList);
    }
    
    private void assertWillDocument(CollectionMember<WillDocument> willDocument, String label, String date, 
                                    String binary) {
        assertEquals(label, willDocument.getValue().getDocumentLabel());
        assertEquals(date, willDocument.getValue().getDocumentDate());
        assertEquals(binary, willDocument.getValue().getDocumentLink().getDocumentBinaryUrl());
    }

    private Document buildDocument(String fileName, String dateAdded, DocumentType docType) {
        return Document.builder()
            .documentFileName("will1")
            .documentDateAdded(dateAdded == null ? null : LocalDate.from(willDateFormatter.parse(dateAdded)))
            .documentType(docType)
            .documentLink(DocumentLink.builder().documentBinaryUrl("binaryurl-" + fileName).build())
            .build();
    }

}