package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DataExtractGrantType;
import uk.gov.hmcts.probate.model.ccd.raw.Grantee;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IronMountainFileService extends BaseFileService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final String DELIMITER = "|";
    private final TextFileBuilderService textFileBuilderService;
    private ImmutableList.Builder<String> fileData;

    public File createIronMountainFile(List<ReturnedCaseDetails> ccdCases, String fileName) {
        log.info("Creating IronMountain file=" + fileName);
        fileData = new ImmutableList.Builder<>();
        fileData.add("\n");
        for (ReturnedCaseDetails ccdCase : ccdCases) {
            prepareData(ccdCase.getId(), ccdCase.getData());
        }
        log.info("Created IronMountain file=" + fileName);
        return textFileBuilderService.createFile(fileData.build(), DELIMITER, fileName);
    }

    private void prepareData(Long id, CaseData data) {
        try {
            log.info("Preparing row data for Iron Mountain, caseId={}", id);
            final List<String> deceasedAddress = addressManager(data.getDeceasedAddress());
            final List<String> applicantAddress = addressManager(data.getApplicationType().equals(ApplicationType
                .PERSONAL) ? data.getPrimaryApplicantAddress() : data.getSolsSolicitorAddress());

            fileData.add(Optional.ofNullable(data.getBoDeceasedTitle()).orElse(""));
            fileData.add(data.getDeceasedForenames());
            fileData.add(data.getDeceasedSurname());
            fileData.add(DATE_FORMAT.format(data.getDeceasedDateOfDeath()));
            fileData.add("");
            fileData.add(DATE_FORMAT.format(data.getDeceasedDateOfBirth()));
            fileData.add(String.valueOf(ageCalculator(data)));
            addAddress(fileData, deceasedAddress);
            fileData.add(id.toString());
            fileData.add(DATE_FORMAT.format(LocalDate.parse(data.getGrantIssuedDate())));
            addGranteeDetails(fileData, createGrantee(data, 1));
            addGranteeDetails(fileData, createGrantee(data, 2));
            addGranteeDetails(fileData, createGrantee(data, 3));
            addGranteeDetails(fileData, createGrantee(data, 4));
            fileData.add(data.getApplicationType().name());
            fileData
                .add(data.getApplicationType().equals(ApplicationType.PERSONAL) ? data.getPrimaryApplicantSurname() :
                    data.getSolsSolicitorFirmName());
            addAddress(fileData, applicantAddress);
            fileData.add(getPoundValue(data.getIhtGrossValue()));
            fileData.add(getPoundValue(data.getIhtNetValue()));
            fileData.add(DataExtractGrantType.valueOf(data.getCaseType()).getCaseTypeMapped());
            fileData.add(registryLocationCheck(data.getRegistryLocation()));
            fileData.add("\n");
        } catch (Exception e) {
            log.info("Exception preparing row data for Iron Mountain, caseId={}, exception={}", id, e.getMessage());
            fileData.add("Exception proparing IM row data: " + e.getMessage());
            fileData.add("\n");
        }
    }

    protected void addGranteeDetails(ImmutableList.Builder<String> fileData, Grantee grantee) {
        fileData.add("");
        fileData.add(grantee.getFirstName());
        fileData.add(grantee.getLastName());
        addAddress(fileData, grantee.getAddress());
    }


}