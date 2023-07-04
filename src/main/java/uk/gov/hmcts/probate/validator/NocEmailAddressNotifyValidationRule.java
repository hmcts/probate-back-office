package uk.gov.hmcts.probate.validator;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.raw.ChangeOfRepresentative;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.hmcts.probate.model.ApplicationType.PERSONAL;
import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class NocEmailAddressNotifyValidationRule {

    private final BusinessValidationMessageService businessValidationMessageService;

    public List<FieldErrorResponse> validate(CaseData caseData) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        if (SOLICITOR.equals(caseData.getApplicationType()) && StringUtils
                .isEmpty(getRemovedSolicitorEmail(caseData))) {
            errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR,
                    "notifyApplicantNoEmailSOLS"));
        }
        return new ArrayList<>(errors);
    }

    private String getRemovedSolicitorEmail(CaseData caseData) {
        CollectionMember<ChangeOfRepresentative> representative = caseData.getChangeOfRepresentatives()!= null ?
                caseData.getChangeOfRepresentatives().get(caseData.getChangeOfRepresentatives().size() - 1) : null;

        if (representative!= null){
            return representative.getValue().getRemovedRepresentative().getSolicitorEmail();
        }
        return null;
    }
}
