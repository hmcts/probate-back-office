package uk.gov.hmcts.probate.service.template.markdown;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MarkdownSubstitutionServiceTest {

    private static final String RESOURCE_PATH = "template/nextSteps.md";

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    @Mock
    private FileSystemResource fileSystemResource;

    private File file;

    @InjectMocks
    private MarkdownSubstitutionService underTest;

    @Before
    public void setup() {
        initMocks(this);
        file = Optional.ofNullable(this.getClass().getClassLoader().getResource(RESOURCE_PATH))
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

        Map<String, String> keyValue = Maps.newHashMap("name", "markdown");

        TemplateResponse response = underTest.generatePage(RESOURCE_PATH, MarkdownTemplate.NEXT_STEPS, keyValue);

        assertTrue(response != null);
        verify(fileSystemResourceService).getFileSystemResource(any(String.class));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenGeneratePageCouldNotParseFile() {
        when(fileSystemResourceService.getFileSystemResource(any(String.class)))
            .thenReturn(Optional.empty());

        Map<String, String> keyValue = Maps.newHashMap("name", "markdown");
        underTest.generatePage(RESOURCE_PATH, MarkdownTemplate.NEXT_STEPS, keyValue);
    }
}
