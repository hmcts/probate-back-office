package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.ProbateAliasName;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmeeAndFordPersonalisationService {
    private static final DateTimeFormatter DATA_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter CONTENT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String PERSONALISATION_SMEE_AND_FORD_NAME = "smeeAndFordName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String COLDICILS_FLAG = "Codicils";
    private static final String SEP = ",";
    private static final String NEW_LINE = "\n";
    private static final DocumentType[] GRANT_TYPES = {DIGITAL_GRANT, ADMON_WILL_GRANT};

    public Map<String, String> getSmeeAndFordPersonalisation(List<ReturnedCaseDetails> cases) {
        HashMap<String, String> personalisation = new HashMap<>();

        StringBuilder data = getSmeeAndFordBuiltData(cases);

        personalisation.put(PERSONALISATION_SMEE_AND_FORD_NAME, LocalDateTime.now().format(DATA_DATE) + "sf");
        personalisation.put(PERSONALISATION_CASE_DATA, data.toString());

        return personalisation;
    }

    private StringBuilder getSmeeAndFordBuiltData(List<ReturnedCaseDetails> cases) {
        StringBuilder data = new StringBuilder();

        for (ReturnedCaseDetails retCase : cases) {
            data.append(retCase.getData().getRegistryLocation());
            data.append(SEP);
            data.append(CONTENT_DATE.format(LocalDate.parse(retCase.getData().getGrantIssuedDate())));
            data.append(SEP);
            data.append(retCase.getId().toString());
            data.append(SEP);
            data.append(getDeceasedNameWithHonours(retCase.getData()));
            data.append(SEP);
            data.append(getDeceasedAliasNames(retCase.getData()));
            data.append(SEP);
            data.append(getCaseType(retCase.getData()));
            data.append(SEP);
            data.append(CONTENT_DATE.format(retCase.getData().getDeceasedDateOfDeath()));
            data.append(SEP);
            data.append(getAddress(retCase.getData().getDeceasedAddress()));
            data.append(SEP);
            data.append(getApplyingExecutorDetails(retCase.getData().getAdditionalExecutorsApplying()));
            data.append(SEP);
            data.append(getPrimaryApplicantDetails(retCase.getData()));
            data.append(SEP);
            data.append(retCase.getData().getIhtGrossValue().toString());
            data.append(SEP);
            data.append(retCase.getData().getIhtNetValue().toString());
            data.append(SEP);
            data.append(getSolicitorDetails(retCase.getData()));
            data.append(SEP);
            data.append(CONTENT_DATE.format(retCase.getData().getDeceasedDateOfBirth()));
            data.append(SEP);
            data.append(hasCodicil(retCase.getData()));
            data.append(SEP);
            data.append(getWillFileName(retCase.getData()));
            data.append(SEP);
            data.append(getGrantFileName(retCase.getData()));

            data.append(NEW_LINE);
        }
        return data;
    }

    private String getCaseType(CaseData data) {
        return data.getCaseType();
    }

    private String getGrantFileName(CaseData data) {
        List<DocumentType> documentTypes = Arrays.asList(GRANT_TYPES);
        for (CollectionMember<Document> document : data.getProbateDocumentsGenerated()) {
            if (documentTypes.contains(document.getValue().getDocumentType())) {
                return document.getValue().getDocumentFileName();
            }
        }
        return "";
    }

    private String getWillFileName(CaseData data) {
        if (data.getScannedDocuments() != null) {
            for (CollectionMember<ScannedDocument> document : data.getScannedDocuments()) {
                if (DocumentType.OTHER.name().equalsIgnoreCase(document.getValue().getType()) 
                    && DOC_SUBTYPE_WILL.equals(document.getValue().getSubtype())) {
                    return document.getValue().getFileName();
                }
            }
        }
        return "";
    }

    private String hasCodicil(CaseData data) {
        if (YES.equals(data.getWillHasCodicils())) {
            return COLDICILS_FLAG;
        } else {
            return "";
        }
    }

    private String getSolicitorDetails(CaseData data) {
        String sol = "";
        if (SOLICITOR.equals(data.getApplicationType())) {
            sol = sol + ifNotEmpty(data.getSolsSolicitorFirmName()) + SEP;
            sol = sol + ifNotEmpty(getAddress(data.getSolsSolicitorAddress())) + SEP;
            sol = sol + ifNotEmpty(data.getSolsSolicitorAppReference());
        }
        
        return sol;
    }

    private String getPrimaryApplicantDetails(CaseData data) {
        String primary = "";
        primary = primary + ifNotEmpty(data.getPrimaryApplicantForenames());
        primary = primary + ifNotEmpty(data.getPrimaryApplicantSurname());
        primary = primary + ifNotEmpty(data.getPrimaryApplicantAlias()) + SEP;
        primary = primary + ifNotEmpty(getAddress(data.getPrimaryApplicantAddress()));
        
        return primary;
    }

    private String getApplyingExecutorDetails(List<CollectionMember<AdditionalExecutorApplying>> 
                                                  additionalExecutorsApplying) {
        String allExecs = "";
        if (additionalExecutorsApplying != null) {
            for (CollectionMember<AdditionalExecutorApplying> applying : additionalExecutorsApplying) {
                allExecs = allExecs + ifNotEmpty(applying.getValue().getApplyingExecutorName()) + SEP;
                allExecs = allExecs + ifNotEmpty(applying.getValue().getApplyingExecutorFirstName()) + SEP;
                allExecs = allExecs + ifNotEmpty(applying.getValue().getApplyingExecutorLastName()) + SEP;
                allExecs = allExecs + ifNotEmpty(applying.getValue().getApplyingExecutorOtherNames()) + SEP;
                allExecs = allExecs + ifNotEmpty(getAddress(applying.getValue().getApplyingExecutorAddress())) + SEP;
            }
        }
        
        return allExecs;
    }

    private String getAddress(SolsAddress address) {
        String add = "";
        if (address != null) {
            add = add + ifNotEmpty(address.getAddressLine1()) + SEP;
            add = add + ifNotEmpty(address.getAddressLine2()) + SEP;
            add = add + ifNotEmpty(address.getAddressLine3()) + SEP;
            add = add + ifNotEmpty(address.getCounty()) + SEP;
            add = add + ifNotEmpty(address.getPostTown()) + SEP;
            add = add + ifNotEmpty(address.getPostCode()) + SEP;
            add = add + ifNotEmpty(address.getCountry()) + SEP;
        }
        
        return add;
    }

    private String ifNotEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        } else {
            return value + " ";
        }
    }

    private String getDeceasedAliasNames(CaseData data) {
        String aliases = "";
        if (data.getDeceasedAliasNameList() != null)  {
            for (CollectionMember<ProbateAliasName> alias : data.getDeceasedAliasNameList()) {
                aliases = aliases + ifNotEmpty(alias.getValue().getForenames()) + " " 
                    + ifNotEmpty(alias.getValue().getLastName()) + SEP;
            }
        }
        return aliases;
    }

    private String getDeceasedNameWithHonours(CaseData data) {
        return ifNotEmpty(data.getDeceasedForenames()) + " " + ifNotEmpty(data.getDeceasedSurname()) 
            + " " + ifNotEmpty(data.getBoDeceasedHonours());
    }
}
