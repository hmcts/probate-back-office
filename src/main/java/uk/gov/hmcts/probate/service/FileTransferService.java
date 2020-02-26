package uk.gov.hmcts.probate.service;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import uk.gov.hmcts.probate.exception.BadRequestException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Optional;

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
        Response response = null;
        //MultipartFile multipartFile = buildMultipartFile(file);

        try {
            response = fileTransferApi.sendFile(
                file,
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
        } catch (IOException e) {
            log.error("Error handling file: " + e.getMessage());
            throw new BadRequestException("Failed to initiate file transfer request: " + e.getMessage());
        }
        return response.status();
    }

    private MultipartFile buildMultipartFile(File file) {
        Optional<FileInputStream> inputStreamOptional = Optional.empty();
        try {
            inputStreamOptional = Optional.of(new FileInputStream(file));
            FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false,
                file.getName(), (int) file.length(), file.getParentFile());
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(inputStreamOptional.get(), os);
            inputStreamOptional.get().close();

            return new CommonsMultipartFile(fileItem);
        } catch (IOException e) {
            log.error("Error building multipart file: " + e.getMessage());
            throw new BadRequestException("Error building multipart file: " + e.getMessage());
        }

    }
}
