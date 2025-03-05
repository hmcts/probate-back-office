package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StopReasonValidationRule {

    private static final String NOT_ALLOWED_REASON_CODE = "Other";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        Optional.ofNullable(caseDetails.getData())
                .map(CaseData::getBoCaseStopReasonList)
                .ifPresent(stopReasonList -> stopReasonList.stream()
                        .map(CollectionMember::getValue)
                        .filter(value -> NOT_ALLOWED_REASON_CODE.equals(value.getCaseStopReason()))
                        .findFirst()
                        .ifPresent(value -> {
                            String userMessage = businessValidationMessageRetriever.getMessage(
                                    "notAllowedStopReason", null, Locale.UK);
                            log.info("Forbidden stop reason chosen for case reference {}", caseDetails.getId());
                            throw new BusinessValidationException(userMessage, userMessage);
                        }));

    }

}
