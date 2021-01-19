package uk.gov.hmcts.probate.transformer;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CaveatDataTransformerTest {

    @Mock
    private CaveatCallbackRequest callbackRequestMock;

    @Mock
    private CaveatDetails caveatDeatilsMock;

    @Mock
    private CaveatData caveatDataMock;

    @InjectMocks
    private CaveatDataTransformer underTest;

    private static final LocalDate EXPIRY_DATE = LocalDate.now();
    private static final LocalDate APP_SUBMITTED_DATE = LocalDate.of(2018, 1, 2);
    private static final String[] LAST_MODIFIED_STR = {"2018", "1", "2", "0", "0", "0", "0"};
    private static final String CAVEATOR_EMAIL = "caveator@probate-test.com";
    private static final String REGISTRY_LOCATION = "registryLocation";
    private static final String SOL_APP_REF = "solsSolicitorAppReference";
    private static final String SOL_PAY_METHODS = "solsPaymentMethods";
    private static final String SOL_PAY_FEE_ACCT_NUMBER = "solsFeeAccountNumber";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(callbackRequestMock.getCaseDetails()).thenReturn(caveatDeatilsMock);
        when(caveatDeatilsMock.getData()).thenReturn(caveatDataMock);
    }

    @Test
    public void shouldTransformCaveats() {
        when(caveatDataMock.getExpiryDate()).thenReturn(EXPIRY_DATE);
        when(caveatDataMock.getCaveatorEmailAddress()).thenReturn(CAVEATOR_EMAIL);

        CaveatData caveatData = underTest.transformCaveats(callbackRequestMock);

        assertEquals(CAVEATOR_EMAIL, caveatData.getCaveatorEmailAddress());
        assertEquals(EXPIRY_DATE, caveatData.getExpiryDate());
    }

    @Test
    public void shouldTransformCaveatsWithNoEmail() {
        when(caveatDataMock.getExpiryDate()).thenReturn(EXPIRY_DATE);

        CaveatData caveatData = underTest.transformCaveats(callbackRequestMock);

        assertEquals("", caveatData.getCaveatorEmailAddress());
        assertEquals(EXPIRY_DATE, caveatData.getExpiryDate());
    }

    @Test
    public void shouldTransformSolsCaveats() {
        when(caveatDataMock.getExpiryDate()).thenReturn(EXPIRY_DATE);
        when(caveatDataMock.getCaveatorEmailAddress()).thenReturn(CAVEATOR_EMAIL);
        when(caveatDataMock.getRegistryLocation()).thenReturn(REGISTRY_LOCATION);
        when(caveatDataMock.getSolsSolicitorAppReference()).thenReturn(SOL_APP_REF);
        when(caveatDeatilsMock.getLastModified()).thenReturn(LAST_MODIFIED_STR);
        when(caveatDataMock.getSolsPaymentMethods()).thenReturn(SOL_PAY_METHODS);
        when(caveatDataMock.getSolsFeeAccountNumber()).thenReturn(SOL_PAY_FEE_ACCT_NUMBER);

        CaveatData caveatData = underTest.transformSolsCaveats(callbackRequestMock);

        assertEquals(CAVEATOR_EMAIL, caveatData.getCaveatorEmailAddress());
        assertEquals(REGISTRY_LOCATION, caveatData.getRegistryLocation());
        assertEquals(SOL_APP_REF, caveatData.getSolsSolicitorAppReference());
        assertEquals(APP_SUBMITTED_DATE, caveatData.getApplicationSubmittedDate());
        assertEquals(SOL_PAY_METHODS, caveatData.getSolsPaymentMethods());
        assertEquals(SOL_PAY_FEE_ACCT_NUMBER, caveatData.getSolsFeeAccountNumber());
    }

    @Test
    public void shouldTransformSolsCaveatsWithNullValues() {
        when(caveatDeatilsMock.getLastModified()).thenReturn(LAST_MODIFIED_STR);

        CaveatData caveatData = underTest.transformSolsCaveats(callbackRequestMock);

        assertEquals("", caveatData.getCaveatorEmailAddress());
        assertEquals("", caveatData.getRegistryLocation());
        assertEquals("", caveatData.getSolsSolicitorAppReference());
        assertEquals(APP_SUBMITTED_DATE, caveatData.getApplicationSubmittedDate());
        assertEquals("", caveatData.getSolsPaymentMethods());
        assertEquals("", caveatData.getSolsFeeAccountNumber());
    }

    @Test
    public void shouldTransformSolsCaveatsWithAllNullValues() {
        CaveatData caveatData = underTest.transformSolsCaveats(callbackRequestMock);

        assertEquals("", caveatData.getCaveatorEmailAddress());
        assertEquals("", caveatData.getRegistryLocation());
        assertEquals("", caveatData.getSolsSolicitorAppReference());
        assertEquals(null, caveatData.getApplicationSubmittedDate());
        assertEquals("", caveatData.getSolsPaymentMethods());
        assertEquals("", caveatData.getSolsFeeAccountNumber());
    }
}