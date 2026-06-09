package uk.gov.hmcts.probate.query;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.service.FileSystemResourceService;

@ExtendWith(SpringExtension.class)
class ElasticSearchQueryTest {

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    private static final String QUERY_TEMPLATE = "{ \"query\": { \"range\": "
            + "{ \"timestamp\": { \"gte\": \"%s\", \"lte\": \"%s\" } } }, \"size\": %d }";

    @BeforeEach
    void setUp() {
        when(fileSystemResourceService.getFileFromResourceAsString(any())).thenReturn(QUERY_TEMPLATE);
    }

    @Test
    void shouldReturnInitialQueryWhenInitialSearchIsTrue() {
        ElasticSearchQuery query = ElasticSearchQuery.builder()
                .fileSystemResourceService(fileSystemResourceService)
                .qryPath("some/path")
                .startDateTime("2024-01-01")
                .endDateTime("2024-01-31")
                .size(50)
                .initialSearch(true)
                .build();
        String resultQuery = query.getQuery();
        assertNotNull(resultQuery);
        assertTrue(resultQuery.contains("\"gte\": \"2024-01-01\""));
        assertTrue(resultQuery.contains("\"lte\": \"2024-01-31\""));
        assertTrue(resultQuery.contains("\"size\": 50"));
        assertTrue(resultQuery.endsWith("\n    }"));
    }

    @Test
    void shouldReturnSubsequentQueryWhenInitialSearchIsFalse() {
        ElasticSearchQuery query = ElasticSearchQuery.builder()
                .fileSystemResourceService(fileSystemResourceService)
                .qryPath("some/path")
                .startDateTime("2024-01-01")
                .endDateTime("2024-01-31")
                .size(50)
                .searchAfterValue("12345")
                .initialSearch(false)
                .build();

        String resultQuery = query.getQuery();

        assertNotNull(resultQuery);
        assertTrue(resultQuery.contains("\"gte\": \"2024-01-01\""));
        assertTrue(resultQuery.contains("\"lte\": \"2024-01-31\""));
        assertTrue(resultQuery.contains("\"size\": 50"));
        assertTrue(resultQuery.contains("\"search_after\": [12345]"));
        assertTrue(resultQuery.endsWith("\n    }"));
    }

    @Test
    void shouldLoadQueryFromFile() {
        ElasticSearchQuery query = ElasticSearchQuery.builder()
                .fileSystemResourceService(fileSystemResourceService)
                .qryPath("test/path/to/query.json")
                .startDateTime("2024-01-01")
                .endDateTime("2024-01-31")
                .size(50)
                .initialSearch(true)
                .build();

        String queryContent = query.getQuery();

        verify(fileSystemResourceService, times(1)).getFileFromResourceAsString("test/path/to/query.json");
        assertNotNull(queryContent);
    }
}
