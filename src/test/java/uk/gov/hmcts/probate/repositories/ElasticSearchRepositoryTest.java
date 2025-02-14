package uk.gov.hmcts.probate.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.query.ElasticSearchQuery;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ElasticSearchRepositoryTest {

    @Mock
    private FileSystemResourceService fileSystemResourceService;
    @Mock
    private CoreCaseDataApi coreCaseDataApi;
    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @Mock
    private ElasticSearchQuery elasticSearchQuery;

    private ElasticSearchRepository elasticSearchRepository;

    private static final String USER_TOKEN = "userToken";
    private static final String CASE_TYPE = "GrantOfRepresentation";
    private static final String QUERY_PATH = "path/to/query.json";
    private static final String FROM_DATE = "2024-01-01";
    private static final String TO_DATE = "2024-01-31";
    private static final String SEARCH_AFTER_VALUE = "12345";
    private static final int QUERY_SIZE = 50;
    private static final String QUERY_TEMPLATE = "{ \"query\": { \"range\": { \"timestamp\":"
            + " { \"gte\": \"%s\", \"lte\": \"%s\" } } }, \"size\": %d }";

    @BeforeEach
    void setUp() {
        when(authTokenGenerator.generate()).thenReturn("mockAuthToken");
        when(fileSystemResourceService.getFileFromResourceAsString(any())).thenReturn(QUERY_TEMPLATE);
        when(elasticSearchQuery.getQuery()).thenReturn(QUERY_TEMPLATE);
        elasticSearchRepository = new ElasticSearchRepository(coreCaseDataApi, authTokenGenerator,
                fileSystemResourceService, QUERY_SIZE);
    }

    @Test
    void shouldFetchFirstPageSuccessfully() {
        SearchResult mockResult = SearchResult.builder().total(1).cases(Collections.emptyList()).build();
        when(coreCaseDataApi.searchCases(any(), any(), any(), any())).thenReturn(mockResult);

        SearchResult result = elasticSearchRepository
                .fetchFirstPage(USER_TOKEN, CASE_TYPE, QUERY_PATH, FROM_DATE, TO_DATE);

        assertNotNull(result);
        verify(authTokenGenerator, times(1)).generate();
        verify(coreCaseDataApi, times(1))
                .searchCases(eq(USER_TOKEN), eq("mockAuthToken"), eq(CASE_TYPE), any());
    }

    @Test
    void shouldFetchNextPageSuccessfully() {
        SearchResult mockResult = SearchResult.builder().total(1).cases(Collections.emptyList()).build();
        when(coreCaseDataApi.searchCases(any(), any(), any(), any())).thenReturn(mockResult);

        SearchResult result = elasticSearchRepository
                .fetchNextPage(USER_TOKEN, CASE_TYPE, SEARCH_AFTER_VALUE, QUERY_PATH, FROM_DATE, TO_DATE);

        assertNotNull(result);
        verify(authTokenGenerator, times(1)).generate();
        verify(coreCaseDataApi, times(1))
                .searchCases(eq(USER_TOKEN), eq("mockAuthToken"), eq(CASE_TYPE), any());
    }

    @Test
    void shouldThrowExceptionWhenSearchFails() {
        when(coreCaseDataApi
                .searchCases(any(), any(), any(), any())).thenThrow(new RuntimeException("Elasticsearch failure"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                elasticSearchRepository.fetchFirstPage(USER_TOKEN, CASE_TYPE, QUERY_PATH, FROM_DATE, TO_DATE));

        assertEquals("Elasticsearch failure", exception.getMessage());
    }
}