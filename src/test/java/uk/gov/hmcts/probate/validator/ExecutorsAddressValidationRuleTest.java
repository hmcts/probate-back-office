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

class ExecutorsAddressValidationRuleTest {

    @InjectMocks
    private ExecutorsAddressValidationRule executorsAddressValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdData;

    @Mock
    private Executor executor;

    private FieldErrorResponse executorAddressIsNullError;
    private FieldErrorResponse executorAddressIsNullError2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        executorAddressIsNullError = FieldErrorResponse.builder().message("executorAddressIsNull").build();
        executorAddressIsNullError2 = FieldErrorResponse.builder().message("executorAddressIsNullWelsh").build();

        when(executor.isApplying()).thenReturn(true);

        when(ccdData.getExecutors()).thenReturn(Collections.singletonList(executor));

        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq("executorAddressIsNull")))
                .thenReturn(executorAddressIsNullError);
        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq("executorAddressIsNullWelsh")))
                .thenReturn(executorAddressIsNullError2);

    }

    @Test
    void shouldReturnAddressAndPostcodeErrorMessagesWhenNoAddressProvided() {
        when(executor.getAddress()).thenReturn(null);

        List<FieldErrorResponse> errors = executorsAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    void shouldReturnAddressErrorMessageWhenNoAddressLineProvided() {
        when(executor.getAddress()).thenReturn(SolsAddress.builder().postCode("PS1 0LS").build());

        List<FieldErrorResponse> errors = executorsAddressValidationRule.validate(ccdData);

        assertEquals(2, errors.size());
        assertTrue(errors.contains(executorAddressIsNullError2));
    }

    @Test
    void shouldNotReturnErrorMessagesWhenAddressProvided() {
        when(executor.getAddress()).thenReturn(SolsAddress.builder().addressLine1("1 White St").build());

        List<FieldErrorResponse> errors = executorsAddressValidationRule.validate(ccdData);

        assertTrue(errors.isEmpty());
    }

}
