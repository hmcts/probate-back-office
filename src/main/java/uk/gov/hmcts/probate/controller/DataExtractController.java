package uk.gov.hmcts.probate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.ExelaDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.HmrcDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.IronMountainDataExtractService;
import uk.gov.hmcts.probate.service.dataextract.SmeeAndFordDataExtractService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/data-extract")
@RestController
@Api(tags = "Initiate data extract for HMRC, IronMountain and Exela")
public class DataExtractController {

    private final HmrcDataExtractService hmrcDataExtractService;
    private final IronMountainDataExtractService ironMountainDataExtractService;
    private final ExelaDataExtractService exelaDataExtractService;
    private final SmeeAndFordDataExtractService smeeAndFordDataExtractService;
    private final DataExtractDateValidator dataExtractDateValidator;

    @ApiOperation(value = "Initiate HMRC data extract within 2 dates", notes = "Dates MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/hmrc")
    public ResponseEntity initiateHmrcExtractFromDate(@RequestParam(value = "fromDate") String fromDate,
                                                      @RequestParam(value = "toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform HMRC data extract from dates...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            hmrcDataExtractService.performHmrcExtractFromDate(fromDate, toDate);
        });
        log.info("Perform HMRC data extract from dates finished");

        return ResponseEntity.accepted().body("Perform HMRC data extract finished");
    }

    @ApiOperation(value = "Initiate IronMountain data extract with date", notes = "Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/iron-mountain")
    public ResponseEntity initiateIronMountainExtract(@ApiParam(value = "Date to find cases against", required = true)
                                                      @RequestParam("date") String date) {
        dataExtractDateValidator.dateValidator(date);

        log.info("Calling perform Iron Mountain data extract from date...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            ironMountainDataExtractService.performIronMountainExtractForDate(date);
        });
        log.info("Perform Iron Mountain data extract from date finished");

        return ResponseEntity.accepted().body("Perform Iron Mountain data extract finished");
    }

    @ApiOperation(value = "Initiate Exela data extract", notes = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/exela")
    public ResponseEntity initiateExelaExtractDateRange(@ApiParam(value = "Date to find cases against", required = true)
                                               @RequestParam("fromDate") String fromDate,
                                                @RequestParam("toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform Exela data extract from date...");
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            exelaDataExtractService.performExelaExtractForDateRange(fromDate, toDate);
        });
        log.info("Perform Exela data extract from date finished");

        return ResponseEntity.accepted().body("Exela data extract finished");
    }

    @ApiOperation(value = "Initiate S and F data extract", notes = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/smee-and-ford")
    public ResponseEntity<Document> initiateSmeeAndFordExtract(
                @ApiParam(value = "Date to find cases against", required = true)
                @RequestParam(value = "fromDate") String fromDate,
                @RequestParam(value = "toDate") String toDate) {

        dataExtractDateValidator.dateValidator(fromDate, toDate);

        log.info("Calling perform SF data extract from date...");
        Document doc = smeeAndFordDataExtractService.performSFExtractForDateRange(fromDate, toDate);
        log.info("Perform SF data extract from date finished");

        return ResponseEntity.ok(doc);
    }

}
