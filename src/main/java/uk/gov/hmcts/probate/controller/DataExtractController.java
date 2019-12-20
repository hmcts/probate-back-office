package uk.gov.hmcts.probate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.HmrcDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.HttpStatus.ACCEPTED;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/data-extract")
@RestController
@Api(tags = "Initiate data extract for HMRC, IronMountain and Excela")
public class DataExtractController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final HmrcDataExtractService hmrcDataExtractService;
    private final IronMountainDataExtractService ironMountainDataExtractService;
    private final ExelaDataExtractService exelaDataExtractService;
    private final DataExtractDateValidator dataExtractDateValidator;

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
        dataExtractDateValidator.dateValidator(date);

        log.info("Calling perform HMRC data extract...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            hmrcDataExtractService.performHmrcExtract(date);
        });
        log.info("Perform HMRC data extract called");

        return new ResponseEntity("Perform HMRC data extract called", ACCEPTED);
    }

    @ApiOperation(value = "Initiate HMRC data extract within 2 dates", notes = "Dates MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/hmrcFromTo")
    public ResponseEntity initiateHmrcExtractFromDate(@RequestParam(value = "fromDate", required = true) String fromDate,
                                                      @RequestParam(value = "toDate", required = true) String toDate) {
        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform HMRC data extract from date...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            hmrcDataExtractService.performHmrcExtractFromDate(fromDate, toDate);
        });
        log.info("Perform HMRC data extract from date finished");

        return new ResponseEntity("Perform HMRC data extract finished", ACCEPTED);
    }

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
        dataExtractDateValidator.dateValidator(date);

        log.info("Calling perform Iron Mountain data extract from date...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            ironMountainDataExtractService.performIronMountainExtractForDate(date);
        });
        log.info("Perform Iron Mountain data extract from date finished");

        return new ResponseEntity("Perform Iron Mountain data extract finished", ACCEPTED);
    }

    @ApiOperation(value = "Initiate Excela data extract", notes = "Will find cases for yesterdays date")
    @PostMapping(path = "/excela")
    public ResponseEntity initiateExcelaExtract() {
        log.info("Extract initiated for Excela");
        return initiateExcelaExtract(DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate Excela data extract", notes = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/excela/{date}")
    public ResponseEntity initiateExcelaExtract(@ApiParam(value = "Date to find cases against", required = true)
                                                @PathVariable("date") String date) {
        dataExtractDateValidator.dateValidator(date);

        log.info("Calling perform Excela data extract from date...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            exelaDataExtractService.performExelaExtractForDate(date);
        });
        log.info("Perform Excela data extract from date finished");

        return new ResponseEntity("Excela data extract finished", ACCEPTED);

    }

}
