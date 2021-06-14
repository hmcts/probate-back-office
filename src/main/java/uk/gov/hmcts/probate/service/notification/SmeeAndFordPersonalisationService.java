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
import uk.gov.hmcts.probate.service.FileSystemResourceService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static uk.gov.hmcts.probate.model.ApplicationType.SOLICITOR;
import static uk.gov.hmcts.probate.model.Constants.DOC_SUBTYPE_WILL;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.DocumentType.ADMON_WILL_GRANT;
import static uk.gov.hmcts.probate.model.DocumentType.DIGITAL_GRANT;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmeeAndFordPersonalisationService {
    private static final DateTimeFormatter CONTENT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String PERSONALISATION_SMEE_AND_FORD_NAME = "smeeAndFordName";
    private static final String PERSONALISATION_CASE_DATA = "caseData";
    private static final String COLDICILS_FLAG = "Codicils";
    private static final String DELIMITER = "|";
    private static final String NEW_LINE = "\n";
    private static final String SPACE = " ";
    private static final String PDF_EXT = ".pdf";
    private static final DocumentType[] GRANT_TYPES = {DIGITAL_GRANT, ADMON_WILL_GRANT};
    private static final String SUBJECT = "Smee And Ford Data extract from :fromDate to :toDate";
    private static final String HEADER_ROW_FILE = "templates/dataExtracts/SmeeAndFordHeaderRow.csv";

    private final FileSystemResourceService fileSystemResourceService;

    public Map<String, String> getSmeeAndFordPersonalisation(List<ReturnedCaseDetails> cases, String fromDate,
                                                             String toDate) {
        HashMap<String, String> personalisation = new HashMap<>();

        StringBuilder data = getSmeeAndFordBuiltData(cases);

        personalisation.put(PERSONALISATION_SMEE_AND_FORD_NAME, getSubject(fromDate, toDate));
        personalisation.put(PERSONALISATION_CASE_DATA, removeLastNewLine(data.toString()));

        return personalisation;
    }

    private String getSubject(String fromDate, String toDate) {
        return SUBJECT.replace(":fromDate", fromDate)
            .replace(":toDate", toDate);
    }

    private StringBuilder getSmeeAndFordBuiltData(List<ReturnedCaseDetails> cases) {
        StringBuilder data = new StringBuilder();
        addHeaderRow(data);
        data.append(NEW_LINE);

        for (ReturnedCaseDetails retCase : cases) {
            CaseData currentCaseData = retCase.getData();
            try {
                data.append(currentCaseData.getRegistryLocation());
                data.append(DELIMITER);
                data.append(CONTENT_DATE.format(LocalDate.parse(currentCaseData.getGrantIssuedDate())));
                data.append(DELIMITER);
                data.append(retCase.getId().toString());
                data.append(DELIMITER);
                data.append(replaceDelimeters(getDeceasedNameWithHonours(currentCaseData)));
                data.append(DELIMITER);
                data.append(replaceDelimeters(getDeceasedAliasNames(currentCaseData)));
                data.append(DELIMITER);
                data.append(getCaseType(currentCaseData));
                data.append(DELIMITER);
                data.append(CONTENT_DATE.format(currentCaseData.getDeceasedDateOfDeath()));
                data.append(DELIMITER);
                data.append(getFullAddress(currentCaseData.getDeceasedAddress()));
                data.append(getApplyingExecutorsDetails(currentCaseData.getAdditionalExecutorsApplying()));
                data.append(getPrimaryApplicantName(currentCaseData));
                data.append(DELIMITER);
                data.append(getFullAddress(currentCaseData.getPrimaryApplicantAddress()));
                data.append(currentCaseData.getIhtGrossValue().toString());
                data.append(DELIMITER);
                data.append(currentCaseData.getIhtNetValue().toString());
                data.append(DELIMITER);
                data.append(getSolicitorDetails(currentCaseData));
                data.append(CONTENT_DATE.format(currentCaseData.getDeceasedDateOfBirth()));
                data.append(DELIMITER);
                data.append(hasCodicil(currentCaseData));
                data.append(DELIMITER);
                data.append(getWillFileName(currentCaseData));
                data.append(DELIMITER);
                data.append(getGrantFileName(currentCaseData));
                data.append(NEW_LINE);
            } catch (Exception e) {
                data.append(retCase.getId().toString());
                data.append(DELIMITER);
                data.append(e.toString());
                data.append(NEW_LINE);
            }
        }
        return data;
    }

    private String replaceDelimeters(String value) {
        return value.replaceAll(Pattern.quote(DELIMITER), SPACE);
    }

    private void addHeaderRow(StringBuilder data) {
        String header = fileSystemResourceService.getFileFromResourceAsString(HEADER_ROW_FILE);
        data.append(header);
    }

    private String getCaseType(CaseData data) {
        return data.getCaseType();
    }

    private String getGrantFileName(CaseData data) {
        List<DocumentType> documentTypes = Arrays.asList(GRANT_TYPES);
        for (CollectionMember<Document> document : data.getProbateDocumentsGenerated()) {
            if (documentTypes.contains(document.getValue().getDocumentType())) {
                return document.getValue().getDocumentType().getTemplateName() + PDF_EXT;
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
            sol = sol + ifNotEmpty(replaceDelimeters(data.getSolsSolicitorFirmName()));
            sol = sol + DELIMITER;
            sol = sol + ifNotEmpty(replaceDelimeters(data.getSolsSolicitorAppReference()));
            sol = sol + DELIMITER;
            sol = sol + getFullAddress(data.getSolsSolicitorAddress());
        } else {
            sol = sol + DELIMITER;
            sol = sol + DELIMITER;
            sol = sol + DELIMITER;
            sol = sol + DELIMITER;
            sol = sol + DELIMITER;
            sol = sol + DELIMITER;
            sol = sol + DELIMITER;
        }
        
        return sol;
    }

    private String getPrimaryApplicantName(CaseData data) {
        String primary = "";
        primary = primary + ifNotEmptyWithSpace(data.getPrimaryApplicantForenames());
        primary = primary + ifNotEmptyWithSpace(data.getPrimaryApplicantSurname());
        primary = primary + ifNotEmpty(data.getPrimaryApplicantAlias());
        
        return replaceDelimeters(primary);
    }

    private String getApplyingExecutorsDetails(List<CollectionMember<AdditionalExecutorApplying>> 
                                                  additionalExecutorsApplying) {
        int execCount = 0;
        StringBuilder allExecs = new StringBuilder();
        if (additionalExecutorsApplying != null) {
            for (CollectionMember<AdditionalExecutorApplying> applying : additionalExecutorsApplying) {
                allExecs.append(getApplyingExecutorDetails(applying));
                execCount++;
                if (execCount == 3) {
                    break;
                }
            }

        }
        while (execCount < 3) {
            for (var i = 0; i < 6; i++) {
                allExecs.append(DELIMITER);
            }
            execCount++;
        }

        return allExecs.toString();
    }

    private String getApplyingExecutorDetails(CollectionMember<AdditionalExecutorApplying> applying) {
        var execNames = new StringBuilder();
        execNames.append(ifNotEmptyWithSpace(applying.getValue().getApplyingExecutorName()));
        execNames.append(ifNotEmptyWithSpace(applying.getValue().getApplyingExecutorFirstName()));
        execNames.append(ifNotEmptyWithSpace(applying.getValue().getApplyingExecutorLastName()));
        execNames.append(ifNotEmpty(applying.getValue().getApplyingExecutorOtherNames()));
        
        var allExecs = new StringBuilder();
        allExecs.append(replaceDelimeters(execNames.toString()));
        allExecs.append(DELIMITER);
        
        allExecs.append(ifNotEmpty(getFullAddress(applying.getValue().getApplyingExecutorAddress())));
        return allExecs.toString();
    }

    private String getFullAddress(SolsAddress address) {
        var addBuilder = new StringBuilder();
        if (address != null) {
            addBuilder.append(getAddress(address));
            addBuilder.append(DELIMITER);
            addBuilder.append(getPostTown(address));
            addBuilder.append(DELIMITER);
            addBuilder.append(getCounty(address));
            addBuilder.append(DELIMITER);
            addBuilder.append(getPostCode(address));
            addBuilder.append(DELIMITER);
            addBuilder.append(getCountry(address));
            addBuilder.append(DELIMITER);
        }
        return addBuilder.toString();
    }
    
    private String getAddress(SolsAddress address) {
        var addBuilder = new StringBuilder();
        addBuilder.append(ifNotEmptyWithSpace(address.getAddressLine1()));
        addBuilder.append(ifNotEmptyWithSpace(address.getAddressLine2()));
        addBuilder.append(ifNotEmpty(address.getAddressLine3()));
        
        return replaceDelimeters(addBuilder.toString());
    }

    private String getCounty(SolsAddress address) {
        var addBuilder = new StringBuilder();
        addBuilder.append(ifNotEmpty(address.getCounty()));

        return addBuilder.toString();
    }

    private String getPostTown(SolsAddress address) {
        var addBuilder = new StringBuilder();
        addBuilder.append(ifNotEmpty(address.getPostTown()));

        return replaceDelimeters(addBuilder.toString());
    }

    private String getPostCode(SolsAddress address) {
        var addBuilder = new StringBuilder();
        addBuilder.append(ifNotEmpty(address.getPostCode()));

        return replaceDelimeters(addBuilder.toString());
    }

    private String getCountry(SolsAddress address) {
        StringBuilder addBuilder = new StringBuilder();
        if (address != null) {
            addBuilder.append(ifNotEmpty(address.getCountry()));
        }

        return replaceDelimeters(addBuilder.toString());
    }

    private String ifNotEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        } else {
            return value;
        }
    }

    private String ifNotEmptyWithSpace(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        } else {
            return value + SPACE;
        }
    }

    private String getDeceasedAliasNames(CaseData data) {
        StringBuilder aliases = new StringBuilder();
        if (data.getDeceasedAliasNameList() != null)  {
            for (CollectionMember<ProbateAliasName> alias : data.getDeceasedAliasNameList()) {
                aliases.append(ifNotEmptyWithSpace(
                    ifNotEmptyWithSpace(alias.getValue().getForenames())
                    + ifNotEmpty(alias.getValue().getLastName())));
            }
        }
        return removeAnyLastSpace(aliases.toString());
    }

    private String getDeceasedNameWithHonours(CaseData data) {
        return ifNotEmpty(data.getDeceasedForenames()) + SPACE + ifNotEmptyWithSpace(data.getDeceasedSurname()) 
            + ifNotEmpty(data.getBoDeceasedHonours());
    }
    
    private String removeAnyLastSpace(String data) {
        if (data.indexOf(SPACE) <= 0) {
            return data;
        }
        return data.substring(0, data.lastIndexOf(SPACE));
    }

    private String removeLastNewLine(String data) {
        return data.substring(0, data.lastIndexOf(NEW_LINE));
    }

}
