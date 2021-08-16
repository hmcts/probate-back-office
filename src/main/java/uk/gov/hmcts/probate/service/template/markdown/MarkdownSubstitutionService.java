package uk.gov.hmcts.probate.service.template.markdown;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.template.MarkdownTemplate;
import uk.gov.hmcts.probate.model.template.TemplateResponse;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownSubstitutionService {

    private final FileSystemResourceService fileSystemResourceService;

    public TemplateResponse generatePage(String templateDir, MarkdownTemplate markdownTemplate,
                                         Map<String, String> keyValue) {
        return this.getTemplateFile(templateDir, markdownTemplate)
            .map(FileSystemResource::getFile)
            .map(file -> {
                try {
                    return FileUtils.readFileToString(file, Charset.defaultCharset());
                } catch (IOException e) {
                    log.warn("Cannot parse file {}/{}.md", templateDir, markdownTemplate.getFilename(), e);
                    return null;
                }
            })
            .map(file -> this.substitute(keyValue, file))
            .map(TemplateResponse::new)
            .orElseThrow(() ->
                new RuntimeException("Cannot parse file " + templateDir + markdownTemplate.getFilename() + ".md")
            );
    }

    private String substitute(Map<String, String> paramValues, String data) {
        for (Map.Entry<String, String> entry : paramValues.entrySet()) {
            data = StringUtils.replace(data, entry.getKey(), entry.getValue());
        }

        return data;
    }

    private Optional<FileSystemResource> getTemplateFile(String templateDir, MarkdownTemplate markdownTemplate) {
        String templateFilename = markdownTemplate.getFilename();
        String templatePath = templateDir + templateFilename + ".md";
        return fileSystemResourceService.getFileSystemResource(templatePath);
    }
}
