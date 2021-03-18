package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CodicilDateInPastRuleTest {
    @InjectMocks
    private CodicilDateInPastRule underTest;

    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;

    @Mock
    private CCDData ccdDataMock;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldErrorIfOneOfTheDatesIsToday() {
        final ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.of(2020,10,10));
        dates.add(LocalDate.now());
        dates.add(LocalDate.of(2020,12,10));

        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(1, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    public void shouldErrorIfOneOfTheDatesIsInTheFuture() {
        final ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.of(2020,10,10));
        dates.add(LocalDate.now().plusDays(1));
        dates.add(LocalDate.of(2020,12,10));

        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(1, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    public void shouldPassIfOneOfTheDatesIsInThePast() {
        final ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.of(2020,10,10));
        dates.add(LocalDate.now().minusDays(1));
        dates.add(LocalDate.of(2020,12,10));

        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertTrue(validationError.isEmpty());
    }

    @Test
    public void shouldPassIfNoCodicilsEvenIfOneOfTheDatesIsInTheFuture() {
        final ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.of(2020,10,10));
        dates.add(LocalDate.now().plusDays(1));
        dates.add(LocalDate.of(2020,12,10));

        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.NO);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertTrue(validationError.isEmpty());
    }

}
