package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.util.CommonVariables;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolicitorExecutorTransformerTest {

    private CaseData.CaseDataBuilder caseDataBuilder;

    private ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder;

    @Mock
    private CaseDetails caseDetailsMock;

    @InjectMocks
    private SolicitorExecutorTransformer solicitorExecutorTransformerMock;

    @Before
    public void setUp() {
        caseDataBuilder = CaseData.builder()
            .solsSolicitorIsExec(CommonVariables.YES)
            .solsSolicitorIsMainApplicant(CommonVariables.YES)
            .solsSOTForenames(CommonVariables.SOLICITOR_SOT_FORENAME)
            .solsSOTSurname(CommonVariables.SOLICITOR_SOT_SURNAME)
            .solsSolicitorPhoneNumber(CommonVariables.SOLICITOR_FIRM_PHONE)
            .solsSolicitorEmail(CommonVariables.SOLICITOR_FIRM_EMAIL)
            .solsSolicitorAddress(CommonVariables.SOLICITOR_ADDRESS);

        responseCaseDataBuilder = ResponseCaseData.builder();
    }

    @Test
    public void shouldSetMainApplicantDetailsWithSolicitorInfo(){
        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(CommonVariables.SOLICITOR_SOT_FORENAME, responseCaseDataBuilder.build().getPrimaryApplicantForenames());
        assertEquals(CommonVariables.SOLICITOR_SOT_SURNAME, responseCaseDataBuilder.build().getPrimaryApplicantSurname());
        assertEquals(CommonVariables.SOLICITOR_FIRM_PHONE, responseCaseDataBuilder.build().getPrimaryApplicantPhoneNumber());
        assertEquals(CommonVariables.SOLICITOR_FIRM_EMAIL, responseCaseDataBuilder.build().getPrimaryApplicantEmailAddress());
        assertEquals(CommonVariables.SOLICITOR_ADDRESS, responseCaseDataBuilder.build().getPrimaryApplicantAddress());
        assertEquals(null, responseCaseDataBuilder.build().getPrimaryApplicantAlias());
        assertEquals(CommonVariables.NO, responseCaseDataBuilder.build().getPrimaryApplicantHasAlias());
        assertEquals(CommonVariables.YES, responseCaseDataBuilder.build().getPrimaryApplicantIsApplying());
        assertEquals(CommonVariables.YES, responseCaseDataBuilder.build().getSolsSolicitorIsApplying());
        assertEquals(null, responseCaseDataBuilder.build().getSolsSolicitorNotApplyingReason());
        assertEquals(null, responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorAsMainApplicant(){
        caseDataBuilder
                .solsSolicitorIsExec(CommonVariables.NO)
                .solsSolicitorIsMainApplicant(CommonVariables.NO)
                .solsSolicitorIsApplying(CommonVariables.NO)
                .solsSolicitorNotApplyingReason("Not applying");

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertEquals(null, responseCaseDataBuilder.build().getSolsSolicitorIsMainApplicant());
        assertEquals(null, responseCaseDataBuilder.build().getSolsSolicitorIsApplying());
        assertEquals(null, responseCaseDataBuilder.build().getSolsSolicitorNotApplyingReason());
    }

}
