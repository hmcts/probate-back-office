package uk.gov.hmcts.probate.config;

import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.service.FileTransferApi;
import uk.gov.hmcts.probate.service.FileTransferService;

@Configuration
public class FileTransferConfiguration {

    @Bean
    public FileTransferService fileTransferService(@Value("${ftp.client.url}") String provider) {

        final FileTransferApi fileTransferApi = Feign.builder()
                .encoder(new SpringFormEncoder())
                .decoder(new Decoder.Default())
                .contract(new Contract.Default())
                .target(FileTransferApi.class, provider);

        return new FileTransferService(fileTransferApi);
    }
}
