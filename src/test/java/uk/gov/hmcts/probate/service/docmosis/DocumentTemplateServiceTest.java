package uk.gov.hmcts.probate.service.docmosis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.SendEmailResponse;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentTemplateServiceTest {

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @MockBean
    private PDFManagementService pdfManagementService;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockBean
    private CaveatQueryService caveatQueryServiceMock;

    @MockBean
    private AppInsights appInsights;

    @MockBean
    private SendEmailResponse sendEmailResponse;

    @Test
    public void shouldGetGrantOfProbateTemplate() {
        DocumentType responseEnglish = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.GRANT,DocumentCaseType.GOP);
        assertEquals(DocumentType.DIGITAL_GRANT, responseEnglish);

        DocumentType responseEnglishDraft = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,DocumentCaseType.GOP);
        assertEquals(DocumentType.DIGITAL_GRANT_DRAFT, responseEnglishDraft);

        DocumentType response = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,DocumentCaseType.GOP);
        assertEquals(DocumentType.DIGITAL_GRANT_REISSUE, response);

        DocumentType responseWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.GRANT, DocumentCaseType.GOP);
        assertEquals(DocumentType.WELSH_DIGITAL_GRANT, responseWelsh);

        DocumentType responseDraft = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,DocumentCaseType.GOP);
        assertEquals(DocumentType.DIGITAL_GRANT_REISSUE_DRAFT, responseDraft);

        DocumentType responseDraftWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW,  DocumentIssueType.GRANT,DocumentCaseType.GOP);
        assertEquals(DocumentType.WELSH_DIGITAL_GRANT_DRAFT, responseDraftWelsh);

        DocumentType responseReissueWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,DocumentCaseType.GOP);
        assertEquals(DocumentType.WELSH_DIGITAL_GRANT_REISSUE, responseReissueWelsh);

        DocumentType responseReissueWelshDraft = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,DocumentCaseType.GOP);
        assertEquals(DocumentType.WELSH_DIGITAL_GRANT_REISSUE_DRAFT, responseReissueWelshDraft);

    }


    @Test
    public void shouldGetIntestacyTemplate() {
        DocumentType responseEnglish = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.GRANT,DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.INTESTACY_GRANT, responseEnglish);

        DocumentType responseEnglishDraft = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.INTESTACY_GRANT_DRAFT, responseEnglishDraft);

        DocumentType response = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE, DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.INTESTACY_GRANT_REISSUE, response);

        DocumentType responseWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.GRANT, DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.WELSH_INTESTACY_GRANT, responseWelsh);

        DocumentType responseDraft = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE, DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.INTESTACY_GRANT_REISSUE_DRAFT, responseDraft);

        DocumentType responseDraftWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT, DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.WELSH_INTESTACY_GRANT_DRAFT, responseDraftWelsh);
    
        DocumentType responseReissueWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.WELSH_INTESTACY_GRANT_REISSUE, responseReissueWelsh);

        DocumentType responseReissueWelshDraft = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.WELSH_INTESTACY_GRANT_REISSUE_DRAFT, responseReissueWelshDraft);
    }

    @Test
    public void shouldGetAdmonWillTemplate() {
        DocumentType responseEnglish = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.GRANT,DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.ADMON_WILL_GRANT, responseEnglish);

        DocumentType responseEnglishDraft = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.ADMON_WILL_GRANT_DRAFT, responseEnglishDraft);

        DocumentType response = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.ADMON_WILL_GRANT_REISSUE, response);

        DocumentType responseWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.GRANT,DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.WELSH_ADMON_WILL_GRANT, responseWelsh);

        DocumentType responseDraft = documentTemplateService.getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE, DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT, responseDraft);

        DocumentType responseDraftWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.WELSH_ADMON_WILL_GRANT_DRAFT, responseDraftWelsh);

        DocumentType responseReissueWelsh = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE, responseReissueWelsh);

        DocumentType responseReissueWelshDraft = documentTemplateService.getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT, responseReissueWelshDraft);

    }
}
