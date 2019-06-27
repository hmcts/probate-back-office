package uk.gov.hmcts.probate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.probate.config.properties.registries.RegistriesProperties;
import uk.gov.hmcts.probate.config.properties.registries.Registry;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.docmosis.GenericMapperService;
import uk.gov.hmcts.probate.service.template.pdf.PDFManagementService;
import uk.gov.hmcts.reform.probate.model.cases.CaseType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT_DRAFT;

public class DocumentGeneratorServiceTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private static final String CREST_IMAGE = "GrantOfProbateCrest";
    private static final String SEAL_IMAGE = "GrantOfProbateSeal";
    private static final String CREST_FILE_PATH = "crestImage.txt";
    private static final String SEAL_FILE_PATH = "sealImage.txt";
    private static final String REGISTRY_LOCATION = "bristol";
    private static final String DIGITAL_GRANT_REISSUE_FILE_NAME = "digitalGrantDraftReissue.pdf";
    private CallbackRequest callbackRequest;

    @InjectMocks
    private DocumentGeneratorService documentGeneratorService;

    @Mock
    private PDFManagementService pdfManagementService;

    @Mock
    private GenericMapperService genericMapperService;

    @Mock
    private RegistryDetailsService registryDetailsService;

    @Mock
    private DocumentService documentService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Registry registry = new Registry();
        registry.setPhone("01010101010101");
        registry.setAddressLine1("addressLine1");
        registry.setAddressLine2("addressLine2");
        registry.setAddressLine3("addressLine3");
        registry.setAddressLine4("addressLine4");
        registry.setPostcode("postcode");
        registry.setTown("town");

        Map<String, Registry> registryMap = new HashMap<>();
        registryMap.put(REGISTRY_LOCATION, registry);
        registryMap.put(CTSC, registry);

        CaseData caseData = CaseData.builder()
                .caseType("gop")
                .deceasedSurname("Smith")
                .deceasedForenames("John")
                .registryLocation("Bristol")
                .build();
        CaseDetails caseDetails = new CaseDetails(caseData, LAST_MODIFIED, CASE_ID);
        callbackRequest = new CallbackRequest(caseDetails);

        CaseDetails returnedCaseDetails = caseDetails;
        returnedCaseDetails.setRegistryTelephone("01010101010101");
        returnedCaseDetails.setRegistryAddressLine1("addressLine1");
        returnedCaseDetails.setRegistryAddressLine2("addressLine2");
        returnedCaseDetails.setRegistryAddressLine3("addressLine3");
        returnedCaseDetails.setRegistryAddressLine4("addressLine4");
        returnedCaseDetails.setRegistryPostcode("postcode");
        returnedCaseDetails.setRegistryTown("town");
        returnedCaseDetails.setCtscTelephone("01010101010101");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> expectedMap = mapper.convertValue(caseData, Map.class);

        when(registryDetailsService.getRegistryDetails(caseDetails)).thenReturn(returnedCaseDetails);

        when(genericMapperService.caseDataWithImages(any(), any())).thenReturn(expectedMap);
        when(pdfManagementService.generateDocmosisDocumentAndUpload(expectedMap,
                DocumentType.DIGITAL_GRANT_DRAFT_REISSUE, true))
                .thenReturn(Document.builder().documentFileName(DIGITAL_GRANT_REISSUE_FILE_NAME).build());
        doNothing().when(documentService).expire(any(CallbackRequest.class), any());
    }

    @Test
    public void testGenerateReissueDraftProducesCorrectDocument() {
        assertEquals(DIGITAL_GRANT_REISSUE_FILE_NAME,
                documentGeneratorService.generateGrantReissueDraft(callbackRequest).getDocumentFileName());
    }
}
