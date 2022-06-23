package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentTest {
    @Test
    void canDeserialiseDateAdded() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Document document = Document.builder().documentDateAdded(LocalDate.now()).build();
        String json = objectMapper.writeValueAsString(document);

        Document documentFromJson = objectMapper.readValue(json, Document.class);
        assertEquals(document.getDocumentDateAdded(), documentFromJson.getDocumentDateAdded());
    }
}
