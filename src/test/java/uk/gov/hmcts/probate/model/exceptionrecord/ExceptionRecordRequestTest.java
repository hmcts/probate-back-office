package uk.gov.hmcts.probate.model.exceptionrecord;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionRecordRequestTest {

    @Test
    void shouldReturnFormScannedDate() {
        LocalDateTime scannedDate = LocalDateTime.of(2024, 5, 10, 12, 30);

        ExceptionRecordRequest request = new ExceptionRecordRequest(
                "erId",
                "caseTypeId",
                "poBox",
                "jurisdiction",
                "formType",
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(
                        new InputScannedDoc("cover-sheet", null, null, null, null, LocalDateTime.now(), null),
                        new InputScannedDoc("form", null, null, null, null, scannedDate, null)
                ),
                List.of(),
                "envelopeId",
                false
        );

        assertEquals(
                Optional.of(LocalDate.of(2024, 5, 10)),
                request.getFormScannedDate()
        );
    }

    @Test
    void shouldReturnEmptyWhenNoFormScannedDocumentFound() {
        ExceptionRecordRequest request = new ExceptionRecordRequest(
                "erId",
                "caseTypeId",
                "poBox",
                "jurisdiction",
                "formType",
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(
                        new InputScannedDoc("will", null, null, null, null, LocalDateTime.now(), null)
                ),
                List.of(),
                "envelopeId",
                false
        );

        assertTrue(request.getFormScannedDate().isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenFormScannedDateIsNull() {
        ExceptionRecordRequest request = new ExceptionRecordRequest(
                "erId",
                "caseTypeId",
                "poBox",
                "jurisdiction",
                "formType",
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(
                        new InputScannedDoc("form", null, null, null, null, null, null)
                ),
                List.of(),
                "envelopeId",
                false
        );

        assertTrue(request.getFormScannedDate().isEmpty());
    }

}
