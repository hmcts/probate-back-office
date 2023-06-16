package uk.gov.hmcts.probate.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;

class DocumentTypeTest {

    @Test
    void lookupCherishedUpperCase() {
        ScannedDocument scannedDocument = ScannedDocument.builder().type("CHERISHED").build();
        assertEquals(DocumentType.CHERISHED,DocumentType.lookup(scannedDocument.getType()));
    }

    @Test
    void lookupOtherLowerCase() {
        ScannedDocument scannedDocument = ScannedDocument.builder().type("other").build();
        assertEquals(DocumentType.OTHER,DocumentType.lookup(scannedDocument.getType()));
    }

}