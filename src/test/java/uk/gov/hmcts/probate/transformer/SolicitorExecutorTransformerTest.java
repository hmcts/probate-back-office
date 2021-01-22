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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SolicitorExecutorTransformerTest {

    private CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    private ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder = ResponseCaseData.builder();

    @Mock
    private CaseDetails caseDetailsMock;

    @InjectMocks
    private SolicitorExecutorTransformer solicitorExecutorTransformerMock;

    @Test
    public void shouldSetMainApplicantDetailsWithSolicitorInfo(){

        caseDataBuilder.solsSolicitorIsExec(CommonVariables.YES)
                .solsSolicitorIsMainApplicant(CommonVariables.YES)
                .solsSOTForenames(CommonVariables.SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(CommonVariables.SOLICITOR_SOT_SURNAME)
                .solsSolicitorPhoneNumber(CommonVariables.SOLICITOR_FIRM_PHONE)
                .solsSolicitorEmail(CommonVariables.SOLICITOR_FIRM_EMAIL)
                .solsSolicitorAddress(CommonVariables.SOLICITOR_ADDRESS);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals(CommonVariables.SOLICITOR_SOT_FORENAME, responseCaseData.getPrimaryApplicantForenames());
        assertEquals(CommonVariables.SOLICITOR_SOT_SURNAME, responseCaseData.getPrimaryApplicantSurname());
        assertEquals(CommonVariables.SOLICITOR_FIRM_PHONE, responseCaseData.getPrimaryApplicantPhoneNumber());
        assertEquals(CommonVariables.SOLICITOR_FIRM_EMAIL, responseCaseData.getPrimaryApplicantEmailAddress());
        assertEquals(CommonVariables.SOLICITOR_ADDRESS, responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseDataBuilder.build().getPrimaryApplicantAlias());
        assertEquals(CommonVariables.NO, responseCaseData.getPrimaryApplicantHasAlias());
        assertEquals(CommonVariables.YES, responseCaseData.getPrimaryApplicantIsApplying());
        assertEquals(CommonVariables.YES, responseCaseData.getSolsSolicitorIsApplying());
        assertNull(responseCaseData.getSolsSolicitorNotApplyingReason());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorAsMainApplicant(){
        caseDataBuilder
                .solsSolicitorIsExec(CommonVariables.NO)
                .solsSolicitorIsMainApplicant(CommonVariables.NO)
                .solsSolicitorIsApplying(CommonVariables.NO)
                .solsSolicitorNotApplyingReason(CommonVariables.SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertNull(responseCaseData.getSolsSolicitorIsMainApplicant());
        assertNull(responseCaseData.getSolsSolicitorIsApplying());
        assertNull(responseCaseData.getSolsSolicitorNotApplyingReason());
    }


    @Test
    public void shouldRemoveSolicitorDetailsFromPrimaryApplicant(){
        // Solicitor names are same as primary names.
        caseDataBuilder
                .solsSolicitorIsExec(CommonVariables.YES)
                .solsSolicitorIsMainApplicant(CommonVariables.NO)
                .solsSolicitorIsApplying(CommonVariables.NO)
                .solsSOTForenames(CommonVariables.SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(CommonVariables.SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(CommonVariables.SOLICITOR_SOT_FORENAME)
                .primaryApplicantSurname(CommonVariables.SOLICITOR_SOT_SURNAME)
                .primaryApplicantEmailAddress(CommonVariables.SOLICITOR_FIRM_EMAIL)
                .primaryApplicantPhoneNumber(CommonVariables.SOLICITOR_FIRM_PHONE)
                .primaryApplicantAddress(CommonVariables.SOLICITOR_ADDRESS)
                .primaryApplicantAlias(CommonVariables.PRIMARY_EXEC_ALIAS_NAMES)
                .primaryApplicantHasAlias(CommonVariables.YES)
                .primaryApplicantIsApplying(CommonVariables.YES)
                .solsSolicitorNotApplyingReason(CommonVariables.SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertNull(responseCaseData.getPrimaryApplicantForenames());
        assertNull(responseCaseData.getPrimaryApplicantSurname());
        assertNull(responseCaseData.getPrimaryApplicantPhoneNumber());
        assertNull(responseCaseData.getPrimaryApplicantEmailAddress());
        assertNull(responseCaseData.getPrimaryApplicantAddress());
        assertNull(responseCaseData.getPrimaryApplicantAlias());
        assertNull(responseCaseData.getPrimaryApplicantHasAlias());
        assertNull(responseCaseData.getPrimaryApplicantIsApplying());
        assertNull(responseCaseData.getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorNotApplyingReasonWhenSolicitorApplying(){
        caseDataBuilder
                .solsSolicitorIsExec(CommonVariables.YES)
                .solsSolicitorIsMainApplicant(CommonVariables.NO)
                .solsSolicitorIsApplying(CommonVariables.YES)
                .solsSOTForenames(CommonVariables.SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(CommonVariables.SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(CommonVariables.PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(CommonVariables.PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(CommonVariables.YES)
                .solsSolicitorNotApplyingReason(CommonVariables.SOLICITOR_NOT_APPLYING_REASON);


        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertNull(responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldRemoveSolicitorNotApplyingReasonWhenSolicitorApplyingIsNull(){
        caseDataBuilder
                .solsSolicitorIsExec(CommonVariables.YES)
                .solsSolicitorIsMainApplicant(CommonVariables.NO)
                .solsSolicitorIsApplying(null)
                .solsSOTForenames(CommonVariables.SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(CommonVariables.SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(CommonVariables.PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(CommonVariables.PRIMARY_APPLICANT_SURNAME)
                .primaryApplicantIsApplying(CommonVariables.YES)
                .solsSolicitorNotApplyingReason(CommonVariables.SOLICITOR_NOT_APPLYING_REASON);


        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        assertNull(responseCaseDataBuilder.build().getSolsPrimaryExecutorNotApplyingReason());
    }

    @Test
    public void shouldNotChangeResponseCaseData() {
        caseDataBuilder
                .solsSolicitorIsExec(CommonVariables.YES)
                .solsSolicitorIsMainApplicant(CommonVariables.NO)
                .solsSOTForenames(CommonVariables.SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(CommonVariables.SOLICITOR_SOT_SURNAME)
                .primaryApplicantForenames(CommonVariables.PRIMARY_APPLICANT_FORENAME)
                .primaryApplicantSurname(CommonVariables.PRIMARY_APPLICANT_SURNAME)
                .solsSolicitorIsApplying(CommonVariables.NO)
                .solsSolicitorNotApplyingReason("Not applying");

        responseCaseDataBuilder
                .solsSolicitorIsExec(CommonVariables.YES)
                .solsSolicitorIsMainApplicant(CommonVariables.NO)
                .solsSOTForenames(CommonVariables.SOLICITOR_SOT_FORENAME)
                .solsSOTSurname(CommonVariables.SOLICITOR_SOT_SURNAME)
                .solsSolicitorIsApplying(CommonVariables.NO)
                .solsSolicitorNotApplyingReason(CommonVariables.SOLICITOR_NOT_APPLYING_REASON);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());
        solicitorExecutorTransformerMock.mainApplicantTransformation(caseDetailsMock.getData(), responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertEquals(CommonVariables.YES, responseCaseData.getSolsSolicitorIsExec());
        assertEquals(CommonVariables.NO, responseCaseData.getSolsSolicitorIsMainApplicant());
        assertEquals(CommonVariables.SOLICITOR_SOT_FORENAME, responseCaseData.getSolsSOTForenames());
        assertEquals(CommonVariables.SOLICITOR_SOT_SURNAME, responseCaseData.getSolsSOTSurname());
        assertEquals(CommonVariables.NO, responseCaseData.getSolsSolicitorIsApplying());
        assertEquals(CommonVariables.SOLICITOR_NOT_APPLYING_REASON, responseCaseData.getSolsSolicitorNotApplyingReason());
    }

}
