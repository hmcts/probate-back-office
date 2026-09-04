package uk.gov.hmcts.probate.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AzureTableConfig {
    private final String connectionString;
    private final String disposedCasesTable;
    private final String disposedDocumentsTable;
    private final String partition;

    public AzureTableConfig(
            @Value("${azure.storage.table.connectionString}")
            final String connectionString,
            @Value("${azure.storage.table.disposedCasesTable}")
            final String disposedCasesTable,
            @Value("${azure.storage.table.disposedDocumentsTable}")
            final String disposedDocumentsTable,
            @Value("${azure.storage.table.partition}")
            final String partition) {
        this.connectionString = connectionString;
        this.disposedCasesTable = disposedCasesTable;
        this.disposedDocumentsTable = disposedDocumentsTable;
        this.partition = partition;
    }
}
