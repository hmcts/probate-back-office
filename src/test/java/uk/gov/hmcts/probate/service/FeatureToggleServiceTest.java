package uk.gov.hmcts.probate.service;

import org.junit.jupiter.api.function.Executable;
import uk.gov.hmcts.probate.service.FeatureToggleService.DocGen;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

class FeatureToggleServiceTest {
    @Test
    void testDocGenMapping() {
        final BiFunction<String, DocGen, Executable> genAssert = (v, e) -> {
            final DocGen a = DocGen.fromString(v);
            return () -> assertEquals(e, a, "Expected [" + v + "] to give [" + e + "]");
        };

        assertAll(
                genAssert.apply("master", DocGen.MASTER),
                genAssert.apply("pr", DocGen.PR),
                genAssert.apply("html", DocGen.HTML),
                genAssert.apply("html_proc", DocGen.HTML_PROC),
                genAssert.apply("other", DocGen.MASTER)
        );
    }
}
