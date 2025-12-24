package uk.gov.hmcts.probate.service.notification;

import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;

import java.util.List;
import java.util.Map;

public class CaseStopReasonHelper {
    private CaseStopReasonHelper() {
    }

    private static final String CASE_STOP_REASON_LIST = "boCaseStopReasonList";
    private static final String STOP_REASON_FIELD_NAME = "caseStopReason";
    private static final List<String> CAVEAT_STOP_REASONS = List.of("CaveatMatch", "Permanent Caveat");

    public static boolean isCaveatStop(CaseDetails caseDetails) {
        Object raw = caseDetails.getData().get(CASE_STOP_REASON_LIST);
        if (!(raw instanceof List<?> reasons)) {
            return false;
        }
        return reasons.stream()
                .map(CaseStopReasonHelper::extractCaseStopReason)
                .anyMatch(reason -> reason != null && CAVEAT_STOP_REASONS.contains(reason));
    }

    public static String extractCaseStopReason(Object item) {
        if (item instanceof StopReason stopReason) {
            return stopReason.getCaseStopReason();
        } else if (item instanceof Map<?, ?> map) {
            Object reason = ((Map<?, ?>) map.get("value")).get(STOP_REASON_FIELD_NAME);
            return reason != null ? reason.toString() : null;
        }
        return null;
    }
}
