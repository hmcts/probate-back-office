package uk.gov.hmcts.probate.model.evidencemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EvidenceManagementFileTest {

    @Test
    void shouldSetLinks() {
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
        linkMap.put("LABEl1", Link.of("LINK1_HREF"));
        linkMap.put("LABEL2", Link.of("LINK2_HREF"));

        evidenceManagementFile.setLinks(linkMap);

        Links links = evidenceManagementFile.getLinks();
        assertTrue(links.hasSize(2));
    }
}
