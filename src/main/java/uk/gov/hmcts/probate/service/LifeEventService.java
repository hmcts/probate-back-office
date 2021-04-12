package uk.gov.hmcts.probate.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.lifeevents.client.model.Deceased;
import uk.gov.hmcts.lifeevents.client.model.V1Death;
import uk.gov.hmcts.lifeevents.client.service.DeathService;
import uk.gov.hmcts.probate.model.ccd.CcdCaseType;
import uk.gov.hmcts.probate.model.ccd.EventId;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.security.SecurityUtils;
import uk.gov.hmcts.probate.service.ccd.CcdClientApi;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.DeathRecord;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantOfRepresentationData;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class LifeEventService {

    private DeathService deathService;
    private CcdClientApi ccdClientApi;
    private SecurityUtils securityUtils;

    @Autowired
    public LifeEventService(final DeathService deathService, final CcdClientApi ccdClientApi,
                            final SecurityUtils securityUtils) {
        this.deathService = deathService;
        this.ccdClientApi = ccdClientApi;
        this.securityUtils = securityUtils;
    }

    public void findDeathRecords(final CaseDetails caseDetails) {
        final CaseData caseData = caseDetails.getData();
        final String deceasedForenames = caseData.getDeceasedForenames();
        final String deceasedSurname = caseData.getDeceasedSurname();
        final LocalDate deceasedDateOfDeath = caseData.getDeceasedDateOfDeath();

        List<V1Death> records = deathService
                .searchForDeathRecordsByNamesAndDate(deceasedForenames, deceasedSurname, deceasedDateOfDeath);
        log.info("Records returned: " + records);
        final List<CollectionMember<DeathRecord>> collectionMembers = mapDeathRecords(records);
        GrantOfRepresentationData grantOfRepresentationData = GrantOfRepresentationData.builder()
                .deathRecords(collectionMembers)
                .build();

        final uk.gov.hmcts.reform.ccd.client.model.CaseDetails caseDetailsReturned =
                ccdClientApi.updateCaseAsCitizen(
                CcdCaseType.GRANT_OF_REPRESENTATION,
                caseDetails.getId().toString(),
                grantOfRepresentationData,
                EventId.DEATH_RECORD_VERIFIED,
                securityUtils.getSecurityDTO());

        log.info("caseDetailsReturned: {}", caseDetailsReturned.getData().toString());
    }

    private List<CollectionMember<DeathRecord>> mapDeathRecords(List<V1Death> deathRecords) {
        return deathRecords
                .stream()
                .map(this::mapDeathRecord)
                .collect(toList());
    }

    private CollectionMember<DeathRecord> mapDeathRecord(V1Death deathRecord) {
        final Deceased deceased = deathRecord.getDeceased();

        DeathRecord dr = DeathRecord
                .builder()
                .systemNumber(deathRecord.getId())
                .name(String.format("%s %s", deceased.getForenames(), deceased.getSurname()))
                .dateOfBirth(deceased.getDateOfBirth())
                .sex(deceased.getSex().getValue())
                .address(deceased.getAddress())
                .dateOfDeath(deceased.getDateOfDeath())
                .build();

        return new CollectionMember<>(null, dr);
    }
}
