package uk.gov.hmcts.probate.model.evidencemanagement;

import org.junit.Test;
import org.springframework.hateoas.Link;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EvidenceManagementFileTest {

    @Test
    public void shouldSetLinks() {
        EvidenceManagementFile evidenceManagementFile = new EvidenceManagementFile();
        evidenceManagementFile.setDocumentType("TEST_DOCUMENT_TYPE");
        evidenceManagementFile.setSize(200L);
        evidenceManagementFile.setOriginalDocumentName("ORIGINAL_DOCUMENT_NAME");
        evidenceManagementFile.setCreatedBy("TEST_USER");
        evidenceManagementFile.setLastModifiedBy("TEST_USER");
        evidenceManagementFile.setModifiedOn(new Date());
        evidenceManagementFile.setCreatedOn(new Date());
        evidenceManagementFile.setMimeType("mime type");
        Map<String, Link> linkMap = new HashMap<>();
        linkMap.put("LABEl1", new Link("LINK1_HREF"));
        linkMap.put("LABEL2", new Link("LINK2_HREF"));

        evidenceManagementFile.setLinks(linkMap);

        List<Link> links = evidenceManagementFile.getLinks();
        assertThat(links, hasSize(2));
        assertThat(links, containsInAnyOrder(
                hasProperty("rel", is("LABEl1")),
                hasProperty("rel", is("LABEL2"))
        ));
    }
}
