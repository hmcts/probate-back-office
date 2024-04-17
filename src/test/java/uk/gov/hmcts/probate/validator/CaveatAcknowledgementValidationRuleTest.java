package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.List;

class CaveatAcknowledgementValidationRuleTest {

    private static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    private static final Long CASE_ID = 12345678987654321L;
    @InjectMocks
    private CaveatAcknowledgementValidationRule caveatAcknowledgementValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaveatData caveatDataNoException;
    private CaveatData caveatDataWithException;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        caveatDataNoException = CaveatData.builder()
            .applicationType(ApplicationType.PERSONAL)
            .paymentConfirmCheckbox(List.of("paymentAcknowledgement"))
            .registryLocation("Bristol").build();

        caveatDataWithException = CaveatData.builder()
                .applicationType(ApplicationType.PERSONAL)
                .registryLocation("Bristol").build();
    }

    @Test
    void shouldNotThrowExceptionWhenCheckboxIsChecked() {

        CaveatDetails caveatDetails =
                new CaveatDetails(caveatDataNoException, LAST_MODIFIED, CASE_ID);
        caveatAcknowledgementValidationRule.validate(caveatDetails);
    }

    @Test
    void shouldThrowExceptionWhenCheckboxIsNotChecked() {
        CaveatDetails caveatDetails =
                new CaveatDetails(caveatDataWithException, LAST_MODIFIED, CASE_ID);
        Assertions.assertThatThrownBy(() -> {
            caveatAcknowledgementValidationRule.validate(caveatDetails);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("Payment confirmation checkbox is not checked: 12345678987654321");
    }


}

