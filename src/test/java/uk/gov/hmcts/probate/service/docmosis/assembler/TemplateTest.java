package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import static org.junit.Assert.assertEquals;

class TemplateTest {

    @Test
    void shouldGetHashCode() {
        Template template1 = Template.builder().value("AAA").build();

        assertEquals(65072, template1.hashCode());
    }

    @Test
    void shouldEqualWhenValuesEqual() {
        Template template1 = Template.builder().value("AAA").build();
        Template template2 = Template.builder().value("AAA").build();

        assertEquals(true, template1.equals(template2));
    }

    @Test
    void shouldNotEqualWhenValuesDoNotEqual() {
        Template template1 = Template.builder().value("AAA").build();
        Template template2 = Template.builder().value("AAAA").build();

        assertEquals(false, template1.equals(template2));
    }

    @Test
    void shouldNotEqualNull() {
        Template template1 = Template.builder().build();
        Template template2 = null;

        assertEquals(false, template1.equals(template2));
    }

    @Test
    void shouldNotEqualForAnotherClass() {
        Template template1 = Template.builder().build();
        CaseData template2 = CaseData.builder().build();

        assertEquals(false, template1.equals(template2));
    }
}
