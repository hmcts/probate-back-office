package uk.gov.hmcts.probate.functional.search;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import java.io.IOException;

@RunWith(SpringIntegrationSerenityRunner.class)
public class StandingSearchDocumentRemovalTests extends IntegrationTestBase {

    @Autowired
    protected FunctionalTestUtils utils;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void shouldPostForDocumentRemovals() throws IOException {
        validatePostSuccess("search/standingSearchPayload.json", "/standing-search/setup-for-permanent-removal");
        validatePostSuccess("search/standingSearchDocumentsPayload.json",
                "/standing-search/permanently-delete-removed");
    }
}
