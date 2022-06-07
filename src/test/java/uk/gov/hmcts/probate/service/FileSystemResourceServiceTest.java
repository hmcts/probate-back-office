package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileSystemResourceServiceTest {

    @InjectMocks
    private FileSystemResourceService underTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFileSystemResourceSuccess() {
        Optional<FileSystemResource> resource = underTest.getFileSystemResource("success.json");
        assertTrue(resource.isPresent());
    }

    @Test
    void getFileSystemResourceAsStringSuccess() {
        String resource = underTest.getFileFromResourceAsString("success.json");
        assertNotNull(resource);
    }

    @Test
    void getFileSystemResourceFileNotFound() {
        Optional<FileSystemResource> resource = underTest.getFileSystemResource("file_not_found.json");
        assertTrue(!resource.isPresent());
    }

    @Test
    void shouldReturnNullWhenGettingFileFromResourceStringAndFileIsNotPresent() {
        FileSystemResourceService fileSystemResourceServiceSpy = Mockito.spy(new FileSystemResourceService());
        when(fileSystemResourceServiceSpy.getFileSystemResource(anyString())).thenReturn(Optional.empty());

        String resource = fileSystemResourceServiceSpy.getFileFromResourceAsString("");

        assertNull(resource);
    }

    @Test
    void shouldReturnNullFromResourceStringAndIOExceptionIsThrown() {
        FileSystemResourceService fileSystemResourceServiceSpy = Mockito.spy(new FileSystemResourceService());
        File mockFile = Mockito.mock(File.class);
        FileSystemResource fileSystemResource = mock(FileSystemResource.class);
        Mockito.when(fileSystemResource.getFile()).thenReturn(mockFile);
        when(fileSystemResourceServiceSpy.getFileSystemResource(anyString()))
            .thenReturn(Optional.of(fileSystemResource));

        String resource = fileSystemResourceServiceSpy.getFileFromResourceAsString("");

        assertNull(resource);
    }
}
