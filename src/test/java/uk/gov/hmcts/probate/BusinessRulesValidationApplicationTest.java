package uk.gov.hmcts.probate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class BusinessRulesValidationApplicationTest {

    @Test
    public void shouldRunApplicationMain() {
        BusinessRulesValidationApplication.main(new String[] {});
    }
}
