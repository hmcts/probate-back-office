package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;

@RunWith(MockitoJUnitRunner.class)
public class IHTFourHundredDateValidationRuleTest {

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataWithValidDate;
    private CaseData caseDataWithInvalidDate20DaysBeforeToday;
    private CaseData caseDataWithInvalidDateInFuture;
    private IHTFourHundredDateValidationRule underTest;
    private final LocalDate validDate = LocalDate.now().minusDays(20);
    private final LocalDate invalidDate20DaysBeforeToday = LocalDate.now().minusDays(25);
    private final LocalDate invalidDateInFuture = LocalDate.now().plusDays(5);

    private static final String[] LAST_MODIFIED = {"2020", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new IHTFourHundredDateValidationRule(businessValidationMessageRetriever);
        caseDataWithValidDate = CaseData.builder().solsIHT400Date(validDate).build();
        caseDataWithInvalidDate20DaysBeforeToday = CaseData.builder().solsIHT400Date(invalidDate20DaysBeforeToday).build();
        caseDataWithInvalidDateInFuture = CaseData.builder().solsIHT400Date(invalidDateInFuture).build();
    }

    @Test
    public void testValidateWithSuccess() {
        CaseDetails caseDetails = new CaseDetails(caseDataWithValidDate, LAST_MODIFIED, CASE_ID);
        underTest.validate(caseDetails);
    }

    @Test
    public void testValidateFailureWithDate20DaysBeforeToday() {
        CaseDetails caseDetails = new CaseDetails(caseDataWithInvalidDate20DaysBeforeToday, LAST_MODIFIED, CASE_ID);
        Assertions.assertThatThrownBy(() -> {
            underTest.validate(caseDetails);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Case ID 12345678987654321: IHT400421 date ("+invalidDate20DaysBeforeToday+") needs to be before 20 days before current date ("+validDate+")");
    }

    @Test
    public void testValidateFailureWithDateInFuture() {
        CaseDetails caseDetails = new CaseDetails(caseDataWithInvalidDateInFuture, LAST_MODIFIED, CASE_ID);
        Assertions.assertThatThrownBy(() -> {
            underTest.validate(caseDetails);
        })
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Case ID 12345678987654321: IHT400421 date ("+invalidDateInFuture+") needs to be in the past");
    }
}
