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

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

class RedeclarationSoTValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    @InjectMocks
    private RedeclarationSoTValidationRule redeclarationSoTValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaseData caseDataPaper;
    private CaseData caseDataDigital;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        caseDataPaper = CaseData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .paperForm(YES)
            .registryLocation("Bristol").build();

        caseDataDigital = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .paperForm(NO)
            .registryLocation("Bristol").build();
    }

    @Test
    void shouldThrowPaperCaseIsNotDigital() {
        CaseDetails caseDetails =
            new CaseDetails(caseDataPaper, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            redeclarationSoTValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage(
                "A caseworker is trying to access redeclaration event with a paper case for case id 12345678987654321");
    }

    @Test
    void shouldNotThrowWhenCaseIsDigital() {
        CaseDetails caseDetails =
            new CaseDetails(caseDataDigital, LAST_MODIFIED, CASE_ID);

        redeclarationSoTValidationRule.validate(caseDetails);
    }
}

