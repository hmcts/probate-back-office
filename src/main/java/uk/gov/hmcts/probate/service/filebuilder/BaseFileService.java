package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import uk.gov.hmcts.probate.model.ApplicationType;
import uk.gov.hmcts.probate.model.ccd.raw.Grantee;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.probate.model.Constants.CTSC;
import static uk.gov.hmcts.probate.model.Constants.PRINCIPAL_REGISTRY;
import static uk.gov.hmcts.probate.model.Constants.YES;

public abstract class BaseFileService {
    private static final SolsAddress EMPTY_ADDRESS = SolsAddress.builder()
            .addressLine1("")
            .addressLine2("")
            .addressLine3("")
            .postCode("")
            .country("")
            .county("")
            .postTown("")
            .build();

    protected void addAddress(ImmutableList.Builder<String> fileData, List<String> address) {
        fileData.add(address.get(0));
        fileData.add(address.get(1));
        fileData.add(address.get(2));
        fileData.add(address.get(3));
        fileData.add(address.get(6));
    }

    protected int ageCalculator(CaseData data) {
        return Period.between(data.getDeceasedDateOfBirth(), data.getDeceasedDateOfDeath()).getYears();
    }

    protected List<String> addressManager(SolsAddress address) {
        if (address == null) {
            address = EMPTY_ADDRESS;
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

    protected Grantee createGrantee(CaseData data, int i) {
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
        return EMPTY_ADDRESS;
    }

    private String getApplyingExecutorName(CaseData caseData, int index) {
        if (caseData.getAdditionalExecutorsApplying() != null
                && caseData.getAdditionalExecutorsApplying().size() >= (index + 1)) {
            return caseData.getAdditionalExecutorsApplying().get(index).getValue().getApplyingExecutorName();
        }
        return "";
    }

    protected String registryLocationCheck(String registry) {
        return registry.equalsIgnoreCase(CTSC) ? PRINCIPAL_REGISTRY : registry;
    }

    protected Boolean isYes(String yesNoValue) {
        return yesNoValue.equals(YES);
    }
}