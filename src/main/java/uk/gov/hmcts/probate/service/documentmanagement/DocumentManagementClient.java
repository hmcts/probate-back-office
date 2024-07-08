package uk.gov.hmcts.probate.service.documentmanagement;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name = "case-document-am-api", url = "${case_document_am.url}/cases/documents")
public interface DocumentManagementClient {
    static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    @GetMapping(value = "/{documentId}/binary")
    ResponseEntity<Resource> getDocumentBinary(@RequestHeader(AUTHORIZATION) String authorisation,
                                               @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuth,
                                               @RequestBody final String binaryUrl);

}
