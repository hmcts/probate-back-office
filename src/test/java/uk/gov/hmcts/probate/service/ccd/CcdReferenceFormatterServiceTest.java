package uk.gov.hmcts.probate.service.ccd;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class CcdReferenceFormatterServiceTest {

    private CcdReferenceFormatterService underTest = new CcdReferenceFormatterService();

    @Test
    void shouldGetFormattedCaseReference() {
        String reference = "1234567890123456";
        String formattedResponse = underTest.getFormattedCaseReference(reference);
        assertEquals("#1234-5678-9012-3456", formattedResponse);
    }

    @Test
    void shouldGetFormattedCaseReferenceMax16() {
        String reference = "123456789012345678";
        String formattedResponse = underTest.getFormattedCaseReference(reference);
        assertEquals("#1234-5678-9012-3456", formattedResponse);
    }

}
