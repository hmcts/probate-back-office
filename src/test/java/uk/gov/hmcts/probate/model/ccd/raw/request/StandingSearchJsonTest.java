package uk.gov.hmcts.probate.model.ccd.raw.request;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.ccd.standingsearch.request.StandingSearchCallbackRequest;
import uk.gov.hmcts.probate.util.TestUtils;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@JsonTest
class StandingSearchJsonTest {
    private String jsonContent;

    @Autowired
    private JacksonTester<StandingSearchCallbackRequest> jacksonTester;

    private TestUtils testUtils = new TestUtils();

    @BeforeEach
    public void setUp() throws IOException {
        jsonContent = testUtils.getStringFromFile("standingSearchPayload.json");
    }

    @Test
    void canDeserialiseLastModified() throws IOException {
        StandingSearchCallbackRequest standingSearchDetails = jacksonTester.parseObject(jsonContent);
        Assert.assertEquals("2018", standingSearchDetails.getCaseDetails().getLastModified()[0]);
    }
}
