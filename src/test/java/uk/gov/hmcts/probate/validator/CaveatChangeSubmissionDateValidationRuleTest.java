package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

class CaveatChangeSubmissionDateValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaveatChangeSubmissionDateValidationRule underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new CaveatChangeSubmissionDateValidationRule(businessValidationMessageRetriever);

        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE,
                "future-en");
        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE_WELSH,
                "future-cy");
        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD,
                "before-dod-en");
        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD_WELSH,
                "before-dod-cy");
        stubMessage(CaveatChangeSubmissionDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID,
                "dod-missing-en");
        stubMessage(CaveatChangeSubmissionDateValidationRule
                        .CODE_APPLICATION_SUBMITTED_DATE_DOD_MISSING_OR_INVALID_WELSH,
                "dod-missing-cy");
    }

    @Test
    void shouldReturnNoErrorsWhenSubmissionDateValid() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2024, 2, 1))
                .deceasedDateOfDeath(LocalDate.of(2024, 1, 1))
                .build();

        List<String> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(0, errors.size());
    }

    @Test
    void shouldReturnErrorsWhenSubmissionDateIsInFuture() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.now().plusDays(1))
                .deceasedDateOfDeath(LocalDate.of(2024, 1, 1))
                .build();

        List<String> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("future-en", "future-cy"), errors);
    }

    @Test
    void shouldReturnErrorsWhenSubmissionDateBeforeDod() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2024, 1, 1))
                .deceasedDateOfDeath(LocalDate.of(2024, 1, 2))
                .build();

        List<String> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("before-dod-en", "before-dod-cy"), errors);
    }

    @Test
    void shouldReturnErrorsWhenDodMissing() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2024, 1, 1))
                .deceasedDateOfDeath(null)
                .build();

        List<String> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("dod-missing-en", "dod-missing-cy"), errors);
    }

    @Test
    void shouldReturnErrorsWhenDodInFuture() {
        CaveatData data = CaveatData.builder()
                .applicationSubmittedDate(LocalDate.of(2024, 1, 1))
                .deceasedDateOfDeath(LocalDate.now().plusDays(1))
                .build();

        List<String> errors = underTest.validate(new CaveatDetails(data, LAST_MODIFIED, 1L));

        assertEquals(List.of("dod-missing-en", "dod-missing-cy"), errors);
    }

    private void stubMessage(String code, String message) {
        when(businessValidationMessageRetriever.getMessage(eq(code), isNull(), eq(Locale.UK)))
                .thenReturn(message);
    }
}





