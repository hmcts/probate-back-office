package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicListItem;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaveatDataTransformerTest {

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
    private static final String SOL_SELECTED_PBA = "selectedPBA";
    private static final String SOL_PAY_REF = "solsPayRef";
    private static final String SOL_PAY_FEE_ACCT_NUMBER = "solsFeeAccountNumber";
    private static final String LANGUAGE_PREFERENCE_WELSH = "Yes";

    @BeforeEach
    public void setup() {
        when(callbackRequestMock.getCaseDetails()).thenReturn(caveatDeatilsMock);
        when(caveatDeatilsMock.getData()).thenReturn(caveatDataMock);
    }

    @Test
    void shouldTransformCaveats() {
        when(caveatDataMock.getExpiryDate()).thenReturn(EXPIRY_DATE);
        when(caveatDataMock.getCaveatorEmailAddress()).thenReturn(CAVEATOR_EMAIL);

        CaveatData caveatData = underTest.transformCaveats(callbackRequestMock);

        assertEquals(CAVEATOR_EMAIL, caveatData.getCaveatorEmailAddress());
        assertEquals(EXPIRY_DATE, caveatData.getExpiryDate());
    }

    @Test
    void shouldTransformCaveatsWithNoEmail() {
        when(caveatDataMock.getExpiryDate()).thenReturn(EXPIRY_DATE);

        CaveatData caveatData = underTest.transformCaveats(callbackRequestMock);

        assertEquals("", caveatData.getCaveatorEmailAddress());
        assertEquals(EXPIRY_DATE, caveatData.getExpiryDate());
    }

    @Test
    void shouldTransformSolsCaveats() {
        when(caveatDataMock.getCaveatorEmailAddress()).thenReturn(CAVEATOR_EMAIL);
        when(caveatDataMock.getRegistryLocation()).thenReturn(REGISTRY_LOCATION);
        when(caveatDataMock.getSolsSolicitorAppReference()).thenReturn(SOL_APP_REF);
        when(caveatDeatilsMock.getLastModified()).thenReturn(LAST_MODIFIED_STR);
        when(caveatDataMock.getSolsPaymentMethods()).thenReturn(SOL_PAY_METHODS);
        when(caveatDataMock.getSolsFeeAccountNumber()).thenReturn(SOL_PAY_FEE_ACCT_NUMBER);
        when(caveatDataMock.getSolsPBAPaymentReference()).thenReturn(SOL_PAY_REF);
        when(caveatDataMock.getLanguagePreferenceWelsh()).thenReturn(LANGUAGE_PREFERENCE_WELSH);
        when(caveatDataMock.getSolsPBANumber()).thenReturn(DynamicList.builder()
            .value(DynamicListItem.builder().code(SOL_SELECTED_PBA).label(SOL_SELECTED_PBA).build())
            .build());

        CaveatData caveatData = underTest.transformSolsCaveats(callbackRequestMock);

        assertEquals(CAVEATOR_EMAIL, caveatData.getCaveatorEmailAddress());
        assertEquals(REGISTRY_LOCATION, caveatData.getRegistryLocation());
        assertEquals(SOL_APP_REF, caveatData.getSolsSolicitorAppReference());
        assertEquals(APP_SUBMITTED_DATE, caveatData.getApplicationSubmittedDate());
        assertEquals(SOL_PAY_METHODS, caveatData.getSolsPaymentMethods());
        assertEquals(SOL_SELECTED_PBA, caveatData.getSolsPBANumber().getValue().getCode());
        assertEquals(SOL_PAY_FEE_ACCT_NUMBER, caveatData.getSolsFeeAccountNumber());
        assertEquals(SOL_PAY_REF, caveatData.getSolsPBAPaymentReference());
        assertEquals(LANGUAGE_PREFERENCE_WELSH, caveatData.getLanguagePreferenceWelsh());
    }

    @Test
    void shouldTransformSolsCaveatsWithNullValues() {
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
    void shouldTransformSolsCaveatsWithAllNullValues() {
        CaveatData caveatData = underTest.transformSolsCaveats(callbackRequestMock);

        assertEquals("", caveatData.getCaveatorEmailAddress());
        assertEquals("", caveatData.getRegistryLocation());
        assertEquals("", caveatData.getSolsSolicitorAppReference());
        assertNull(caveatData.getApplicationSubmittedDate());
        assertEquals("", caveatData.getSolsPaymentMethods());
        assertEquals("", caveatData.getSolsFeeAccountNumber());
    }
}
