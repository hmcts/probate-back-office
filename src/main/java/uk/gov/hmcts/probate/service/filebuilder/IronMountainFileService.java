package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.Document;
import uk.gov.hmcts.probate.model.ccd.raw.Grantee;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.Case;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.io.File;
import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class IronMountainFileService {

    private TextFileBuilderService textFileBuilderService = new TextFileBuilderService();
    private static final String DELIMITER = "|";
    private Long id;
    private Grantee grantee1;
    private Grantee grantee2;
    private Grantee grantee3;
    private Grantee grantee4;

    public File createIronMountainFile(Case ccdCase, String fileName) throws IOException {
        this.id = ccdCase.getId();
        return textFileBuilderService.createFile(prepareData(ccdCase.getData()), DELIMITER, fileName, false);
    }

    private List<String> prepareData(CaseData data) {
        ImmutableList.Builder<String> fileData = ImmutableList.builder();

        final List<String> deceasedAddress = addressManager(data.getDeceasedAddress());
        grantees(data);
        List<String> applicantAddress = addressManager(data.getApplicationType().equals(ApplicationType.PERSONAL) ? data
                .getPrimaryApplicantAddress() : data.getSolsSolicitorAddress());

        fileData.add(Optional.ofNullable(data.getBoDeceasedTitle()).orElse(""));
        fileData.add(data.getDeceasedForenames());
        fileData.add(data.getDeceasedSurname());
        fileData.add(data.getDeceasedDateOfDeath().toString());
        fileData.add("");
        fileData.add(data.getDeceasedDateOfBirth().toString());
        fileData.add(String.valueOf(ageCalculator(data)));
        fileData.add(deceasedAddress.get(0));
        fileData.add(deceasedAddress.get(1));
        fileData.add(deceasedAddress.get(2));
        fileData.add(deceasedAddress.get(3));
        fileData.add(deceasedAddress.get(6));
        fileData.add(this.id.toString());
        fileData.add(getGrantIssueDate(data));
        fileData.add("");
        fileData.add(grantee1.getFirstName());
        fileData.add(grantee1.getLastName());
        fileData.add(grantee1.getAddress().get(0));
        fileData.add(grantee1.getAddress().get(1));
        fileData.add(grantee1.getAddress().get(2));
        fileData.add(grantee1.getAddress().get(3));
        fileData.add(grantee1.getAddress().get(6));
        fileData.add("");
        fileData.add(grantee2.getFirstName());
        fileData.add(grantee2.getLastName());
        fileData.add(grantee2.getAddress().get(0));
        fileData.add(grantee2.getAddress().get(1));
        fileData.add(grantee2.getAddress().get(2));
        fileData.add(grantee2.getAddress().get(3));
        fileData.add(grantee2.getAddress().get(6));
        fileData.add("");
        fileData.add(grantee3.getFirstName());
        fileData.add(grantee3.getLastName());
        fileData.add(grantee3.getAddress().get(0));
        fileData.add(grantee3.getAddress().get(1));
        fileData.add(grantee3.getAddress().get(2));
        fileData.add(grantee3.getAddress().get(3));
        fileData.add(grantee3.getAddress().get(6));
        fileData.add("");
        fileData.add(grantee4.getFirstName());
        fileData.add(grantee4.getLastName());
        fileData.add(grantee4.getAddress().get(0));
        fileData.add(grantee4.getAddress().get(1));
        fileData.add(grantee4.getAddress().get(2));
        fileData.add(grantee4.getAddress().get(3));
        fileData.add(grantee4.getAddress().get(6));
        fileData.add(data.getApplicationType().name());
        fileData.add(data.getApplicationType().equals(ApplicationType.PERSONAL) ? data.getPrimaryApplicantSurname() : data
                .getSolsSolicitorFirmName());
        fileData.add(applicantAddress.get(0));
        fileData.add(applicantAddress.get(1));
        fileData.add(applicantAddress.get(2));
        fileData.add(applicantAddress.get(3));
        fileData.add(applicantAddress.get(6));
        fileData.add(data.getIhtGrossValue().toString());
        fileData.add(data.getIhtNetValue().toString());
        fileData.add(caseTypeMapping(data.getCaseType()));
        fileData.add(data.getRegistryLocation());

        return fileData.build();
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

    private void grantees(CaseData data) {
        SolsAddress emptyAddress = SolsAddress.builder()
                .addressLine1("")
                .addressLine2("")
                .addressLine3("")
                .postCode("")
                .country("")
                .county("")
                .postTown("")
                .build();

        grantee1 = Grantee.builder()
                .firstName(data.getPrimaryApplicantIsApplying().equals("Yes") ? data.getPrimaryApplicantForenames() : data
                        .getAdditionalExecutorsApplying().size() >= 1 ? data.getAdditionalExecutorsApplying().get(0).getValue()
                        .getApplyingExecutorName() : "")
                .lastName(data.getPrimaryApplicantIsApplying().equals("Yes") ? data.getPrimaryApplicantSurname() : "")
                .address(addressManager(data.getPrimaryApplicantIsApplying().equals("Yes") ? data
                        .getPrimaryApplicantAddress() : data.getAdditionalExecutorsApplying().size() >= 1 ? data
                        .getAdditionalExecutorsApplying().get(0).getValue().getApplyingExecutorAddress() : emptyAddress))
                .build();
        grantee2 = Grantee.builder()
                .firstName(data.getPrimaryApplicantIsApplying().equals("Yes") ? data.getAdditionalExecutorsApplying().size()
                        >= 1 ? data.getAdditionalExecutorsApplying().get(0).getValue().getApplyingExecutorName() : "" : data
                        .getAdditionalExecutorsApplying().size() >= 2 ? data.getAdditionalExecutorsApplying().get(1).getValue()
                        .getApplyingExecutorName() : "")
                .lastName("")
                .address(addressManager(data.getPrimaryApplicantIsApplying().equals("Yes") ? data
                        .getAdditionalExecutorsApplying().size() >= 1 ? data.getAdditionalExecutorsApplying().get(0).getValue()
                        .getApplyingExecutorAddress() : emptyAddress : data.getAdditionalExecutorsApplying().size() >= 2 ? data
                        .getAdditionalExecutorsApplying().get(1).getValue().getApplyingExecutorAddress() : emptyAddress))
                .build();

        grantee3 = Grantee.builder()
                .firstName(data.getPrimaryApplicantIsApplying().equals("Yes") ? data.getAdditionalExecutorsApplying().size()
                        >= 2 ? data.getAdditionalExecutorsApplying().get(1).getValue().getApplyingExecutorName() : "" : data
                        .getAdditionalExecutorsApplying().size() >= 3 ? data.getAdditionalExecutorsApplying().get(2).getValue()
                        .getApplyingExecutorName() : "")
                .lastName("")
                .address(addressManager(data.getPrimaryApplicantIsApplying().equals("Yes") ? data
                        .getAdditionalExecutorsApplying().size() >= 2 ? data.getAdditionalExecutorsApplying().get(1).getValue()
                        .getApplyingExecutorAddress() : emptyAddress : data.getAdditionalExecutorsApplying().size() >= 3 ? data
                        .getAdditionalExecutorsApplying().get(2).getValue().getApplyingExecutorAddress() : emptyAddress))
                .build();

        grantee4 = Grantee.builder()
                .firstName(data.getPrimaryApplicantIsApplying().equals("Yes") ? data.getAdditionalExecutorsApplying().size()
                        >= 3 ? data.getAdditionalExecutorsApplying().get(2).getValue().getApplyingExecutorName() : "" : data
                        .getAdditionalExecutorsApplying().size() >= 4 ? data.getAdditionalExecutorsApplying().get(3).getValue()
                        .getApplyingExecutorName() : "")
                .lastName("")
                .address(addressManager(data.getPrimaryApplicantIsApplying().equals("Yes") ? data
                        .getAdditionalExecutorsApplying().size() >= 3 ? data.getAdditionalExecutorsApplying().get(2).getValue()
                        .getApplyingExecutorAddress() : emptyAddress : data.getAdditionalExecutorsApplying().size() >= 4 ? data
                        .getAdditionalExecutorsApplying().get(3).getValue().getApplyingExecutorAddress() : emptyAddress))
                .build();
    }

    private String caseTypeMapping(String caseType) {
        String ironMountainCaseType;
        switch (caseType) {
            case "gop":
                ironMountainCaseType = "PROBATE";
                break;
            case "intestacy":
                ironMountainCaseType = "ADMINISTRATION";
                break;
            case "admonWill":
                ironMountainCaseType = "ADMON/WILL";
                break;
            default:
                ironMountainCaseType = "";
                break;
        }
        return ironMountainCaseType;
    }
}
