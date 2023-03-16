package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Solicitor;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;

class CaseworkersSolicitorPostcodeValidationRuleTest {
    @InjectMocks
    private CaseworkersSolicitorPostcodeValidationRule underTest;

    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;
    @Mock
    private CCDData ccdDataMock;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldErrorIfSolicitorPostCodeMissing() {
        SolsAddress solAddress = SolsAddress.builder().build();
        Solicitor solicitor = Solicitor.builder().firmAddress(solAddress).build();
        when(ccdDataMock.getSolicitor()).thenReturn(solicitor);
        when(ccdDataMock.getApplicationType()).thenReturn(String.valueOf(SOLICITOR));
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().code("somecode").build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(1, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
        assertEquals("somecode", validationError.get(0).getCode());
    }

    @Test
    void shouldNotErrorIfSolicitorHasPostCode() {
        SolsAddress solAddress = SolsAddress.builder().postCode("SOME PC").build();
        Solicitor solicitor = Solicitor.builder().firmAddress(solAddress).build();
        when(ccdDataMock.getSolicitor()).thenReturn(solicitor);
        when(ccdDataMock.getApplicationType()).thenReturn(String.valueOf(SOLICITOR));
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(0, validationError.size());
    }
}
