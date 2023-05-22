package uk.gov.hmcts.probate.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.probate.model.Constants.YES;

class AttachDocumentsTransformerTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();
    @InjectMocks
    private AttachDocumentsTransformer attachDocumentsTransformer;
    @Mock
    private CaseData caseDataMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSetAttachDocuments() {
        attachDocumentsTransformer.updateAttachDocuments(caseDataMock);
        verify(caseDataMock).setAttachDocuments(YES);
    }

    @Test
    void shouldSetDocumentsReceivedNotificationSent() {
        attachDocumentsTransformer.updateDocsReceivedNotificationSent(caseDataMock);
        verify(caseDataMock).setDocumentsReceivedNotificationSent(YES);
    }
}
