package uk.gov.hmcts.probate.service.exceptionrecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.LocalDateTimeSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.exceptionrecord.CaveatCaseUpdateRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulCaveatUpdateResponse;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
import uk.gov.hmcts.probate.service.CaveatNotificationService;
import uk.gov.hmcts.probate.service.EventValidationService;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ExceptionRecordCaveatMapper;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ExceptionRecordGrantOfRepresentationMapper;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ScannedDocumentMapper;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.probate.model.cases.ApplicationType;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;

@RunWith(SpringRunner.class)
public class ExceptionRecordServiceTest {

    private static final String EXCEPTION_RECORD_CAVEAT_CASE_TYPE_ID = "Caveat";
    private static final String EXCEPTION_RECORD_CAVEAT_EVENT_ID = "raiseCaveat";
    private static final String EXCEPTION_RECORD_GOR_CASE_TYPE_ID = "GrantofRepresentation";
    private static final String EXCEPTION_RECORD_GOR_EVENT_ID = "caseCreated";
    @Mock
    CaveatNotificationService caveatNotificationService;
    @InjectMocks
    private ExceptionRecordService erService;

    @Mock
    private ExceptionRecordCaveatMapper erCaveatMapper;

    @Mock
    private ExceptionRecordGrantOfRepresentationMapper erGrantOfRepresentationMapper;

    @Mock
    private ScannedDocumentMapper documentMapper;

    @Mock
    private CaveatCallbackResponseTransformer caveatTransformer;

    @Mock
    private CallbackResponseTransformer grantOfProbatetransformer;

    @Mock
    private EventValidationService eventValidationService;

    private TestUtils testUtils = new TestUtils();

    private ExceptionRecordRequest erRequestCaveat;

    private ExceptionRecordRequest erRequestGrantOfProbate;

    private CaveatCaseUpdateRequest caveatCaseUpdateRequest;

    private String exceptionRecordPayloadPA8A;

    private String exceptionRecordPayloadPA1P;

    private List<String> warnings;

    private CaveatData caveatData;

    private GrantOfRepresentationData grantOfRepresentationData;

    private CaseCreationDetails caveatCaseDetailsResponse;

    private CaseCreationDetails grantOfProbateCaseDetailsResponse;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        exceptionRecordPayloadPA8A =
            testUtils.getStringFromFile("expectedExceptionRecordDataCitizenPA8A.json");

        exceptionRecordPayloadPA1P =
            testUtils.getStringFromFile("expectedExceptionRecordDataCitizenSingleExecutorPA1P.json");

        erRequestCaveat = getObjectMapper().readValue(exceptionRecordPayloadPA8A, ExceptionRecordRequest.class);

        erRequestGrantOfProbate = getObjectMapper().readValue(exceptionRecordPayloadPA1P, ExceptionRecordRequest.class);

        warnings = new ArrayList<String>();
        caveatData = new CaveatData();
        caveatData.setRegistryLocation(RegistryLocation.CTSC);
        caveatData.setCaveatorSurname("Jones");
        caveatData.setApplicationType(ApplicationType.PERSONAL);
        caveatCaseDetailsResponse = CaseCreationDetails.builder().<ResponseCaveatData>
            eventId(EXCEPTION_RECORD_CAVEAT_EVENT_ID).caseData(caveatData)
            .caseTypeId(EXCEPTION_RECORD_CAVEAT_CASE_TYPE_ID).build();

        grantOfRepresentationData = new GrantOfRepresentationData();
        grantOfRepresentationData.setRegistryLocation(RegistryLocation.CTSC);
        grantOfRepresentationData.setPrimaryApplicantSurname("Smith");
        grantOfRepresentationData.setApplicationType(ApplicationType.PERSONAL);
        grantOfRepresentationData.setGrantType(GrantType.GRANT_OF_PROBATE);
        grantOfProbateCaseDetailsResponse =
            CaseCreationDetails.builder().<ResponseCaveatData>eventId(EXCEPTION_RECORD_GOR_EVENT_ID)
                .caseData(grantOfRepresentationData)
                .caseTypeId(EXCEPTION_RECORD_GOR_CASE_TYPE_ID).build();

        when(erCaveatMapper.toCcdData(any())).thenReturn(caveatData);
        when(erGrantOfRepresentationMapper.toCcdData(any(), any())).thenReturn(grantOfRepresentationData);
        when(caveatTransformer.bulkScanCaveatCaseTransform(any())).thenReturn(caveatCaseDetailsResponse);
        when(grantOfProbatetransformer.bulkScanGrantOfRepresentationCaseTransform(any()))
            .thenReturn(grantOfProbateCaseDetailsResponse);
    }

    @Test
    public void createCaveatCaseFromExceptionRecord() {
        SuccessfulTransformationResponse response =
            erService.createCaveatCaseFromExceptionRecord(erRequestCaveat, warnings);
        CaveatData caveatDataResponse = (CaveatData) response.getCaseCreationDetails().getCaseData();
        assertEquals(RegistryLocation.CTSC, caveatDataResponse.getRegistryLocation());
        assertEquals(ApplicationType.PERSONAL, caveatDataResponse.getApplicationType());
        assertEquals("Jones", caveatDataResponse.getCaveatorSurname());
        assertEquals(NO, caveatDataResponse.getIsAutomatedProcess());
    }

    @Test
    public void testRequestForPA8A() {
        assertEquals("qwertyuio", erRequestCaveat.getEnvelopeId());
        assertEquals(false, erRequestCaveat.getIsAutomatedProcess());
    }

    @Test
    public void testRequestForPA1P() {
        assertEquals("qwertyuio", erRequestGrantOfProbate.getEnvelopeId());
        assertEquals(false, erRequestGrantOfProbate.getIsAutomatedProcess());
    }

    @Test
    public void shouldUpdateCaveatCaseFromExceptionRecord() throws IOException, NotificationClientException {
        exceptionRecordPayloadPA8A = testUtils.getStringFromFile("updateExceptionRecordDataPA8A.json");
        caveatCaseUpdateRequest =
            getObjectMapper().readValue(exceptionRecordPayloadPA8A, CaveatCaseUpdateRequest.class);
        CaveatCallbackResponse caveatCallbackResponse = Mockito.mock(CaveatCallbackResponse.class);
        when(caveatCallbackResponse.getErrors()).thenReturn(Collections.emptyList());
        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), nullable(List.class)))
            .thenReturn(caveatCallbackResponse);
        when(caveatNotificationService.caveatExtend(any(CaveatCallbackRequest.class)))
            .thenReturn(caveatCallbackResponse);
        ResponseCaveatData responseCaseveatData = Mockito.mock(ResponseCaveatData.class);
        when(responseCaseveatData.getScannedDocuments()).thenReturn(Arrays.asList(
            new CollectionMember(null, null)));
        when(caveatCallbackResponse.getCaveatData()).thenReturn(responseCaseveatData);

        SuccessfulCaveatUpdateResponse response =
            erService.updateCaveatCaseFromExceptionRecord(caveatCaseUpdateRequest);
        List<CollectionMember<ScannedDocument>> scannedDocuments =
            response.getCaseUpdateDetails().getCaseData().getScannedDocuments();
        assertEquals(1, scannedDocuments.size());
    }

    @Test
    public void shouldUpdateCaveatCaseFromExceptionRecordWithUnmatchedCaveatNumbersWarning()
        throws IOException, NotificationClientException {
        exceptionRecordPayloadPA8A =
            testUtils.getStringFromFile("updateExceptionRecordDataPA8ADiffCaseNumbers.json");
        caveatCaseUpdateRequest =
            getObjectMapper().readValue(exceptionRecordPayloadPA8A, CaveatCaseUpdateRequest.class);
        CaveatCallbackResponse caveatCallbackResponse = Mockito.mock(CaveatCallbackResponse.class);
        when(caveatCallbackResponse.getErrors()).thenReturn(Collections.emptyList());
        List warnings = new ArrayList();
        when(caveatCallbackResponse.getWarnings()).thenReturn(warnings);
        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), nullable(List.class)))
            .thenReturn(caveatCallbackResponse);
        when(caveatNotificationService.caveatExtend(any(CaveatCallbackRequest.class)))
            .thenReturn(caveatCallbackResponse);
        ResponseCaveatData responseCaseveatData = Mockito.mock(ResponseCaveatData.class);
        when(responseCaseveatData.getScannedDocuments()).thenReturn(Arrays.asList(
            new CollectionMember(null, null)));
        when(caveatCallbackResponse.getCaveatData()).thenReturn(responseCaseveatData);

        SuccessfulCaveatUpdateResponse response =
            erService.updateCaveatCaseFromExceptionRecord(caveatCaseUpdateRequest);
        List<CollectionMember<ScannedDocument>> scannedDocuments =
            response.getCaseUpdateDetails().getCaseData().getScannedDocuments();
        assertEquals(1, scannedDocuments.size());
        assertEquals(1, response.warnings.size());
    }

    @Test(expected = OCRMappingException.class)
    public void shouldNotUpdateCaveatCaseFromExceptionRecordNoAdditionalDocuments()
        throws IOException, NotificationClientException {
        exceptionRecordPayloadPA8A =
            testUtils.getStringFromFile("updateExceptionRecordDataPA8ANoAdditionalDocuments.json");
        caveatCaseUpdateRequest =
            getObjectMapper().readValue(exceptionRecordPayloadPA8A, CaveatCaseUpdateRequest.class);
        CaveatCallbackResponse caveatCallbackResponse = Mockito.mock(CaveatCallbackResponse.class);
        when(caveatCallbackResponse.getErrors()).thenReturn(Collections.emptyList());
        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), nullable(List.class)))
            .thenReturn(caveatCallbackResponse);
        when(caveatNotificationService.caveatExtend(any(CaveatCallbackRequest.class)))
            .thenReturn(caveatCallbackResponse);
        ResponseCaveatData responseCaseveatData = Mockito.mock(ResponseCaveatData.class);
        ScannedDocument scannedDocument = ScannedDocument.builder().build();
        when(responseCaseveatData.getScannedDocuments())
            .thenReturn(Arrays.asList(new CollectionMember(null, scannedDocument)));
        when(caveatCallbackResponse.getCaveatData()).thenReturn(responseCaseveatData);

        erService.updateCaveatCaseFromExceptionRecord(caveatCaseUpdateRequest);
    }

    @Test(expected = OCRMappingException.class)
    public void shouldNotUpdateCaveatCaseFromExceptionRecordNoDocuments()
        throws IOException, NotificationClientException {
        exceptionRecordPayloadPA8A =
            testUtils.getStringFromFile("updateExceptionRecordDataPA8ANoDocuments.json");
        caveatCaseUpdateRequest =
            getObjectMapper().readValue(exceptionRecordPayloadPA8A, CaveatCaseUpdateRequest.class);
        CaveatCallbackResponse caveatCallbackResponse = Mockito.mock(CaveatCallbackResponse.class);
        when(caveatCallbackResponse.getErrors()).thenReturn(Collections.emptyList());
        when(eventValidationService.validateCaveatRequest(any(CaveatCallbackRequest.class), nullable(List.class)))
            .thenReturn(caveatCallbackResponse);
        when(caveatNotificationService.caveatExtend(any(CaveatCallbackRequest.class)))
            .thenReturn(caveatCallbackResponse);
        ResponseCaveatData responseCaseveatData = Mockito.mock(ResponseCaveatData.class);
        ScannedDocument scannedDocument = ScannedDocument.builder().build();
        when(responseCaseveatData.getScannedDocuments())
            .thenReturn(Arrays.asList(new CollectionMember(null, scannedDocument)));
        when(caveatCallbackResponse.getCaveatData()).thenReturn(responseCaseveatData);

        erService.updateCaveatCaseFromExceptionRecord(caveatCaseUpdateRequest);
    }

    @Test
    public void createGrantOfProbateCaseFromExceptionRecord() {
        SuccessfulTransformationResponse response =
            erService
                .createGrantOfRepresentationCaseFromExceptionRecord(erRequestGrantOfProbate, GrantType.GRANT_OF_PROBATE,
                    warnings);
        GrantOfRepresentationData grantOfRepresentationDataResponse
            = (GrantOfRepresentationData) response.getCaseCreationDetails().getCaseData();
        assertEquals(RegistryLocation.CTSC, grantOfRepresentationDataResponse.getRegistryLocation());
        assertEquals(ApplicationType.PERSONAL, grantOfRepresentationDataResponse.getApplicationType());
        assertEquals(GrantType.GRANT_OF_PROBATE, grantOfRepresentationDataResponse.getGrantType());
        assertEquals("Smith", grantOfRepresentationDataResponse.getPrimaryApplicantSurname());
        assertEquals(NO, grantOfRepresentationDataResponse.getIsAutomatedProcess());
    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        objectMapper.registerModule(module);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new LocalDateTimeSerializer());
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}
