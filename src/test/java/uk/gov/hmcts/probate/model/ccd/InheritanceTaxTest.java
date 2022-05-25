package uk.gov.hmcts.probate.model.ccd;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;

public class InheritanceTaxTest {


    @Test
    public void shouldGetNetValueInPounds() {

        InheritanceTax inheritanceTax = InheritanceTax.builder().formName("FORM_NAME")
                .grossValue(BigDecimal.valueOf(1000F)).netValue(BigDecimal.valueOf(800F)).build();

        assertThat(inheritanceTax.getNetValueInPounds(), comparesEqualTo(new BigDecimal(8.00)));
    }
}
