package uk.gov.hmcts.probate.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@RunWith(MockitoJUnitRunner.class)
public class CaveatsExpiryLessThanOneMonthValidationRuleTest {

    @Mock
    private BusinessValidationMessageService businessValidationMessageService;

    @Mock
    private CaveatData ccdDataMock;

    private LocalDate expiryDate;

    private CaveatsExpiryLessThanOneMonthValidationRule underTest;

    private static final String CAVEAT_EXPIRY_MESSAGE_KEY = "caveatExpiryMoreThan1MonthAway";
    private static final String CAVEAT_EXPIRY_MESSAGE = "Caveat expiry error";

    @Before
    public void setUp() {
        underTest = new CaveatsExpiryLessThanOneMonthValidationRule(businessValidationMessageService);

        when(businessValidationMessageService.generateError(BUSINESS_ERROR, CAVEAT_EXPIRY_MESSAGE_KEY))
            .thenReturn(FieldErrorResponse.builder().code(CAVEAT_EXPIRY_MESSAGE).build());
    }

    @Test
    public void shouldErrorWithExpiryMoreThan1MonthAway() {
        expiryDate = LocalDate.now().plusMonths(1).plusDays(1);
        when(ccdDataMock.getExpiryDate()).thenReturn(expiryDate);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(CAVEAT_EXPIRY_MESSAGE, validationErrors.get(0).getCode());
    }

    @Test
    public void shouldNotErrorWithExpiry1MonthAway() {
        expiryDate = LocalDate.now().plusMonths(1);
        when(ccdDataMock.getExpiryDate()).thenReturn(expiryDate);

        List<FieldErrorResponse> validationErrors = underTest.validate(ccdDataMock);

        assertEquals(0, validationErrors.size());
    }
}
