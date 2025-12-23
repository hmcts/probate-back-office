package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.StopReason;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CaseStopReasonHelperTest {
    @Test
    void returnsTrueWhenCaveatMatchStopReasonPresent() {
        Map<String, Object> data = new HashMap<>();
        data.put("boCaseStopReasonList", List.of(
                Map.of("value", Map.of("caseStopReason", "CaveatMatch"))
        ));
        CaseDetails caseDetails = CaseDetails.builder().data(data).build();

        assertTrue(CaseStopReasonHelper.isCaveatStop(caseDetails));
    }

    @Test
    void returnsTrueWhenPermanentCaveatStopReasonPresent() {
        Map<String, Object> data = new HashMap<>();
        data.put("boCaseStopReasonList", List.of(
                Map.of("value", Map.of("caseStopReason", "Permanent Caveat"))
        ));
        CaseDetails caseDetails = CaseDetails.builder().data(data).build();

        assertTrue(CaseStopReasonHelper.isCaveatStop(caseDetails));
    }

    @Test
    void returnsFalseWhenNoCaveatStopReasonPresent() {
        Map<String, Object> data = new HashMap<>();
        data.put("boCaseStopReasonList", List.of(
                Map.of("value", Map.of("caseStopReason", "OtherReason"))
        ));
        CaseDetails caseDetails = CaseDetails.builder().data(data).build();

        assertFalse(CaseStopReasonHelper.isCaveatStop(caseDetails));
    }

    @Test
    void returnsFalseWhenStopReasonListIsMissing() {
        Map<String, Object> data = new HashMap<>();
        CaseDetails caseDetails = CaseDetails.builder().data(data).build();

        assertFalse(CaseStopReasonHelper.isCaveatStop(caseDetails));
    }

    @Test
    void extractCaseStopReasonReturnsReasonFromStopReasonObject() {
        StopReason stopReason = StopReason.builder().caseStopReason("CaveatMatch").build();
        assertEquals("CaveatMatch", CaseStopReasonHelper.extractCaseStopReason(stopReason));
    }

    @Test
    void extractCaseStopReasonReturnsReasonFromMap() {
        Map<String, Object> value = Map.of("caseStopReason", "CaveatMatch");
        Map<String, Object> item = Map.of("value", value);

        assertEquals("CaveatMatch", CaseStopReasonHelper.extractCaseStopReason(item));
    }
}
