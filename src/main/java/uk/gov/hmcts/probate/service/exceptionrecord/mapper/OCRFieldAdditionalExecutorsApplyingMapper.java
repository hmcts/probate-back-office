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

        if (ocrFields.getExecutorsApplying0applyingExecutorName() != null
                && !ocrFields.getExecutorsApplying0applyingExecutorName().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getExecutorsApplying0applyingExecutorName(),
                    ocrFields.getExecutorsApplying0applyingExecutorOtherNames(),
                    ocrFields.getExecutorsApplying0applyingExecutorAddressLine1(),
                    ocrFields.getExecutorsApplying0applyingExecutorAddressLine2(),
                    ocrFields.getExecutorsApplying0applyingExecutorAddressTown(),
                    ocrFields.getExecutorsApplying0applyingExecutorAddressCounty(),
                    ocrFields.getExecutorsApplying0applyingExecutorAddressPostCode(),
                    ocrFields.getExecutorsApplying0applyingExecutorEmail()
            ));
        }

        if (ocrFields.getExecutorsApplying1applyingExecutorName() != null
                && !ocrFields.getExecutorsApplying1applyingExecutorName().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getExecutorsApplying1applyingExecutorName(),
                    ocrFields.getExecutorsApplying1applyingExecutorOtherNames(),
                    ocrFields.getExecutorsApplying1applyingExecutorAddressLine1(),
                    ocrFields.getExecutorsApplying1applyingExecutorAddressLine2(),
                    ocrFields.getExecutorsApplying1applyingExecutorAddressTown(),
                    ocrFields.getExecutorsApplying1applyingExecutorAddressCounty(),
                    ocrFields.getExecutorsApplying1applyingExecutorAddressPostCode(),
                    ocrFields.getExecutorsApplying1applyingExecutorEmail()
            ));
        }

        if (ocrFields.getExecutorsApplying2applyingExecutorName() != null
                && !ocrFields.getExecutorsApplying2applyingExecutorName().isEmpty()) {
            collectionMemberList.add(buildExecutor(
                    ocrFields.getExecutorsApplying2applyingExecutorName(),
                    ocrFields.getExecutorsApplying2applyingExecutorOtherNames(),
                    ocrFields.getExecutorsApplying2applyingExecutorAddressLine1(),
                    ocrFields.getExecutorsApplying2applyingExecutorAddressLine2(),
                    ocrFields.getExecutorsApplying2applyingExecutorAddressTown(),
                    ocrFields.getExecutorsApplying2applyingExecutorAddressCounty(),
                    ocrFields.getExecutorsApplying2applyingExecutorAddressPostCode(),
                    ocrFields.getExecutorsApplying2applyingExecutorEmail()
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
                .applyingExecutorHasOtherName((otherNames == null || otherNames.isEmpty() ? false : true))
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