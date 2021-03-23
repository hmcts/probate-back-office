package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.ExcelaCriteriaService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmeeAndFordDataExtractService {
    private final CaseQueryService caseQueryService;
    private final NotificationService notificationService;
    private final ExcelaCriteriaService excelaCriteriaService;


    public Document performSFExtractForDateRange(String fromDate, String toDate) {
        if (fromDate.equals(toDate)) {
            return performSFExtractForDate(fromDate);
        } else {
            log.info("SF data extract initiated from date: {} to {}", fromDate, toDate);
            List<ReturnedCaseDetails> cases = caseQueryService.findCaseStateWithinTimeFrame(fromDate, toDate);
            log.info("Found {} cases with dated document for SF from-to", cases.size());

            return sendSmeeAndFordEmail(cases);
        }


    }

    private Document performSFExtractForDate(String date) {
        log.info("SF data extract initiated for date: {}", date);
        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        log.info("Found {} cases with dated document for SF", cases.size());

        return sendSmeeAndFordEmail(cases);
    }

    private Document sendSmeeAndFordEmail(List<ReturnedCaseDetails> cases) {
        List<ReturnedCaseDetails> filteredCases = excelaCriteriaService.getFilteredCases(cases);

        log.info("Sending email to SF for {} filtered cases", filteredCases.size());
        if (!filteredCases.isEmpty()) {
            log.info("Sending email to SF");
            try {
                return notificationService.sendSmeeAndFordEmail(filteredCases);
            } catch (NotificationClientException e) {
                log.warn("NotificationService exception sending email to SF", e);
                throw new ClientException(HttpStatus.BAD_GATEWAY.value(),
                        "Error on NotificationService sending email to SF");
            }
        }
        
        return null;
    }
}
