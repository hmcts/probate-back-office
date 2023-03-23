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

    private static final String PAYLOAD_DEFAULT = "search/standingSearchPayload.json";

    private static final String SETUP_FOR_PERMANENT_DOCUMENT_REMOVAL = "/standing-search/setup-for-permanent-removal";
    private static final String PERMANENTLY_DELETE_REMOVED = "/standing-search/permanently-delete-removed";


    @Autowired
    protected FunctionalTestUtils utils;

    @Before
    public void setUp() {
        initialiseConfig();
    }

    @Test
    public void shouldPostForDocumentRemovals() throws IOException {
        validatePostSuccess(PAYLOAD_DEFAULT, SETUP_FOR_PERMANENT_DOCUMENT_REMOVAL);
        validatePostSuccess(PAYLOAD_DEFAULT, PERMANENTLY_DELETE_REMOVED);
    }
}
