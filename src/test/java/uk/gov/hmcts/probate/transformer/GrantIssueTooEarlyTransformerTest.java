package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;

import java.time.Clock;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;


class GrantIssueTooEarlyTransformerTest {
    private GrantIssueTooEarlyTransformer transformer;
    private ResponseCaseDataBuilder responseBuilder;
    private CaseData caseData;

    @BeforeEach
    void setUp() {
        transformer = new GrantIssueTooEarlyTransformer(Clock.systemUTC());
        responseBuilder = mock(ResponseCaseDataBuilder.class);
        caseData = mock(CaseData.class);
    }

    @Test
    void testNullDateOfDeathShouldAlwaysSwitchNo() {
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(null);
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(NO, result);
    }

    @Test
    void testGopCaseTypeBeforeDateShouldissueEarlySwitchYes() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 7).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(YES, result);
    }

    @Test
    void testGopCaseTypeAfterShouldissueEarlySwitchNo() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 9).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(NO, result);
    }

    @Test
    void testAdmonWillCaseTypeBeforeDateShouldissueEarlySwitchYes() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 7).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        when(caseData.getCaseType()).thenReturn("admonWill");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(YES, result);
    }

    @Test
    void testAdmonWillCaseTypeAfterShouldissueEarlySwitchNo() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 9).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        when(caseData.getCaseType()).thenReturn("admonWill");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(NO, result);
    }

    @Test
    void testIntestacyCaseTypeBeforeDateShouldissueEarlySwitchYes() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 14).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        when(caseData.getCaseType()).thenReturn("intestacy");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(YES, result);
    }

    @Test
    void testIntestacyCaseTypeBeforeDateShouldissueEarlySwitchNo() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 16).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        when(caseData.getCaseType()).thenReturn("intestacy");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(NO, result);
    }

    @Test
    void testAdColligendaBonaCaseTypeBeforeDateShouldissueEarlySwitchYes() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 14).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        when(caseData.getCaseType()).thenReturn("adColligendaBona");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(YES, result);
    }

    @Test
    void testAdColligendaBonaCaseTypeAfterShouldissueEarlySwitchNo() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 16).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        when(caseData.getCaseType()).thenReturn("adColligendaBona");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        String result = transformer.defaultIssueTooEarlySwitch(caseData);
        assertEquals(NO, result);
    }
}
