package uk.gov.hmcts.probate.controller;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DisposedCase;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.disposed.DisposedCaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequestMapping("/disposed-cases")
@RestController
@Tag(name = "Allows querying information about disposed cases")
public class DisposedCaseInfoController {

    private final DisposedCaseService disposedCaseService;

    DisposedCaseInfoController(
            final DisposedCaseService disposedCaseService) {
        this.disposedCaseService = Objects.requireNonNull(disposedCaseService);
    }

    @PostMapping(
            path = "/store-case",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> storeCase(
            @RequestBody final CallbackRequest callbackRequest,
            final BindingResult bindingResult,
            final HttpServletRequest request) {
        log.info("POST: {}", request.getRequestURI());
        disposedCaseService.writeCaseToTables(callbackRequest.getCaseDetails());
    }

    @PostMapping(
            path = "/query",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> runDisposedQuery(
            @RequestBody final CallbackRequest callbackRequest,
            final BindingResult bindingResult,
            final HttpServletRequest request) {
        log.info("POST: {}", request.getRequestURI());

        final String idToSearch = callbackRequest.getCaseDetails().getData().getDisposedCasesCcdId();


        final List<CollectionMember<DisposedCase>> disposedCasesReturn = new ArrayList<>();
        if (StringUtils.isBlank(idToSearch) || idToSearch.trim().equalsIgnoreCase("*")) {
            final var disposedCases = disposedCaseService.getAllCases();
            disposedCasesReturn.addAll(disposedCases);
        } else {
            // get single case if exists in table
            final CollectionMember<DisposedCase> dc = disposedCaseService.getCase(idToSearch);

            disposedCasesReturn.add(dc);
        }

        final ResponseCaseData rcd = ResponseCaseData.builder()
                .disposedCasesReturn(disposedCasesReturn)
                .build();
        final CallbackResponse cr = CallbackResponse.builder()
                .data(rcd)
                .build();
        return new ResponseEntity<>(cr, HttpStatus.OK);
    }

    @PostMapping(
            path = "/block-submit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CallbackResponse> blockSubmission(
            @RequestBody final CallbackRequest callbackRequest,
            final BindingResult bindingResult,
            final HttpServletRequest request) {

        throw new NotImplementedException();
    }
}
