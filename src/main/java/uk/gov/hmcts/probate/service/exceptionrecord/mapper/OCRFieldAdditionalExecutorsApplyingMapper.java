package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorsApplying;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OCRFieldAdditionalExecutorsApplyingMapper {

    @SuppressWarnings("squid:S1168")
    @ToAdditionalExecutorsApplying
    public List<CollectionMember<ExecutorApplying>> toAdditionalCollectionMember(ExceptionRecordOCRFields ocrFields) {
        log.info("Beginning mapping for Additional Executor Applying collection");

        List<CollectionMember<ExecutorApplying>> collectionMemberList = new ArrayList<>();

        if (ocrFields.getExecutorsApplying_0_applyingExecutorName() != null
                && !ocrFields.getExecutorsApplying_0_applyingExecutorName().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getExecutorsApplying_0_applyingExecutorName(),
                    ocrFields.getExecutorsApplying_0_applyingExecutorOtherNames(),
                    ocrFields.getExecutorsApplying_0_applyingExecutorAddressLine1(),
                    ocrFields.getExecutorsApplying_0_applyingExecutorAddressLine2(),
                    ocrFields.getExecutorsApplying_0_applyingExecutorAddressTown(),
                    ocrFields.getExecutorsApplying_0_applyingExecutorAddressCounty(),
                    ocrFields.getExecutorsApplying_0_applyingExecutorAddressPostCode(),
                    ocrFields.getExecutorsApplying_0_applyingExecutorEmail()
            ));
        }

        if (ocrFields.getExecutorsApplying_1_applyingExecutorName() != null
                && !ocrFields.getExecutorsApplying_1_applyingExecutorName().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getExecutorsApplying_1_applyingExecutorName(),
                    ocrFields.getExecutorsApplying_1_applyingExecutorOtherNames(),
                    ocrFields.getExecutorsApplying_1_applyingExecutorAddressLine1(),
                    ocrFields.getExecutorsApplying_1_applyingExecutorAddressLine2(),
                    ocrFields.getExecutorsApplying_1_applyingExecutorAddressTown(),
                    ocrFields.getExecutorsApplying_1_applyingExecutorAddressCounty(),
                    ocrFields.getExecutorsApplying_1_applyingExecutorAddressPostCode(),
                    ocrFields.getExecutorsApplying_1_applyingExecutorEmail()
            ));
        }

        if (ocrFields.getExecutorsApplying_2_applyingExecutorName() != null
                && !ocrFields.getExecutorsApplying_2_applyingExecutorName().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getExecutorsApplying_2_applyingExecutorName(),
                    ocrFields.getExecutorsApplying_2_applyingExecutorOtherNames(),
                    ocrFields.getExecutorsApplying_2_applyingExecutorAddressLine1(),
                    ocrFields.getExecutorsApplying_2_applyingExecutorAddressLine2(),
                    ocrFields.getExecutorsApplying_2_applyingExecutorAddressTown(),
                    ocrFields.getExecutorsApplying_2_applyingExecutorAddressCounty(),
                    ocrFields.getExecutorsApplying_2_applyingExecutorAddressPostCode(),
                    ocrFields.getExecutorsApplying_2_applyingExecutorEmail()
            ));
        }

        return collectionMemberList;
    }

    private CollectionMember<ExecutorApplying> buildExecutor(
            String executorName,
            String otherNames,
            String addressLine1,
            String addressLine2,
            String postTown,
            String county,
            String postCode,
            String email
    ) {
        ExecutorApplying applying = ExecutorApplying.builder()
                .applyingExecutorName(executorName)
                .applyingExecutorOtherNames(otherNames)
                .applyingExecutorHasOtherName((otherNames.isEmpty()?false:true))
                .applyingExecutorEmail(email)
                .applyingExecutorAddress(buildAddress(addressLine1, addressLine2, postTown, county, postCode))
                .build();
        return new CollectionMember<>(null, applying);
    }

    private Address buildAddress(String execAddress1, String execAddress2, String execPostTown, String execCounty,
                                 String execPostCode) {
        return Address.builder()
                .addressLine1(execAddress1)
                .addressLine2(execAddress2)
                .postTown(execPostTown)
                .county(execCounty)
                .postCode(execPostCode)
                .build();
    }
}