package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void shouldReturnNullWhenGettingFileFromResourceStringAndFileIsNotPresent() {
        FileSystemResourceService fileSystemResourceServiceSpy = Mockito.spy(new FileSystemResourceService());
        when(fileSystemResourceServiceSpy.getFileSystemResource(anyString())).thenReturn(Optional.empty());

        String resource = fileSystemResourceServiceSpy.getFileFromResourceAsString("");

        assertNull(resource);
    }

    @Test
    public void shouldReturnNullFromResourceStringAndIOExceptionIsThrown() {
        FileSystemResourceService fileSystemResourceServiceSpy = Mockito.spy(new FileSystemResourceService());
        File mockFile = Mockito.mock(File.class);
        FileSystemResource fileSystemResource = mock(FileSystemResource.class);
        Mockito.when(fileSystemResource.getFile()).thenReturn(mockFile);
        when(fileSystemResourceServiceSpy.getFileSystemResource(anyString())).thenReturn(Optional.of(fileSystemResource));

        String resource = fileSystemResourceServiceSpy.getFileFromResourceAsString("");

        assertNull(resource);
    }
}
