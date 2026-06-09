package uk.gov.hmcts.probate.service.docmosis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.DocumentIssueType;
import uk.gov.hmcts.probate.model.DocumentStatus;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.LanguagePreference;
import uk.gov.hmcts.probate.service.CaveatQueryService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.service.notify.SendEmailResponse;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class DocumentTemplateServiceIT {

    @Autowired
    private DocumentTemplateService documentTemplateService;

    @MockitoBean
    private PDFManagementService pdfManagementService;

    @MockitoBean
    private CoreCaseDataApi coreCaseDataApi;

    @MockitoBean
    private CaveatQueryService caveatQueryServiceMock;

    @MockitoBean
    private SendEmailResponse sendEmailResponse;

    @Test
    void shouldGetGrantOfProbateTemplate() {
        DocumentType responseEnglish = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                DocumentCaseType.GOP);
        assertEquals(DocumentType.DIGITAL_GRANT, responseEnglish);

        DocumentType responseEnglishDraft = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,
                DocumentCaseType.GOP);
        assertEquals(DocumentType.DIGITAL_GRANT_DRAFT, responseEnglishDraft);

        DocumentType response = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP);
        assertEquals(DocumentType.DIGITAL_GRANT_REISSUE, response);

        DocumentType responseWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                DocumentCaseType.GOP);
        assertEquals(DocumentType.WELSH_DIGITAL_GRANT, responseWelsh);

        DocumentType responseDraft = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP);
        assertEquals(DocumentType.DIGITAL_GRANT_REISSUE_DRAFT, responseDraft);

        DocumentType responseDraftWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,
                DocumentCaseType.GOP);
        assertEquals(DocumentType.WELSH_DIGITAL_GRANT_DRAFT, responseDraftWelsh);

        DocumentType responseReissueWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP);
        assertEquals(DocumentType.WELSH_DIGITAL_GRANT_REISSUE, responseReissueWelsh);

        DocumentType responseReissueWelshDraft = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.GOP);
        assertEquals(DocumentType.WELSH_DIGITAL_GRANT_REISSUE_DRAFT, responseReissueWelshDraft);

    }


    @Test
    void shouldGetIntestacyTemplate() {
        DocumentType responseEnglish = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.INTESTACY_GRANT, responseEnglish);

        DocumentType responseEnglishDraft = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,
                DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.INTESTACY_GRANT_DRAFT, responseEnglishDraft);

        DocumentType response = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.INTESTACY_GRANT_REISSUE, response);

        DocumentType responseWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.WELSH_INTESTACY_GRANT, responseWelsh);

        DocumentType responseDraft = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.INTESTACY_GRANT_REISSUE_DRAFT, responseDraft);

        DocumentType responseDraftWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,
                DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.WELSH_INTESTACY_GRANT_DRAFT, responseDraftWelsh);

        DocumentType responseReissueWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.WELSH_INTESTACY_GRANT_REISSUE, responseReissueWelsh);

        DocumentType responseReissueWelshDraft = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.INTESTACY);
        assertEquals(DocumentType.WELSH_INTESTACY_GRANT_REISSUE_DRAFT, responseReissueWelshDraft);
    }

    @Test
    void shouldGetAdmonWillTemplate() {
        DocumentType responseEnglish = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.ADMON_WILL_GRANT, responseEnglish);

        DocumentType responseEnglishDraft = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,
                DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.ADMON_WILL_GRANT_DRAFT, responseEnglishDraft);

        DocumentType response = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.ADMON_WILL_GRANT_REISSUE, response);

        DocumentType responseWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.WELSH_ADMON_WILL_GRANT, responseWelsh);

        DocumentType responseDraft = documentTemplateService
            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.ADMON_WILL_GRANT_REISSUE_DRAFT, responseDraft);

        DocumentType responseDraftWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,
                DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.WELSH_ADMON_WILL_GRANT_DRAFT, responseDraftWelsh);

        DocumentType responseReissueWelsh = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE, responseReissueWelsh);

        DocumentType responseReissueWelshDraft = documentTemplateService
            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                DocumentCaseType.ADMON_WILL);
        assertEquals(DocumentType.WELSH_ADMON_WILL_GRANT_REISSUE_DRAFT, responseReissueWelshDraft);

    }

    @Test
    void shouldGetAdColligendaBonaTemplate() {
        assertAll("AdColligendaBonaTemplate",
                () -> {
                    DocumentType responseEnglish = documentTemplateService
                            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                                    DocumentCaseType.AD_COLLIGENDA_BONA);
                    assertEquals(DocumentType.AD_COLLIGENDA_BONA_GRANT, responseEnglish,
                            "Expected AD_COLLIGENDA_BONA_GRANT for English FINAL GRANT");
                },
                () -> {
                    DocumentType responseEnglishDraft = documentTemplateService
                            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,
                                    DocumentCaseType.AD_COLLIGENDA_BONA);
                    assertEquals(DocumentType.AD_COLLIGENDA_BONA_GRANT_DRAFT, responseEnglishDraft,
                            "Expected AD_COLLIGENDA_BONA_GRANT_DRAFT for English PREVIEW GRANT");
                },
                () -> {
                    DocumentType response = documentTemplateService
                            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                                    DocumentCaseType.AD_COLLIGENDA_BONA);
                    assertEquals(DocumentType.AD_COLLIGENDA_BONA_GRANT_REISSUE, response,
                            "Expected AD_COLLIGENDA_BONA_GRANT_REISSUE for English FINAL REISSUE");
                },
                () -> {
                    DocumentType responseWelsh = documentTemplateService
                            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.GRANT,
                                    DocumentCaseType.AD_COLLIGENDA_BONA);
                    assertEquals(DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT, responseWelsh,
                            "Expected WELSH_AD_COLLIGENDA_BONA_GRANT for Welsh FINAL GRANT");
                },
                () -> {
                    DocumentType responseDraft = documentTemplateService
                            .getTemplateId(LanguagePreference.ENGLISH, DocumentStatus.PREVIEW, DocumentIssueType
                                            .REISSUE, DocumentCaseType.AD_COLLIGENDA_BONA);
                    assertEquals(DocumentType.AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT, responseDraft,
                            "Expected AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT for English PREVIEW REISSUE");
                },
                () -> {
                    DocumentType responseDraftWelsh = documentTemplateService
                            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.GRANT,
                                    DocumentCaseType.AD_COLLIGENDA_BONA);
                    assertEquals(DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_DRAFT, responseDraftWelsh,
                            "Expected WELSH_AD_COLLIGENDA_BONA_GRANT_DRAFT for Welsh PREVIEW GRANT");
                },
                () -> {
                    DocumentType responseReissueWelsh = documentTemplateService
                            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.FINAL, DocumentIssueType.REISSUE,
                                    DocumentCaseType.AD_COLLIGENDA_BONA);
                    assertEquals(DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE, responseReissueWelsh,
                            "Expected WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE for Welsh FINAL REISSUE");
                },
                () -> {
                    DocumentType responseReissueWelshDraft = documentTemplateService
                            .getTemplateId(LanguagePreference.WELSH, DocumentStatus.PREVIEW, DocumentIssueType.REISSUE,
                                    DocumentCaseType.AD_COLLIGENDA_BONA);
                    assertEquals(DocumentType.WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT, responseReissueWelshDraft,
                            "Expected WELSH_AD_COLLIGENDA_BONA_GRANT_REISSUE_DRAFT for Welsh PREVIEW REISSUE");
                }
        );
    }
}
