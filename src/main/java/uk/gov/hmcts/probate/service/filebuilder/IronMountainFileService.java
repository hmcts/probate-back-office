package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DataExtractGrantType;
import uk.gov.hmcts.probate.model.ccd.raw.Grantee;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
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
    private final TextFileBuilderService textFileBuilderService;
    private static final String DELIMITER = "|";
    private ImmutableList.Builder<String> fileData;

    public File createIronMountainFile(List<ReturnedCaseDetails> ccdCases, String fileName) {
        fileData = new ImmutableList.Builder<>();
        fileData.add("\n");
        for (ReturnedCaseDetails ccdCase : ccdCases) {
            prepareData(ccdCase.getId(), ccdCase.getData());
        }
        log.info("Creating IronMountain file.");
        return textFileBuilderService.createFile(fileData.build(), DELIMITER, fileName);
    }

    private void prepareData(Long id, CaseData data) {

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
        fileData.add(data.getApplicationType().equals(ApplicationType.PERSONAL) ? data.getPrimaryApplicantSurname() :
                data.getSolsSolicitorFirmName());
        addAddress(fileData, applicantAddress);
        fileData.add(data.getIhtGrossValue().toString().substring(0, data.getIhtGrossValue().toString().length() - 2));
        fileData.add(data.getIhtNetValue().toString().substring(0, data.getIhtNetValue().toString().length() - 2));
        fileData.add(DataExtractGrantType.valueOf(data.getCaseType()).getCaseTypeMapped());
        fileData.add(registryLocationCheck(data.getRegistryLocation()));
        fileData.add("\n");
    }

    protected void addGranteeDetails(ImmutableList.Builder<String> fileData, Grantee grantee) {
        fileData.add("");
        fileData.add(grantee.getFirstName());
        fileData.add(grantee.getLastName());
        addAddress(fileData, grantee.getAddress());
    }


}