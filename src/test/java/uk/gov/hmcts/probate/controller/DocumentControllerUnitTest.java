package uk.gov.hmcts.probate.controller;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.service.BulkPrintService;
import uk.gov.hmcts.probate.service.DocumentGeneratorService;
import uk.gov.hmcts.probate.service.DocumentValidation;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.RegistryDetailsService;
import uk.gov.hmcts.probate.service.ReprintService;
import uk.gov.hmcts.probate.service.documentmanagement.DocumentManagementService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.WillLodgementCallbackResponseTransformer;
import uk.gov.hmcts.probate.validator.BulkPrintValidationRule;
import uk.gov.hmcts.probate.validator.EmailAddressNotificationValidationRule;
import uk.gov.hmcts.probate.validator.RedeclarationSoTValidationRule;
import uk.gov.hmcts.reform.ccd.document.am.model.Document;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentControllerUnitTest {

    private static final String VALID_FILE_NAME = "valid_file.png";

    @Mock
    private DocumentGeneratorService documentGeneratorService;
    @Mock
    private RegistryDetailsService registryDetailsService;
    @Mock
    private PDFManagementService pdfManagementService;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformer;
    @Mock
    private WillLodgementCallbackResponseTransformer willLodgementCallbackResponseTransformer;
    @Mock
    private NotificationService notificationService;
    @Mock
    private RegistriesProperties registriesProperties;
    @Mock
    private BulkPrintService bulkPrintService;
    @Mock
    private EventValidationService eventValidationService;
    @Mock
    private List<EmailAddressNotificationValidationRule> emailAddressNotificationValidationRules;
    @Mock
    private List<BulkPrintValidationRule> bulkPrintValidationRules;
    @Mock
    private RedeclarationSoTValidationRule redeclarationSoTValidationRule;
    @Mock
    private ReprintService reprintService;
    @Mock
    private DocumentValidation documentValidation;
    @Mock
    private DocumentManagementService documentManagementService;

    private DocumentController documentController;

    private static final String DUMMY_OAUTH_2_TOKEN = "oauth2Token";
    private static final String DUMMY_SAUTH_TOKEN = "serviceToken";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        documentValidation = new DocumentValidation();
        ReflectionTestUtils.setField(documentValidation,
            "allowedFileExtensions", ".pdf .jpeg .bmp .tif .tiff .png .pdf");
        ReflectionTestUtils.setField(documentValidation,
            "allowedMimeTypes", "image/jpeg application/pdf image/tiff image/png image/bmp");

        documentController = new DocumentController(documentGeneratorService, registryDetailsService,
            pdfManagementService, callbackResponseTransformer, willLodgementCallbackResponseTransformer,
            notificationService, registriesProperties, bulkPrintService, eventValidationService, 
            emailAddressNotificationValidationRules, bulkPrintValidationRules, redeclarationSoTValidationRule,
            reprintService, documentValidation, documentManagementService);
    }

    @Test
    public void shouldReturnErrorIfThereAreNoFilesInTheRequest() {
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("Error: no files passed");

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, DUMMY_SAUTH_TOKEN, null);
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void shouldReturnErrorForEmptyFileList() {
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("Error: no files passed");

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, DUMMY_SAUTH_TOKEN, 
            Collections.emptyList());
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void shouldReturnErrorForTooManyFiles() {
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("Error: too many files");

        MultipartFile file = Mockito.mock(MultipartFile.class);
        List<MultipartFile> files = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            files.add(file);
        }

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, DUMMY_SAUTH_TOKEN, files);
        assertThat(actualResult, equalTo(expectedResult));

    }

    @Test
    public void shouldReturnErrorForInvalidFileExtension() {
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("Error: invalid file type");
        MockMultipartFile file = new MockMultipartFile("testData", "filename.txt", "text/plain", "some xml".getBytes());
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, DUMMY_SAUTH_TOKEN, files);
        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    public void shouldUploadSuccessfully() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/" + VALID_FILE_NAME));


        MockMultipartFile file = new MockMultipartFile(VALID_FILE_NAME, VALID_FILE_NAME, "image/jpeg", bytes);
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);

        UploadResponse uploadResponseMock = mock(UploadResponse.class);
        Document.Links links = new Document.Links();
        Document.Link selfLink = new Document.Link();
        selfLink.href = "someHref";
        links.self = selfLink;
        Document document = Document.builder().links(links).build();
        when(uploadResponseMock.getDocuments()).thenReturn(Collections.singletonList(document));
        
        when(documentManagementService.uploadForCitizen(any(List.class), any(String.class), any(DocumentType.class)))
            .thenReturn(uploadResponseMock);

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, DUMMY_SAUTH_TOKEN, files);
        assertThat(actualResult, hasItems());
    }
}
