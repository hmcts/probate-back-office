package uk.gov.hmcts.probate.service;

import feign.Request;
import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.probate.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileTransferServiceTest {

    @Mock
    private FileTransferApi fileTransferApi;

    @InjectMocks
    FileTransferService fileTransferService;
    
    Response response;
    
    File file;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Map headers = Collections.singletonMap("key", Arrays.asList("value1", "value2"));
        Request request = Request.create(Request.HttpMethod.PUT, "url", headers, null, Charset.defaultCharset());
        response = Response.builder().status(201)
            .reason("message")
            .request(request)
            .headers(headers)
            .body("body", StandardCharsets.UTF_8)
            .build();
    }

    @Test
    public void shouldSendFile() throws FileNotFoundException {
        when(fileTransferApi.sendFile(any(), any(), any(), any(), any(), any(), any(),
            any(), any(), any(), any())).thenReturn(response);

        file = ResourceUtils.getFile(FileUtils.class.getResource("/FileTransferFile.dat"));

        fileTransferService.uploadFile(file);

        verify(fileTransferApi).sendFile(contains("1582286096748941"),
            eq(null),
            eq("FileTransferFile.dat"),
            eq("2019-02-02"),
            eq("bfqt"),
            eq("sco"),
            eq("rwdlacup"),
            eq("2029-02-10T18:49:08Z"),
            eq("2020-02-10T10:49:08Z"),
            eq("https,http"),
            eq(null));

    }
}