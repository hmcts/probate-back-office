package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.Grantee;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.io.File;
import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IronMountainFileService {
    private static final SolsAddress EMPTY_ADDRESS = SolsAddress.builder()
            .addressLine1("")
            .addressLine2("")
            .addressLine3("")
            .postCode("")
            .country("")
            .county("")
            .postTown("")
            .build();
    private final TextFileBuilderService textFileBuilderService;
    private static final String DELIMITER = "|";

    public File createIronMountainFile(CaseDetails ccdCase, String fileName) throws IOException {
        return textFileBuilderService.createFile(prepareData(ccdCase.getId(), ccdCase.getData()), DELIMITER, fileName);
    }

    private List<String> prepareData(Long id, CaseData data) {
        ImmutableList.Builder<String> fileData = ImmutableList.builder();
        final List<String> deceasedAddress = addressManager(data.getDeceasedAddress());
        final List<String> applicantAddress = addressManager(data.getApplicationType().equals(ApplicationType
                .PERSONAL) ? data.getPrimaryApplicantAddress() : data.getSolsSolicitorAddress());

        fileData.add(Optional.ofNullable(data.getBoDeceasedTitle()).orElse(""));
        fileData.add(data.getDeceasedForenames());
        fileData.add(data.getDeceasedSurname());
        fileData.add(data.getDeceasedDateOfDeath().toString());
        fileData.add("");
        fileData.add(data.getDeceasedDateOfBirth().toString());
        fileData.add(String.valueOf(ageCalculator(data)));
        addDeceasedAddress(fileData, deceasedAddress);
        fileData.add(id.toString());
        fileData.add(getGrantIssueDate(data));
        addGranteeDetails(fileData, createGrantee(data, 1));
        addGranteeDetails(fileData, createGrantee(data, 2));
        addGranteeDetails(fileData, createGrantee(data, 3));
        addGranteeDetails(fileData, createGrantee(data, 4));
        fileData.add(data.getApplicationType().name());
        fileData.add(data.getApplicationType().equals(ApplicationType.PERSONAL) ? data.getPrimaryApplicantSurname() :
                data.getSolsSolicitorFirmName());
        addDeceasedAddress(fileData, applicantAddress);
        fileData.add(data.getIhtGrossValue().toString());
        fileData.add(data.getIhtNetValue().toString());
        fileData.add(CaseTypeMapping.valueOf(data.getCaseType()).getCaseTypeMapped());
        fileData.add(data.getRegistryLocation());
        return fileData.build();
    }

    private void addDeceasedAddress(ImmutableList.Builder<String> fileData, List<String> deceasedAddress) {
        fileData.add(deceasedAddress.get(0));
        fileData.add(deceasedAddress.get(1));
        fileData.add(deceasedAddress.get(2));
        fileData.add(deceasedAddress.get(3));
        fileData.add(deceasedAddress.get(6));
    }

    private void addGranteeDetails(ImmutableList.Builder<String> fileData, Grantee grantee) {
        fileData.add("");
        fileData.add(grantee.getFirstName());
        fileData.add(grantee.getLastName());
        addDeceasedAddress(fileData, grantee.getAddress());
    }

    private int ageCalculator(CaseData data) {
        return Period.between(data.getDeceasedDateOfBirth(), data.getDeceasedDateOfDeath()).getYears();
    }

    private List<String> addressManager(SolsAddress address) {
        String[] addressArray = {Optional.ofNullable(address.getAddressLine1()).orElse(""),
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

    private String getGrantIssueDate(CaseData data) {
        for (CollectionMember<Document> documentInfo : data.getProbateDocumentsGenerated()) {
            if (documentInfo.getValue().getDocumentType().equals(DocumentType.DIGITAL_GRANT)) {
                return documentInfo.getValue().getDocumentDateAdded().toString();
            }
        }
        return Strings.EMPTY;
    }

    private Grantee createGrantee(CaseData data, int i) {
        return Grantee.builder()
                .fullName(getFirstName(data, i))
                .address(addressManager(getAddress(data, i)))
                .build();
    }

    private String getFirstName(CaseData caseData, int granteeNumber) {
        if (isYes(caseData.getPrimaryApplicantIsApplying())) {
            return granteeNumber == 1 ? caseData.getPrimaryApplicantForenames() + " " + caseData
                    .getPrimaryApplicantSurname() : getApplyingExecutorName(caseData, granteeNumber - 2);
        }
        return getApplyingExecutorName(caseData, granteeNumber - 1);
    }

    private SolsAddress getAddress(CaseData caseData, int granteeNumber) {
        if (isYes(caseData.getPrimaryApplicantIsApplying())) {
            return granteeNumber == 1 ? caseData.getPrimaryApplicantAddress() : getAdditionalExecutorAddress(caseData,
                    granteeNumber - 2);
        }
        return getAdditionalExecutorAddress(caseData, granteeNumber - 1);
    }

    private SolsAddress getAdditionalExecutorAddress(CaseData caseData, int index) {
        if (caseData.getAdditionalExecutorsApplying().size() >= (index + 1)) {
            return caseData.getAdditionalExecutorsApplying().get(index).getValue().getApplyingExecutorAddress();
        }
        return EMPTY_ADDRESS;
    }

    private String getApplyingExecutorName(CaseData caseData, int index) {
        if (caseData.getAdditionalExecutorsApplying().size() >= (index + 1)) {
            return caseData.getAdditionalExecutorsApplying().get(index).getValue().getApplyingExecutorName();
        }
        return Strings.EMPTY;
    }

    private enum CaseTypeMapping {
        gop("PROBATE"),
        intestacy("ADMINISTRATION"),
        admonWill("ADMON/WILL");

        private String caseTypeItem;

        CaseTypeMapping(String caseType) {
            this.caseTypeItem = caseType;
        }

        private String getCaseTypeMapped() {
            return caseTypeItem;
        }
    }

    private Boolean isYes(String yesNoValue) {
        return yesNoValue.equals("Yes");
    }
}