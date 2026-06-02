package uk.gov.hmcts.probate.service.disposed;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableServiceException;
import com.azure.data.tables.models.TableTransactionAction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DisposedCase;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.AzureTableService;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
        TableClient disposedCases = azureTableService.getDisposedCases();

        final String ccdId = caseDetails.getId().toString();
        final CaseData caseData = caseDetails.getData();

        final String deceasedForenames = caseData.getDeceasedForenames();
        final String deceasedSurname = caseData.getDeceasedSurname();
        final String deceasedDateOfDeath = caseData.getDeceasedDateOfDeath().toString();

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
                .addProperty("application_type", applicationType)
                .addProperty("disposed_date", disposedDate)
                .addProperty("has_scanned_docs", hasScannedDocs);

        if (hasScannedDocs) {
            final List<String> addedDocuments = writeDocumentsToTable(ccdId, scannedDocs);
            final boolean allDocsAdded = addedDocuments != null && !addedDocuments.isEmpty();
            final String documentKeys = addedDocuments.stream().collect(Collectors.joining(","));

            caseEntity
                    .addProperty("added_documents", documentKeys)
                    .addProperty("all_documents_added", allDocsAdded);
        }

        disposedCases.upsertEntity(caseEntity);

        throw new NotImplementedException();
    }

    List<TableEntity> writeDocumentsToTable(final String ccdId, final List<CollectionMember<ScannedDocument>> scannedDocs) {
        throw new NotImplementedException();
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
        } catch (TableServiceException e) {
            log.warn("No disposed case found for id {}", ccdId);
            throw new NoDisposedCaseFoundException();
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
                .setFilter(String.format("PartitionKey eq '%s' and ccdId eq '%s'",
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

    final class NoDisposedCaseFoundException extends BusinessValidationException {
        public NoDisposedCaseFoundException() {
            super("No case matching provided case ID was found in the disposed case history", "No matching disposed case record found");
        }
    }
}
