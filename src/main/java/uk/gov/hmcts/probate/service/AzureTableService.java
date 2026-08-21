package uk.gov.hmcts.probate.service;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.AzureTableConfig;

import java.util.Objects;

@Slf4j
@Service
public class AzureTableService {
    private final AzureTableConfig azureTableConfig;

    public AzureTableService(
            final AzureTableConfig azureTableConfig) {
        this.azureTableConfig = Objects.requireNonNull(azureTableConfig);
    }

    public TableClient getDisposedCases() {
        // should this be persisted rather than a new connection every time?
        return new TableClientBuilder()
                .connectionString(azureTableConfig.getConnectionString())
                .tableName(azureTableConfig.getDisposedCasesTable())
                .buildClient();
    }

    public TableClient getDisposedDocuments() {
        // should this be persisted rather than a new connection every time?
        return new TableClientBuilder()
                .connectionString(azureTableConfig.getConnectionString())
                .tableName(azureTableConfig.getDisposedDocumentsTable())
                .buildClient();
    }

    public String getPartition() {
        return azureTableConfig.getPartition();
    }

}
