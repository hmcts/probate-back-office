package uk.gov.hmcts.probate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData.ResponseCaseDataBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.ADMON_WILL_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.GRANT_OF_PROBATE_NAME;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType.Constants.INTESTACY_NAME;

@RunWith(MockitoJUnitRunner.class)
public class GrantChangeServiceTest {

    @InjectMocks
    private GrantChangeService grantChangeService;

    @Mock
    private CaseData caseData;

    @Mock
    private CaseDetails caseDetails;

    @Mock
    private CallbackRequest callbackRequest;

    private ResponseCaseDataBuilder responseCaseDataBuilder;

    private static final String SOLS_EXECUTOR_ALIAS_NAME = "SolsExecAliasName";
    private static final String SOLS_PRIMARY_EXEC_NOT_APPLYING_REASON = "PowerReserved";
    private static final String PRIMARY_PHONE_NUMBER = "07777777777";
    private static final String PRIMARY_EMAIL_ADDRESS = "test@test.com";
    private static final String DECEASED_MARITAL_STATUS = "widowed";
    private static final String RELATIONSHIP_TO_DECEASED = "Child";
    private static final String RESIDUARY_TYPE = "Legatee";
    private static final String STATE_GRANT_TYPE_PROBATE = "SolProbateCreated";
    private static final String STATE_GRANT_TYPE_INTESTACY = "SolIntestacyCreated";
    private static final String STATE_GRANT_TYPE_ADMON = "SolAdmonCreated";


    List<CollectionMember<AdditionalExecutor>> additionalExecutors = new ArrayList<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        responseCaseDataBuilder = ResponseCaseData.builder()
                .willAccessOriginal(YES)
                .willHasCodicils(NO)
                .willNumberOfCodicils(NO)
                .primaryApplicantHasAlias(NO)
                .solsExecutorAliasNames(SOLS_EXECUTOR_ALIAS_NAME)
                .solsPrimaryExecutorNotApplyingReason(SOLS_PRIMARY_EXEC_NOT_APPLYING_REASON)
                .otherExecutorExists(NO)
                .solsAdditionalExecutorList(additionalExecutors)
                .solsMinorityInterest(NO)
                .solsApplicantSiblings(NO)
                .primaryApplicantPhoneNumber(PRIMARY_PHONE_NUMBER)
                .primaryApplicantEmailAddress(PRIMARY_EMAIL_ADDRESS)
                .deceasedMaritalStatus(DECEASED_MARITAL_STATUS)
                .solsApplicantRelationshipToDeceased(RELATIONSHIP_TO_DECEASED)
                .solsSpouseOrCivilRenouncing(YES)
                .solsAdoptedEnglandOrWales(NO)
                .solsEntitledMinority(NO)
                .solsDiedOrNotApplying(YES)
                .solsResiduary(YES)
                .solsResiduaryType(RESIDUARY_TYPE)
                .solsLifeInterest(NO);

        when(caseDetails.getData()).thenReturn(caseData);
        when(callbackRequest.getCaseDetails()).thenReturn(caseDetails);
    }

    @Test
    public void shouldClearGrantOfProbateData(){
        when(caseData.getCaseType()).thenReturn(GRANT_OF_PROBATE_NAME);
        ResponseCaseData response = grantChangeService.clearGrantSpecificData(callbackRequest, responseCaseDataBuilder, STATE_GRANT_TYPE_ADMON).build();

        assertNull(response.getWillAccessOriginal());
        assertNull(response.getWillHasCodicils());
        assertNull(response.getWillNumberOfCodicils());
        assertNull(response.getPrimaryApplicantHasAlias());
        assertNull(response.getSolsExecutorAliasNames());
        assertNull(response.getSolsPrimaryExecutorNotApplyingReason());
        assertNull(response.getOtherExecutorExists());
        assertNull(response.getSolsAdditionalExecutorList());
    }

    @Test
    public void shouldClearIntestacyData(){
        when(caseData.getCaseType()).thenReturn(INTESTACY_NAME);
        ResponseCaseData response = grantChangeService.clearGrantSpecificData(callbackRequest, responseCaseDataBuilder, STATE_GRANT_TYPE_PROBATE).build();

        assertNull(response.getSolsMinorityInterest());
        assertNull(response.getSolsApplicantSiblings());
        assertNull(response.getPrimaryApplicantPhoneNumber());
        assertNull(response.getPrimaryApplicantEmailAddress());
        assertNull(response.getDeceasedMaritalStatus());
        assertNull(response.getSolsApplicantRelationshipToDeceased());
        assertNull(response.getSolsSpouseOrCivilRenouncing());
        assertNull(response.getSolsAdoptedEnglandOrWales());
    }

    @Test
    public void shouldClearAdmonWillData(){
        when(caseData.getCaseType()).thenReturn(ADMON_WILL_NAME);
        ResponseCaseData response = grantChangeService.clearGrantSpecificData(callbackRequest, responseCaseDataBuilder, STATE_GRANT_TYPE_INTESTACY).build();

        assertNull(response.getWillAccessOriginal());
        assertNull(response.getWillHasCodicils());
        assertNull(response.getWillNumberOfCodicils());
        assertNull(response.getSolsEntitledMinority());
        assertNull(response.getSolsDiedOrNotApplying());
        assertNull(response.getSolsResiduary());
        assertNull(response.getSolsResiduaryType());
        assertNull(response.getSolsLifeInterest());
        assertNull(response.getPrimaryApplicantPhoneNumber());
        assertNull(response.getPrimaryApplicantEmailAddress());
    }
}
