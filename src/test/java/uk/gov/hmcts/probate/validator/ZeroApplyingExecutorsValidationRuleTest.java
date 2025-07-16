package uk.gov.hmcts.probate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_FIRST_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_TRUST_CORP_POS;
import static uk.gov.hmcts.probate.util.CommonVariables.PARTNER_EXEC;


class ZeroApplyingExecutorsValidationRuleTest {

    @InjectMocks
    private ZeroApplyingExecutorsValidationRule underTest;

    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private CaseData caseDataMock;

    private List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList;
    private List<CollectionMember<AdditionalExecutorTrustCorps>> emptyTrustCorpsExecutorList;

    private List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList;
    private List<CollectionMember<AdditionalExecutorPartners>> emptyPartnerExecutorList;

    private static final String NO_EXECUTORS = "zeroExecutors";
    private static final String NO_EXECUTORS_WELSH = "zeroExecutorsWelsh";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        trustCorpsExecutorList = new ArrayList<>();
        trustCorpsExecutorList.add(new CollectionMember(EXEC_ID,
                AdditionalExecutorTrustCorps.builder()
                        .additionalExecForenames(EXEC_FIRST_NAME)
                        .additionalExecLastname(EXEC_SURNAME)
                        .additionalExecutorTrustCorpPosition(EXEC_TRUST_CORP_POS)
                        .build()));

        partnerExecutorList = new ArrayList<>();
        partnerExecutorList.add(PARTNER_EXEC);

        emptyPartnerExecutorList = new ArrayList<>();
        emptyTrustCorpsExecutorList = new ArrayList<>();
    }

    @Test
    void shouldReturnErrorForZeroExecutors() {
        caseDataMock = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorIsExec("No")
                .appointExec("Yes")
                .numberOfExecutors(0L)
                .primaryApplicantIsApplying("No")
                .otherExecutorExists("No")
                .solsSolicitorIsApplying("No")
                .titleAndClearingType("TCTPartSuccPowerRes")
                .primaryApplicantForenames("Probate")
                .primaryApplicantSurname("Practitioner")
                .anyOtherApplyingPartners("No")
                .registryLocation("Bristol")
                .otherPartnersApplyingAsExecutors(emptyPartnerExecutorList)
                .additionalExecutorsTrustCorpList(emptyTrustCorpsExecutorList)
                .build();

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(caseDetailsMock.getId()).thenReturn(0L);

        String[] args = {"0"};
        when(businessValidationMessageRetriever.getMessage(NO_EXECUTORS, args, Locale.UK))
                .thenReturn("There must be at least one executor applying");
        when(businessValidationMessageRetriever.getMessage(NO_EXECUTORS_WELSH, args, Locale.UK))
                .thenReturn("Rhaid bod o leiaf un ysgutor yn gwneud cais");

        BusinessValidationException bve = assertThrows(BusinessValidationException.class, () -> {
            underTest.validate(caseDetailsMock);
        });
        assertThat(bve.getMessage(),
                containsString("There must be at least one executor applying for case id 0"));
    }

    @Test
    void shouldNotThrowExceptionWhenExecutorsExist() {
        caseDataMock = CaseData.builder()
                .applicationType(ApplicationType.SOLICITOR)
                .solsSolicitorIsExec("No")
                .numberOfExecutors(1L)
                .otherExecutorExists("No")
                .solsSolicitorIsApplying("No")
                .titleAndClearingType("TCTPartSuccPowerRes")
                .primaryApplicantForenames("Probate")
                .primaryApplicantSurname("Practitioner")
                .anyOtherApplyingPartners("No")
                .registryLocation("Bristol")
                .otherPartnersApplyingAsExecutors(partnerExecutorList)
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .build();

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);

        assertDoesNotThrow(() -> underTest.validate(caseDetailsMock));
    }
}
