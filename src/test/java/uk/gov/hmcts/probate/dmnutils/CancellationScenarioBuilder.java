package uk.gov.hmcts.probate.dmnutils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Map;
import java.util.stream.Stream;

public class CancellationScenarioBuilder implements ArgumentsProvider {

    public Map<String,String> cancelWithProperties(String eventId,
                                                   String fromState,
                                                   String toState, String processCategories, String action) {
        return Map.of(
                "event", eventId,
                "fromState", fromState,
                "state", toState,
                "action", action,
                "processCategories", processCategories
        );
    }

    public Map<String,String> invalidEventOrStateEntryMap(String eventId,
                                                   String fromState,
                                                   String toState) {
        return Map.of(
                "event", eventId,
                "fromState", fromState,
                "state", toState
        );
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                Arguments.of(
                    cancelWithProperties(
                            "boWithdrawApplicationForCasePrinted",
                            "CasePrinted",
                            "BOCaseClosed",
                            "case progression",
                            "Cancel"
                    )
                ),
                Arguments.of(
                        cancelWithProperties(
                                "boWithdrawApplicationForReadyToIssue",
                                "BOReadyToIssue",
                                "BOCaseClosed",
                                "case progression",
                                "Cancel"
                        )
                ),
                Arguments.of(
                    invalidEventOrStateEntryMap(
                            "someInvalidEvent",
                            "CasePrinted",
                            "BOCaseClosed"
                    )
                ),
                Arguments.of(
                        invalidEventOrStateEntryMap(
                        "boWithdrawApplicationForCasePrinted",
                        "BOCaseClosed",
                        "BOCaseClosed"
                        )
                ),
                Arguments.of(
                        invalidEventOrStateEntryMap(
                        "boWithdrawApplicationForCasePrinted",
                        "CasePrinted",
                        "CasePrinted"
                        )
                ),
                Arguments.of(
                        invalidEventOrStateEntryMap(
                                "boWithdrawApplicationForReadyToIssue",
                                "CasePrinted",
                                "BOCaseClosed"
                        )
                ),
                Arguments.of(
                        invalidEventOrStateEntryMap(
                                "boWithdrawApplicationForReadyToIssue",
                                "BOReadyToIssue",
                                "CasePrinted"
                        )
                )
        );
    }
}
