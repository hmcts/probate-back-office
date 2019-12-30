package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.DataExtractConfiguration;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.exception.DataExtractUnauthorisedException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataExtractScheduleValidator {
    private final DataExtractConfiguration dataExtractConfiguration;

    public void validateHmrc(String pathKey) {
        validateScheduleKey(dataExtractConfiguration.getHmrc(), pathKey);
    }

    public void validateIronMountain(String pathKey) {
        validateScheduleKey(dataExtractConfiguration.getIron(), pathKey);
    }

    public void validateExela(String pathKey) {
        validateScheduleKey(dataExtractConfiguration.getExela(), pathKey);
    }

    private void validateScheduleKey(String cronConfig, String pathKey) {
        if (!cronConfig.equals(pathKey)) {
            log.error("Data extract for {} does not have a valid auth key", cronConfig);
            throw new DataExtractUnauthorisedException();
        }
    }

}
