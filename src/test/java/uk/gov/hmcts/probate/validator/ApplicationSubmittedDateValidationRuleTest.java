package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationSubmittedDateValidationRuleTest {

    @InjectMocks
    private ApplicationSubmittedDateValidationRule applicationSubmittedDateValidationRule;

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    private DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private CaseData dataMock;
    @Mock
    private CaseDetails detailsMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnErrorForFutureApplicationSubmittedDate() {
        dataMock = CaseData.builder()
                .applicationSubmittedDate(TOMORROW.format(pattern))
                .deceasedDateOfDeath(LocalDate.of(2022, 1, 1))
                .build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            applicationSubmittedDateValidationRule.validate(detailsMock);
        });
        assertEquals("Application Submitted Date cannot be a future date for case: 12345678987654321",
                exception.getMessage());
    }

    @Test
    void shouldReturnErrorForDODAfterApplicationSubmittedDate() {
        dataMock = CaseData.builder()
                .applicationSubmittedDate("2021-12-31")
                .deceasedDateOfDeath(LocalDate.of(2022, 1, 1))
                .build();
        detailsMock = new CaseDetails(dataMock, LAST_MODIFIED, CASE_ID);

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            applicationSubmittedDateValidationRule.validate(detailsMock);
        });
        assertEquals("Date of Death cannot be after Application Submitted Date for case: 12345678987654321",
                exception.getMessage());
    }

}
