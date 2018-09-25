package uk.gov.hmcts.probate.model.ccd.raw.request;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@JsonTest
public class CaseDataJsonTest {

    private String jsonContent;

    @Autowired
    private JacksonTester<CaseData> jacksonTester;

    private TestUtils testUtils = new TestUtils();

    @Before
    public void setUp() throws IOException {
        jsonContent = testUtils.getStringFromFile("paCaseData.json");
    }

    @Test
    public void shouldDeserializePaymentsCorrectly() throws Exception {
        CaseData caseData = jacksonTester.parseObject(jsonContent);
        assertThat(caseData.getPayments(), Matchers.hasSize(1));
        assertThat(caseData.getPayments().get(0).getValue().getAmount(), is("27000"));
        assertThat(caseData.getPayments().get(0).getValue().getDate(), is("2018-09-17"));
        assertThat(caseData.getPayments().get(0).getValue().getMethod(), is("online"));
        assertThat(caseData.getPayments().get(0).getValue().getReference(), is("RC-1537-1988-5489-1985"));
        assertThat(caseData.getPayments().get(0).getValue().getSiteId(), is("P223"));
        assertThat(caseData.getPayments().get(0).getValue().getStatus(), is("Success"));
        assertThat(caseData.getPayments().get(0).getValue().getTransactionId(), is("r23k178busa0rp2mh27m0vchja"));
    }
}
