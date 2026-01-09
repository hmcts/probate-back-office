package uk.gov.hmcts.probate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.caveat.response.CaveatCallbackResponse;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.CallbackResponse;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;
import uk.gov.hmcts.probate.service.migration.GorMigrationHandler;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Controller
@RequestMapping("/migration")
public class DataMigrationController {
    private final CallbackResponseTransformer callbackResponseTransformer;
    private final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer;

    private final Map<String, GorMigrationHandler> gorMigrationHandlers;
    private final Map<String, CaveatMigrationHandler> caveatMigrationHandlers;


    DataMigrationController(
            final CallbackResponseTransformer callbackResponseTransformer,
            final CaveatCallbackResponseTransformer caveatCallbackResponseTransformer,
            final Map<String, GorMigrationHandler> gorMigrationHandlers,
            final Map<String, CaveatMigrationHandler> caveatMigrationHandlers) {
        this.callbackResponseTransformer = Objects.requireNonNull(callbackResponseTransformer);
        this.caveatCallbackResponseTransformer = Objects.requireNonNull(caveatCallbackResponseTransformer);

        this.gorMigrationHandlers = Objects.requireNonNull(gorMigrationHandlers);
        this.caveatMigrationHandlers = Objects.requireNonNull(caveatMigrationHandlers);
    }

    @PostMapping(
            path="/grant_of_representation/apply" ,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> runDataMigration(
            @RequestBody final CallbackRequest callbackRequest,
            final HttpServletRequest request) {
        return gorDataMigration(callbackRequest, request, MigrationOperation.APPLY);
    }

    @PostMapping(
            path = "/grant_of_representation/rollback",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CallbackResponse> rollbackDataMigration(
            @RequestBody final CallbackRequest callbackRequest,
            final HttpServletRequest request) {
        return gorDataMigration(callbackRequest, request, MigrationOperation.ROLLBACK);
    }

    @PostMapping(
            path="/caveat/apply" ,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CaveatCallbackResponse> runDataMigration(
            @RequestBody final CaveatCallbackRequest callbackRequest,
            final HttpServletRequest request) {
        return caveatDataMigration(callbackRequest, request, MigrationOperation.APPLY);
    }

    @PostMapping(
            path = "/caveat/rollback",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CaveatCallbackResponse> rollbackDataMigration(
            @RequestBody final CaveatCallbackRequest callbackRequest,
            final HttpServletRequest request) {
        return caveatDataMigration(callbackRequest, request, MigrationOperation.ROLLBACK);
    }

    ResponseEntity<CallbackResponse> gorDataMigration(
            final CallbackRequest callbackRequest,
            final HttpServletRequest request,
            final MigrationOperation migrationOperation) {
        final CaseDetails caseDetails = Objects.requireNonNull(callbackRequest.getCaseDetails());
        final CaseData caseData = Objects.requireNonNull(caseDetails.getData());
        // might be null if not provided by migration caller
        final String migrationData = caseData.getMigrationCallbackMetadata();
        final Long caseId = Objects.requireNonNull(caseDetails.getId());
        log.info("POST {} for {} of GrantOfRepresentation case {}",
                migrationOperation,
                request.getRequestURI(),
                caseId);

        final JSONObject migrationDataJson;
        if (migrationData != null) {
            try {
                migrationDataJson = new JSONObject(migrationData);
            } catch (JSONException e) {
                log.error("GrantOfRepresentation {} {} - Unable to parse migration data: {}",
                        migrationOperation,
                        caseId,
                        migrationData,
                        e);
                throw new RuntimeException(e);
            }
        } else {
            migrationDataJson = null;
        }

        final CallbackRequest migrated;
        if (migrationDataJson != null) {
            final String migrationId = migrationDataJson.getString("migrationId");
            log.info("GrantOfRepresentation {} {} with migrationId: {}",
                    migrationOperation,
                    caseId,
                    migrationId);
            final GorMigrationHandler migration = gorMigrationHandlers.get(migrationId);
            if (migration == null) {
                log.error("No GrantOfRepresentation migration found for migrationId: {}", migrationId);
                final CallbackResponse callbackResponse = CallbackResponse
                        .builder()
                        .errors(List.of("No GrantOfRepresentation migration found for migrationId: " + migrationId))
                        .build();

                return ResponseEntity.ok(callbackResponse);
            }

            migrated = migration.migrate(callbackRequest, migrationDataJson);
            log.info("GrantOfRepresentation {} {} with migrationId: {} complete",
                    migrationOperation,
                    caseId,
                    migrationId);
        } else {
            log.info("GrantOfRepresentation {} {} without migration data", migrationOperation, caseId);
            migrated = callbackRequest;
        }

        log.info("GrantOfRepresentation {} data migration for case: {}", migrationOperation, caseId);

        CallbackResponse response = callbackResponseTransformer.updateTaskList(migrated, Optional.empty());
        return ResponseEntity.ok(response);
    }

    ResponseEntity<CaveatCallbackResponse> caveatDataMigration(
            final CaveatCallbackRequest callbackRequest,
            HttpServletRequest request,
            MigrationOperation migrationOperation) {
        final CaveatDetails caveatDetails = Objects.requireNonNull(callbackRequest.getCaseDetails());
        final CaveatData caveatData = Objects.requireNonNull(caveatDetails.getData());
        // might be null if not provided by migration caller
        final String migrationData = caveatData.getMigrationCallbackMetadata();
        final Long caseId = Objects.requireNonNull(caveatDetails.getId());

        log.info("POST {} for {} of Caveat case {}",
                request.getRequestURI(),
                migrationOperation,
                caseId);

        final JSONObject migrationDataJson;
        if (migrationData != null) {
            try {
                migrationDataJson = new JSONObject(migrationData);
            } catch (JSONException e) {
                log.error("Caveat {} {} - Unable to parse migration data: {}",
                        caseId,
                        migrationOperation,
                        migrationData,
                        e);
                throw new RuntimeException(e);
            }
        } else {
            migrationDataJson = null;
        }

        final CaveatCallbackRequest migrated;
        if (migrationDataJson != null) {
            final String migrationId = migrationDataJson.getString("migrationId");
            log.info("Caveat {} {} with migrationId: {}",
                    caseId,
                    migrationOperation,
                    migrationId);
            final CaveatMigrationHandler migration = caveatMigrationHandlers.get(migrationId);
            if (migration == null) {
                log.error("No Caveat migration found for migrationId: {}", migrationId);
                final CaveatCallbackResponse caveatCallbackResponse = CaveatCallbackResponse
                        .builder()
                        .errors(List.of("No Caveat migration found for migrationId: " + migrationId))
                        .build();

                return ResponseEntity.ok(caveatCallbackResponse);
            }

            migrated = migration.migrate(callbackRequest, migrationDataJson);
            log.info("Caveat {} {} with migrationId: {} complete",
                    caseId,
                    migrationOperation,
                    migrationId);
        } else {
            log.info("Caveat {} {} without migration data", migrationOperation, caseId);
            migrated = callbackRequest;
        }

        log.info("{} Data migration for case: {}", migrationOperation, caseId);
        CaveatCallbackResponse response = caveatCallbackResponseTransformer.transformResponseWithNoChanges(migrated);
        return ResponseEntity.ok(response);
    }

    enum MigrationOperation {
        APPLY, ROLLBACK;
    }

}
