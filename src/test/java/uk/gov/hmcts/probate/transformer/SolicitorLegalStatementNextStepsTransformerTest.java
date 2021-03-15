package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class SolicitorLegalStatementNextStepsTransformerTest {

    @InjectMocks
    SolicitorLegalStatementNextStepsTransformer solicitorLegalStatementNextStepsTransformer;

    @Mock
    private CaseDetails caseDetails;
    @Mock
    private CaseData caseData;

    private ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        responseCaseDataBuilder = ResponseCaseData.builder();
    }

    @Test
    public void shouldTransformLegalStatmentAmendStatesForProbate() {
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getSolsWillType()).thenReturn("WillLeft");
        solicitorLegalStatementNextStepsTransformer
            .transformLegalStatmentAmendStates(caseDetails, responseCaseDataBuilder);

        assertThat(responseCaseDataBuilder.build().getSolsAmendLegalStatmentSelect().getListItems().size(), is(3));
        assertListCode(0, "SolAppCreatedSolicitorDtls");
        assertListValue(0, "Solicitor Details");
        assertListCode(1, "SolAppCreatedDeceasedDtls");
        assertListValue(1, "Deceased Details");
        assertListCode(2, "WillLeft");
        assertListValue(2, "Grant of probate where the deceased left a will");
    }

    @Test
    public void shouldTransformLegalStatmentAmendStatesForIntestacy() {
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getSolsWillType()).thenReturn("NoWill");
        solicitorLegalStatementNextStepsTransformer
            .transformLegalStatmentAmendStates(caseDetails, responseCaseDataBuilder);

        assertThat(responseCaseDataBuilder.build().getSolsAmendLegalStatmentSelect().getListItems().size(), is(3));
        assertListCode(0, "SolAppCreatedSolicitorDtls");
        assertListValue(0, "Solicitor Details");
        assertListCode(1, "SolAppCreatedDeceasedDtls");
        assertListValue(1, "Deceased Details");
        assertListCode(2, "NoWill");
        assertListValue(2, "Letters of administration where the deceased left no will");
    }

    @Test
    public void shouldTransformLegalStatmentAmendStatesForAdmon() {
        when(caseDetails.getData()).thenReturn(caseData);
        when(caseData.getSolsWillType()).thenReturn("WillLeftAnnexed");
        solicitorLegalStatementNextStepsTransformer
            .transformLegalStatmentAmendStates(caseDetails, responseCaseDataBuilder);

        assertThat(responseCaseDataBuilder.build().getSolsAmendLegalStatmentSelect().getListItems().size(), is(3));
        assertListCode(0, "SolAppCreatedSolicitorDtls");
        assertListValue(0, "Solicitor Details");
        assertListCode(1, "SolAppCreatedDeceasedDtls");
        assertListValue(1, "Deceased Details");
        assertListCode(2, "WillLeftAnnexed");
        assertListValue(2,
            "Letters of administration with will annexed where the deceased left a will but none of the executors can"
                + " apply");
    }

    private void assertListCode(int ind, String value) {
        assertThat(responseCaseDataBuilder.build().getSolsAmendLegalStatmentSelect().getListItems().get(ind).getCode(),
            is(value));
    }

    private void assertListValue(int ind, String label) {
        assertThat(responseCaseDataBuilder.build().getSolsAmendLegalStatmentSelect().getListItems().get(ind).getLabel(),
            is(label));
    }
}