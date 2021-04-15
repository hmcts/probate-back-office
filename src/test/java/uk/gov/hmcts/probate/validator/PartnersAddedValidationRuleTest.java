package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

public class PartnersAddedValidationRuleTest {
    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;

    @InjectMocks
    private PartnersAddedValidationRule partnersAddedValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    private CaseData caseDataNotNoYes;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        caseDataNotNoYes = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("Yes")
            .solsSolicitorIsApplying("Yes")
            .anyOtherApplyingPartners("No")
            .registryLocation("Bristol").build();

    }

    @Test
    public void shouldThrowNeedAtLeastOneMorePartner() {
        CaseDetails caseDetails =
            new CaseDetails(caseDataNotNoYes, LAST_MODIFIED, CASE_ID);

        Assertions.assertThatThrownBy(() -> {
            partnersAddedValidationRule.validate(caseDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("'Yes' needs to be selected for question "
                + "anyOtherApplyingPartners for case id 12345678987654321");
    }
}
