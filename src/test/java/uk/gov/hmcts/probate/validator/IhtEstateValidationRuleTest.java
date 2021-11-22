package uk.gov.hmcts.probate.validator;

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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.YES;

@RunWith(MockitoJUnitRunner.class)
public class IhtEstateValidationRuleTest {
    private IhtEstateValidationRule ihtEstateValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        this.ihtEstateValidationRule = new IhtEstateValidationRule(businessValidationMessageRetriever);
    }

    @Test
    public void testValidateWithUnusedAllowanceError() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(BigDecimal.valueOf(100000000));
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(BigDecimal.valueOf(90000000));
        when(caseDataMock.getIhtGrossValue()).thenReturn(BigDecimal.valueOf(30000000));
        when(caseDataMock.getIhtNetValue()).thenReturn(BigDecimal.valueOf(29000000));
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(BigDecimal.valueOf(50000000));
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("unused iht message");
        try {
            ihtEstateValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("unused iht message", bve.getUserMessage());
        }
    }
    
    @Test
    public void testValidateWithExpectedIht() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(BigDecimal.valueOf(100000000));
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(BigDecimal.valueOf(90000000));
        when(caseDataMock.getIhtGrossValue()).thenReturn(BigDecimal.valueOf(30000000));
        when(caseDataMock.getIhtNetValue()).thenReturn(BigDecimal.valueOf(29000000));
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(BigDecimal.valueOf(70000000));
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("expected iht message");
        try {
            ihtEstateValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("expected iht message", bve.getUserMessage());
        }
    }

    @Test
    public void testValidateNoMessage() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(BigDecimal.valueOf(100000000));
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(BigDecimal.valueOf(90000000));
        when(caseDataMock.getIhtGrossValue()).thenReturn(BigDecimal.valueOf(30000000));
        when(caseDataMock.getIhtNetValue()).thenReturn(BigDecimal.valueOf(29000000));
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(BigDecimal.valueOf(1000000));
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateNoMessageNoNvq() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(BigDecimal.valueOf(100000000));
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(BigDecimal.valueOf(90000000));
        when(caseDataMock.getIhtGrossValue()).thenReturn(BigDecimal.valueOf(30000000));
        when(caseDataMock.getIhtNetValue()).thenReturn(BigDecimal.valueOf(29000000));
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateNoMessageNoEstateValues() {
        when(caseDataMock.getIhtGrossValue()).thenReturn(BigDecimal.valueOf(30000000));
        when(caseDataMock.getIhtNetValue()).thenReturn(BigDecimal.valueOf(29000000));
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(BigDecimal.valueOf(1000000));
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

    @Test
    public void testValidateNoMessageNoIhtValues() {
        when(caseDataMock.getIhtEstateGrossValue()).thenReturn(BigDecimal.valueOf(100000000));
        when(caseDataMock.getIhtEstateNetValue()).thenReturn(BigDecimal.valueOf(90000000));
        when(caseDataMock.getIhtEstateNetQualifyingValue()).thenReturn(BigDecimal.valueOf(1000000));
        when(caseDataMock.getIhtUnusedAllowanceClaimed()).thenReturn(YES);
        ihtEstateValidationRule.validate(caseDetailsMock);
    }

}