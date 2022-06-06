package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class IHTFourHundredDateValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2020", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    private final LocalDate validDate = IHTFourHundredDateValidationRule.minusBusinessDays(LocalDate.now(), 20);
    private final LocalDate invalidDateAfter20DaysBeforeToday =
        IHTFourHundredDateValidationRule.minusBusinessDays(LocalDate.now(), 10);
    private final LocalDate invalidDateInFuture = LocalDate.now().plusDays(5);
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaseData caseDataWithValidDate;
    private CaseData caseDataWithInvalidDate20DaysBeforeToday;
    private CaseData caseDataWithInvalidDateInFuture;
    private IHTFourHundredDateValidationRule underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new IHTFourHundredDateValidationRule(businessValidationMessageRetriever);
        caseDataWithValidDate = CaseData.builder().solsIHT400Date(validDate).build();
        caseDataWithInvalidDate20DaysBeforeToday = CaseData.builder()
            .solsIHT400Date(invalidDateAfter20DaysBeforeToday)
            .build();
        caseDataWithInvalidDateInFuture = CaseData.builder().solsIHT400Date(invalidDateInFuture).build();
    }

    @Test
    void testValidateWithSuccess() {
        CaseDetails caseDetails = new CaseDetails(caseDataWithValidDate, LAST_MODIFIED, CASE_ID);
        underTest.validate(caseDetails);
    }

    @Test
    void testValidateFailureWithDate20DaysBeforeToday() {
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("message1", "message2",
            "message3");

        CaseDetails caseDetails = new CaseDetails(caseDataWithInvalidDate20DaysBeforeToday, LAST_MODIFIED, CASE_ID);
        try {
            underTest.validate(caseDetails);
        } catch (BusinessValidationException bve) {
            assertEquals("Case ID 12345678987654321: IHT400421 date (" + invalidDateAfter20DaysBeforeToday
                + ") needs to be before 20 working days before current date", bve.getMessage());
            assertEquals("message1", bve.getUserMessage());
            assertEquals("message2", bve.getAdditionalMessages()[0]);
            assertEquals("message3", bve.getAdditionalMessages()[1]);
        }
    }

    @Test
    void testValidateFailureWithDateInFuture() {
        CaseDetails caseDetails = new CaseDetails(caseDataWithInvalidDateInFuture, LAST_MODIFIED, CASE_ID);
        Assertions.assertThatThrownBy(() -> {
            underTest.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Case ID 12345678987654321: IHT400421 date ("
                + invalidDateInFuture + ") needs to be in the past");
    }
}
