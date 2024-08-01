package uk.gov.hmcts.probate.functional.caveats;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.probate.functional.IntegrationTestBase;
import uk.gov.hmcts.probate.functional.util.FunctionalTestUtils;

import java.io.IOException;

@ExtendWith(SerenityJUnit5Extension.class)
public class CaveatDocumentRemovalTests extends IntegrationTestBase {

    private static final String PAYLOAD_DEFAULT = "caveat/success.caveatDocumentSetupRemoval.json";

    private static final String SETUP_FOR_PERMANENT_DOCUMENT_REMOVAL = "/caveat/setup-for-permanent-removal";
    private static final String PERMANENTLY_DELETE_REMOVED = "/caveat/permanently-delete-removed";


    @Autowired
    protected FunctionalTestUtils utils;

    @BeforeEach
    public void setUp() {
        initialiseConfig();
    }

    @Test
    void shouldPostForDocumentRemovals() throws IOException {
        validatePostSuccess(PAYLOAD_DEFAULT, SETUP_FOR_PERMANENT_DOCUMENT_REMOVAL);
        validatePostSuccess(PAYLOAD_DEFAULT, PERMANENTLY_DELETE_REMOVED);
    }
}
