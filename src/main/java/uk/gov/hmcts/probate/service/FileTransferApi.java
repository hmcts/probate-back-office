package uk.gov.hmcts.probate.service;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.config.FeignFTPConfiguration;

@FeignClient(name = "ftp-client", url = "${ftp.client.url}", configuration = FeignFTPConfiguration.class)
public interface FileTransferApi {

    @RequestLine("PUT /{environment}/{fileName}?sv={sv}&ss={ss}&srt={srt}&sp={sp}&se={se}&st={st}&spr={spr}&sig={sig}")
    @Headers({"x-ms-type: file", "x-ms-blob-type: BlockBlob", "Content-Type: multipart/form-data"})
    Response sendFile(
        @RequestBody final MultipartFile file,
        @Param(encoded = true, value = "environment") final String environment,
        @Param(encoded = true, value = "fileName") final String fileName,
        @Param(encoded = true, value = "sv") final String validFromDate,
        @Param(encoded = true, value = "ss") final String ss,
        @Param(encoded = true, value = "srt") final String srt,
        @Param(encoded = true, value = "sp") final String sp,
        @Param(encoded = true, value = "se") final String expiryDate,
        @Param(encoded = true, value = "st") final String timeCreated,
        @Param(encoded = true, value = "spr") final String spr,
        @Param(encoded = true, value = "sig") final String sig
    );
}
