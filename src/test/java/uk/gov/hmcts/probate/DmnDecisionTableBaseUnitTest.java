package uk.gov.hmcts.probate;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public abstract class DmnDecisionTableBaseUnitTest {

    protected static DmnDecisionTable CURRENT_DMN_DECISION_TABLE;
    protected DmnEngine dmnEngine;
    protected DmnDecision decision;

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
                .createDefaultDmnEngineConfiguration()
                .buildEngine();

        // Parse decision
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        InputStream inputStream = null;
        try {
            inputStream = contextClassLoader.getResourceAsStream(CURRENT_DMN_DECISION_TABLE.getFileName());
            decision = dmnEngine.parseDecision(CURRENT_DMN_DECISION_TABLE.getKey(), inputStream);
        } finally {
            if (inputStream != null) {
                closeStream(inputStream);
            }
        }
    }

    public DmnDecisionTableResult evaluateDmnTable(Map<String, Object> variables) {
        return dmnEngine.evaluateDecisionTable(decision, variables);
    }

    private static void closeStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            log.error("Unable to close stream with exception : {}", e.getMessage());
        }
    }
}
