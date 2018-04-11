package uk.gov.hmcts.probate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.fee.FeeServiceResponse;
import uk.gov.hmcts.probate.service.ConfirmationResponseService;
import uk.gov.hmcts.probate.service.fee.FeeService;
import uk.gov.hmcts.probate.transformer.CCDDataTransformer;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NextStepsUnitTest {

    private NextStepsController underTest;

    @Mock
    private CallbackRequest callbackRequestMock;
    @Mock
    private CCDDataTransformer ccdBeanTransformerMock;
    @Mock
    private ConfirmationResponseService confirmationResponseServiceMock;
    @Mock
    private CallbackResponseTransformer callbackResponseTransformerMock;
    @Mock
    private ObjectMapper objectMapperMock;
    @Mock
    private FeeService feeServiceMock;

    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private CCDData ccdDataMock;
    @Mock
    private InheritanceTax inheritanceTaxMock;
    @Mock
    private Fee feeMock;
    @Mock
    private FeeServiceResponse feeServiceResponseMock;
    @Mock
    private CallbackResponse callbackResponseMock;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        underTest = new NextStepsController(ccdBeanTransformerMock, confirmationResponseServiceMock, callbackResponseTransformerMock,
                objectMapperMock, feeServiceMock);
    }

    @Test
    public void shouldGetFeesWithNoErrors() {
        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(ccdBeanTransformerMock.transform(callbackRequestMock)).thenReturn(ccdDataMock);
        when(ccdDataMock.getIht()).thenReturn(inheritanceTaxMock);
        when(ccdDataMock.getFee()).thenReturn(feeMock);
        when(feeServiceMock.getTotalFee(null, 0L, 0L)).thenReturn(feeServiceResponseMock);
        when(callbackResponseTransformerMock.transform(callbackRequestMock, feeServiceResponseMock)).thenReturn(callbackResponseMock);


        ResponseEntity<CallbackResponse> response = underTest.getFees(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test(expected = BadRequestException.class)
    public void shouldGetFeesWithError() {
        when(bindingResultMock.hasErrors()).thenReturn(true);

        ResponseEntity<CallbackResponse> response = underTest.getFees(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }

    @Test(expected = BadRequestException.class)
    public void shouldGetFeesWithErrorAndLogRequest() throws JsonProcessingException {
        when(bindingResultMock.hasErrors()).thenReturn(true);
        when(objectMapperMock.writeValueAsString(callbackRequestMock)).thenThrow(JsonProcessingException.class);

        ResponseEntity<CallbackResponse> response = underTest.getFees(callbackRequestMock,
                bindingResultMock, httpServletRequestMock);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(callbackResponseMock));
    }
}
