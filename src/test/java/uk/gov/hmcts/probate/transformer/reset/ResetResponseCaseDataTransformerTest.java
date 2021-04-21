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
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.TitleAndClearingTypeService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.probate.util.CommonVariables.DIRECTOR;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_LEAVE;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_LEAVE_DATE;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_OVERVIEW;
import static uk.gov.hmcts.probate.util.CommonVariables.DISPENSE_WITH_NOTICE_SUPPORTING_DOCS;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_FIRST_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_ID;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_SURNAME;
import static uk.gov.hmcts.probate.util.CommonVariables.EXEC_TRUST_CORP_POS;
import static uk.gov.hmcts.probate.util.CommonVariables.LODGEMENT_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.DATE;
import static uk.gov.hmcts.probate.util.CommonVariables.NO;
import static uk.gov.hmcts.probate.util.CommonVariables.PARTNER_EXEC;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_ADDRESS;
import static uk.gov.hmcts.probate.util.CommonVariables.SOLICITOR_FIRM_NAME;
import static uk.gov.hmcts.probate.util.CommonVariables.TRUST_CORP_NAME;

@RunWith(MockitoJUnitRunner.class)
public class ResetResponseCaseDataTransformerTest {

    private final CaseData.CaseDataBuilder<?, ?> caseDataBuilder = CaseData.builder();

    private final ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder = ResponseCaseData.builder();

    @Mock
    private CaseDetails caseDetailsMock;

    @Mock
    private TitleAndClearingTypeService titleAndClearingTypeService;

    @InjectMocks
    private ResetResponseCaseDataTransformer resetResponseCaseDataTransformer;


    private List<CollectionMember<AdditionalExecutorTrustCorps>> trustCorpsExecutorList;
    private List<CollectionMember<AdditionalExecutorPartners>> partnerExecutorList;
    private List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeExecList;
    private List<String> sharesInCompanyProfits;

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

        sharesInCompanyProfits = new ArrayList<>();
        sharesInCompanyProfits.add(DIRECTOR);

        responseCaseDataBuilder
                .otherPartnersApplyingAsExecutors(partnerExecutorList)
                .nameOfSucceededFirm(SOLICITOR_FIRM_NAME)
                .nameOfFirmNamedInWill(SOLICITOR_FIRM_NAME)
                .whoSharesInCompanyProfits(sharesInCompanyProfits)
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .trustCorpName(TRUST_CORP_NAME)
                .trustCorpAddress(SOLICITOR_ADDRESS)
                .addressOfSucceededFirm(SOLICITOR_ADDRESS)
                .addressOfFirmNamedInWill(SOLICITOR_ADDRESS)
                .lodgementAddress(LODGEMENT_ADDRESS)
                .lodgementDate("01-01-2020")
                .dispenseWithNotice(NO)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList)
                .dispenseWithNoticeLeaveGiven(DISPENSE_WITH_NOTICE_LEAVE)
                .dispenseWithNoticeLeaveGivenDate(DISPENSE_WITH_NOTICE_LEAVE_DATE)
                .dispenseWithNoticeOverview(DISPENSE_WITH_NOTICE_OVERVIEW)
                .dispenseWithNoticeSupportingDocs(DISPENSE_WITH_NOTICE_SUPPORTING_DOCS);

    }

    @Test
    public void shouldResetTitleAndClearingPartnerOptions() {

        caseDataBuilder
                .otherPartnersApplyingAsExecutors(partnerExecutorList)
                .nameOfSucceededFirm(SOLICITOR_FIRM_NAME)
                .nameOfFirmNamedInWill(SOLICITOR_FIRM_NAME)
                .addressOfSucceededFirm(SOLICITOR_ADDRESS)
                .addressOfFirmNamedInWill(SOLICITOR_ADDRESS)
                .whoSharesInCompanyProfits(sharesInCompanyProfits);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(titleAndClearingTypeService.trustCorpTitleAndClearingOptionSelected(caseDataBuilder.build()))
                .thenReturn(true);

        resetResponseCaseDataTransformer.resetTitleAndClearingFields(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertNull(responseCaseData.getOtherPartnersApplyingAsExecutors());
        assertNull(responseCaseData.getNameOfFirmNamedInWill());
        assertNull(responseCaseData.getNameOfSucceededFirm());
        assertNull(responseCaseData.getAddressOfSucceededFirm());
        assertNull(responseCaseData.getAddressOfFirmNamedInWill());
        assertNull(responseCaseData.getWhoSharesInCompanyProfits());
    }

    @Test
    public void shouldResetTitleAndClearingTrustCorpOptions() {

        caseDataBuilder
                .additionalExecutorsTrustCorpList(trustCorpsExecutorList)
                .trustCorpName(TRUST_CORP_NAME)
                .trustCorpAddress(SOLICITOR_ADDRESS)
                .lodgementAddress(LODGEMENT_ADDRESS)
                .lodgementDate(DATE);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(titleAndClearingTypeService.partnerTitleAndClearingOptionSelected(caseDataBuilder.build()))
                .thenReturn(true);

        resetResponseCaseDataTransformer.resetTitleAndClearingFields(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertNull(responseCaseData.getAdditionalExecutorsTrustCorpList());
        assertNull(responseCaseData.getTrustCorpName());
        assertNull(responseCaseData.getTrustCorpAddress());
        assertNull(responseCaseData.getLodgementAddress());
        assertNull(responseCaseData.getLodgementDate());
    }

    @Test
    public void shouldResetSuccessorFirmName() {

        caseDataBuilder
                .nameOfSucceededFirm(SOLICITOR_FIRM_NAME);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        when(titleAndClearingTypeService.partnerTitleAndClearingOptionSelected(caseDataBuilder.build()))
                .thenReturn(true);
        when(titleAndClearingTypeService.successorFirmTitleAndClearingOptionSelected(caseDataBuilder.build()))
                .thenReturn(false);

        resetResponseCaseDataTransformer.resetTitleAndClearingFields(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertNull(responseCaseData.getNameOfSucceededFirm());
    }

    @Test
    public void shouldResetDispenseWithNoticeOptions() {

        caseDataBuilder
                .dispenseWithNotice(NO)
                .dispenseWithNoticeOtherExecsList(dispenseWithNoticeExecList)
                .dispenseWithNoticeLeaveGiven(DISPENSE_WITH_NOTICE_LEAVE)
                .dispenseWithNoticeLeaveGivenDate(DISPENSE_WITH_NOTICE_LEAVE_DATE)
                .dispenseWithNoticeOverview(DISPENSE_WITH_NOTICE_OVERVIEW)
                .dispenseWithNoticeSupportingDocs(DISPENSE_WITH_NOTICE_SUPPORTING_DOCS);

        when(caseDetailsMock.getData()).thenReturn(caseDataBuilder.build());

        resetResponseCaseDataTransformer.resetTitleAndClearingFields(caseDetailsMock.getData(),
                responseCaseDataBuilder);

        ResponseCaseData responseCaseData = responseCaseDataBuilder.build();

        assertNull(responseCaseData.getDispenseWithNoticeOtherExecsList());
        assertNull(responseCaseData.getDispenseWithNoticeLeaveGiven());
        assertNull(responseCaseData.getDispenseWithNoticeLeaveGiven());
        assertNull(responseCaseData.getDispenseWithNoticeOverview());
        assertNull(responseCaseData.getDispenseWithNoticeSupportingDocs());
    }

}
