package uk.gov.hmcts.probate.service.template.markdown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class MarkdownSubstitutionServiceTest {

    private static final String RESOURCE_PATH = "template/nextSteps.md";

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    @Mock
    private FileSystemResource fileSystemResource;

    @InjectMocks
    private MarkdownSubstitutionService underTest;

    @BeforeEach
    public void setup() {
        openMocks(this);
        File file = Optional.ofNullable(this.getClass().getClassLoader().getResource(RESOURCE_PATH))
                .map(URL::getPath)
                .map(FileSystemResource::new)
                .get()
                .getFile();

        when(fileSystemResource.getFile()).thenReturn(file);
    }

    @Test
    public void shouldGenerateMarkdown() {
        when(fileSystemResourceService.getFileSystemResource(any(String.class)))
                .thenReturn(Optional.of(fileSystemResource));

        Map<String, String> keyValue = Collections.singletonMap("name", "markdown");

        TemplateResponse response = underTest.generatePage(RESOURCE_PATH, MarkdownTemplate.NEXT_STEPS, keyValue);

        assertNotNull(response);
        verify(fileSystemResourceService).getFileSystemResource(any(String.class));
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenGeneratePageCouldNotParseFile() {
        assertThrows(RuntimeException.class, () -> {
            when(fileSystemResourceService.getFileSystemResource(any(String.class)))
                    .thenReturn(Optional.empty());

            Map<String, String> keyValue = Collections.singletonMap("name", "markdown");
            underTest.generatePage(RESOURCE_PATH, MarkdownTemplate.NEXT_STEPS, keyValue);
        });
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenFileDoesNotExist() {
        assertThrows(RuntimeException.class, () -> {
            when(fileSystemResource.getFile()).thenReturn(new File(UUID.randomUUID().toString()));
            when(fileSystemResourceService.getFileSystemResource(any(String.class)))
                    .thenReturn(Optional.of(fileSystemResource));

            Map<String, String> keyValue = Collections.singletonMap("name", "markdown");

            underTest.generatePage(RESOURCE_PATH, MarkdownTemplate.NEXT_STEPS, keyValue);
        });
    }
}
