package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OriginalWillDateValidationRuleTest {
    @InjectMocks
    private OriginalWillSignedDateValidationRule underTest;

    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;

    @Mock
    private CCDData ccdDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldErrorIfDateIsToday() {
        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(LocalDate.now());
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(1, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    public void shouldErrorIfDateIsInTheFuture() {
        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(LocalDate.now().plusDays(1));
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(1, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    public void shouldErrorIfDateIsAfterDateOfDeath() {
        LocalDate dod = LocalDate.now().minusDays(2);
        when(ccdDataMock.getDeceasedDateOfDeath()).thenReturn(dod);
        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(dod.plusDays(1));
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(1, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    public void shouldErrorIfDateIsOnDateOfDeath() {
        LocalDate dod = LocalDate.now().minusDays(1);
        when(ccdDataMock.getDeceasedDateOfDeath()).thenReturn(dod);
        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(dod);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(1, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    public void shouldGetTwoErrorsIfDateIsAfterDateOfDeathAndToday() {
        LocalDate dod = LocalDate.now().minusDays(1);
        when(ccdDataMock.getDeceasedDateOfDeath()).thenReturn(dod);
        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(dod.plusDays(1));
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(2, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
        assertEquals(fieldErrorResponse, validationError.get(1));
    }

    @Test
    public void shouldPassIfDateIsInThePastAndAfterDateOfDeath() {
        when(ccdDataMock.getDeceasedDateOfDeath()).thenReturn(LocalDate.now().minusDays(1));
        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(LocalDate.now().minusDays(2));
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertTrue(validationError.isEmpty());
    }
}
