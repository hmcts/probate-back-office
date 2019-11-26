package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.exception.BadRequestException;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DataExtractGrantType;
import uk.gov.hmcts.probate.model.ccd.raw.AliasName;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Grantee;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Service
@RequiredArgsConstructor
public class HmrcFileService {
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
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public File createHmrcFile(List<ReturnedCaseDetails> ccdCases, String fileName) {
        ImmutableList.Builder<String> fileData = new ImmutableList.Builder<>();
        fileData.add(ROW_HEADER + ROW_DELIMITER);
        int rowCount = 0;
        for (ReturnedCaseDetails ccdCase : ccdCases) {
            rowCount = rowCount + prepareData(fileData, ccdCase.getId(), ccdCase.getData());
        }
        addFooter(fileData, rowCount);
        log.info("Creating HMRC file.");
        return textFileBuilderService.createFile(fileData.build(), DELIMITER, fileName);
    }

    private int prepareData(ImmutableList.Builder<String> fileData, Long id, CaseData data) {
        int rowCount = 0;
        fileData.add(ROW_TYPE_GRANT_DETAILS);
        fileData.add(id.toString());
        fileData.add(data.getRegistryLocation());
        fileData.add(Optional.ofNullable(data.getBoDeceasedTitle()).orElse(""));
        fileData.add(data.getDeceasedForenames());
        fileData.add(data.getDeceasedSurname());
        fileData.add(StringUtils.isEmpty(data.getBoDeceasedHonours()) ? "" : data.getBoDeceasedHonours());
        fileData.add("");
        fileData.add(fileExtractDateFormatter.formatDataDate(data.getDeceasedDateOfDeath()));
        fileData.add("");
        fileData.add(fileExtractDateFormatter.formatDataDate(data.getDeceasedDateOfBirth()));
        fileData.add(String.valueOf(ageCalculator(data)));
        fileData.add(DOMICILE);
        addAddress(fileData, addressManager(data.getDeceasedAddress()));
        fileData.add(fileExtractDateFormatter.formatDataDate(LocalDate.parse(data.getGrantIssuedDate())));
        addGranteeDetails(fileData, createGrantee(data, 1));
        addGranteeDetails(fileData, createGrantee(data, 2));
        addGranteeDetails(fileData, createGrantee(data, 3));
        addGranteeDetails(fileData, createGrantee(data, 4));
        addSolicitorDetails(fileData, data);
        fileData.add(data.getIhtGrossValue().toString().substring(0, data.getIhtGrossValue().toString().length() - 2));
        addExpectedEstateIndicator(fileData, data);
        fileData.add(data.getIhtNetValue().toString().substring(0, data.getIhtNetValue().toString().length() - 2));
        fileData.add(DataExtractGrantType.valueOf(data.getCaseType()).getCaseTypeMapped());
        fileData.add(FINAL_GRANT);
        fileData.add(ROW_DELIMITER);
        rowCount++;
        for (CollectionMember<AliasName> member : data.getSolsDeceasedAliasNamesList()) {
            rowCount = rowCount + addAliasRow(fileData, id.toString(), member.getValue());
        }
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
            throw new BadRequestException("Unsupported IHT Form Type for "+data.getIhtFormId());
        }
        
        fileData.add(type);
    }

    private void addAddress(ImmutableList.Builder<String> fileData, List<String> address) {
        fileData.add(address.get(0));
        fileData.add(address.get(1));
        fileData.add(address.get(2));
        fileData.add(address.get(3));
        fileData.add(address.get(6));
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

    private int ageCalculator(CaseData data) {
        return Period.between(data.getDeceasedDateOfBirth(), data.getDeceasedDateOfDeath()).getYears();
    }

    private List<String> addressManager(SolsAddress address) {
        if (address == null) {
            address = getEmptyAddress();
        }
        String[] addressArray = {(Optional.ofNullable(address.getAddressLine1()).orElse("")).replace("\n", " "),
            Optional.ofNullable(address.getAddressLine2()).orElse(""),
            Optional.ofNullable(address.getAddressLine3()).orElse(""),
            Optional.ofNullable(address.getPostTown()).orElse(""),
            Optional.ofNullable(address.getCounty()).orElse(""),
            Optional.ofNullable(address.getCountry()).orElse("")};
        Arrays.sort(addressArray, Comparator.comparingInt(value -> value == null || value.isEmpty() ? 1 : 0));
        List<String> formattedAddress = new ArrayList<>(7);
        formattedAddress.addAll(Arrays.asList(addressArray));
        formattedAddress.add(Optional.ofNullable(address.getPostCode()).orElse(""));
        return formattedAddress;
    }

    private Grantee createGrantee(CaseData data, int i) {
        return Grantee.builder()
            .fullName(getName(data, i))
            .address(addressManager(getAddress(data, i)))
            .build();
    }

    private String getName(CaseData caseData, int granteeNumber) {
        if (isYes(caseData.getPrimaryApplicantIsApplying())) {
            return granteeNumber == 1 ? caseData.getPrimaryApplicantForenames() + " " + caseData
                .getPrimaryApplicantSurname() : getApplyingExecutorName(caseData, granteeNumber - 2);
        }
        if (granteeNumber == 1 && caseData.getAdditionalExecutorsApplying() == null && caseData.getApplicationType()
            .equals(ApplicationType.SOLICITOR)) {
            return caseData.getSolsSOTName();
        }
        return getApplyingExecutorName(caseData, granteeNumber - 1);
    }

    private SolsAddress getAddress(CaseData caseData, int granteeNumber) {
        if (isYes(caseData.getPrimaryApplicantIsApplying())) {
            return granteeNumber == 1 ? caseData.getPrimaryApplicantAddress() : getAdditionalExecutorAddress(caseData,
                granteeNumber - 2);
        }
        if (granteeNumber == 1 && caseData.getAdditionalExecutorsApplying() == null && caseData.getApplicationType()
            .equals(ApplicationType.SOLICITOR)) {
            return caseData.getSolsSolicitorAddress();
        }
        return getAdditionalExecutorAddress(caseData, granteeNumber - 1);
    }

    private SolsAddress getAdditionalExecutorAddress(CaseData caseData, int index) {
        if (caseData.getAdditionalExecutorsApplying() != null
            && caseData.getAdditionalExecutorsApplying().size() >= (index + 1)) {
            return caseData.getAdditionalExecutorsApplying().get(index).getValue().getApplyingExecutorAddress();
        }
        return getEmptyAddress();
    }

    private String getApplyingExecutorName(CaseData caseData, int index) {
        if (caseData.getAdditionalExecutorsApplying() != null
            && caseData.getAdditionalExecutorsApplying().size() >= (index + 1)) {
            return caseData.getAdditionalExecutorsApplying().get(index).getValue().getApplyingExecutorName();
        }
        return "";
    }

    private Boolean isYes(String yesNoValue) {
        return yesNoValue.equals(YES);
    }

    private SolsAddress getEmptyAddress() {
        return SolsAddress.builder()
            .addressLine1("")
            .addressLine2("")
            .addressLine3("")
            .postCode("")
            .country("")
            .county("")
            .postTown("")
            .build();
    }

}