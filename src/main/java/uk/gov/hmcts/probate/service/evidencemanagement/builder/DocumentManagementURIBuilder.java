package uk.gov.hmcts.probate.service.evidencemanagement.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DocumentManagementURIBuilder {

    private final String dmHost;

    private final String evidenceManagementServiceURL;

    @Autowired
    public DocumentManagementURIBuilder(
        @Value("${evidence.management.host}") String dmHost,
        @Value("${evidence.management.upload.file.url}") String evidenceManagementServiceURL) {
        this.dmHost = dmHost;
        this.evidenceManagementServiceURL = evidenceManagementServiceURL;
    }

    public String buildUrl() {
        return dmHost + evidenceManagementServiceURL;
    }

    public String buildUrl(String id) {
        return buildUrl() + "/" + id;
    }
}
