package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NumberOfApplyingExecutorsValidationRuleTest {

    @InjectMocks
    private NumberOfApplyingExecutorsValidationRule underTest;

    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private Executor executor1;
    @Mock
    private Executor executor2;
    @Mock
    private Executor executor3;
    @Mock
    private Executor executor4;
    @Mock
    private Executor executor5;

    private final List<Executor> executors = new ArrayList<>();

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

        List<FieldErrorResponse> validationError = underTest.validate(caseDetailsMock);

        assertEquals(1, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    public void shouldNotErrorForExecutors() {
        when(executor1.isApplying()).thenReturn(true);
        when(executor2.isApplying()).thenReturn(true);
        when(executor3.isApplying()).thenReturn(true);
        when(executor4.isApplying()).thenReturn(true);
        when(executor5.isApplying()).thenReturn(false);
        when(ccdDataMock.getExecutors()).thenReturn(executors);

        List<FieldErrorResponse> validationError = underTest.validate(caseDetailsMock);

        assertTrue(validationError.isEmpty());
    }

    @Test
    public void shouldErrorForTooManyExecutorsForPPApplying() {
        when(executor1.isApplying()).thenReturn(true);
        when(executor2.isApplying()).thenReturn(true);
        when(executor3.isApplying()).thenReturn(true);
        when(executor4.isApplying()).thenReturn(true);
        when(caseDetailsMock.getExecutors()).thenReturn(executors);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
            .thenReturn(fieldErrorResponse);

        underTest.validate(caseDetailsMock);

    }

    @Test
    public void shouldNotErrorForExecutorsPPNotApplying() {
        when(executor1.isApplying()).thenReturn(true);
        when(executor2.isApplying()).thenReturn(true);
        when(executor3.isApplying()).thenReturn(true);
        when(executor4.isApplying()).thenReturn(true);
        when(ccdDataMock.getSolsSolicitorIsApplying()).thenReturn("No");
        when(ccdDataMock.getExecutors()).thenReturn(executors);

        List<FieldErrorResponse> validationError = underTest.validate(caseDetailsMock);

        assertTrue(validationError.isEmpty());
    }
}
