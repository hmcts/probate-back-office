package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Slf4j
public class FileTransferService {

    private final FileTransferApi fileTransferApi;
    private static final String SV_VALID_FROM = "2019-02-02";
    private static final String SS = "bfqt";
    private static final String SRT = "sco";
    private static final String SP = "rwdlacup";
    private static final String SE_SIG_EXPIRY_DATE = "2029-02-10T18:49:08Z";
    private static final String ST_SIG_CREATION_DATE = "2020-02-10T10:49:08Z";
    private static final String SPR = "https,http";

    @Value("${ftp.client.signature}")
    private String signature;

    @Value("${ftp.client.environment}")
    private String targetEnv;

    @Autowired
    public FileTransferService(FileTransferApi fileTransferApi) {
        this.fileTransferApi = fileTransferApi;
    }

    public int uploadFile(File file) {
        String serverUrl = "https://green.blob.core.windows.net/probate-aat/" + file.getName() + "?" +
            "sv=2019-02-02&ss=bfqt&srt=sco&sp=rwdlacup&se=2029-02-10T18:49:08Z&st=2020-02-10T10:49:08Z&spr=https,http" +
            "&sig=Lbo%2BZwQj0g62ru4lqvgsd8mTnhRzNjzFZxzLHUqIbgw%3D";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("x-ms-blob-type", "BlockBlob");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);


        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        log.info("serverUrl: {}", serverUrl);
        log.info("requestEntity: {}", requestEntity);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(serverUrl, HttpMethod.PUT, requestEntity, String.class);

        log.info("response: {}", response);
        return response.getStatusCode().value();
    }
}
