package uk.gov.hmcts.probate.service.evidencemanagement.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.probate.model.evidencemanagement.EvidenceManagementFileUpload;

import java.util.List;

public class UploadRequestBuilder {

    private static final String FILES_PARAMETER = "files";
    private static final String CLASSIFICATION_PARAMETER = "classification";
    private static final String CLASSIFICATION_PRIVATE_PARAMETER = "PRIVATE";

    private static final Logger log = LoggerFactory.getLogger(UploadRequestBuilder.class);

    public static MultiValueMap<String, Object> prepareRequest(EvidenceManagementFileUpload file) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        HttpEntity<Resource> resourceHttpEntity = UploadRequestBuilder.buildPartFromFile(file);
        parameters.add(FILES_PARAMETER, resourceHttpEntity);
        parameters.add(CLASSIFICATION_PARAMETER, CLASSIFICATION_PRIVATE_PARAMETER);
        log.info(parameters.toString());

        return parameters;
    }

    public static MultiValueMap<String, Object> prepareRequest(List<EvidenceManagementFileUpload> files) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        files.stream()
            .map(UploadRequestBuilder::buildPartFromFile)
            .forEach(file -> parameters.add(FILES_PARAMETER, file));

        parameters.add(CLASSIFICATION_PARAMETER, CLASSIFICATION_PRIVATE_PARAMETER);
        log.info(parameters.toString());

        return parameters;
    }

    private static HttpEntity<Resource> buildPartFromFile(EvidenceManagementFileUpload file) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(file.getContentType());

        return new HttpEntity<>(buildByteArrayResource(file), headers);
    }

    private static ByteArrayResource buildByteArrayResource(EvidenceManagementFileUpload file) {
        /*
         * We need to override the getFileName method to return something
         * otherwise spring calls this method, gets null and throws a NPE.
         * If you leave this out then you end up hitting the wrong EM end point
         * because the list of files is mapped properly.
         */
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getFileName();
            }
        };
    }
}
