package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;

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
        transformer = new GrantIssueTooEarlyTransformer();
        responseBuilder = mock(ResponseCaseDataBuilder.class);
    }

    @Test
    void testGopCaseTypeTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testGopCaseTypeNotTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now().minusDays(9));
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testAdmonWillCaseTypeTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("AdmonWill");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testIntestacyCaseTypeTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("intestacy");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testIntestacyCaseTypeNotTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("intestacy");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now().minusDays(16));
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testAdColligendaBonaCaseTypeTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("AdColligendaBona");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(YES);
    }

    @Test
    void testAdColligendaBonaCaseTypeNotTooEarly() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("AdColligendaBona");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now().minusDays(16));
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testNullDateOfDeath() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("gop");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(null);
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }

    @Test
    void testUnknownCaseType() {
        CaseData caseData = mock(CaseData.class);
        when(caseData.getCaseType()).thenReturn("other");
        when(caseData.getDeceasedDateOfDeath()).thenReturn(LocalDate.now());
        transformer.validate(caseData, responseBuilder);
        verify(responseBuilder).issueEarlySwitch(NO);
    }
}
