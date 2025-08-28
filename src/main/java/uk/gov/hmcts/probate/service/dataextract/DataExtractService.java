package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DataExtractType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.DATE_FORMAT;

@Service
@RequiredArgsConstructor
@Slf4j
@Configuration
public class DataExtractService {
    private final CaseQueryService caseQueryService;
    private final List<DataExtractStrategy> strategies;

    public void performExtractForDateRange(String fromDate, String toDate, DataExtractType type) {
        LocalDate start = LocalDate.parse(fromDate, DATE_FORMAT);
        LocalDate end   = LocalDate.parse(toDate, DATE_FORMAT);
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            performExtractForDate(d.format(DATE_FORMAT), type);
        }
    }

    private void performExtractForDate(String date, DataExtractType type) {
        DataExtractStrategy strategy = strategies.stream()
            .filter(s -> s.matchesType(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No strategy found for type: " + type));

        try {
            log.info("Fetch and process data extract for type: {} date: {}", type, date);
            List<ReturnedCaseDetails> cases =
                    caseQueryService.findAllCasesWithGrantIssuedDate(String.valueOf(type), date);
            log.info("ES query executed for date: {}, cases found: {}", date, cases.size());
            File zipFile = strategy.generateZipFile(cases, date);
            log.info("Zip file generated for type: {} date: {} filename: {}", type, date, zipFile.getName());
            strategy.uploadToBlobStorage(zipFile);
            log.info("Data extract for type: {} date: {} completed successfully", type, date);
        } catch (Exception e) {
            final String errorMsg = String.format("Error running data extract for type: %s for date: %s", type, date);
            log.error(errorMsg, e);
        }
    }
}
