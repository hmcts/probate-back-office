package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IhtEstateDefaulterTest {
    @InjectMocks
    private IhtEstateDefaulter ihtEstateDefaulter;

    @Mock
    private CaseData caseDataMock;
    @Mock
    private ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilderMock;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(ihtEstateDefaulter, "ihtEstateSwitchDate", "2022-01-01");

    }

    @Test
    public void shouldSwitchPageFlowForDateOn2022Jan1() {
        when(caseDataMock.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2022, 01, 01));
        ihtEstateDefaulter.defaultPageFlowIhtSwitchDate(caseDataMock, responseCaseDataBuilderMock);
        verify(responseCaseDataBuilderMock).dateOfDeathAfterEstateSwitch("Yes");
    }

    @Test
    public void shouldSwitchPageFlowForDateAfter2022Jan1() {
        when(caseDataMock.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2022, 06, 30));
        ihtEstateDefaulter.defaultPageFlowIhtSwitchDate(caseDataMock, responseCaseDataBuilderMock);
        verify(responseCaseDataBuilderMock).dateOfDeathAfterEstateSwitch("Yes");
    }

    @Test
    public void shouldNotSwitchPageFlowForDateAfter2022Jan1() {
        when(caseDataMock.getDeceasedDateOfDeath()).thenReturn(LocalDate.of(2021, 12, 31));
        ihtEstateDefaulter.defaultPageFlowIhtSwitchDate(caseDataMock, responseCaseDataBuilderMock);
        verify(responseCaseDataBuilderMock).dateOfDeathAfterEstateSwitch("No");
    }
}
