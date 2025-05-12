package uk.gov.hmcts.probate.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.probate.query.ElasticSearchQuery;
import uk.gov.hmcts.probate.service.FileSystemResourceService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.SearchResult;

@Repository
@Slf4j
public class ElasticSearchRepository {

    private final CoreCaseDataApi coreCaseDataApi;

    private final AuthTokenGenerator authTokenGenerator;

    private final FileSystemResourceService fileSystemResourceService;

    private final int querySize;

    @Autowired
    public ElasticSearchRepository(CoreCaseDataApi coreCaseDataApi,
                                   AuthTokenGenerator authTokenGenerator,
                                      FileSystemResourceService fileSystemResourceService,
                                   @Value("${core_case_data.elasticsearch.querySize}") int querySize) {
        this.coreCaseDataApi = coreCaseDataApi;
        this.authTokenGenerator = authTokenGenerator;
        this.fileSystemResourceService = fileSystemResourceService;
        this.querySize = querySize;
    }

    public SearchResult fetchFirstPage(String userToken, String caseType, String queryPath,
                                       String fromDate, String toDate) {
        ElasticSearchQuery elasticSearchQuery = ElasticSearchQuery.builder()
                .fileSystemResourceService(fileSystemResourceService)
                .qryPath(queryPath)
                .startDateTime(fromDate)
                .endDateTime(toDate)
                .initialSearch(true)
                .size(querySize)
                .build();
        log.info("Fetching first page from elastic search for queryPath {}.", queryPath);
        String authToken = authTokenGenerator.generate();
        return coreCaseDataApi.searchCases(userToken,
                authToken,
                caseType, elasticSearchQuery.getQuery()
        );
    }

    public SearchResult fetchNextPage(String userToken, String caseType, String searchAfterValue, String queryPath,
                                      String fromDate, String toDate) {
        ElasticSearchQuery subsequentElasticSearchQuery = ElasticSearchQuery.builder()
                .fileSystemResourceService(fileSystemResourceService)
                .qryPath(queryPath)
                .startDateTime(fromDate)
                .endDateTime(toDate)
                .initialSearch(false)
                .size(querySize)
                .searchAfterValue(searchAfterValue)
                .build();
        log.info("Fetching next page from elastic search for queryPath {}.", queryPath);
        String authToken = authTokenGenerator.generate();
        return coreCaseDataApi.searchCases(userToken,
                authToken,
                caseType, subsequentElasticSearchQuery.getQuery()
        );
    }
}