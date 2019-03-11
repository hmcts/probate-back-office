package uk.gov.hmcts.probate.service.probateman.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.probate.service.probateman.mapper.qualifiers.ToAdditionalExecutorApplyingMember;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.AdditionalExecutorApplying;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AdditionalExecutorMapper {

    @ToAdditionalExecutorApplyingMember
    public List<CollectionMember<AdditionalExecutorApplying>> toAdditionalCollectionMember(GrantApplication grantApplication) {
        log.info("Adding additionalExecutorApplying to collection for legacy case mapping");
        List<CollectionMember<AdditionalExecutorApplying>> collectionMemberArrayList = new ArrayList();

        if (grantApplication.getSolicitorReference() == null) {
            addAdditionalExecutor(grantApplication.getGrantee1Forenames(),
                    grantApplication.getGrantee1Surname(),
                    grantApplication.getGrantee1Address(),
                    collectionMemberArrayList);
            addAdditionalExecutor(grantApplication.getGrantee2Forenames(),
                    grantApplication.getGrantee2Surname(),
                    grantApplication.getGrantee2Address(),
                    collectionMemberArrayList);
            addAdditionalExecutor(grantApplication.getGrantee3Forenames(),
                    grantApplication.getGrantee3Surname(),
                    grantApplication.getGrantee3Address(),
                    collectionMemberArrayList);
            addAdditionalExecutor(grantApplication.getGrantee4Forenames(),
                    grantApplication.getGrantee4Surname(),
                    grantApplication.getGrantee4Address(),
                    collectionMemberArrayList);
        } else {
            addAdditionalExecutor(grantApplication.getGrantee2Forenames(),
                    grantApplication.getGrantee2Surname(),
                    grantApplication.getGrantee2Address(),
                    collectionMemberArrayList);
            addAdditionalExecutor(grantApplication.getGrantee3Forenames(),
                    grantApplication.getGrantee3Surname(),
                    grantApplication.getGrantee3Address(),
                    collectionMemberArrayList);
            addAdditionalExecutor(grantApplication.getGrantee4Forenames(),
                    grantApplication.getGrantee4Surname(),
                    grantApplication.getGrantee4Address(),
                    collectionMemberArrayList);
        }

        return collectionMemberArrayList;
    }

    private void addAdditionalExecutor(String granteeForenames, String granteeSurname, String granteeAddress,
                                       List<CollectionMember<AdditionalExecutorApplying>> collectionMemberArrayList) {
        if (StringUtils.isNotBlank(granteeForenames)
                || StringUtils.isNotBlank(granteeSurname)
                || StringUtils.isNotBlank(granteeAddress)) {
            collectionMemberArrayList.add(buildExecutor(granteeForenames, granteeSurname, granteeAddress));
        }
    }

    private CollectionMember<AdditionalExecutorApplying> buildExecutor(String granteeForenames,
                                                                       String granteeSurname, String granteeAddress) {
        AdditionalExecutorApplying applying = AdditionalExecutorApplying.builder()
                .applyingExecutorName(granteeForenames + " " + granteeSurname)
                .applyingExecutorAddress(buildAddress(granteeAddress))
                .build();
        return new CollectionMember(null, applying);
    }

    private Address buildAddress(String grantee1Address) {
        return Address.builder().addressLine1(grantee1Address).build();
    }

}
