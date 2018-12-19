package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

public class DocumentTest {
    @Test
    public void canDeserialiseDateAdded() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Document document = Document.builder().documentDateAdded(LocalDate.now()).build();
        String json = objectMapper.writeValueAsString(document);

        Document documentFromJson = objectMapper.readValue(json, Document.class);
        Assert.assertEquals(document.getDocumentDateAdded(), documentFromJson.getDocumentDateAdded());
    }
}
