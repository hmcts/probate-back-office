package uk.gov.hmcts.probate.model.ccd;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

        assertEquals(caseMatch1, caseMatch2);
    }

    @Test
    public void shouldMatchByIdForNullRefs() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("11111")
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("11111")
                .build();

        assertEquals(caseMatch1, caseMatch2);
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

        assertEquals(caseMatch1, caseMatch2);
    }

    @Test
    public void shouldNotMatchByIdOnly() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .id("22222")
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .id("11111")
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    public void shouldNotMatchByCaseLinkRefOnly() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef2").build())
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    public void shouldNotMatchByCaseLinkWithNullRef() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef2").build())
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    public void shouldNotMatchByCaseLinkWithNullRefOnOther() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().caseReference("SomeRef1").build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().build())
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

    @Test
    public void shouldNotMatchByCaseLinkWithNullRefOnBoth() {

        CaseMatch caseMatch1 = CaseMatch.builder()
                .caseLink(CaseLink.builder().build())
                .build();

        CaseMatch caseMatch2 = CaseMatch.builder()
                .caseLink(CaseLink.builder().build())
                .build();

        assertNotEquals(caseMatch1, caseMatch2);
    }

}
