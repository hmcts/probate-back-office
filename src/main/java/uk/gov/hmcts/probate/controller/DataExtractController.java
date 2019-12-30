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
import uk.gov.hmcts.probate.config.DataExtractConfiguration;
import uk.gov.hmcts.probate.service.dataextract.DataExtractDateValidator;
import uk.gov.hmcts.probate.service.dataextract.DataExtractScheduleValidator;
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
    private final DataExtractScheduleValidator dataExtractScheduleValidator;

    @ApiOperation(value = "Initiate HMRC data extract", notes = "Will find cases for yesterdays date")
    @PostMapping(path = "/hmrc/{cronKeyHmrc}")
    public ResponseEntity initiateHmrcExtract(@ApiParam(value = "Cron Key for HMRC", required = true)
                                                  @PathVariable("cronKeyHmrc") String cronKeyHmrc) {
        dataExtractScheduleValidator.validateHmrc(cronKeyHmrc);
        log.info("Extract initiated for HMRC");
        return initiateHmrcExtract(cronKeyHmrc, DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate HMRC data extract with date", notes = "Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/hmrc/{cronKeyHmrc}/{date}")
    public ResponseEntity initiateHmrcExtract(@ApiParam(value = "Cron Key for HMRC", required = true)
                                                  @PathVariable("cronKeyHmrc") String cronKeyHmrc,
                                            @ApiParam(value = "Date to find cases against", required = true)
                                                @PathVariable("date") String date) {
        
        dataExtractScheduleValidator.validateHmrc(cronKeyHmrc);
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
    @PostMapping(path = "/hmrc/{cronKeyHmrc}/hmrcFromTo")
    public ResponseEntity initiateHmrcExtractFromDate(@ApiParam(value = "Cron Key for HMRC", required = true)
                                                          @PathVariable("cronKeyHmrc") String cronKeyHmrc,
                                                      @RequestParam(value = "fromDate", required = true) String fromDate,
                                                      @RequestParam(value = "toDate", required = true) String toDate) {

        dataExtractScheduleValidator.validateHmrc(cronKeyHmrc);
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
    @PostMapping(path = "/iron-mountain/{cronKeyIron}")
    public ResponseEntity initiateIronMountainExtract(@ApiParam(value = "Cron Key for IronMountain", required = true)
                                                          @PathVariable("cronKeyIron") String cronKeyIron) {

        dataExtractScheduleValidator.validateIronMountain(cronKeyIron);

        log.info("Extract initiated for Iron Mountain");
        return initiateIronMountainExtract(cronKeyIron, DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate IronMountain data extract with date", notes = "Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/iron-mountain/{cronKeyIron}/{date}")
    public ResponseEntity initiateIronMountainExtract(@ApiParam(value = "Cron Key for IronMountain", required = true)
                                                          @PathVariable("cronKeyIron") String cronKeyIron,
                                                      @ApiParam(value = "Date to find cases against", required = true)
                                                      @PathVariable("date") String date) {
        dataExtractScheduleValidator.validateIronMountain(cronKeyIron);

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
    @PostMapping(path = "/excela/{cronKeyExela}")
    public ResponseEntity initiateExcelaExtract(@ApiParam(value = "Cron Key for Exela", required = true)
                                                    @PathVariable("cronKeyExela") String cronKeyExela) {
        dataExtractScheduleValidator.validateExela(cronKeyExela);

        log.info("Extract initiated for Excela");
        return initiateExcelaExtract(cronKeyExela, DATE_FORMAT.format(LocalDate.now().minusDays(1L)));
    }

    @ApiOperation(value = "Initiate Excela data extract", notes = " Date MUST be in format 'yyyy-MM-dd'")
    @PostMapping(path = "/excela/{cronKeyExela}/{date}")
    public ResponseEntity initiateExcelaExtract(@ApiParam(value = "Cron Key for Exela", required = true)
                                                    @PathVariable("cronKeyExela") String cronKeyExela,
                                                @ApiParam(value = "Date to find cases against", required = true)
                                                @PathVariable("date") String date) {

        dataExtractScheduleValidator.validateExela(cronKeyExela);

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
