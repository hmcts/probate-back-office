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
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.ScannedDocumentMapper;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;
import uk.gov.hmcts.probate.util.TestUtils;
import uk.gov.hmcts.reform.probate.model.cases.RegistryLocation;
import uk.gov.hmcts.reform.probate.model.cases.caveat.CaveatData;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ExceptionRecordServiceTest {

    private static final String EXCEPTION_RECORD_CASE_TYPE_ID = "Caveat";
    private static final String EXCEPTION_RECORD_EVENT_ID = "raiseCaveat";

    @InjectMocks
    private ExceptionRecordService erService;

    @Mock
    private ExceptionRecordCaveatMapper erCaveatMapper;

    @Mock
    private ScannedDocumentMapper documentMapper;

    @Mock
    private CaveatCallbackResponseTransformer caveatTransformer;

    private TestUtils testUtils = new TestUtils();

    private ExceptionRecordRequest erRequest;

    private String exceptionRecordPayloadPA8A;

    private List<String> warnings;

    private CaveatData caveatData;

    private CaseCreationDetails caveatCaseDetailsResponse;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        exceptionRecordPayloadPA8A = testUtils.getStringFromFile("expectedExceptionRecordDataPA8A.json");

        erRequest = getObjectMapper().readValue(exceptionRecordPayloadPA8A, ExceptionRecordRequest.class);
        warnings = new ArrayList<String>();
        caveatData = new CaveatData();
        caveatData.setRegistryLocation(RegistryLocation.LEEDS);
        caveatData.setCaveatorSurname("Jones");
        caveatCaseDetailsResponse = CaseCreationDetails.builder().<ResponseCaveatData>
                eventId(EXCEPTION_RECORD_EVENT_ID).caseData(caveatData).caseTypeId(EXCEPTION_RECORD_CASE_TYPE_ID).build();

        when(erCaveatMapper.toCcdData(any())).thenReturn(caveatData);
        when(caveatTransformer.newCaveatCaseTransform(any())).thenReturn(caveatCaseDetailsResponse);
    }

    @Test
    public void createCaveatCaseFromExceptionRecord() {
        SuccessfulTransformationResponse response = erService.createCaveatCaseFromExceptionRecord(erRequest, warnings);
        CaveatData caveatDataResponse = (CaveatData)response.getCaseCreationDetails().getCaseData();
        assertEquals(RegistryLocation.LEEDS, caveatDataResponse.getRegistryLocation());
        assertEquals("Jones", caveatDataResponse.getCaveatorSurname());
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
