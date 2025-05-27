package uk.gov.hmcts.probate.transformer;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.NO;

@Service
public class AttachDocumentsTransformer {

    public void updateAttachDocuments(CaseData data) {
        data.setAttachDocuments(YES);
    }

    public void updateDocsReceivedNotificationSent(CaseData data) {
        data.setDocumentsReceivedNotificationSent(YES);
    }
}
