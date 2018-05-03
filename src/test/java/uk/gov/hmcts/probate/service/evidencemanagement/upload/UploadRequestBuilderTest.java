package uk.gov.hmcts.probate.service.evidencemanagement.upload;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UploadRequestBuilderTest {

    @Test
    public void shouldPrepareRequest() {
        byte[] bytes = {32, 43, 86};
        EvidenceManagementFileUpload fileUpload = new EvidenceManagementFileUpload(MediaType.APPLICATION_PDF, bytes);

        MultiValueMap<String, Object> parameters = UploadRequestBuilder.prepareRequest(
                Collections.singletonList(fileUpload));

        assertEquals(2, parameters.size());
        assertTrue(parameters.containsKey("files"));
        assertTrue(parameters.containsKey("classification"));
        ByteArrayResource body = (ByteArrayResource) ((HttpEntity) parameters.get("files").get(0)).getBody();
        assertTrue(body.getFilename().contains(".pdf"));
    }
}
