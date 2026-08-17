package uk.gov.hmcts.probate.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.probate.DmnDecisionTableBaseUnitTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.probate.DmnDecisionTable.WA_TASK_INITIATION_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DE_BONIS_NON;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_INTESTACY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_FIAT_WILL;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WINDRUSH_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.CamundaVerifier.resultsMatchUsingNameKey;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_HORIZON_SCHEME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.EXAMINE_WITNESS_INTERVIEW;

class CamundaTaskWaInitiationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_PROBATE;
    }

    private static Map<String, Map<String, Object>> additionalData(boolean evidenceHandled,
                                                                   String caseType,
                                                                   boolean caseHandedOffToLegacySite,
                                                                   List<Map<String,Object>> boHandoffReasonList,
                                                                   String createTask) {
        return Map.of(
                "Data", Map.of(
                        "evidenceHandled", evidenceHandled,
                        "caseType", caseType,
                        "caseHandedOffToLegacySite", caseHandedOffToLegacySite,
                        "boHandoffReasonList", boHandoffReasonList,
                        "createTask", createTask
                )
        );
    }

    private static Map<String, Map<String, Object>> additionalDataNoHandOffList() {
        return Map.of(
                "Data", Map.of(
                        "evidenceHandled", false,
                        "caseType", "",
                        "caseHandedOffToLegacySite", true
                )
        );
    }

    private static final List<Map<String,Object>> handOffReasonListFiatWill = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "FiatWill")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListInfectedBloodCompensationAuthority = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "IBCA")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListDeBonisNon = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "DeBonisNon")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListWillOrCodicilToBeNotated = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "WillCodicilNotated")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListWitnessInterview = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "WitnessInterview")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListHorizonScheme = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "HorizonScheme")
            ),
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListOtherReason = List.of(
            Map.of(
                    "id", "df3be732-2172-49da-80fe-cad8586e4928",
                    "value", Map.of("caseHandoffReason", "OtherReason")
            )
    );

    private static final List<Map<String,Object>> handOffReasonListWindrush = List.of(
        Map.of(
            "id", "df3be732-2172-49da-80fe-cad8586e4928",
            "value", Map.of("caseHandoffReason", "WindrushScheme")
        ),
        Map.of(
            "id", "df3be732-2172-49da-80fe-cad8586e4928",
            "value", Map.of("caseHandoffReason", "OtherReason")
        )
    );

    static Stream<Arguments> probateScenarios() {

        Map<String,Object> examineDigitalCaseProbateTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_PROBATE,
                "name", "Examine Digital Case - Probate",
                "processCategories", "case progression"
        );

        Map<String,Object> examineDigitalCaseProbateReadyToIssueTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_PROBATE_READY_TO_ISSUE,
                "name", "Examine Digital Case - Probate",
                "processCategories", "case progression"
        );


        return Stream.of(
                Arguments.of(
                        "someOtherEventId",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(true, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "gop", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "gop", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boAmendCaseDetailsForAwaitingDocumentation",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),"Yes"),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "boAmendCaseDetailsForAwaitingDocumentation",
                        "CasePrinted",
                        additionalData(true, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boAmendCaseDetailsForAwaitingDocumentation",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boAmendCaseDetailsForAwaitingDocumentation",
                        "CasePrinted",
                        additionalData(false, "gop", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boAmendCaseDetailsForAwaitingDocumentation",
                        "CasePrinted",
                        additionalData(false, "gop", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                  "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(true, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "gop", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "gop", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(true, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "gop", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "gop", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(true, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "gop", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "gop", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(true, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "gop", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "gop", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(true, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "gop", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "gop", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "gop", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseProbateTaskAttributes)
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(true, "gop", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "other", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> admonScenarios() {

        Map<String,Object> examineDigitalCaseAdmonTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_ADMON,
                "name", "Examine Digital Case - Admon",
                "processCategories", "case progression"
        );

        Map<String,Object> examineDigitalCaseAdmonReadyToIssueTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_ADMON_READY_TO_ISSUE,
                "name", "Examine Digital Case - Admon",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "someOtherEventId",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(true, "admonWill", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonTaskAttributes)
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(true, "admonWill", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonTaskAttributes)
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(true, "admonWill", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(true, "admonWill", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(true, "admonWill", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(true, "admonWill", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonTaskAttributes)
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(true, "admonWill", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "admonWill", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "admonWill", true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "admonWill", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseAdmonReadyToIssueTaskAttributes)
                )
        );
    }

    static Stream<Arguments> intestacyScenarios() {

        Map<String, Object> examineDigitalCaseIntestacyTaskAttributes = Map.of(
                "taskId", EXAMINE_DIGITAL_CASE_INTESTACY,
                "name", "Examine Digital Case - Intestacy",
                "processCategories", "case progression"
        );


        return Stream.of(
                Arguments.of(
                        "someOtherEventId",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "applyforGrantPaperApplicationMan",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCaseFromBulkScan",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "attachScannedDocs",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "solicitorPaymentSuccessCase",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "solicitorPaymentSuccessCase",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "solicitorPaymentSuccessCase",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "solicitorPaymentSuccessCase",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCase",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "createCase",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCase",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCase",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCasePaymentSuccess",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "createCasePaymentSuccess",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCasePaymentSuccess",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "createCasePaymentSuccess",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "paymentSuccessApp",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, Collections.emptyList(),""),
                        List.of(examineDigitalCaseIntestacyTaskAttributes)
                ),
                Arguments.of(
                        "paymentSuccessApp",
                        "CasePrinted",
                        additionalData(true, "intestacy", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "paymentSuccessApp",
                        "CasePrinted",
                        additionalData(false, "other", false, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "paymentSuccessApp",
                        "CasePrinted",
                        additionalData(false, "intestacy", false, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> deBonisNonScenarios() {

        Map<String,Object> examineDeBonisNonTaskAttributes = Map.of(
                "taskId", EXAMINE_DE_BONIS_NON,
                "name", "Examine - De Bonis Non",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListDeBonisNon,""),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListDeBonisNon,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListDeBonisNon,""),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListDeBonisNon,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListDeBonisNon,""),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListDeBonisNon,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListDeBonisNon,""),
                        List.of(examineDeBonisNonTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListDeBonisNon,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> fiatWillScenarios() {

        Map<String,Object> examineFiatWillTaskAttributes = Map.of(
                "taskId", EXAMINE_FIAT_WILL,
                "name", "Examine - Fiat Will",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListFiatWill,""),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListFiatWill,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListFiatWill,""),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListFiatWill,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListFiatWill,""),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListFiatWill,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListFiatWill,""),
                        List.of(examineFiatWillTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListFiatWill,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> infectedBloodCompensationAuthorityScenarios() {

        Map<String,Object> examineInfectedBloodCompensationAuthorityTaskAttributes = Map.of(
                "taskId", EXAMINE_INFECTED_BLOOD_COMPENSATION_AUTHORITY,
                "name", "Examine - Infected Blood Compensation Authority",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListInfectedBloodCompensationAuthority,""),
                        List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListInfectedBloodCompensationAuthority,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListInfectedBloodCompensationAuthority,""),
                        List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListInfectedBloodCompensationAuthority,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListInfectedBloodCompensationAuthority,""),
                        List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListInfectedBloodCompensationAuthority,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListInfectedBloodCompensationAuthority,""),
                        List.of(examineInfectedBloodCompensationAuthorityTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListInfectedBloodCompensationAuthority,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> windRushScenarios() {

        Map<String,Object> examineWindrushSchemeTaskAttributes = Map.of(
            "taskId", EXAMINE_WINDRUSH_SCHEME,
            "name", "Examine - Windrush Scheme",
            "processCategories", "case progression"
        );

        return Stream.of(
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWindrush,""),
                List.of(examineWindrushSchemeTaskAttributes)
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalData(false, "",false, handOffReasonListWindrush,""),
                Collections.emptyList()
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListOtherReason,""),
                Collections.emptyList()
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalData(false, "",true, Collections.emptyList(),""),
                Collections.emptyList()
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                additionalDataNoHandOffList(),
                Collections.emptyList()
            ),
            Arguments.of(
                "handleEvidence",
                "BOReadyToIssue",
                null,
                Collections.emptyList()
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWindrush,""),
                List.of(examineWindrushSchemeTaskAttributes)
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalData(false, "",false, handOffReasonListWindrush,""),
                Collections.emptyList()
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListOtherReason,""),
                Collections.emptyList()
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalData(false, "",true, Collections.emptyList(),""),
                Collections.emptyList()
            ),
            Arguments.of(
                "boResolveStop",
                "BOReadyToIssue",
                additionalDataNoHandOffList(),
                Collections.emptyList()
            ),
            Arguments.of(
                "resolveCWEscalation",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWindrush,""),
                List.of(examineWindrushSchemeTaskAttributes)
            ),
            Arguments.of(
                "resolveCWEscalation",
                "BOReadyToIssue",
                additionalData(false, "",false, handOffReasonListWindrush,""),
                Collections.emptyList()
            ),
            Arguments.of(
                "resolveCWEscalation",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListOtherReason,""),
                Collections.emptyList()
            ),
            Arguments.of(
                "resolveCWEscalation",
                "BOReadyToIssue",
                additionalData(false, "",true, Collections.emptyList(),""),
                Collections.emptyList()
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListWindrush,""),
                List.of(examineWindrushSchemeTaskAttributes)
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalData(false, "",false, handOffReasonListWindrush,""),
                Collections.emptyList()
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalData(false, "",true, handOffReasonListOtherReason,""),
                Collections.emptyList()
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalData(false, "",true, Collections.emptyList(),""),
                Collections.emptyList()
            ),
            Arguments.of(
                "changeState",
                "BOReadyToIssue",
                additionalDataNoHandOffList(),
                Collections.emptyList()
            )
        );
    }

    static Stream<Arguments> willOrCodicilToBeNotatedScenarios() {

        Map<String,Object> examineWillOrCodicilToBeNotatedTaskAttributes = Map.of(
                "taskId", EXAMINE_WILL_OR_CODICIL_TO_BE_NOTATED,
                "name", "Examine - Will or Codicil to be Notated",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWillOrCodicilToBeNotated,""),
                        List.of(examineWillOrCodicilToBeNotatedTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWillOrCodicilToBeNotated,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWillOrCodicilToBeNotated,""),
                        List.of(examineWillOrCodicilToBeNotatedTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWillOrCodicilToBeNotated,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWillOrCodicilToBeNotated,""),
                        List.of(examineWillOrCodicilToBeNotatedTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWillOrCodicilToBeNotated,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWillOrCodicilToBeNotated,""),
                        List.of(examineWillOrCodicilToBeNotatedTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWillOrCodicilToBeNotated,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> witnessInterviewScenarios() {

        Map<String,Object> examineWitnessInterviewTaskAttributes = Map.of(
                "taskId", EXAMINE_WITNESS_INTERVIEW,
                "name", "Examine - Witness Interview",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWitnessInterview,""),
                        List.of(examineWitnessInterviewTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWitnessInterview,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWitnessInterview,""),
                        List.of(examineWitnessInterviewTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWitnessInterview,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWitnessInterview,""),
                        List.of(examineWitnessInterviewTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWitnessInterview,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListWitnessInterview,""),
                        List.of(examineWitnessInterviewTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListWitnessInterview,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    static Stream<Arguments> horizonSchemeScenarios() {

        Map<String,Object> examineHorizonSchemeTaskAttributes = Map.of(
                "taskId", EXAMINE_HORIZON_SCHEME,
                "name", "Examine - Horizon Scheme",
                "processCategories", "case progression"
        );

        return Stream.of(
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListHorizonScheme,""),
                        List.of(examineHorizonSchemeTaskAttributes)
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListHorizonScheme,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListHorizonScheme,""),
                        List.of(examineHorizonSchemeTaskAttributes)
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListHorizonScheme,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "changeState",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListHorizonScheme,""),
                        List.of(examineHorizonSchemeTaskAttributes)
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListHorizonScheme,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "resolveCWEscalation",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListHorizonScheme,""),
                        List.of(examineHorizonSchemeTaskAttributes)
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",false, handOffReasonListHorizonScheme,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, handOffReasonListOtherReason,""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "boResolveStop",
                        "BOReadyToIssue",
                        additionalData(false, "",true, Collections.emptyList(),""),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        null,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "handleEvidence",
                        "BOReadyToIssue",
                        additionalDataNoHandOffList(),
                        Collections.emptyList()
                )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(8));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(13));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} evidenceHandled: {2} caseType: {3}")
    @MethodSource({"probateScenarios","admonScenarios","deBonisNonScenarios", "fiatWillScenarios",
        "infectedBloodCompensationAuthorityScenarios","windRushScenarios","willOrCodicilToBeNotatedScenarios",
        "witnessInterviewScenarios", "horizonSchemeScenarios","intestacyScenarios"})
    void given_multiple_event_ids_should_evaluate_dmn_for_probate_scenarios(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> additionalData,
                                                      List<Map<String, Object>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        if (additionalData != null) {
            inputVariables.putValue("additionalData", additionalData);
        }
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        resultsMatchUsingNameKey(dmnDecisionTableResult.getResultList(), expectation);
    }
}
