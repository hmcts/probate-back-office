package uk.gov.hmcts.probate.validator;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.transformer.solicitorexecutors.ExecutorsTransformer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.util.CommonVariables.EXECUTOR_TYPE_NAMED;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_FORENAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_FULLNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_SOT_SURNAME;

public class NumberOfApplyingExecutorsValidationRuleTest {

    @InjectMocks
    private NumberOfApplyingExecutorsValidationRule underTest;

    @Mock
    private CaseDetails caseDetailsMock;
    @Mock
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;
    @Mock
    private ExecutorsTransformer executorsTransformer;
    private CaseData caseDataMock;

    private final CollectionMember<AdditionalExecutorApplying> ADD_EXEC = new CollectionMember(
        SOLICITOR_ID, AdditionalExecutorApplying.builder()
        .applyingExecutorFirstName(SOLICITOR_SOT_FORENAME)
        .applyingExecutorLastName(SOLICITOR_SOT_SURNAME)
        .applyingExecutorName(SOLICITOR_SOT_FULLNAME)
        .applyingExecutorType(EXECUTOR_TYPE_NAMED)
        .applyingExecutorAddress(SOLICITOR_ADDRESS)
        .build());

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        caseDataMock = CaseData.builder()
            .applicationType(ApplicationType.SOLICITOR)
            .solsSolicitorIsExec("No")
            .solsSolicitorIsApplying("Yes")
            .titleAndClearingType("TCTPartSuccPowerRes")
            .primaryApplicantForenames("Probate")
            .primaryApplicantSurname("Practitioner")
            .anyOtherApplyingPartners("Yes")
            .registryLocation("Bristol")
            .build();
    }

    @Test
    public void shouldErrorForTooManyExecutors() {
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = new ArrayList<>();
        execsApplying.add(ADD_EXEC);
        execsApplying.add(ADD_EXEC);
        execsApplying.add(ADD_EXEC);
        execsApplying.add(ADD_EXEC);
        execsApplying.add(ADD_EXEC);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(executorsTransformer.createCaseworkerApplyingList(caseDetailsMock.getData())).thenReturn(execsApplying);
        when(executorsTransformer.setExecutorApplyingListWithSolicitorInfo(execsApplying,
            caseDetailsMock.getData())).thenReturn(execsApplying);

        Assertions.assertThatThrownBy(() -> {
            underTest.validate(caseDetailsMock);
        })
            .isInstanceOf(BusinessValidationException.class)
            .hasMessage("The total number executors applying cannot exceed 4 for case id 0");
    }

    @Test
    public void shouldNotErrorForExecutors() {
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = new ArrayList<>();
        execsApplying.add(ADD_EXEC);
        execsApplying.add(ADD_EXEC);

        when(caseDetailsMock.getData()).thenReturn(caseDataMock);
        when(executorsTransformer.createCaseworkerApplyingList(caseDetailsMock.getData())).thenReturn(execsApplying);
        when(executorsTransformer.setExecutorApplyingListWithSolicitorInfo(execsApplying,
            caseDetailsMock.getData())).thenReturn(execsApplying);


        underTest.validate(caseDetailsMock);

    }
}
