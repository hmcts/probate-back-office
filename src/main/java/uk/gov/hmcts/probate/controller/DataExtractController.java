package uk.gov.hmcts.probate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;
import uk.gov.hmcts.probate.service.CaseQueryService;
import uk.gov.hmcts.probate.service.ExcelaCriteriaService;
import uk.gov.hmcts.probate.service.FileTransferService;
import uk.gov.hmcts.probate.service.NotificationService;
import uk.gov.hmcts.probate.service.filebuilder.FileExtractDateFormatter;
import uk.gov.hmcts.probate.service.filebuilder.HmrcFileService;
import uk.gov.hmcts.probate.service.filebuilder.IronMountainFileService;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/data-extract")
@RestController
@Api(tags = "Initiate data extract for IronMountain and Excela")
public class DataExtractController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final CaseQueryService caseQueryService;
    private final NotificationService notificationService;
    private final FileTransferService fileTransferService;
    private final IronMountainFileService ironMountainFileService;
    private final HmrcFileService hmrcFileService;
    private final FileExtractDateFormatter fileExtractDateFormatter;
    private final ExcelaCriteriaService excelaCriteriaService;

    @Scheduled(cron = "${cron.data_extract}")
    @ApiOperation(value = "Initiate HMRC data extract", notes = "Will find cases for yesterdays date")
    @PostMapping(path = "/hmrc")
    public ResponseEntity initiateHmrcExtract() {
        log.info("Extract initiated for HMRC");
        return initiateHmrcExtract(DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate HMRC data extract with date", notes = "Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/hmrc/{date}")
    public ResponseEntity initiateHmrcExtract(@ApiParam(value = "Date to find cases against", required = true)
                                              @PathVariable("date") String date) {
        dateValidator(date);
        log.info("HMRC data extract initiated for date: {}", date);

        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        log.info("Cases found for HMRC: {}", cases.size());

        return uploadHmrcFile(null, date, cases);
    }

    @ApiOperation(value = "Initiate HMRC data extract within 2 dates", notes = "Dates MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/hmrcFromTo")
    public ResponseEntity initiateHmrcExtractFromDate(@RequestParam(value = "fromDate", required = true) String fromDate,
                                                      @RequestParam(value = "toDate", required = true) String toDate) {
        dateValidator(fromDate, toDate);
        log.info("HMRC data extract initiated for dates from-to: {}-{}", fromDate, toDate);

        List<ReturnedCaseDetails> cases = caseQueryService.findCaseStateWithinTimeFrame(fromDate, toDate);
        log.info("Cases found for HMRC: {}", cases.size());

        return uploadHmrcFile(fromDate, toDate, cases);
    }

    @Scheduled(cron = "${cron.data_extract}")
    @ApiOperation(value = "Initiate IronMountain data extract", notes = "Will find cases for yesterdays date")
    @PostMapping(path = "/iron-mountain")
    public ResponseEntity initiateIronMountainExtract() {
        log.info("Extract initiated for Iron Mountain");
        return initiateIronMountainExtract(DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate IronMountain data extract with date", notes = "Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/iron-mountain/{date}")
    public ResponseEntity initiateIronMountainExtract(@ApiParam(value = "Date to find cases against", required = true)
                                                      @PathVariable("date") String date) {
        dateValidator(date);
        log.info("Iron Mountain data extract initiated for date: {}", date);

        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        log.info("Cases found for Iron Mountain: {}", cases.size());

        if (!cases.isEmpty()) {
            log.info("preparing for file upload");
            int response = fileTransferService.uploadFile(ironMountainFileService.createIronMountainFile(
                cases, fileExtractDateFormatter.formatFileDate() + "grant.txt"));

            if (response != 201) {
                log.error("Failed to upload file for: " + date);
                throw new ClientException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Failed to upload file for date: " + date);
            }
        }
        return ResponseEntity.ok(cases.size() + " cases successfully found for date: " + date);
    }

    @Scheduled(cron = "${cron.data_extract}")
    @ApiOperation(value = "Initiate Excela data extract", notes = "Will find cases for yesterdays date")
    @PostMapping(path = "/excela")
    public ResponseEntity initiateExcelaExtract() throws NotificationClientException {
        log.info("Extract initiated for Excela");
        return initiateExcelaExtract(DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate Excela data extract", notes = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/excela/{date}")
    public ResponseEntity initiateExcelaExtract(@ApiParam(value = "Date to find cases against", required = true)
                                                @PathVariable("date") String date) throws NotificationClientException {
        dateValidator(date);
        log.info("Excela data extract initiated for date: {}", date);

        List<ReturnedCaseDetails> cases = caseQueryService.findCasesWithDatedDocument(date);
        List<ReturnedCaseDetails> filteredCases = excelaCriteriaService.getFilteredCases(cases);

        if (!filteredCases.isEmpty()) {
            log.info("Sending email to Excela");
            notificationService.sendExcelaEmail(filteredCases);
        }

        return ResponseEntity.ok(filteredCases.size() + " cases found and emailed for date: " + date);
    }

    private void dateValidator(String date) {
        dateValidator(null, date);
    }

    private void dateValidator(String fromDate, String toDate) {
        try {
            LocalDate to = LocalDate.parse(toDate, DATE_FORMAT);
            if (!StringUtils.isBlank(fromDate)) {
                LocalDate from = LocalDate.parse(fromDate, DATE_FORMAT);
                if (!from.isBefore(to)) {
                    throw new ClientException(HttpStatus.BAD_REQUEST.value(),
                        "Error on extract dates, fromDate is not before toDate: " + fromDate + "," + toDate);
                }
            }
        } catch (DateTimeParseException e) {
            log.error("Error parsing date, use the format of 'yyyy-MM-dd': ");
            throw new ClientException(HttpStatus.BAD_REQUEST.value(),
                "Error parsing date, use the format of 'yyyy-MM-dd': " + e.getMessage());
        }
    }

    private ResponseEntity uploadHmrcFile(String fromDate, String date, List<ReturnedCaseDetails> cases) {
        String dateDesc = (StringUtils.isEmpty(fromDate) ? " date:" : " from " + fromDate + " to") + " " + date;
        if (!cases.isEmpty()) {
            log.info("preparing for file upload");
            int response = fileTransferService.uploadFile(hmrcFileService.createHmrcFile(
                cases, "1_" + fileExtractDateFormatter.formatFileDate() + ".dat"));

            if (response != 201) {
                log.error("Failed to upload file for :" + dateDesc);
                throw new ClientException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Failed to upload file for " + dateDesc);
            }
        }
        return ResponseEntity.ok(cases.size() + " cases successfully found for" + dateDesc + " for HMRC");
    }

}
