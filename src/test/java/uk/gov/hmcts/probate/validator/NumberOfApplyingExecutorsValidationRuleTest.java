package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NumberOfApplyingExecutorsValidationRuleTest {

    @InjectMocks
    NumberOfApplyingExecutorsValidationRule underTest;

    @Mock
    BusinessValidationMessageService businessValidationMessageServiceMock;
    @Mock
    CCDData ccdDataMock;
    @Mock
    Executor executor1;
    @Mock
    Executor executor2;
    @Mock
    Executor executor3;
    @Mock
    Executor executor4;
    @Mock
    Executor executor5;

    private List<Executor> executors = new ArrayList<>();

    @Before
    public void setup() {
        initMocks(this);

        executors.add(executor1);
        executors.add(executor2);
        executors.add(executor3);
        executors.add(executor4);
        executors.add(executor5);
    }

    @Test
    public void shouldErrorForTooManyExecutors() {
        when(executor1.isApplying()).thenReturn(true);
        when(executor2.isApplying()).thenReturn(true);
        when(executor3.isApplying()).thenReturn(true);
        when(executor4.isApplying()).thenReturn(true);
        when(executor5.isApplying()).thenReturn(true);
        when(ccdDataMock.getExecutors()).thenReturn(executors);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
            .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertThat(validationError.get(0), is(fieldErrorResponse));

    }

    @Test
    public void shouldNotErrorForExecutors() {
        when(executor1.isApplying()).thenReturn(true);
        when(executor2.isApplying()).thenReturn(true);
        when(executor3.isApplying()).thenReturn(true);
        when(executor4.isApplying()).thenReturn(true);
        when(executor5.isApplying()).thenReturn(false);
        when(ccdDataMock.getExecutors()).thenReturn(executors);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertThat(validationError.isEmpty(), is(true));

    }
}
