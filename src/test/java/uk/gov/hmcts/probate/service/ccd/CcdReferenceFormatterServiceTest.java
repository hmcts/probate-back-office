package uk.gov.hmcts.probate.service.ccd;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CcdReferenceFormatterServiceTest {

    private CcdReferenceFormatterService underTest = new CcdReferenceFormatterService();

    private static Stream<Arguments> caseReferenceTests() {
        return Stream.of(arguments("1234567890123456", "#1234-5678-9012-3456"),
            arguments("1234-5678-9012-3456", "#1234-5678-9012-3456"),
            arguments("123456789012345678", "#1234-5678-9012-3456")
        );
    }

    @ParameterizedTest
    @MethodSource("caseReferenceTests")
    void shouldGetFormattedCaseReference(String referenceIn, String referenceOut) {
        String formattedResponse = underTest.getFormattedCaseReference(referenceIn);
        assertEquals(referenceOut, formattedResponse);
    }

    @Test
    void shouldReturnTrueWhenReferenceIsAlreadyFormatted() {
        String reference = "1234-5678-9012-3456";
        boolean formattedResponse = underTest.isAlreadyFormattedWithHyphens(reference);
        assertEquals(true, formattedResponse);
    }

    @Test
    void shouldReturnFalseWhenReferenceIsNotAlreadyFormatted() {
        String reference = "1234567890123456";
        boolean formattedResponse = underTest.isAlreadyFormattedWithHyphens(reference);
        assertEquals(false, formattedResponse);
    }

}
