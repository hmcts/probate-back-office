package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;

import java.time.Clock;
import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;


class GrantIssueTooEarlyTransformerTest {
    private GrantIssueTooEarlyTransformer transformer;
    private ResponseCaseDataBuilder responseBuilder;

    @BeforeEach
    void setUp() {
        transformer = new GrantIssueTooEarlyTransformer(Clock.systemUTC());
        responseBuilder = mock(ResponseCaseDataBuilder.class);
    }

    @Test
    void testGopCaseTypeTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testGopCaseTypeNotTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now().minusDays(9));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testAdmonWillCaseTypeTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("AdmonWill");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testIntestacyCaseTypeTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("intestacy");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testIntestacyCaseTypeNotTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("intestacy");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now().minusDays(16));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testAdColligendaBonaCaseTypeTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("AdColligendaBona");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testAdColligendaBonaCaseTypeNotTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("AdColligendaBona");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now().minusDays(16));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testNullDateOfDeathShouldAlwaysSwitchNo() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(null);
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testGopCaseTypeBeforeDateShouldissueEarlySwitchYes() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 7).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        ResponseCaseDataBuilder responseBuilder = mock(ResponseCaseDataBuilder.class);
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testGopCaseTypeAfterShouldissueEarlySwitchNo() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 9).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        ResponseCaseDataBuilder responseBuilder = mock(ResponseCaseDataBuilder.class);
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testAdmonWillCaseTypeBeforeDateShouldissueEarlySwitchYes() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 7).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        ResponseCaseDataBuilder responseBuilder = mock(ResponseCaseDataBuilder.class);
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("admonWill");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testAdmonWillCaseTypeAfterShouldissueEarlySwitchNo() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 9).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        ResponseCaseDataBuilder responseBuilder = mock(ResponseCaseDataBuilder.class);
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("admonWill");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testIntestacyCaseTypeBeforeDateShouldissueEarlySwitchYes() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 14).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        ResponseCaseDataBuilder responseBuilder = mock(ResponseCaseDataBuilder.class);
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("intestacy");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testIntestacyCaseTypeBeforeDateShouldissueEarlySwitchNo() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 16).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        ResponseCaseDataBuilder responseBuilder = mock(ResponseCaseDataBuilder.class);
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("intestacy");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testAdColligendaBonaCaseTypeBeforeDateShouldissueEarlySwitchYes() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 14).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        ResponseCaseDataBuilder responseBuilder = mock(ResponseCaseDataBuilder.class);
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("adColligendaBona");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testAdColligendaBonaCaseTypeAfterShouldissueEarlySwitchNo() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 12, 16).atStartOfDay(
                Clock.systemUTC().getZone()).toInstant(), Clock.systemUTC().getZone());
        GrantIssueTooEarlyTransformer transformer = new GrantIssueTooEarlyTransformer(fixedClock);
        ResponseCaseDataBuilder responseBuilder = mock(ResponseCaseDataBuilder.class);
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("adColligendaBona");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2025, 12, 1));
        transformer.defaultIssueTooEarlySwitch(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }
}
