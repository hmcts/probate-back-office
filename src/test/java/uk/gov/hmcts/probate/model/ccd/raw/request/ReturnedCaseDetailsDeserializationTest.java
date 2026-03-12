package uk.gov.hmcts.probate.model.ccd.raw.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringBootTest.class })
@JsonTest
class ReturnedCaseDetailsDeserializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldDeserializeLastModifiedWith3DigitFraction() throws Exception {
        String json = """
        {
          "cases": [
            {
              "case_data": { "deceasedSurname": "Smith" },
              "last_modified": "2026-02-26T16:53:45.979",
              "id": 1
            }
          ],
          "total": 1
        }
            """;

        ReturnedCases returnedCases = objectMapper.readValue(json, ReturnedCases.class);

        assertAll("3-digit last_modified deserialization",
                () -> assertEquals(1, returnedCases.getCases().size()),
                () -> assertEquals(1L, returnedCases.getCases().getFirst().getId()),
                () -> assertEquals("Smith", returnedCases.getCases().getFirst().getData().getDeceasedSurname()),
                () -> assertEquals(LocalDateTime.parse("2026-02-26T16:53:45.979"),
                        returnedCases.getCases().getFirst().getLastModified())
        );
    }

    @Test
    void shouldDeserializeLastModifiedWith6DigitFraction() throws Exception {
        String json = """
        {
          "cases": [
            {
              "case_data": { "deceasedSurname": "Smith" },
              "last_modified": "2026-02-26T16:53:45.979202",
              "id": 1
            }
          ],
          "total": 1
        }
            """;

        ReturnedCases returnedCases = objectMapper.readValue(json, ReturnedCases.class);

        assertAll("6-digit last_modified deserialization",
                () -> assertEquals(1, returnedCases.getCases().size()),
                () -> assertEquals(1L, returnedCases.getCases().getFirst().getId()),
                () -> assertEquals(LocalDateTime.parse("2026-02-26T16:53:45.979202"),
                        returnedCases.getCases().getFirst().getLastModified())
        );
    }
}