package uk.gov.hmcts.probate.model.ccd.raw.request;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringBootTest.class })
@JsonTest
class CaseDataJsonIT {

    private String jsonContent;

    @Autowired
    private JacksonTester<CaseData> jacksonTester;

    private TestUtils testUtils = new TestUtils();

    @BeforeEach
    public void setUp() throws IOException {
        jsonContent = testUtils.getStringFromFile("paCaseData.json");
    }

    @Test
    void shouldDeserializePaymentsCorrectly() throws Exception {
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

    @Test
    void canDeserialiseDateAdded() throws IOException {

        final CaseData caseData = CaseData.builder()
                .deceasedDateOfDeath(LocalDate.of(2000,01,04))
                .build();

        CaseData caseDataFromJson = jacksonTester.parseObject(jacksonTester.write(caseData).getJson());

        assertEquals(caseData.getDeceasedDateOfDeath(), caseDataFromJson.getDeceasedDateOfDeath());
    }
}
