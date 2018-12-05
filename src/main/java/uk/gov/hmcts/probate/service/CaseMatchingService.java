package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.hmcts.probate.config.CCDGatewayConfiguration;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.MatchedCases;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.insights.AppInsightsEvent.REQUEST_SENT;

@Service
@RequiredArgsConstructor
public class CaseMatchingService {

    private static final String TEMPLATE_DIRECTORY = "templates/elasticsearch/";
    private static final String ES_QUERY = "case_matching_query.json";
    private static final String CASE_TYPE_ID = "ctid";
    private static final String CASE_TYPE_VALUE = "GrantOfRepresentation";

    private final CCDGatewayConfiguration ccdGatewayConfiguration;
    private final RestTemplate restTemplate;
    private final AppInsights appInsights;
    private final HttpHeadersFactory headers;
    private final FileSystemResourceService fileSystemResourceService;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    public List<CaseMatch> findMatches(CaseDetails caseDetails) {
        CaseData data = caseDetails.getData();

        String jsonQuery = getQueryTemplate()
                .replace(":deceasedForenames", data.getDeceasedForenames())
                .replace(":deceasedSurname", data.getDeceasedSurname())
                .replace(":deceasedDateOfDeath", data.getDeceasedDateOfDeath().format(dateTimeFormatter));

        URI uri = UriComponentsBuilder
                .fromHttpUrl(ccdGatewayConfiguration.getHost() + ccdGatewayConfiguration.getCaseMatchingPath())
                .queryParam(CASE_TYPE_ID, CASE_TYPE_VALUE)
                .build().encode().toUri();

        HttpEntity<String> entity = new HttpEntity<>(jsonQuery, headers.getAuthorizationHeaders());

        MatchedCases matchedCases = restTemplate.postForObject(uri, entity, MatchedCases.class);

        appInsights.trackEvent(REQUEST_SENT, uri.toString());

        return matchedCases.getCases().stream()
                .filter(c -> !c.getId().equals(caseDetails.getId()))
                .map(c -> CaseMatch.builder()
                        .ccdId(c.getId().toString())
                        .fullName(c.getData().getDeceasedFullName())
                        .dod(c.getData().getDeceasedDateOfDeath().format(dateTimeFormatter))
                        .postcode(c.getData().getDeceasedAddress().getPostCode())
                        .build())
                .collect(Collectors.toList());
    }

    private String getQueryTemplate() {
        return fileSystemResourceService.getFileFromResourceAsString(TEMPLATE_DIRECTORY + ES_QUERY);
    }
}
