package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.exception.ClientException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DataExtractGrantType;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Grantee;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class HmrcFileService extends BaseFileService {
    private final TextFileBuilderService textFileBuilderService;
    private final FileExtractDateFormatter fileExtractDateFormatter;

    private static final String DELIMITER = "~";
    private static final String ROW_DELIMITER = "\n";
    private static final String ROW_HEADER = "ENGLAND";
    private static final String DOMICILE = "England and Wales";
    private static final String ROW_TYPE_GRANT_DETAILS = "T";
    private static final String ROW_TYPE_ALIAS_DETAILS = "A";
    private static final String ROW_TYPE_FOOTER = "Z";
    private static final String NUMBER_OF_FILE = "1";
    private static final String LAST_FILE = "Y";
    private static final String FINAL_GRANT = "Y";
    private static final Map<String, String> iht2EstateMap = Stream.of(new String[][]{
        {"IHT400421", "N"},
        {"IHT205", "Y"},
        {"IHT207", "E"},
        {"NA", "X"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public File createHmrcFile(List<ReturnedCaseDetails> ccdCases, String fileName) {
        ImmutableList.Builder<String> fileData = prepareFileData(ccdCases, fileName);
        return textFileBuilderService.createFile(fileData.build(), DELIMITER, fileName);
    }

    private ImmutableList.Builder<String> prepareFileData(List<ReturnedCaseDetails> ccdCases, String fileName) {
        ImmutableList.Builder<String> fileData = new ImmutableList.Builder<>();
        try {
            fileData.add(ROW_HEADER + ROW_DELIMITER);
            int rowCount = 0;
            for (ReturnedCaseDetails ccdCase : ccdCases) {
                rowCount = rowCount + prepareData(fileData, ccdCase.getId(), ccdCase.getData());
            }
            addFooter(fileData, rowCount);
        } catch (Exception e) {
            log.error("Failed to prepare data HMRC file for :" + fileName);
            throw new ClientException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Failed to prepare data HMRC file for " + fileName + " exception:" + e.getStackTrace());
        }
        return fileData;
    }

    private int prepareData(ImmutableList.Builder<String> fileData, Long id, CaseData data) {
        log.info("Perparing row data for HMRC, caseId={}", id);
        fileData.add(ROW_TYPE_GRANT_DETAILS);
        log.info("LOG id");
        fileData.add(id.toString());
        log.info("LOG getRegistryLocation");
        fileData.add(data.getRegistryLocation());
        log.info("LOG getBoDeceasedTitle");
        fileData.add(Optional.ofNullable(data.getBoDeceasedTitle()).orElse(""));
        log.info("LOG getDeceasedForenames");
        fileData.add(data.getDeceasedForenames());
        log.info("LOG getDeceasedSurname");
        fileData.add(data.getDeceasedSurname());
        log.info("LOG getBoDeceasedHonours");
        fileData.add(StringUtils.isEmpty(data.getBoDeceasedHonours()) ? "" : data.getBoDeceasedHonours());
        fileData.add("");
        log.info("LOG getDeceasedDateOfDeath");
        fileData.add(fileExtractDateFormatter.formatDataDate(data.getDeceasedDateOfDeath()));
        fileData.add("");
        log.info("LOG getDeceasedDateOfBirth");
        fileData.add(fileExtractDateFormatter.formatDataDate(data.getDeceasedDateOfBirth()));
        log.info("LOG ageCalculator");
        fileData.add(String.valueOf(ageCalculator(data)));
        fileData.add(DOMICILE);
        log.info("LOG getDeceasedAddress");
        addAddress(fileData, addressManager(data.getDeceasedAddress()));
        log.info("LOG getGrantIssuedDate");
        fileData.add(fileExtractDateFormatter.formatDataDate(LocalDate.parse(data.getGrantIssuedDate())));
        addGranteeDetails(fileData, createGrantee(data, 1));
        log.info("LOG createGrantee2");
        addGranteeDetails(fileData, createGrantee(data, 2));
        log.info("LOG createGrantee3");
        addGranteeDetails(fileData, createGrantee(data, 3));
        log.info("LOG createGrantee4");
        addGranteeDetails(fileData, createGrantee(data, 4));
        log.info("LOG addSolicitorDetails");
        addSolicitorDetails(fileData, data);
        log.info("LOG getIhtGrossValue");
        fileData.add(getPoundValue(data.getIhtGrossValue()));
        log.info("LOG addExpectedEstateIndicator");
        addExpectedEstateIndicator(fileData, data);
        log.info("LOG 1");
        fileData.add(getPoundValue(data.getIhtNetValue()));
        log.info("LOG getIhtNetValue");
        fileData.add(DataExtractGrantType.valueOf(data.getCaseType()).getCaseTypeMapped());
        fileData.add(FINAL_GRANT);
        fileData.add(ROW_DELIMITER);
        int rowCount = 1;
        if (data.getSolsDeceasedAliasNamesList() != null) {
            for (CollectionMember<AliasName> member : data.getSolsDeceasedAliasNamesList()) {
                rowCount = rowCount + addAliasRow(fileData, id.toString(), member.getValue());
                log.info("LOG AliasName");
            }
        }
        log.info("LOG rowCount {}", rowCount);
        return rowCount;
    }

    private void addFooter(ImmutableList.Builder<String> fileData, int rowCount) {
        fileData.add(ROW_TYPE_FOOTER);

        fileData.add("1_" + fileExtractDateFormatter.formatFileDate() + ".dat");
        fileData.add(String.valueOf(rowCount));
        fileData.add(NUMBER_OF_FILE);
        fileData.add(LAST_FILE);
    }

    private int addAliasRow(ImmutableList.Builder<String> fileData, String caseId, AliasName aliasName) {
        fileData.add(ROW_TYPE_ALIAS_DETAILS);
        fileData.add(caseId);
        fileData.add("");
        String fullName = aliasName.getSolsAliasname();
        String forenames = fullName.lastIndexOf(" ") > 0 ? fullName.substring(0, fullName.lastIndexOf(" ")) : fullName;
        String surname = fullName.lastIndexOf(" ") > 0 ? fullName.substring(fullName.lastIndexOf(" ") + 1) : "";
        fileData.add(forenames);
        fileData.add(surname);
        fileData.add(ROW_DELIMITER);
        return 1;
    }

    private void addExpectedEstateIndicator(ImmutableList.Builder<String> fileData, CaseData data) {
        String type = iht2EstateMap.get(data.getIhtFormId());

        if (type == null) {
            throw new BadRequestException("Unsupported IHT Form Type for " + data.getIhtFormId());
        } 

        fileData.add(type);
    }

    private void addGranteeDetails(ImmutableList.Builder<String> fileData, Grantee grantee) {
        fileData.add("");
        fileData.add(grantee.getFirstName());
        fileData.add(grantee.getLastName());
        fileData.add("");
        addAddress(fileData, grantee.getAddress());
    }

    private void addSolicitorDetails(ImmutableList.Builder<String> fileData, CaseData data) {
        if (data.getApplicationType().equals(ApplicationType.SOLICITOR)) {
            fileData.add(data.getSolsSolicitorFirmName());
            List<String> solicitorAddress = addressManager(data.getSolsSolicitorAddress());
            addAddress(fileData, solicitorAddress);
        } else {
            fileData.add("");
            fileData.add("");
            fileData.add("");
            fileData.add("");
            fileData.add("");
            fileData.add("");
        }
    }
}