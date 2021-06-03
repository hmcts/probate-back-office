package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DeathRecord;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LifeEventValidationRule implements CaseDetailsValidationRule {
    @Override
    public void validate(CaseDetails caseDetails) {
        final CaseData data = caseDetails.getData();
        final List<CollectionMember<DeathRecord>> deathRecords = data.getDeathRecords();
        if (deathRecords.size() != data.getNumberOfDeathRecords()
            || deathRecords.stream().anyMatch(r -> r.getValue().getSystemNumber() == null)) {
            final String message = "Don't add or remove records here";
            throw new BusinessValidationException(message, message);
        }
        if (1 != deathRecords.stream().filter(r -> r.getValue().getValid().equalsIgnoreCase("Yes")).count()) {
            final String message = "Select one death record";
            throw new BusinessValidationException(message, message);
        }
    }
}
