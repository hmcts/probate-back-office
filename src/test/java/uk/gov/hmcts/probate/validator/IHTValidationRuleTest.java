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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.validator.IHTValidationRule.IHT_ESTATE_NET_GREATER_THAN_GROSS;
import static uk.gov.hmcts.probate.validator.IHTValidationRule.IHT_PROBATE_NET_GREATER_THAN_GROSS;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT207_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

class IHTValidationRuleTest {

    private static final BigDecimal HIGHER_VALUE = BigDecimal.valueOf(20f);
    private static final BigDecimal LOWER_VALUE = BigDecimal.valueOf(1f);

    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;
    @Mock
    private IHTValidationRule ihtValidationRule;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        this.ihtValidationRule = new IHTValidationRule(businessValidationMessageRetriever);
    }

    @Test
    void testValidateWithSuccess() {
        when(caseDataMock.getIhtGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(LOWER_VALUE);
        ihtValidationRule.validate(caseDetailsMock);
    }

    @Test
    void testValidateWithSuccessWhenEqual() {
        when(caseDataMock.getIhtGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(HIGHER_VALUE);
        ihtValidationRule.validate(caseDetailsMock);
    }

    @Test
    void testValidateWithSuccessWhenIhtIsNull() {
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(null);
        when(caseDataMock.getIhtFormEstate()).thenReturn(null);
        when(caseDataMock.getIhtGrossValue()).thenReturn(null);
        when(caseDataMock.getIhtNetValue()).thenReturn(null);
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(null);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(null);

        ihtValidationRule.validate(caseDetailsMock);
    }

    @Test
    void testValidateFailureWhenProbateNetHigherThanGross() {
        when(caseDataMock.getIhtGrossValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(HIGHER_VALUE);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn(IHT_PROBATE_NET_GREATER_THAN_GROSS);

        try {
            ihtValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals(IHT_PROBATE_NET_GREATER_THAN_GROSS, bve.getUserMessage());
        }
    }

    @Test
    void testValidateFailureWhenIHTNetHigherThanGross() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(HIGHER_VALUE);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn(IHT_ESTATE_NET_GREATER_THAN_GROSS);

        try {
            ihtValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals(IHT_ESTATE_NET_GREATER_THAN_GROSS, bve.getUserMessage());
        }
    }

    @Test
    void testValidateSuccessWhenIhtValuesNetIsTheSameAsGross() {
        when(caseDataMock.getIhtGrossValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(HIGHER_VALUE);

        ihtValidationRule.validate(caseDetailsMock);
    }

    @Test
    void testValidateIht207WithSuccess() {
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(caseDataMock.getIhtFormEstate()).thenReturn(IHT207_VALUE);
        when(caseDataMock.getIhtGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(LOWER_VALUE);

        ihtValidationRule.validate(caseDetailsMock);
    }

    @Test
    void testValidateIht207FailureWhenIHTNetHigherThanGross() {
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(caseDataMock.getIhtFormEstate()).thenReturn(IHT207_VALUE);
        when(caseDataMock.getIhtGrossValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(HIGHER_VALUE);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn(IHT_PROBATE_NET_GREATER_THAN_GROSS);

        try {
            ihtValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals(IHT_PROBATE_NET_GREATER_THAN_GROSS, bve.getUserMessage());
        }
    }

    @Test
    void testValidateIht400421WithSuccess() {
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(caseDataMock.getIhtFormEstate()).thenReturn(IHT400421_VALUE);
        when(caseDataMock.getIhtGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(LOWER_VALUE);

        ihtValidationRule.validate(caseDetailsMock);
    }

    @Test
    void testValidateIht400421FailureWhenIHTNetHigherThanGross() {
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(YES);
        when(caseDataMock.getIhtFormEstate()).thenReturn(IHT400421_VALUE);
        when(caseDataMock.getIhtGrossValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(HIGHER_VALUE);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn(IHT_PROBATE_NET_GREATER_THAN_GROSS);

        try {
            ihtValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals(IHT_PROBATE_NET_GREATER_THAN_GROSS, bve.getUserMessage());
        }
    }

    @Test
    void testValidateIhtFormNotCompletedWithSuccess() {
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(caseDataMock.getIhtGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(HIGHER_VALUE);

        ihtValidationRule.validate(caseDetailsMock);
    }

    @Test
    void testValidateIhtFormNotCompletedWhenIHTNetHigherThanGross() {
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(caseDataMock.getIhtGrossValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(LOWER_VALUE);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn(IHT_PROBATE_NET_GREATER_THAN_GROSS);

        try {
            ihtValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals(IHT_PROBATE_NET_GREATER_THAN_GROSS, bve.getUserMessage());
        }
    }

    @Test
    void testValidateIhtFormNotCompletedWhenIHTEstateNetHigherThanGross() {
        when(caseDataMock.getIhtFormEstateValuesCompleted()).thenReturn(NO);
        when(caseDataMock.getIhtGrossValue()).thenReturn(HIGHER_VALUE);
        when(caseDataMock.getIhtNetValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(LOWER_VALUE);
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(HIGHER_VALUE);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any()))
                .thenReturn(IHT_ESTATE_NET_GREATER_THAN_GROSS);

        try {
            ihtValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals(IHT_ESTATE_NET_GREATER_THAN_GROSS, bve.getUserMessage());
        }
    }
}
