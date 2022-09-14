package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class IhtNetQualifyingValueValidationRuleTest {
    private IhtNetQualifyingValueValidationRule ihtNetQualifyingValueValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    private static final Double ESTATE_GROSS = Double.valueOf(1000);
    private static final Double ESTATE_NET = Double.valueOf(900);
    private static final Double PROBATE_GROSS = Double.valueOf(800);
    private static final Double PROBATE_NET = Double.valueOf(700);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        this.ihtNetQualifyingValueValidationRule =
                new IhtNetQualifyingValueValidationRule(businessValidationMessageRetriever);
    }

    @Test
    void shouldErrorForNQVGreaterThanEstateGross() {
        assertError(1001d, ESTATE_GROSS, ESTATE_NET, null, null, true);
    }

    @Test
    void shouldErrorForNQVGreaterThanEstateNet() {
        assertError(901d, ESTATE_GROSS, ESTATE_NET, null, null, true);
    }

    @Test
    void shouldNotErrorForNQVLessThanEstate() {
        assertError(700d, ESTATE_GROSS, ESTATE_NET, null, null, false);
    }


    @Test
    void shouldErrorForNQVGreaterThanProbateGross() {
        assertError(801d, ESTATE_GROSS, ESTATE_NET, PROBATE_GROSS, PROBATE_NET, true);
    }

    @Test
    void shouldErrorForNQVGreaterThanProbateNet() {
        assertError(701d, ESTATE_GROSS, ESTATE_NET, PROBATE_GROSS, PROBATE_NET, true);
    }

    @Test
    void shouldNotErrorForNQVLessThanProbate() {
        assertError(601d, ESTATE_GROSS, ESTATE_NET, PROBATE_GROSS, PROBATE_NET, false);
    }


    private void assertError(Double nqv, Double estGross, Double estNet, Double probGross, Double probNet,
                             boolean shouldThrowError) {
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(new BigDecimal(nqv));
        if (estGross != null) {
            when(caseDataMock.getIhtEstateGrossValue()).thenReturn(new BigDecimal(estGross));
        }
        if (estNet != null) {
            when(caseDataMock.getIhtEstateNetValue()).thenReturn(new BigDecimal(estNet));
        }
        if (probGross != null) {
            when(caseDataMock.getIhtGrossValue()).thenReturn(new BigDecimal(probGross));
        }
        if (probNet != null) {
            when(caseDataMock.getIhtNetValue()).thenReturn(new BigDecimal(probNet));
        }
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("iht message");
        try {
            ihtNetQualifyingValueValidationRule.validate(caseDetailsMock);
            if (shouldThrowError) {
                //should not reach here
                assertTrue(false);
            }
        } catch (BusinessValidationException bve) {
            if (shouldThrowError) {
                assertEquals("iht message", bve.getUserMessage());
            } else {
                //should not go here
                assertTrue(false);
            }
        }
    }
}
