package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.exceptionrecord.OCRFieldsList;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToAdditionalExecutorApplying;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AdditionalExecutorOCRMapper {

    private static final String EXEC_APPLYING = "executorsApplying_";
    private static final String EXEC_NAME = "_applying_ExecutorName";
    private static final String OTHER_NAME = "_applyingExecutor_OtherNames";
    private static final String ADDRESS_LINE_1 = "_applying_ExecutorAddressLine1";
    private static final String ADDRESS_LINE_2 = "_applying_ExecutorAddressLine2";
    private static final String ADDRESS_POST_TOWN = "_applying_PostTown";
    private static final String ADDRESS_COUNTY = "_applyingExecutor_County";
    private static final String ADDRESS_POSTCODE = "_applyingExecutorAddress_PostCode";
    private static final String EMAIL = "_applying_ExecutorEmail";

    private String executorName;
    private String otherNames;
    private String addressLine1;
    private String addressLine2;
    private String postTown;
    private String county;
    private String postCode;
    private String email;

    @ToAdditionalExecutorApplying
    public List<CollectionMember<ExecutorApplying>> toAdditionalCollectionMember(OCRFieldsList fields) {
        log.info("Adding additionalExecutorApplying to collection for ocr scanned records mapping");
        List<CollectionMember<ExecutorApplying>> collectionMemberList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (OCRField field : fields.getOcrFields()) {
                if (field.getName().equals(EXEC_APPLYING + i + EXEC_NAME)) {
                    executorName = field.getValue();
                } else if (field.getName().equals(EXEC_APPLYING + i + OTHER_NAME)) {
                    otherNames = field.getValue();
                } else if (field.getName().equals(EXEC_APPLYING + i + ADDRESS_LINE_1)) {
                    addressLine1 = field.getValue();
                } else if (field.getName().equals(EXEC_APPLYING + i + ADDRESS_LINE_2)) {
                    addressLine2 = field.getValue();
                } else if (field.getName().equals(EXEC_APPLYING + i + ADDRESS_POST_TOWN)) {
                    postTown = field.getValue();
                } else if (field.getName().equals(EXEC_APPLYING + i + ADDRESS_COUNTY)) {
                    county = field.getValue();
                } else if (field.getName().equals(EXEC_APPLYING + i + ADDRESS_POSTCODE)) {
                    postCode = field.getValue();
                } else if (field.getName().equals(EXEC_APPLYING + i + EMAIL)) {
                    email = field.getValue();
                }
            }
            addAdditionalExecutor(collectionMemberList);
        }
        return collectionMemberList;
    }

    private void addAdditionalExecutor(List<CollectionMember<ExecutorApplying>> collectionMemberList) {
        collectionMemberList.add(buildExecutor());
    }

    private CollectionMember<ExecutorApplying> buildExecutor() {
        ExecutorApplying applying = ExecutorApplying.builder()
                .applyingExecutorName(executorName)
                .applyingExecutorOtherNames(otherNames)
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