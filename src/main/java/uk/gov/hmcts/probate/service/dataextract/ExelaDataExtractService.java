package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.ExcelaCriteriaService;
import uk.gov.hmcts.probate.service.NotificationService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExelaDataExtractService {
    private final CaseQueryService caseQueryService;
    private final NotificationService notificationService;
    private final ExcelaCriteriaService excelaCriteriaService;


    public void performExelaExtractForDateRange(String fromDate, String toDate) {
        if (fromDate.equals(toDate)) {
            performExelaExtractForDate(fromDate);
        } else {
            log.info("Excela data extract initiated for dates from-to: {}-{}", fromDate, toDate);
            List<ReturnedCaseDetails> cases = caseQueryService.findCaseStateWithinDateRangeExela(fromDate, toDate);
            log.info("Found {} cases with dated document for Excela", cases.size());

            sendExelaEmail(cases);
        }


    }

    public void performExelaExtractForDate(String date) {
        log.info("Excela data extract initiated for date: {}", date);
        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        log.info("Found {} cases with dated document for Excela", cases.size());

        sendExelaEmail(cases);
    }

    private void sendExelaEmail(List<ReturnedCaseDetails> cases) {
        List<ReturnedCaseDetails> filteredCases = excelaCriteriaService.getFilteredCases(cases);

        log.info("Sending email to Excela for {} filtered cases", filteredCases.size());
        if (!filteredCases.isEmpty()) {
            log.info("Sending email to Excela");
            try {
                notificationService.sendExcelaEmail(filteredCases);
            } catch (Exception e) {
                log.info("NotificationService exception sending email to Exela", e);
                throw new ClientException(HttpStatus.BAD_GATEWAY.value(),
                        "Error on NotificationService sending email to Exela");
            }
        }
    }

}
