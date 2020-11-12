package uk.gov.hmcts.probate.service.consumer;

import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.service.template.printservice.PrintService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintServiceConsumerTest {

    @Autowired
    PrintService printService;

    @Rule
    public PactHttpsProviderRuleMk2 mockProvider = new PactHttpsProviderRuleMk2("rpe_sendLetterService", "localhost", 8486, this);


}
