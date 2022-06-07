package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

class ExecutorsAddressPostcodeValidationRuleTest {

    @InjectMocks
    private ExecutorsAddressPostcodeValidationRule executorsAddressPostcodeValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdData;

    @Mock
    private Executor executor;

    private FieldErrorResponse executorPostcodeIsNullError;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        executorPostcodeIsNullError = FieldErrorResponse.builder().message("executorPostcodeIsNull").build();

        when(executor.isApplying()).thenReturn(true);

        when(ccdData.getExecutors()).thenReturn(Collections.singletonList(executor));


        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq("executorPostcodeIsNull")))
            .thenReturn(executorPostcodeIsNullError);
    }

    @Test
    void shouldReturnAddressAndPostcodeErrorMessagesWhenNoAddressProvided() {
        when(executor.getAddress()).thenReturn(null);

        List<FieldErrorResponse> errors = executorsAddressPostcodeValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    void shouldReturnPostcodeErrorMessageWhenNoPostcodeProvided() {
        when(executor.getAddress()).thenReturn(SolsAddress.builder().addressLine1("1 White St").build());

        List<FieldErrorResponse> errors = executorsAddressPostcodeValidationRule.validate(ccdData);

        assertEquals(1, errors.size());
        assertTrue(errors.contains(executorPostcodeIsNullError));
    }

    @Test
    void shouldNotReturnErrorMessagesWhenAddressAndPostcodeProvided() {
        when(executor.getAddress())
            .thenReturn(SolsAddress.builder().addressLine1("1 White St").postCode("SW1 1AZ").build());

        List<FieldErrorResponse> errors = executorsAddressPostcodeValidationRule.validate(ccdData);

        assertTrue(errors.isEmpty());
    }

}
