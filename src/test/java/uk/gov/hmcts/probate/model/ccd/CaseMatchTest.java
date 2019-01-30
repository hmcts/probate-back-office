package uk.gov.hmcts.probate.model.ccd;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CaseMatchTest {

    @Test
    public void shouldMatchByIdForDifferentRefs() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("11111")
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("11111")
                .caseLink(CaseLink.builder().caseReference("SomeRef2").build())
                .build();

        assertTrue(caseMatch1.equals(caseMatch2));
    }

    @Test
    public void shouldMatchByIdForNullRefs() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("11111")
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("11111")
                .build();

        assertTrue(caseMatch1.equals(caseMatch2));
    }

    @Test
    public void shouldMatchOnlyByCaseLinkRef() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("11111")
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("22222")
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        assertTrue(caseMatch1.equals(caseMatch2));
    }

    @Test
    public void shouldNotMatchByIdOnly() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("22222")
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("11111")
                .build();

        assertFalse(caseMatch1.equals(caseMatch2));
    }

    @Test
    public void shouldNotMatchByCaseLinkRefOnly() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef2").build())
                .build();

        assertFalse(caseMatch1.equals(caseMatch2));
    }

}