package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.response.ResponseCaveatData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SolicitorPaymentReferenceDefaulterTest {
    @InjectMocks
    private SolicitorPaymentReferenceDefaulter solicitorPaymentReferenceDefaulter;

    @Mock
    private CaseData caseDataMock;
    @Mock
    private CaveatData caveatDataMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(caseDataMock.getSolsSolicitorAppReference()).thenReturn("SolAppRef");
        when(caveatDataMock.getSolsSolicitorAppReference()).thenReturn("SolAppRef");
    }

    @Test
    void shouldReturnDefaultRef() {
        ResponseCaseData.ResponseCaseDataBuilder responseCaseDataBuilder = ResponseCaseData.builder();
        solicitorPaymentReferenceDefaulter.defaultSolicitorReference(caseDataMock, responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();
        assertEquals("SolAppRef", responseCaseData.getSolsPBAPaymentReference());
    }

    @Test
    void shouldReturnDefaultRefForCaveats() {
        ResponseCaveatData.ResponseCaveatDataBuilder responseCaseDataBuilder = ResponseCaveatData.builder();

        solicitorPaymentReferenceDefaulter.defaultCaveatSolicitorReference(caveatDataMock, responseCaseDataBuilder);

        ResponseCaveatData responseCaseData = responseCaseDataBuilder.build();
        assertEquals("SolAppRef", responseCaseData.getSolsPBAPaymentReference());
    }
}
