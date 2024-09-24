package uk.gov.hmcts.probate.functional.search;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import java.io.IOException;

@ExtendWith(SerenityJUnit5Extension.class)
public class StandingSearchDocumentRemovalTests extends IntegrationTestBase {

    @Autowired
    protected FunctionalTestUtils utils;

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void shouldPostForDocumentRemovals() throws IOException {
        validatePostSuccess("search/standingSearchPayload.json", "/standing-search/setup-for-permanent-removal");
        validatePostSuccess("search/standingSearchDocumentsPayload.json",
                "/standing-search/permanently-delete-removed");
    }
}
