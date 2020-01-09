package uk.gov.hmcts.probate.service.docmosis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.documents.DocumentTemplates;
import uk.gov.hmcts.probate.model.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentTemplateService {

    private final DocumentTemplates documentTemplates;

    public DocumentType getTemplateId(LanguagePreference languagePreference, DocumentStatus documentStatus, DocumentIssueType documentIssueType, DocumentCaseType documentCaseType) {
        return documentTemplates.getDocmosis().get(languagePreference).get(documentStatus).get(documentIssueType).get(documentCaseType);
    }
}
