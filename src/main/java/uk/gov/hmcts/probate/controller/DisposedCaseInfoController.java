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
import uk.gov.hmcts.probate.model.DisposedCaseSearchType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DisposedCase;
import uk.gov.hmcts.probate.model.ccd.raw.DisposedCaseSearch;
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
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

        final DisposedCaseSearch disposedCaseSearch = callbackRequest.getCaseDetails().getData().getDisposedCasesCcdId();

        final String searchType = disposedCaseSearch.getSearchType();
        final DisposedCaseSearchType disposedCaseSearchType = DisposedCaseSearchType.fromCode(searchType);
        final List<CollectionMember<DisposedCase>> disposedCasesReturn = switch (disposedCaseSearchType) {
            case ALL -> {
                yield disposedCaseService.getAllCases();
            }
            case CASE_ID -> {
                yield List.of(disposedCaseService.getCase(disposedCaseSearch.getCcdId().trim()));
            }
            case DATE_OF_DEATH -> {
                yield disposedCaseService.getCasesWithDateOfDeathAndSurname(
                        disposedCaseSearch.getDecDeathDate(),
                        disposedCaseSearch.getDecDeathDateRange(),
                        null);
            }
            case DATE_OF_DEATH_AND_SURNAME -> {
                yield disposedCaseService.getCasesWithDateOfDeathAndSurname(
                        disposedCaseSearch.getDecDeathDate(),
                        disposedCaseSearch.getDecDeathDateRange(),
                        disposedCaseSearch.getDecSurname());
            }
            case FULL_NAME -> throw new NotImplementedException();
        };

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
