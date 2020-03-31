package uk.gov.hmcts.probate.service;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.probate.exception.BadRequestException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
        log.info("Starting file upload to ftp for file:" + file.toPath() + ":" + file.getName());
        log.info(":{} :{} :{} :{} :{} :{} :{} :{} :{} :{}",  targetEnv , file.getName(), SV_VALID_FROM, SS, SRT, 
            SP, SE_SIG_EXPIRY_DATE, ST_SIG_CREATION_DATE, SPR, signature);
        Response response = null;
        String fileAsString = fileAsString(file.toPath().toString());

        try {
            response = fileTransferApi.sendFile(
                fileAsString,
                targetEnv,
                file.getName(),
                SV_VALID_FROM,
                SS,
                SRT,
                SP,
                SE_SIG_EXPIRY_DATE,
                ST_SIG_CREATION_DATE,
                SPR,
                signature);
            log.info("File transfer response: {}", response.status());
            Files.delete(file.toPath());
        } catch (Exception e) {
            log.error("Error handling file: " + e.getMessage());
            throw new BadRequestException("Failed to initiate file transfer request: " + e.getMessage());
        }
        return response.status();
    }

    private String fileAsString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            log.error("Error creating request body from file: {}", e.getMessage());
        }
        return contentBuilder.toString();
    }

}
