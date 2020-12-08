package uk.gov.hmcts.probate.service.dataextract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DocumentLink;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.ExcelaCriteriaService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExelaDataExtractService {
    private final CaseQueryService caseQueryService;
    private final NotificationService notificationService;
    private final ExcelaCriteriaService excelaCriteriaService;

    public void performExelaExtractForDate(String date) {
        log.info("Excela data extract initiated for date: {}", date);
        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        log.info("Found {} cases with dated document for Excela", cases.size());
        List<ReturnedCaseDetails> filteredCases = excelaCriteriaService.getFilteredCases(cases);

        CollectionMember<ScannedDocument> scannedDocument = new CollectionMember<>(new ScannedDocument("23452345234523456",
                "test", "other", "will", LocalDateTime.now(), DocumentLink.builder().build(),
                "test", LocalDateTime.now()));
        List<CollectionMember<ScannedDocument>> scannedDocuments = new ArrayList<>();
        scannedDocuments.add(scannedDocument);
        for (ReturnedCaseDetails caseItem : cases) {
            caseItem.getData().setScannedDocuments(scannedDocuments);
        }

        log.info("Sending email to Excela for {} filtered cases", filteredCases.size());
        if (!filteredCases.isEmpty()) {
            log.info("Sending email to Excela");
            try {
                notificationService.sendExcelaEmail(filteredCases);
            } catch (NotificationClientException e) {
                log.warn("NotificationService exception sending email to Exela", e);
                throw new ClientException(HttpStatus.BAD_GATEWAY.value(),
                    "Error on NotificationService sending email to Exela");
            }
        }
    }

}
