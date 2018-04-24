package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FileSystemResourceServiceTest {

    @InjectMocks
    private FileSystemResourceService underTest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getFileSystemResourceSuccess() {
        Optional<FileSystemResource> resource = underTest.getFileSystemResource("success.json");
        assertTrue(resource.isPresent());
    }

    @Test
    public void getFileSystemResourceAsStringSuccess() {
        String resource = underTest.getFileFromResourceAsString("success.json");
        assertNotNull(resource);
    }

    @Test
    public void getFileSystemResourceFileNotFound() {
        Optional<FileSystemResource> resource = underTest.getFileSystemResource("file_not_found.json");
        assertTrue(!resource.isPresent());
    }
}
