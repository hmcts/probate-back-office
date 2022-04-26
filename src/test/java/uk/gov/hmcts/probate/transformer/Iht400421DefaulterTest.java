package uk.gov.hmcts.probate.transformer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.businessrule.IhtEstate400421BusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT205_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;

public class Iht400421DefaulterTest {
    @InjectMocks
    private Iht400421Defaulter iht400421Defaulter;

    @Mock
    private CaseData caseDataMock;
    @Mock
    private ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilderMock;
    @Mock
    private IhtEstate400421BusinessRule ihtEstate400421BusinessRule;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldShowIht400421DatePageForPre2022Flow() {
        when(caseDataMock.getIhtFormId()).thenReturn(IHT400421_VALUE);
        iht400421Defaulter.defaultPageFlowForIht400421(caseDataMock, responseCaseDataBuilderMock);
        verify(responseCaseDataBuilderMock).showIht400421Page("Yes");
    }

    @Test
    public void shouldNotShowIht400421DatePageForPre2022Flow() {
        when(caseDataMock.getIhtFormId()).thenReturn(IHT205_VALUE);
        iht400421Defaulter.defaultPageFlowForIht400421(caseDataMock, responseCaseDataBuilderMock);
        verify(responseCaseDataBuilderMock).showIht400421Page("No");
    }

    @Test
    public void shouldShowIht400421DatePageForPost2022Flow() {
        when(ihtEstate400421BusinessRule.isApplicable(any())).thenReturn(true);
        iht400421Defaulter.defaultPageFlowForIht400421(caseDataMock, responseCaseDataBuilderMock);
        verify(responseCaseDataBuilderMock).showIht400421Page("Yes");
    }

    @Test
    public void shouldNotShowIht400421DatePageForPost2022Flow() {
        when(ihtEstate400421BusinessRule.isApplicable(any())).thenReturn(false);
        iht400421Defaulter.defaultPageFlowForIht400421(caseDataMock, responseCaseDataBuilderMock);
        verify(responseCaseDataBuilderMock).showIht400421Page("No");
    }

}