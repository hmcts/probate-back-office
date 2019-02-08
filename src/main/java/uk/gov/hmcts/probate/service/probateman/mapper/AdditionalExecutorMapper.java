package uk.gov.hmcts.probate.service.probateman.mapper;

import lombok.extern.slf4j.Slf4j;
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

        String grantee1Forenames = grantApplication.getGrantee1Forenames();
        String grantee1Surname = grantApplication.getGrantee1Surname();
        String grantee1Address = grantApplication.getGrantee1Address();
        CollectionMember<AdditionalExecutorApplying> applying1 = buildExecutor(grantee1Forenames, grantee1Surname, grantee1Address);
        collectionMemberArrayList.add(applying1);

        String grantee2Forenames = grantApplication.getGrantee2Forenames();
        String grantee2Surname = grantApplication.getGrantee2Surname();
        String grantee2Address = grantApplication.getGrantee2Address();
        CollectionMember<AdditionalExecutorApplying> applying2 = buildExecutor(grantee2Forenames, grantee2Surname, grantee2Address);
        collectionMemberArrayList.add(applying2);

        String grantee3Forenames = grantApplication.getGrantee3Forenames();
        String grantee3Surname = grantApplication.getGrantee3Surname();
        String grantee3Address = grantApplication.getGrantee3Address();
        CollectionMember<AdditionalExecutorApplying> applying3 = buildExecutor(grantee3Forenames, grantee3Surname, grantee3Address);
        collectionMemberArrayList.add(applying3);

        String grantee4Forenames = grantApplication.getGrantee4Forenames();
        String grantee4Surname = grantApplication.getGrantee4Surname();
        String grantee4Address = grantApplication.getGrantee4Address();
        CollectionMember<AdditionalExecutorApplying> applying4 = buildExecutor(grantee4Forenames, grantee4Surname, grantee4Address);
        collectionMemberArrayList.add(applying4);

        return collectionMemberArrayList;
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
