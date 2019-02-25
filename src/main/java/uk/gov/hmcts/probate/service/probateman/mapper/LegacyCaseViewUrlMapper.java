package uk.gov.hmcts.probate.service.probateman.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.probateman.Caveat;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;
import uk.gov.hmcts.probate.model.probateman.StandingSearch;
import uk.gov.hmcts.probate.model.probateman.WillLodgement;
import uk.gov.hmcts.probate.service.CaseMatchBuilderService;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToLegacyCaseViewUrl;

@Slf4j
@Component
public class LegacyCaseViewUrlMapper {

    @Autowired
    private CaseMatchBuilderService caseMatchBuilderService;

    @SuppressWarnings("squid:S1168")
    @ToLegacyCaseViewUrl
    public String toLegacyCaseViewUrl(GrantApplication grantApplication) {
        log.info("Adding GrantApplication case view url imported case");
        return caseMatchBuilderService.buildLegacyCaseUrl(grantApplication.getId().toString(),
                LegacyCaseType.GRANT_OF_REPRESENTATION.getName());
    }

    @ToLegacyCaseViewUrl
    public String toLegacyCaseViewUrl(StandingSearch standingSearch) {
        log.info("Adding StandingSearch case view url imported case");
        return caseMatchBuilderService.buildLegacyCaseUrl(standingSearch.getId().toString(),
                LegacyCaseType.STANDING_SEARCH.getName());
    }

    @ToLegacyCaseViewUrl
    public String toLegacyCaseViewUrl(WillLodgement willLodgement) {
        log.info("Adding WillLodgement case view url imported case");
        return caseMatchBuilderService.buildLegacyCaseUrl(willLodgement.getId().toString(),
                LegacyCaseType.WILL_LODGEMENT.getName());
    }

    @ToLegacyCaseViewUrl
    public String toLegacyCaseViewUrl(Caveat caveat) {
        log.info("Adding Caveat case view url imported case");
        return caseMatchBuilderService.buildLegacyCaseUrl(caveat.getId().toString(),
                LegacyCaseType.CAVEAT.getName());
    }

}
