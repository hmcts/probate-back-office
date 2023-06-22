package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static uk.gov.hmcts.probate.model.ApplicationState.CASE_PRINTED;
import static uk.gov.hmcts.probate.model.ApplicationState.CASE_QA;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

class ChangeToSameStateValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    @InjectMocks
    private ChangeToSameStateValidationRule changeToSameStateValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaseData transferToCasePrintedState;
    private CaseData transferToCaseSelectForQA;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        transferToCasePrintedState = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .paperForm(YES)
            .transferToState(CASE_PRINTED.getId())
            .registryLocation("Bristol").build();

        transferToCaseSelectForQA = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .paperForm(NO)
            .transferToState(CASE_QA.getId())
            .registryLocation("Bristol").build();
    }

    @Test
    void shouldThrowWhenTranferToSameState() {
        CaseDetails caseDetails =
            new CaseDetails(transferToCasePrintedState, LAST_MODIFIED, CASE_ID);
        caseDetails.setState(CASE_PRINTED.getId());
        Assertions.assertThatThrownBy(() -> {
            changeToSameStateValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage(
                "The change case state cannot be the same: 12345678987654321");
    }

    @Test
    void shouldNotThrowWhenTransferToDifferentState() {
        CaseDetails caseDetails =
            new CaseDetails(transferToCaseSelectForQA, LAST_MODIFIED, CASE_ID);
        caseDetails.setState(CASE_PRINTED.getId());
        changeToSameStateValidationRule.validate(caseDetails);
    }
}

