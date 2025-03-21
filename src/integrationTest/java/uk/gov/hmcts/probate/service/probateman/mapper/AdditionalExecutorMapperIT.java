package uk.gov.hmcts.probate.service.probateman.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.model.probateman.GrantApplication;
import uk.gov.hmcts.reform.probate.model.cases.Address;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ExecutorApplying;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AdditionalExecutorMapperIT {

    private static final String GRANTEE1_FORENAMES = "GR1FN1 GR1FN2";
    private static final String GRANTEE1_SURNAME = "GR1SN";
    private static final String GRANTEE1_ADDRESS = "GR1AddL1, GR1AddL2, GR1AddL3, GR1AddPC";
    private static final String GRANTEE2_FORENAMES = "GR2FN1 GR2FN2";
    private static final String GRANTEE2_SURNAME = "GR2SN";
    private static final String GRANTEE2_ADDRESS = "GR2AddL1, GR2AddL2, GR2AddL3, GR2AddPC";
    private static final String GRANTEE3_FORENAMES = "GR3FN1 GR3FN2";
    private static final String GRANTEE3_SURNAME = "GR3SN";
    private static final String GRANTEE3_ADDRESS = "GR3AddL1, GR3AddL2, GR3AddL3, GR3AddPC";
    private static final String GRANTEE4_FORENAMES = "GR4FN1 GRFN2";
    private static final String GRANTEE4_SURNAME = "GR4SN";
    private static final String GRANTEE4_ADDRESS = "GR4AddL1, GR4AddL2, GR4AddL3, GR4AddPC";
    @Autowired
    private AdditionalExecutorMapper additionalExecutorMapper;

    @Test
    void shouldMapToExecutorApplyingList() {

        GrantApplication grantApplication = new GrantApplication();
        grantApplication.setGrantee1Forenames(GRANTEE1_FORENAMES);
        grantApplication.setGrantee1Surname(GRANTEE1_SURNAME);
        grantApplication.setGrantee1Address(GRANTEE1_ADDRESS);
        grantApplication.setGrantee2Forenames(GRANTEE2_FORENAMES);
        grantApplication.setGrantee2Surname(GRANTEE2_SURNAME);
        grantApplication.setGrantee2Address(GRANTEE2_ADDRESS);
        grantApplication.setGrantee3Forenames(GRANTEE3_FORENAMES);
        grantApplication.setGrantee3Surname(GRANTEE3_SURNAME);
        grantApplication.setGrantee3Address(GRANTEE3_ADDRESS);
        grantApplication.setGrantee4Forenames(GRANTEE4_FORENAMES);
        grantApplication.setGrantee4Surname(GRANTEE4_SURNAME);
        grantApplication.setGrantee4Address(GRANTEE4_ADDRESS);

        List<CollectionMember<ExecutorApplying>> expectedApplyingExecutors = buildExecutorApplying(grantApplication);

        List<CollectionMember<ExecutorApplying>> additionalCollection =
            additionalExecutorMapper.toAdditionalCollectionMember(grantApplication);

        assertThat(additionalCollection.get(0))
                .usingRecursiveComparison().isEqualTo(expectedApplyingExecutors.get(0));
        assertThat(additionalCollection.get(1))
            .usingRecursiveComparison().isEqualTo(expectedApplyingExecutors.get(1));
        assertThat(additionalCollection.get(2))
            .usingRecursiveComparison().isEqualTo(expectedApplyingExecutors.get(2));
        assertThat(additionalCollection.get(3))
            .usingRecursiveComparison().isEqualTo(expectedApplyingExecutors.get(3));

    }

    @Test
    void shouldMapToLimitedExecutorApplyingList() {

        GrantApplication grantApplication = new GrantApplication();
        grantApplication.setGrantee1Forenames(GRANTEE1_FORENAMES);
        grantApplication.setGrantee1Surname(GRANTEE1_SURNAME);
        grantApplication.setGrantee1Address(GRANTEE1_ADDRESS);
        grantApplication.setGrantee3Forenames(GRANTEE3_FORENAMES);
        grantApplication.setGrantee3Surname(GRANTEE3_SURNAME);
        grantApplication.setGrantee3Address(GRANTEE3_ADDRESS);

        List<CollectionMember<ExecutorApplying>> expectedApplyingExecutors = buildExecutorApplying(grantApplication);

        List<CollectionMember<ExecutorApplying>> additionalCollection =
            additionalExecutorMapper.toAdditionalCollectionMember(grantApplication);

        assertThat(additionalCollection.size()).isEqualTo(2);
        assertThat(additionalCollection.get(0))
            .usingRecursiveComparison().isEqualTo(expectedApplyingExecutors.get(0));
        assertThat(additionalCollection.get(1))
            .usingRecursiveComparison().isEqualTo(expectedApplyingExecutors.get(2));

    }

    private List<CollectionMember<ExecutorApplying>> buildExecutorApplying(GrantApplication grantApplication) {
        List<CollectionMember<ExecutorApplying>> applyingList = new ArrayList<>();
        CollectionMember<ExecutorApplying> applying1 = buildExecutor(grantApplication.getGrantee1Forenames(),
            grantApplication.getGrantee1Surname(),
            grantApplication.getGrantee1Address());

        CollectionMember<ExecutorApplying> applying2 = buildExecutor(grantApplication.getGrantee2Forenames(),
            grantApplication.getGrantee2Surname(),
            grantApplication.getGrantee2Address());

        CollectionMember<ExecutorApplying> applying3 = buildExecutor(grantApplication.getGrantee3Forenames(),
            grantApplication.getGrantee3Surname(),
            grantApplication.getGrantee3Address());

        CollectionMember<ExecutorApplying> applying4 = buildExecutor(grantApplication.getGrantee4Forenames(),
            grantApplication.getGrantee4Surname(),
            grantApplication.getGrantee4Address());

        applyingList.add(applying1);
        applyingList.add(applying2);
        applyingList.add(applying3);
        applyingList.add(applying4);
        return applyingList;
    }

    private CollectionMember<ExecutorApplying> buildExecutor(String granteeForenames,
                                                             String granteeSurname, String granteeAddress) {
        ExecutorApplying applying = ExecutorApplying.builder()
            .applyingExecutorName(granteeForenames + " " + granteeSurname)
            .applyingExecutorAddress(buildAddress(granteeAddress))
            .build();
        return new CollectionMember(null, applying);
    }

    private Address buildAddress(String grantee1Address) {
        return Address.builder().addressLine1(grantee1Address).build();
    }

}
