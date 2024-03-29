package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.CaseType;
import uk.gov.hmcts.probate.model.ccd.CaseMatch;
import uk.gov.hmcts.probate.model.ccd.ProbateFullAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CaseLink;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.Case;
import uk.gov.hmcts.probate.model.ccd.raw.casematching.CaseData;
import uk.gov.hmcts.probate.model.probateman.LegacyCaseType;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;
import static uk.gov.hmcts.probate.model.CaseType.LEGACY;

@Service
@RequiredArgsConstructor
public class CaseMatchBuilderService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    @Value("${ccd.gateway.host}")
    private String printServiceHost;

    @Value("${printservice.legacyPath}")
    private String printServiceLegacyPath;

    CaseMatch buildCaseMatch(Case c) {
        CaseMatch.CaseMatchBuilder caseMatchBuilder = getCaseMatchBuilder(c);
        return caseMatchBuilder.build();
    }

    private CaseMatch.CaseMatchBuilder getCaseMatchBuilder(Case c) {
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

        if (isEmpty(c.getData().getLegacyId())) {
            CaseType.fromCode(c.getCaseTypeId())
                    .ifPresent(caseType -> caseMatchBuilder.type(caseType.getName()));
            caseMatchBuilder.caseLink(CaseLink.builder().caseReference(c.getId().toString()).build());
            if (c.getData().getSolsDeceasedAliasNamesList() != null) {
                String aliases = c.getData().getSolsDeceasedAliasNamesList().stream()
                        .map(CollectionMember::getValue)
                        .map(AliasName::getSolsAliasname)
                        .collect(Collectors.joining(", "));
                caseMatchBuilder.aliases(aliases);
            }
            if (c.getData().getDeceasedFullAliasNameList() != null) {
                String aliases = c.getData().getDeceasedFullAliasNameList().stream()
                        .map(CollectionMember::getValue)
                        .map(ProbateFullAliasName::getFullAliasName)
                        .collect(Collectors.joining(", "));
                caseMatchBuilder.aliases(aliases);
            }
        } else {
            LegacyCaseType legacyCaseType = getLegacyCaseType(c.getData());
            caseMatchBuilder.type(legacyCaseType.getName());
            caseMatchBuilder.id(c.getData().getLegacyId());
            caseMatchBuilder.recordId((c.getData().getRecordId()));
            caseMatchBuilder.legacyCaseViewUrl(buildLegacyCaseUrl(c.getData().getLegacyId(), legacyCaseType));
            caseMatchBuilder.aliases(c.getData().getLegacySearchAliasNames());
            if (c.getData().getCcdCaseId() != null) {
                caseMatchBuilder.caseLink(CaseLink.builder().caseReference(c.getData().getCcdCaseId()).build());
            }
        }

        return caseMatchBuilder;
    }

    public String buildLegacyCaseUrl(String id, LegacyCaseType legacyCaseType) {
        String urlTemplate = printServiceHost + printServiceLegacyPath;
        return String.format(urlTemplate, legacyCaseType.getProbateManType().toString(), id);
    }

    private LegacyCaseType getLegacyCaseType(CaseData caseData) {
        try {
            return LegacyCaseType.getByLegacyCaseTypeName(caseData.getLegacyType());
        } catch (IllegalArgumentException e) {
            return LegacyCaseType.getByLegacyCaseTypeName(LEGACY.getName() + " " + caseData.getLegacyCaseType());
        }
    }
}
