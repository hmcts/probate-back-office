package uk.gov.hmcts.probate.config.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.LanguagePreference;

import javax.validation.Valid;
import java.util.Map;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties("documents.templates")
public class DocumentTemplates {
    @Valid
    private Map<LanguagePreference, Map<DocumentStatus, Map<DocumentIssueType, Map<DocumentCaseType, DocumentType>>>>
        docmosis;
}
