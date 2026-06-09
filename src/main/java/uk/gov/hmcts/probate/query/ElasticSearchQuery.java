package uk.gov.hmcts.probate.query;

import lombok.Builder;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

@Builder
public class ElasticSearchQuery {
    private static final String END_QUERY = "\n    }";
    private static final String SEARCH_AFTER = "\"search_after\": [%s]";
    private final FileSystemResourceService fileSystemResourceService;
    private final String qryPath; // Path to query file
    private final String searchAfterValue;
    private final int size;
    private final String startDateTime;
    private final String endDateTime;
    private final boolean initialSearch;

    private String loadQuery() {
        return fileSystemResourceService.getFileFromResourceAsString(qryPath);
    }

    public String getQuery() {
        String queryTemplate = loadQuery();
        return initialSearch ? getInitialQuery(queryTemplate) : getSubsequentQuery(queryTemplate);
    }

    private String getInitialQuery(String queryTemplate) {
        return String.format(queryTemplate, startDateTime, endDateTime, size) + END_QUERY;
    }

    private String getSubsequentQuery(String queryTemplate) {
        return String.format(queryTemplate, startDateTime, endDateTime, size) + ","
                + String.format(SEARCH_AFTER, searchAfterValue) + END_QUERY;
    }
}
