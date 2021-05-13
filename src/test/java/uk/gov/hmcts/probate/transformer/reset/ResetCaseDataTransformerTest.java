package uk.gov.hmcts.probate.transformer.reset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.TitleAndClearingTypeService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_FIRST_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_TRUST_CORP_POS;
import static uk.gov.hmcts.probate.util.CommonVariables.NO;
import static uk.gov.hmcts.probate.util.CommonVariables.PARTNER_EXEC;

@RunWith(MockitoJUnitRunner.class)
public class ResetCaseDataTransformerTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private TitleAndClearingTypeService titleAndClearingTypeService;

    @InjectMocks
    private ResetCaseDataTransformer resetCaseDataTransformer;

    private List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList;
    private List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList;
    private List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeExecList;

    @Before
    public void setUp() {

        trustCorpsExecutorList = new ArrayList<>();
        trustCorpsExecutorList.add(new CollectionMember(EXEC_ID,
                AdditionalExecutorTrustCorps.builder()
                        .additionalExecForenames(EXEC_FIRST_NAME)
                        .additionalExecLastname(EXEC_SURNAME)
                        .additionalExecutorTrustCorpPosition(EXEC_TRUST_CORP_POS)
                        .build()));

        partnerExecutorList = new ArrayList<>();
        partnerExecutorList.add(PARTNER_EXEC);

        dispenseWithNoticeExecList = new ArrayList<>();
        dispenseWithNoticeExecList.add(DISPENSE_WITH_NOTICE_EXEC);
    }

    @Test
    public void shouldResetTrustCorpsList() {
        caseDataBuilder
                .otherPartnersApplyingAsExecutors(partnerExecutorList)
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(titleAndClearingTypeService.partnerTitleAndClearingOptionSelected(caseDataBuilder.build()))
                .thenReturn(true);

        resetCaseDataTransformer.resetExecutorLists(caseDetailsMock.getData());

        CaseData caseData = caseDetailsMock.getData();

        assertEquals(caseData.getOtherPartnersApplyingAsExecutors(), partnerExecutorList);
        assertEquals(0, caseData.getAdditionalExecutorsTrustCorpList().size());
    }

    @Test
    public void shouldResetPartnerList() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .otherPartnersApplyingAsExecutors(partnerExecutorList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(titleAndClearingTypeService.trustCorpTitleAndClearingOptionSelected(caseDataBuilder.build()))
                .thenReturn(true);

        resetCaseDataTransformer.resetExecutorLists(caseDetailsMock.getData());

        CaseData caseData = caseDetailsMock.getData();

        assertEquals(caseData.getAdditionalExecutorsTrustCorpList(), trustCorpsExecutorList);
        assertEquals(0, caseData.getOtherPartnersApplyingAsExecutors().size());
    }

    @Test
    public void shouldResetTitleAndClearingExecutorLists() {
        caseDataBuilder
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .otherPartnersApplyingAsExecutors(partnerExecutorList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        resetCaseDataTransformer.resetExecutorLists(caseDetailsMock.getData());

        CaseData caseData = caseDetailsMock.getData();

        assertEquals(0, caseData.getOtherPartnersApplyingAsExecutors().size());
        assertEquals(0, caseData.getAdditionalExecutorsTrustCorpList().size());
    }

    @Test
    public void shouldResetPowerReservedExecutorLists() {
        caseDataBuilder
                .dispenseWithNotice(NO)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        resetCaseDataTransformer.resetExecutorLists(caseDetailsMock.getData());

        CaseData caseData = caseDetailsMock.getData();

        assertEquals(0, caseData.getDispenseWithNoticeOtherExecsList().size());
    }
}
