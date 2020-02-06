package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.DataExtractConfiguration;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.DataExtractUnauthorisedException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataExtractScheduleValidator {
    private final DataExtractConfiguration dataExtractConfiguration;
    private static final String SCHEDULE_EXELA = "EXELA";
    private static final String SCHEDULE_HMRC = "HMRC";
    private static final String SCHEDULE_IRON = "IRON MOUNTAIN";

    public void validateHmrc(String pathKey) {
        validateScheduleKey(SCHEDULE_HMRC, dataExtractConfiguration.getHmrc(), pathKey);
    }

    public void validateIronMountain(String pathKey) {
        validateScheduleKey(SCHEDULE_IRON, dataExtractConfiguration.getIron(), pathKey);
    }

    public void validateExela(String pathKey) {
        validateScheduleKey(SCHEDULE_EXELA, dataExtractConfiguration.getExela(), pathKey);
    }

    private void validateScheduleKey(String scheduleType, String cronConfig, String pathKey) {
        try {
            String decodedKey = URLDecoder.decode(pathKey, StandardCharsets.UTF_8.name());
            log.error("Decoded key/config {} / {} ", decodedKey, cronConfig);
            if (!cronConfig.equals(decodedKey)) {
                log.error("Data extract for {} does not have a valid auth key", scheduleType);
                throw new DataExtractUnauthorisedException();
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Error decoding pathKey {} / {} ", pathKey, cronConfig);
        }
    }

}
