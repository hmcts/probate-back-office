package uk.gov.hmcts.probate.model.ccd;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CaseMatchTest {

    @Test
    void shouldMatchByIdForDifferentRefs() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("11111")
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("11111")
                .caseLink(CaseLink.builder().caseReference("SomeRef2").build())
                .build();

        assertEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldMatchByIdForNullRefs() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("11111")
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("11111")
                .build();

        assertEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldMatchOnlyByCaseLinkRef() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("11111")
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("22222")
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        assertEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldNotMatchByIdOnly() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("22222")
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("11111")
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldMatchByCaseLinkRefOnly() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        assertEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldNotMatchByCaseLinkRefOnly() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef2").build())
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldNotMatchByCaseLinkWithNullRef() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef2").build())
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldNotMatchByCaseLinkWithNullRefOnOther() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().build())
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldNotMatchByCaseLinkWithNullRefOnBoth() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().build())
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldMatchByLegacyCaseUrlSame() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .legacyCaseViewUrl("legacy1")
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .legacyCaseViewUrl("legacy1")
                .build();

        assertEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldNotMatchByLegacyCaseUrlDifferent() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .legacyCaseViewUrl("legacy1")
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .legacyCaseViewUrl("legacy2")
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    void shouldNotMatchByLegacyCaseUrlEmpty() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .legacyCaseViewUrl("legacy2")
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }
}
