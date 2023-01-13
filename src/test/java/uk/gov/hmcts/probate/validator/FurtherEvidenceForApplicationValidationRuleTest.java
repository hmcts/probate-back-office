package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FurtherEvidenceForApplicationValidationRuleTest {
    private FurtherEvidenceForApplicationValidationRule furtherEvidenceForApplicationValidationRule;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private CaseData caseDataMock;

    private static final String VALID_INFORMATION = "Some Further Evidence";
    private static final String WHITESPACE = " ";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String LINE_FEED = "\n";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        this.furtherEvidenceForApplicationValidationRule =
                new FurtherEvidenceForApplicationValidationRule(businessValidationMessageRetriever);
    }

    @Test
    void testValidateWithSuccess() {
        when(caseDataMock.getFurtherEvidenceForApplication()).thenReturn(VALID_INFORMATION);
        furtherEvidenceForApplicationValidationRule.validate(caseDetailsMock);
    }

    @Test
    void testValidateWhitespaceWithError() {
        when(caseDataMock.getFurtherEvidenceForApplication()).thenReturn(WHITESPACE);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("error message");
        try {
            furtherEvidenceForApplicationValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("error message", bve.getUserMessage());
        }
    }

    @Test
    void testValidateNullCaseWithError() {
        when(caseDataMock.getFurtherEvidenceForApplication()).thenReturn(null);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("error message");
        try {
            furtherEvidenceForApplicationValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("error message", bve.getUserMessage());
        }
    }

    @Test
    void testValidateCarriageReturnWithError() {
        when(caseDataMock.getFurtherEvidenceForApplication()).thenReturn(CARRIAGE_RETURN);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("error message");
        try {
            furtherEvidenceForApplicationValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("error message", bve.getUserMessage());
        }
    }

    @Test
    void testValidateLineFeedWithError() {
        when(caseDataMock.getFurtherEvidenceForApplication()).thenReturn(LINE_FEED);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("error message");
        try {
            furtherEvidenceForApplicationValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("error message", bve.getUserMessage());
        }
    }

    @Test
    void testValidateCRLFsWithError() {
        when(caseDataMock.getFurtherEvidenceForApplication()).thenReturn(CARRIAGE_RETURN + LINE_FEED);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("error message");
        try {
            furtherEvidenceForApplicationValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("error message", bve.getUserMessage());
        }
    }

    @Test
    void testValidateWhitespaceAndCRLFsWithError() {
        when(caseDataMock.getFurtherEvidenceForApplication()).thenReturn(WHITESPACE + CARRIAGE_RETURN + LINE_FEED);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("error message");
        try {
            furtherEvidenceForApplicationValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("error message", bve.getUserMessage());
        }
    }

    @Test
    void testValidateMixedWhitespaceAndCRLFsWithError() {
        when(caseDataMock.getFurtherEvidenceForApplication())
                .thenReturn(WHITESPACE + CARRIAGE_RETURN + LINE_FEED + CARRIAGE_RETURN + LINE_FEED + WHITESPACE);
        when(businessValidationMessageRetriever.getMessage(any(), any(), any())).thenReturn("error message");
        try {
            furtherEvidenceForApplicationValidationRule.validate(caseDetailsMock);
        } catch (BusinessValidationException bve) {
            assertEquals("error message", bve.getUserMessage());
        }
    }
}
