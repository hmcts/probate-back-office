package uk.gov.hmcts.probate.config.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.model.DocumentIssueType;

import java.util.Map;
import javax.validation.Valid;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties("documents.templates")
public class DocumentTemplates {
    @Valid
    private Map<LanguagePreference, Map<DocumentStatus, Map<DocumentIssueType, Map<DocumentCaseType, DocumentType>>>> docmosis;
}
