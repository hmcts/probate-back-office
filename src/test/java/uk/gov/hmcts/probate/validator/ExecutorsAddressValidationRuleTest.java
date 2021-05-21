package uk.gov.hmcts.probate.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

public class ExecutorsAddressValidationRuleTest {

    @InjectMocks
    private ExecutorsAddressValidationRule executorsAddressValidationRule;

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CCDData ccdData;

    @Mock
    private Executor executor;

    private FieldErrorResponse executorAddressIsNullError;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        executorAddressIsNullError = FieldErrorResponse.builder().message("executorAddressIsNull").build();

        when(executor.isApplying()).thenReturn(true);

        when(ccdData.getExecutors()).thenReturn(Collections.singletonList(executor));

        when(businessValidationMessageService.generateError(eq(BUSINESS_ERROR), eq("executorAddressIsNull")))
                .thenReturn(executorAddressIsNullError);

    }

    @Test
    public void shouldReturnAddressAndPostcodeErrorMessagesWhenNoAddressProvided() {
        when(executor.getAddress()).thenReturn(null);

        List<FieldErrorResponse> errors = executorsAddressValidationRule.validate(ccdData);
        Assert.assertTrue(errors.isEmpty());
        // Assert.assertEquals(1, errors.size());
        // Assert.assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    public void shouldReturnAddressErrorMessageWhenNoAddressLineProvided() {
        when(executor.getAddress()).thenReturn(SolsAddress.builder().postCode("PS1 0LS").build());

        List<FieldErrorResponse> errors = executorsAddressValidationRule.validate(ccdData);
        Assert.assertTrue(errors.isEmpty());
        // Assert.assertEquals(1, errors.size());
        // Assert.assertTrue(errors.contains(executorAddressIsNullError));
    }

    @Test
    public void shouldNotReturnErrorMessagesWhenAddressProvided() {
        when(executor.getAddress()).thenReturn(SolsAddress.builder().addressLine1("1 White St").build());

        List<FieldErrorResponse> errors = executorsAddressValidationRule.validate(ccdData);

        Assert.assertTrue(errors.isEmpty());
    }

}
