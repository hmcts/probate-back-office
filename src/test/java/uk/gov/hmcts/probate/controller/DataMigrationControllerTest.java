package uk.gov.hmcts.probate.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.exception.DataMigrationException;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatDetails;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.migration.CaveatMigrationHandler;
import uk.gov.hmcts.probate.service.migration.GorMigrationHandler;
import uk.gov.hmcts.probate.transformer.CallbackResponseTransformer;
import uk.gov.hmcts.probate.transformer.CaveatCallbackResponseTransformer;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


class DataMigrationControllerTest {
    @Mock
    CallbackResponseTransformer callbackResponseTransformerMock;
    @Mock
    CaveatCallbackResponseTransformer caveatCallbackResponseTransformerMock;
    @Mock
    Map<String, GorMigrationHandler> gorMigrationHandlersMock;
    @Mock
    Map<String, CaveatMigrationHandler> caveatMigrationHandlersMock;

    AutoCloseable closeableMocks;

    DataMigrationController controller;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);
        controller = new DataMigrationController(
                callbackResponseTransformerMock,
                caveatCallbackResponseTransformerMock,
                gorMigrationHandlersMock,
                caveatMigrationHandlersMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void gorDataMigrationThrowsIfMigrationDataNotJson() {
        final CallbackRequest callbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaseDetails caseDetails = mock();
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        final Long caseId = 1L;
        when(caseDetails.getId())
                .thenReturn(caseId);

        final CaseData caseData = mock();
        when(caseDetails.getData())
                .thenReturn(caseData);

        final String migrationMetadata = "a";
        when(caseData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        assertThrows(
                DataMigrationException.class,
                () -> controller.gorDataMigration(callbackRequest, request, migrationOperation));

        assertAll(
                () -> verifyNoInteractions(gorMigrationHandlersMock),
                () -> verify(callbackResponseTransformerMock, never())
                        .updateTaskList(any(), eq(Optional.empty())));
    }

    @Test
    void gorDataMigrationDoesNotGetHandlerIfNoMetadata() {
        final CallbackRequest callbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaseDetails caseDetails = mock();
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        final Long caseId = 1L;
        when(caseDetails.getId())
                .thenReturn(caseId);

        final CaseData caseData = mock();
        when(caseDetails.getData())
                .thenReturn(caseData);

        final String migrationMetadata = null;
        when(caseData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        controller.gorDataMigration(
                callbackRequest,
                request,
                migrationOperation);

        assertAll(
                () -> verifyNoInteractions(gorMigrationHandlersMock),
                () -> verify(callbackResponseTransformerMock)
                        .updateTaskList(callbackRequest, Optional.empty()));
    }

    @Test
    void gorDataMigrationThrowsIfNoMigrationId() {
        final CallbackRequest callbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaseDetails caseDetails = mock();
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        final Long caseId = 1L;
        when(caseDetails.getId())
                .thenReturn(caseId);

        final CaseData caseData = mock();
        when(caseDetails.getData())
                .thenReturn(caseData);

        final String migrationMetadata = "{}";
        when(caseData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        assertThrows(
                DataMigrationException.class,
                () -> controller.gorDataMigration(callbackRequest, request, migrationOperation));

        assertAll(
                () -> verifyNoInteractions(gorMigrationHandlersMock),
                () -> verify(callbackResponseTransformerMock, never())
                        .updateTaskList(any(), eq(Optional.empty())));
    }

    @Test
    void gorDataMigrationThrowsIfNoMigrationHandler() {
        final CallbackRequest callbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaseDetails caseDetails = mock();
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        final Long caseId = 1L;
        when(caseDetails.getId())
                .thenReturn(caseId);

        final CaseData caseData = mock();
        when(caseDetails.getData())
                .thenReturn(caseData);

        final String migrationId = UUID.randomUUID().toString();
        final String migrationMetadata = new JSONObject()
                .put("migrationId", migrationId)
                .toString();
        when(caseData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        assertThrows(
                DataMigrationException.class,
                () -> controller.gorDataMigration(callbackRequest, request, migrationOperation));

        assertAll(
                () -> verify(gorMigrationHandlersMock).get(migrationId),
                () -> verify(callbackResponseTransformerMock, never())
                        .updateTaskList(any(), eq(Optional.empty())));
    }

    @Test
    void gorDataMigrationCallsMigrationHandlerAndTransformsResult() {
        final CallbackRequest callbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaseDetails caseDetails = mock();
        when(callbackRequest.getCaseDetails())
                .thenReturn(caseDetails);

        final Long caseId = 1L;
        when(caseDetails.getId())
                .thenReturn(caseId);

        final CaseData caseData = mock();
        when(caseDetails.getData())
                .thenReturn(caseData);

        final String migrationId = UUID.randomUUID().toString();
        final String migrationMetadata = new JSONObject()
                .put("migrationId", migrationId)
                .toString();
        when(caseData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        final GorMigrationHandler gorMigrationHandler = mock();
        when(gorMigrationHandlersMock.get(migrationId))
                .thenReturn(gorMigrationHandler);

        final CallbackRequest migratedCallbackRequest = mock();
        when(gorMigrationHandler.migrate(eq(callbackRequest), any()))
                .thenReturn(migratedCallbackRequest);

        controller.gorDataMigration(callbackRequest, request, migrationOperation);

        assertAll(
                () -> verify(gorMigrationHandlersMock).get(migrationId),
                () -> verify(gorMigrationHandler).migrate(eq(callbackRequest), any()),
                () -> verify(callbackResponseTransformerMock)
                        .updateTaskList(migratedCallbackRequest, Optional.empty()));
    }

    @Test
    void caveatDataMigrationThrowsIfMigrationDataNotJson() {
        final CaveatCallbackRequest caveatCallbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaveatDetails caveatDetails = mock();
        when(caveatCallbackRequest.getCaseDetails())
                .thenReturn(caveatDetails);

        final Long caseId = 1L;
        when(caveatDetails.getId())
                .thenReturn(caseId);

        final CaveatData caveatData = mock();
        when(caveatDetails.getData())
                .thenReturn(caveatData);

        final String migrationMetadata = "a";
        when(caveatData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        assertThrows(
                DataMigrationException.class,
                () -> controller.caveatDataMigration(caveatCallbackRequest, request, migrationOperation));

        assertAll(
                () -> verifyNoInteractions(caveatMigrationHandlersMock),
                () -> verify(caveatCallbackResponseTransformerMock, never())
                        .transformResponseWithNoChanges(any()));
    }

    @Test
    void caveatDataMigrationDoesNotGetHandlerIfNoMetadata() {
        final CaveatCallbackRequest caveatCallbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaveatDetails caveatDetails = mock();
        when(caveatCallbackRequest.getCaseDetails())
                .thenReturn(caveatDetails);

        final Long caseId = 1L;
        when(caveatDetails.getId())
                .thenReturn(caseId);

        final CaveatData caveatData = mock();
        when(caveatDetails.getData())
                .thenReturn(caveatData);

        final String migrationMetadata = null;
        when(caveatData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        controller.caveatDataMigration(
                caveatCallbackRequest,
                request,
                migrationOperation);

        assertAll(
                () -> verifyNoInteractions(caveatMigrationHandlersMock),
                () -> verify(caveatCallbackResponseTransformerMock)
                        .transformResponseWithNoChanges(caveatCallbackRequest));
    }

    @Test
    void caveatDataMigrationThrowsIfNoMigrationId() {
        final CaveatCallbackRequest caveatCallbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaveatDetails caveatDetails = mock();
        when(caveatCallbackRequest.getCaseDetails())
                .thenReturn(caveatDetails);

        final Long caseId = 1L;
        when(caveatDetails.getId())
                .thenReturn(caseId);

        final CaveatData caveatData = mock();
        when(caveatDetails.getData())
                .thenReturn(caveatData);

        final String migrationMetadata = "{}";
        when(caveatData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        assertThrows(
                DataMigrationException.class,
                () -> controller.caveatDataMigration(caveatCallbackRequest, request, migrationOperation));

        assertAll(
                () -> verifyNoInteractions(caveatMigrationHandlersMock),
                () -> verify(caveatCallbackResponseTransformerMock, never())
                        .transformResponseWithNoChanges(any()));
    }

    @Test
    void caveatDataMigrationThrowsIfNoMigrationHandler() {
        final CaveatCallbackRequest caveatCallbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaveatDetails caveatDetails = mock();
        when(caveatCallbackRequest.getCaseDetails())
                .thenReturn(caveatDetails);

        final Long caseId = 1L;
        when(caveatDetails.getId())
                .thenReturn(caseId);

        final CaveatData caveatData = mock();
        when(caveatDetails.getData())
                .thenReturn(caveatData);

        final String migrationId = UUID.randomUUID().toString();
        final String migrationMetadata = new JSONObject()
                .put("migrationId", migrationId)
                .toString();
        when(caveatData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        assertThrows(
                DataMigrationException.class,
                () -> controller.caveatDataMigration(caveatCallbackRequest, request, migrationOperation));

        assertAll(
                () -> verify(caveatMigrationHandlersMock).get(migrationId),
                () -> verify(caveatCallbackResponseTransformerMock, never())
                        .transformResponseWithNoChanges(any()));
    }

    @Test
    void caveatDataMigrationCallsMigrationHandlerAndTransformsResult() {
        final CaveatCallbackRequest caveatCallbackRequest = mock();
        final HttpServletRequest request = mock();
        final DataMigrationController.MigrationOperation migrationOperation = mock();

        final CaveatDetails caveatDetails = mock();
        when(caveatCallbackRequest.getCaseDetails())
                .thenReturn(caveatDetails);

        final Long caseId = 1L;
        when(caveatDetails.getId())
                .thenReturn(caseId);

        final CaveatData caveatData = mock();
        when(caveatDetails.getData())
                .thenReturn(caveatData);

        final String migrationId = UUID.randomUUID().toString();
        final String migrationMetadata = new JSONObject()
                .put("migrationId", migrationId)
                .toString();
        when(caveatData.getMigrationCallbackMetadata())
                .thenReturn(migrationMetadata);

        final CaveatMigrationHandler caveatMigrationHandler = mock();
        when(caveatMigrationHandlersMock.get(migrationId))
                .thenReturn(caveatMigrationHandler);

        final CaveatCallbackRequest migratedCaveatCallbackRequest = mock();
        when(caveatMigrationHandler.migrate(eq(caveatCallbackRequest), any()))
                .thenReturn(migratedCaveatCallbackRequest);

        controller.caveatDataMigration(caveatCallbackRequest, request, migrationOperation);

        assertAll(
                () -> verify(caveatMigrationHandlersMock).get(migrationId),
                () -> verify(caveatMigrationHandler).migrate(eq(caveatCallbackRequest), any()),
                () -> verify(caveatCallbackResponseTransformerMock)
                        .transformResponseWithNoChanges(migratedCaveatCallbackRequest));
    }
}
