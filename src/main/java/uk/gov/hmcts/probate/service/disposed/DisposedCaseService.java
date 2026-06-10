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
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static uk.gov.hmcts.probate.service.disposed.DisposedCaseService.Constants.*;

@Slf4j
@Service
public class DisposedCaseService {
    private final AzureTableService azureTableService;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    class Constants {
        public static final String DEC_FORENAMES = "dec_forenames";
        public static final String DEC_SURNAME = "dec_surname";
        public static final String DATE_OF_DEATH = "dec_death_date";
        public static final String CASE_TYPE = "case_type";
        public static final String APPL_TYPE = "application_type";
        public static final String DISP_DATE = "disposed_date";
        public static final String HAS_DOCS = "has_scanned_docs";
        public static final String ADDED_DOCS = "added_documents";
        public static final String ALL_DOCS_ADDED = "all_documents_added";

        public static final String CCD_ID = "ccd_id";
        public static final String CONTROL_NUMBER = "control_number";
        public static final String SCAN_DATE = "scan_date";
        public static final String DELIVERY_DATE = "delivery_date";
        public static final String TYPE = "type";
        public static final String SUBTYPE = "subtype";
        public static final String EX_RECORD = "exception_record";
    }

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

        final String deceasedForenames = Optional.ofNullable(caseData.getDeceasedForenames())
                .map(s -> s.trim().toLowerCase())
                .orElse(null);
        final String deceasedSurname = Optional.ofNullable(caseData.getDeceasedSurname())
                .map(s -> s.trim().toLowerCase())
                .orElse(null);
        final String deceasedDateOfDeath = String.valueOf(caseData.getDeceasedDateOfDeath());

        final String caseType = caseData.getCaseType();
        final String applicationType = Optional.ofNullable(caseData.getApplicationType())
                .map(aT -> aT.getCode())
                .orElse(null);

        // This is the wrong date obviously but for the purpose of demonstration...
        // It's also potentially an option if we add this as a post-submission callback for the event into Disposed?
        final String disposedDate = LocalDate.now(clock).toString();

        final List<CollectionMember<ScannedDocument>> scannedDocs = caseData.getScannedDocuments();
        final boolean hasScannedDocs = scannedDocs != null && !scannedDocs.isEmpty();

        TableEntity caseEntity = new TableEntity(azureTableService.getPartition(), ccdId)
                .addProperty(DEC_FORENAMES, deceasedForenames)
                .addProperty(DEC_SURNAME, deceasedSurname)
                .addProperty(DATE_OF_DEATH, deceasedDateOfDeath)
                .addProperty(CASE_TYPE, caseType)
                .addProperty(APPL_TYPE, applicationType)
                .addProperty(DISP_DATE, disposedDate)
                .addProperty(HAS_DOCS, hasScannedDocs);

        if (hasScannedDocs) {
            final List<String> addedDocuments = writeDocumentsToTable(ccdId, scannedDocs);
            final boolean allDocsAdded = addedDocuments.size() == scannedDocs.size();
            final String documentKeys = String.join(",", addedDocuments);

            caseEntity
                    .addProperty(ADDED_DOCS, documentKeys)
                    .addProperty(ALL_DOCS_ADDED, allDocsAdded);
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
                    .addProperty(CCD_ID, ccdId)
                    .addProperty(CONTROL_NUMBER, controlNumber)
                    .addProperty(SCAN_DATE, scanDate)
                    .addProperty(DELIVERY_DATE, deliveryDate)
                    .addProperty(TYPE, type)
                    .addProperty(SUBTYPE, subtype)
                    .addProperty(EX_RECORD, exceptionRecord);

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

    class TakeAllButLast implements IntUnaryOperator {
        final int length;
        int seen;

        TakeAllButLast(final int length) {
            this.length = length;
            this.seen = 0;
        }

        @Override
        public int applyAsInt(int operand) {
            seen++;
            if (seen < length) {
                return operand;
            }
            return operand + 1;
        }
    }

    String getNextString(final String baseString) {
        final int length = baseString.codePointCount(0, baseString.length());
        final IntStream codePoints = baseString.codePoints();
        final String nextString = codePoints
                .map(new TakeAllButLast(length))
                .collect(
                        StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
        return nextString;
    }

    public List<CollectionMember<DisposedCase>> getCasesWithDateOfDeathAndSurname(
            final LocalDate deathDate,
            final Integer deathDateRange,
            final String surname) {
        final TableClient disposedCases = azureTableService.getDisposedCases();

        final StringBuilder filterBuilder = new StringBuilder()
                .append("PartitionKey eq '").append(azureTableService.getPartition()).append("'");

        if (deathDate == null || deathDateRange == 0) {
            filterBuilder.append(" and ").append(DATE_OF_DEATH).append(" eq '").append(deathDate).append("'");
        } else {
            final LocalDate startDate = deathDate.minusDays(deathDateRange);
            final LocalDate endDate = startDate.plusDays(deathDateRange);
            filterBuilder
                    .append(" and ").append(DATE_OF_DEATH).append(" ge '").append(startDate).append("'")
                    .append(" and ").append(DATE_OF_DEATH).append(" le '").append(endDate).append("'");
        }
        if (surname != null && StringUtils.isNotBlank(surname)) {
            final String lcSurname = surname.trim().toLowerCase();
            final String nextString = getNextString(lcSurname);
            filterBuilder
                    .append(" and ").append(DEC_SURNAME).append(" eq '").append(lcSurname).append("'");
        }
        final ListEntitiesOptions caseSearch = new ListEntitiesOptions()
                .setFilter(filterBuilder.toString())
                .setSelect(null);
        final var returnedCases = disposedCases.listEntities(caseSearch, Duration.ofSeconds(3), null);
        return returnedCases.stream()
                .map(this::processCase)
                .collect(Collectors.toList());
    }

    CollectionMember<DisposedCase> processCase(final TableEntity tableEntity) {
        final String ccdId = tableEntity.getRowKey();
        final Map<String, Object> props = tableEntity.getProperties();
        final StringBuilder caseDataBuilder = new StringBuilder();

        final Function<String, StringBuilder> fmtProp = (final String s) -> caseDataBuilder
                    .append("*")
                    .append(s)
                    .append("* : ")
                    .append(props.get(s))
                    .append("\n\n");

        fmtProp.apply(DEC_FORENAMES);
        fmtProp.apply(DEC_SURNAME);
        fmtProp.apply(DATE_OF_DEATH);
        fmtProp.apply(CASE_TYPE);
        fmtProp.apply(APPL_TYPE);
        fmtProp.apply(DISP_DATE);
        fmtProp.apply(HAS_DOCS);
        fmtProp.apply(ALL_DOCS_ADDED);

        final String caseData = caseDataBuilder.toString();

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
                    final Map<String, Object> props = te.getProperties();
                    final StringBuilder documentBuilder = new StringBuilder();

                    final Function<String, StringBuilder> fmtProp = (final String s) -> documentBuilder
                            .append("*")
                            .append(s)
                            .append("* : ")
                            .append(props.get(s))
                            .append("\n\n");

                    fmtProp.apply("RowKey");
                    fmtProp.apply(CONTROL_NUMBER);
                    fmtProp.apply(SCAN_DATE);
                    fmtProp.apply(DELIVERY_DATE);
                    fmtProp.apply(TYPE);
                    fmtProp.apply(SUBTYPE);
                    fmtProp.apply(EX_RECORD);

                    final String docAsString = documentBuilder.toString();
                    return new CollectionMember<>(docAsString);
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
