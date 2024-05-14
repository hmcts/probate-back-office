package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;
import static uk.gov.hmcts.probate.validator.ApplicationSubmittedDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE;
import static uk.gov.hmcts.probate.validator.ApplicationSubmittedDateValidationRule.CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD;

@ExtendWith(SpringExtension.class)
class ApplicationSubmittedDateValidationRuleTest {

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    private DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Mock
    private CCDData ccdDataMock;


    private ApplicationSubmittedDateValidationRule applicationSubmittedDateValidationRule;


    @BeforeEach
    public void setUp() {

        applicationSubmittedDateValidationRule =
                new ApplicationSubmittedDateValidationRule(businessValidationMessageService);
        when(businessValidationMessageService.generateError(BUSINESS_ERROR, CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE))
                .thenReturn(FieldErrorResponse.builder().code(CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE).build());

        when(businessValidationMessageService.generateError(
                BUSINESS_ERROR, CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD))
                .thenReturn(FieldErrorResponse.builder().code(CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD).build());

    }

    @Test
    void shouldReturnErrorForFutureApplicationSubmittedDate() {
        when(ccdDataMock.getCaseSubmissionDate()).thenReturn(TOMORROW);
        when(ccdDataMock.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2022, 1, 2));
        List<FieldErrorResponse> validationErrors = applicationSubmittedDateValidationRule.validate(ccdDataMock);
        assertEquals(1, validationErrors.size());
        assertEquals(CODE_APPLICATION_SUBMITTED_DATE_IS_FUTURE, validationErrors.get(0).getCode());

    }

    @Test
    void shouldReturnErrorForDODAfterApplicationSubmittedDate() {
        when(ccdDataMock.getCaseSubmissionDate()).thenReturn(LocalDate.of(2022, 1, 1));
        when(ccdDataMock.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2022, 1, 2));
        List<FieldErrorResponse> validationErrors = applicationSubmittedDateValidationRule.validate(ccdDataMock);
        assertEquals(1, validationErrors.size());
        assertEquals(CODE_APPLICATION_SUBMITTED_DATE_BEFORE_DOD, validationErrors.get(0).getCode());

    }

}
