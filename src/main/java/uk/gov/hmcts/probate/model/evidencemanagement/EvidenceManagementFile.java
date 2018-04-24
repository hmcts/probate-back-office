package uk.gov.hmcts.probate.model.evidencemanagement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.Date;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvidenceManagementFile extends ResourceSupport {

    private String documentType;
    private long size;
    private String mimeType;
    private String originalDocumentName;
    private String createdBy;
    private String lastModifiedBy;
    private Date modifiedOn;
    private Date createdOn;

    @JsonProperty("_links")
    public void setLinks(Map<String, Link> links) {
        links.forEach((label, link) -> add(link.withRel(label)));
    }
}
