package uk.gov.hmcts.probate.service.exceptionrecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.BigDecimalSerializer;
import uk.gov.hmcts.probate.model.ccd.raw.LocalDateTimeSerializer;
import uk.gov.hmcts.probate.model.exceptionrecord.CaseCreationDetails;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordRequest;
import uk.gov.hmcts.probate.model.exceptionrecord.SuccessfulTransformationResponse;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ExceptionRecordServiceTest {

    private static final String EXCEPTION_RECORD_CAVEAT_CASE_TYPE_ID = "Caveat";
    private static final String EXCEPTION_RECORD_CAVEAT_EVENT_ID = "raiseCaveat";
    private static final String EXCEPTION_RECORD_GOR_CASE_TYPE_ID = "GrantofRepresentation";
    private static final String EXCEPTION_RECORD_GOR_EVENT_ID = "caseCreated";

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

    private TestUtils testUtils = new TestUtils();

    private ExceptionRecordRequest erRequestCaveat;

    private ExceptionRecordRequest erRequestGrantOfProbate;

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

        exceptionRecordPayloadPA8A = testUtils.getStringFromFile("expectedExceptionRecordDataPA8A.json");

        exceptionRecordPayloadPA1P = testUtils.getStringFromFile("expectedExceptionRecordDataPA1P.json");

        erRequestCaveat = getObjectMapper().readValue(exceptionRecordPayloadPA8A, ExceptionRecordRequest.class);

        erRequestGrantOfProbate = getObjectMapper().readValue(exceptionRecordPayloadPA1P, ExceptionRecordRequest.class);

        warnings = new ArrayList<String>();
        caveatData = new CaveatData();
        caveatData.setRegistryLocation(RegistryLocation.CTSC);
        caveatData.setCaveatorSurname("Jones");
        caveatData.setApplicationType(ApplicationType.PERSONAL);
        caveatCaseDetailsResponse = CaseCreationDetails.builder().<ResponseCaveatData>
                eventId(EXCEPTION_RECORD_CAVEAT_EVENT_ID).caseData(caveatData).caseTypeId(EXCEPTION_RECORD_CAVEAT_CASE_TYPE_ID).build();

        grantOfRepresentationData = new GrantOfRepresentationData();
        grantOfRepresentationData.setRegistryLocation(RegistryLocation.CTSC);
        grantOfRepresentationData.setPrimaryApplicantSurname("Smith");
        grantOfRepresentationData.setApplicationType(ApplicationType.PERSONAL);
        grantOfRepresentationData.setGrantType(GrantType.GRANT_OF_PROBATE);
        grantOfProbateCaseDetailsResponse = CaseCreationDetails.builder().<ResponseCaveatData>eventId(EXCEPTION_RECORD_GOR_EVENT_ID)
                .caseData(grantOfRepresentationData)
                .caseTypeId(EXCEPTION_RECORD_GOR_CASE_TYPE_ID).build();

        when(erCaveatMapper.toCcdData(any())).thenReturn(caveatData);
        when(erGrantOfRepresentationMapper.toCcdData(any(), any())).thenReturn(grantOfRepresentationData);
        when(caveatTransformer.bulkScanCaveatCaseTransform(any())).thenReturn(caveatCaseDetailsResponse);
        when(grantOfProbatetransformer.bulkScanGrantOfRepresentationCaseTransform(any())).thenReturn(grantOfProbateCaseDetailsResponse);
    }

    @Test
    public void createCaveatCaseFromExceptionRecord() {
        SuccessfulTransformationResponse response = erService.createCaveatCaseFromExceptionRecord(erRequestCaveat, warnings);
        CaveatData caveatDataResponse = (CaveatData)response.getCaseCreationDetails().getCaseData();
        assertEquals(RegistryLocation.CTSC, caveatDataResponse.getRegistryLocation());
        assertEquals(ApplicationType.PERSONAL, caveatDataResponse.getApplicationType());
        assertEquals("Jones", caveatDataResponse.getCaveatorSurname());
    }

    @Test
    public void createGrantOfProbateCaseFromExceptionRecord() {
        SuccessfulTransformationResponse response =
                erService.createGrantOfRepresentationCaseFromExceptionRecord(erRequestGrantOfProbate, GrantType.GRANT_OF_PROBATE, warnings);
        GrantOfRepresentationData grantOfRepresentationDataResponse
                = (GrantOfRepresentationData)response.getCaseCreationDetails().getCaseData();
        assertEquals(RegistryLocation.CTSC, grantOfRepresentationDataResponse.getRegistryLocation());
        assertEquals(ApplicationType.PERSONAL, grantOfRepresentationDataResponse.getApplicationType());
        assertEquals(GrantType.GRANT_OF_PROBATE, grantOfRepresentationDataResponse.getGrantType());
        assertEquals("Smith", grantOfRepresentationDataResponse.getPrimaryApplicantSurname());
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
