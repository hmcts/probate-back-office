package uk.gov.hmcts.probate.service.disposed;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableEntityUpdateMode;
import com.azure.data.tables.models.TableServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DisposedCase;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.AzureTableService;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DisposedCaseService {
    private final AzureTableService azureTableService;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    DisposedCaseService(
            final AzureTableService azureTableService,
            final ObjectMapper objectMapper,
            final Clock clock) {
        this.azureTableService = Objects.requireNonNull(azureTableService);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.clock = Objects.requireNonNull(clock);
    }

    public TableEntity writeCaseToTables(final CaseDetails caseDetails) {
        final TableClient disposedCases = azureTableService.getDisposedCases();

        final String ccdId = caseDetails.getId().toString();
        final CaseData caseData = caseDetails.getData();

        final String deceasedForenames = caseData.getDeceasedForenames();
        final String deceasedSurname = caseData.getDeceasedSurname();
        final String deceasedDateOfDeath = String.valueOf(caseData.getDeceasedDateOfDeath());

        final String caseType = caseData.getCaseType();
        final String applicationType = caseData.getApplicationType().getCode();

        // This is the wrong date obviously but for the purpose of demonstration...
        // It's also potentially an option if we add this as a post-submission callback for the event into Disposed?
        final String disposedDate = LocalDate.now(clock).toString();

        final List<CollectionMember<ScannedDocument>> scannedDocs = caseData.getScannedDocuments();
        final boolean hasScannedDocs = scannedDocs != null && !scannedDocs.isEmpty();

        TableEntity caseEntity = new TableEntity(azureTableService.getPartition(), ccdId)
                .addProperty("dec_forenames", deceasedForenames)
                .addProperty("dec_surname", deceasedSurname)
                .addProperty("dec_death_date", deceasedDateOfDeath)
                .addProperty("case_type", caseType)
                .addProperty("application_type", applicationType)
                .addProperty("disposed_date", disposedDate)
                .addProperty("has_scanned_docs", hasScannedDocs);

        if (hasScannedDocs) {
            final List<String> addedDocuments = writeDocumentsToTable(ccdId, scannedDocs);
            final boolean allDocsAdded = addedDocuments.size() == scannedDocs.size();
            final String documentKeys = String.join(",", addedDocuments);

            caseEntity
                    .addProperty("added_documents", documentKeys)
                    .addProperty("all_documents_added", allDocsAdded);
        }

        try {
            disposedCases.upsertEntityWithResponse(
                    caseEntity,
                    TableEntityUpdateMode.REPLACE,
                    Duration.ofSeconds(1),
                    null);
        } catch (TableServiceException e) {
            log.warn("Unable to save disposed case {}", ccdId);
            throw new DisposedCaseException("Unable to save case into table", e);
        }

        return caseEntity;
    }

    List<String> writeDocumentsToTable(
            final String ccdId,
            final List<CollectionMember<ScannedDocument>> scannedDocs) {
        final TableClient disposedDocuments = azureTableService.getDisposedDocuments();

        final List<String> addedDocuments = new ArrayList<>();

        for (final var scannedDocE : scannedDocs) {
            final String key = scannedDocE.getId();
            final ScannedDocument scannedDoc = scannedDocE.getValue();

            final String controlNumber = scannedDoc.getControlNumber();
            final String scanDate = String.valueOf(scannedDoc.getScannedDate());
            final String deliveryDate = String.valueOf(scannedDoc.getDeliveryDate());
            final String type = scannedDoc.getType();
            final String subtype = scannedDoc.getSubtype();
            final String exceptionRecord = scannedDoc.getExceptionRecordReference();

            final TableEntity docEntity = new TableEntity(azureTableService.getPartition(), key)
                    .addProperty("ccd_id", ccdId)
                    .addProperty("control_number", controlNumber)
                    .addProperty("scan_date", scanDate)
                    .addProperty("delivery_date", deliveryDate)
                    .addProperty("type", type)
                    .addProperty("subtype", subtype)
                    .addProperty("exception_record", exceptionRecord);

            try {
                disposedDocuments.upsertEntityWithResponse(
                        docEntity,
                        TableEntityUpdateMode.REPLACE,
                        Duration.ofSeconds(1),
                        null);

                addedDocuments.add(key);
            } catch (final TableServiceException e) {
                log.warn("Unable to save disposed document {} for case {}", key, ccdId);
            }
        }

        return addedDocuments;
    }

    public List<CollectionMember<DisposedCase>> getAllCases() {
        final TableClient disposedCases = azureTableService.getDisposedCases();

        final ListEntitiesOptions caseSearch = new ListEntitiesOptions()
                .setFilter(String.format("PartitionKey eq '%s'", azureTableService.getPartition()))
                .setSelect(null);
        final var returnedCases = disposedCases.listEntities(caseSearch, Duration.ofSeconds(3), null);
        return returnedCases.stream()
                .map(this::processCase)
                .collect(Collectors.toList());
    }

    public CollectionMember<DisposedCase> getCase(final String ccdId) {
        final TableClient disposedCases = azureTableService.getDisposedCases();

        try {
            final TableEntity returnedCase = disposedCases.getEntity(azureTableService.getPartition(), ccdId.trim());
            return processCase(returnedCase);
        } catch (final TableServiceException e) {
            log.warn("No disposed case found for id {}", ccdId);
            throw new DisposedCaseException(
                    "No case matching provided case ID was found in the disposed case history",
                    e);
        }
    }

    CollectionMember<DisposedCase> processCase(final TableEntity tableEntity) {
        final String ccdId = tableEntity.getRowKey();
        final String caseData;
        try {
            caseData = objectMapper.writeValueAsString(tableEntity.getProperties());
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        final var documents = getDocumentsForCase(ccdId);

        final DisposedCase dc = DisposedCase.builder()
                .ccdId(ccdId)
                .caseData(caseData)
                .documentData(documents)
                .build();
        return new CollectionMember<>(dc);
    }

    List<CollectionMember<String>> getDocumentsForCase(final String ccdId) {
        final TableClient disposedDocuments = azureTableService.getDisposedDocuments();

        final ListEntitiesOptions documentSearch = new ListEntitiesOptions()
                .setFilter(String.format("PartitionKey eq '%s' and ccd_id eq '%s'",
                        azureTableService.getPartition(),
                        ccdId.trim()))
                .setSelect(null);
        final var returnedDocs = disposedDocuments.listEntities(documentSearch, Duration.ofSeconds(3), null);

        return returnedDocs.stream().map(te -> {
                    try {
                        final String docAsString = objectMapper.writeValueAsString(te.getProperties());
                        return new CollectionMember<>(docAsString);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    static final class DisposedCaseException extends BusinessValidationException {
        public DisposedCaseException(final String userMessage) {
            super(userMessage, "Issue with case disposal");
        }

        public DisposedCaseException(
                final String userMessage,
                final Throwable cause) {
            this(userMessage);
            // seems like just having a super() constructor which took the cause would be simpler but...
            this.initCause(cause);
        }
    }
}
