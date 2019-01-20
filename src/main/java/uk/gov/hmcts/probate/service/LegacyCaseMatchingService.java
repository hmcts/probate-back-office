package uk.gov.hmcts.probate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.config.CCDGatewayConfiguration;
import uk.gov.hmcts.probate.insights.AppInsights;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.ProbateManType;
import uk.gov.hmcts.probate.service.evidencemanagement.header.HttpHeadersFactory;

import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@Service
public class LegacyCaseMatchingService extends CaseMatchingService {

    public LegacyCaseMatchingService(CCDGatewayConfiguration ccdGatewayConfiguration,
                                     RestTemplate restTemplate,
                                     AppInsights appInsights,
                                     HttpHeadersFactory headers,
                                     FileSystemResourceService fileSystemResourceService) {
        super(ccdGatewayConfiguration, restTemplate, appInsights, headers, fileSystemResourceService);
    }

    @Value("${printservice.host}")
    private String printServiceHost;

    @Value("${printservice.legacyPath}")
    private String printServiceLegacyPath;

    public CaseMatch buildCaseMatch(Case c, CaseType caseType) {
        CaseMatch.CaseMatchBuilder caseMatchBuilder = CaseMatch.getCaseMatchBuilder(c, caseType);
        if (caseType.equals(LEGACY)) {
            caseMatchBuilder.legacyCaseViewUrl(buildLegacyCaseViewUrl(c, caseType));
        }

        return caseMatchBuilder.build();
    }

    private String buildLegacyCaseViewUrl(Case c, CaseType caseType) {
        String id = c.getData().getLegacyId();
        String legacyCaseTypeName = caseType.getName() + " " + c.getData().getLegacyCaseType();
        LegacyCaseType legacyCaseType = LegacyCaseType.getByLegacyCaseTypeName(legacyCaseTypeName);
        ProbateManType probateManType = ProbateManType.getByLegacyCaseType(legacyCaseType);

        String urlTemplate = printServiceHost + printServiceLegacyPath;
        String url = String.format(urlTemplate, probateManType.toString(), id);

        return url;
    }


}
