package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.Constants;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CodicilDateValidationRuleTest {
    @InjectMocks
    private CodicilDateValidationRule underTest;

    @Mock
    private BusinessValidationMessageService businessValidationMessageServiceMock;

    @Mock
    private CCDData ccdDataMock;
    
    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    void shouldErrorIfOneOfTheDatesIsToday() {
        final ArrayList<CodicilAddedDate> dates = new ArrayList<>();
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,10,10)).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.now()).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,12,10)).build());

        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(2, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    void shouldErrorIfOneOfTheDatesIsInTheFuture() {
        final ArrayList<CodicilAddedDate> dates = new ArrayList<>();
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,10,10)).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.now().plusDays(1)).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,12,10)).build());

        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(2, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    void shouldErrorIfOneOfTheDatesIsBeforeOriginalWillDate() {
        final LocalDate willDate = LocalDate.now().minusDays(1);
        final ArrayList<CodicilAddedDate> dates = new ArrayList<>();
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(willDate.minusDays(1)).build());
        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(willDate);
        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(2, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
    }

    @Test
    void shouldNotErrorIfOneOfTheDatesIsOnOriginalWillDate() {
        final LocalDate willDate = LocalDate.now().minusDays(1);

        final ArrayList<CodicilAddedDate> dates = new ArrayList<>();

        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(willDate).build());

        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(willDate);
        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldGiveTwoErrorsIfOneOfTheDatesIsInFutureAndOnOriginalWillDate() {
        final LocalDate willDate = LocalDate.now().plusDays(1);
        final ArrayList<CodicilAddedDate> dates = new ArrayList<>();
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,10,10)).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(willDate).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,12,10)).build());

        when(ccdDataMock.getOriginalWillSignedDate()).thenReturn(willDate);
        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertEquals(4, validationError.size());
        assertEquals(fieldErrorResponse, validationError.get(0));
        assertEquals(fieldErrorResponse, validationError.get(2));
    }

    @Test
    void shouldPassIfOneOfTheDatesIsInThePast() {
        final ArrayList<CodicilAddedDate> dates = new ArrayList<>();
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,10,10)).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.now().minusDays(1)).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,12,10)).build());

        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.YES);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertTrue(validationError.isEmpty());
    }

    @Test
    void shouldPassIfNoCodicilsEvenIfOneOfTheDatesIsInTheFuture() {
        final ArrayList<CodicilAddedDate> dates = new ArrayList<>();
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,10,10)).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.now().plusDays(1)).build());
        dates.add(CodicilAddedDate.builder()
                .dateCodicilAdded(LocalDate.of(2020,12,10)).build());

        when(ccdDataMock.getWillHasCodicils()).thenReturn(Constants.NO);
        when(ccdDataMock.getCodicilAddedDateList()).thenReturn(dates);
        FieldErrorResponse fieldErrorResponse = FieldErrorResponse.builder().build();
        when(businessValidationMessageServiceMock.generateError(any(String.class), any(String.class)))
                .thenReturn(fieldErrorResponse);

        List<FieldErrorResponse> validationError = underTest.validate(ccdDataMock);

        assertTrue(validationError.isEmpty());
    }
}
