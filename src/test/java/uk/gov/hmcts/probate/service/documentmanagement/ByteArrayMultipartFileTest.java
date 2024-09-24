package uk.gov.hmcts.probate.service.documentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ByteArrayMultipartFileTest {

    @Test
    void shouldGetAllAttributes() {
        MediaType contentType = MediaType.APPLICATION_JSON;
        byte[] bytes = "SomeString".getBytes(StandardCharsets.UTF_8);
        ByteArrayMultipartFile byteArrayMultipartFile = ByteArrayMultipartFile.builder()
            .content(bytes)
            .contentType(contentType)
            .name("name")
            .build();

        assertEquals(bytes, byteArrayMultipartFile.getBytes());
        assertEquals(bytes, byteArrayMultipartFile.getContent());
        assertEquals(contentType.toString(), byteArrayMultipartFile.getContentType());
        assertEquals(10, byteArrayMultipartFile.getSize());
        assertEquals("name", byteArrayMultipartFile.getName());
        assertEquals("name", byteArrayMultipartFile.getOriginalFilename());
        assertEquals(false, byteArrayMultipartFile.isEmpty());
        assertEquals(true, byteArrayMultipartFile.getInputStream() != null);
    }

    @Test
    public void shotNotTransfer() throws IllegalStateException {
        MediaType contentType = MediaType.APPLICATION_JSON;
        byte[] bytes = "SomeString".getBytes(StandardCharsets.UTF_8);
        ByteArrayMultipartFile byteArrayMultipartFile = ByteArrayMultipartFile.builder()
            .content(bytes)
            .contentType(contentType)
            .name("name")
            .build();
        assertThrows(UnsupportedOperationException.class, () -> {
            byteArrayMultipartFile.transferTo(new File(""));
        });
    }

    @Test
    void shouldEqual() throws IllegalStateException {
        MediaType contentType = MediaType.APPLICATION_JSON;
        byte[] bytes = "SomeString".getBytes(StandardCharsets.UTF_8);
        ByteArrayMultipartFile byteArrayMultipartFile1 = ByteArrayMultipartFile.builder()
            .content(bytes)
            .contentType(contentType)
            .name("name")
            .build();
        ByteArrayMultipartFile byteArrayMultipartFile2 = ByteArrayMultipartFile.builder()
            .content(bytes)
            .contentType(contentType)
            .name("name")
            .build();
        assertEquals(byteArrayMultipartFile1, byteArrayMultipartFile2);
    }
}