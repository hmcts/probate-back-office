package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@Service
@RequiredArgsConstructor
public class CaseMatchBuilderService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    @Value("${ccd.gateway.host}")
    private String printServiceHost;

    @Value("${printservice.legacyPath}")
    private String printServiceLegacyPath;

    public CaseMatch buildCaseMatch(Case c, CaseType caseType) {
        CaseMatch.CaseMatchBuilder caseMatchBuilder = getCaseMatchBuilder(c, caseType);
        return caseMatchBuilder.build();
    }

    private CaseMatch.CaseMatchBuilder getCaseMatchBuilder(Case c, CaseType caseType) {
        CaseMatch.CaseMatchBuilder caseMatchBuilder = CaseMatch.builder();
        caseMatchBuilder.fullName(c.getData().getDeceasedFullName());
        if (c.getData().getDeceasedDateOfBirth() != null) {
            caseMatchBuilder.dob(c.getData().getDeceasedDateOfBirth().format(dateTimeFormatter));
        }
        if (c.getData().getDeceasedDateOfDeath() != null) {
            caseMatchBuilder.dod(c.getData().getDeceasedDateOfDeath().format(dateTimeFormatter));
        }
        if (c.getData().getDeceasedAddress() != null) {
            caseMatchBuilder.postcode(c.getData().getDeceasedAddress().getPostCode());
        }
        if (caseType.equals(LEGACY)) {
            caseMatchBuilder.type(caseType.getName() + " " + c.getData().getLegacyCaseType());
            caseMatchBuilder.id(c.getData().getLegacyId());
            caseMatchBuilder.legacyCaseViewUrl(buildLegacyCaseViewUrl(c, caseType));
            caseMatchBuilder.aliases(c.getData().getLegacySearchAliasNames());
            if (c.getData().getCcdCaseId() != null) {
                caseMatchBuilder.caseLink(CaseLink.builder().caseReference(c.getData().getCcdCaseId()).build());
            }
        } else {
            caseMatchBuilder.caseLink(CaseLink.builder().caseReference(c.getId().toString()).build());
            if (c.getData().getSolsDeceasedAliasNamesList() != null) {
                String aliases = c.getData().getSolsDeceasedAliasNamesList().stream()
                        .map(CollectionMember::getValue)
                        .map(AliasName::getSolsAliasname)
                        .collect(Collectors.joining(", "));
                caseMatchBuilder.aliases(aliases);
            }
        }

        return caseMatchBuilder;
    }

    private String buildLegacyCaseViewUrl(Case c, CaseType caseType) {
        String id = c.getData().getLegacyId();
        String legacyCaseTypeName = caseType.getName() + " " + c.getData().getLegacyCaseType();
        LegacyCaseType legacyCaseType = LegacyCaseType.getByLegacyCaseTypeName(legacyCaseTypeName);

        String urlTemplate = printServiceHost + printServiceLegacyPath;
        return String.format(urlTemplate, legacyCaseType.getProbateManType().toString(), id);
    }
}
