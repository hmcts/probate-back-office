package uk.gov.hmcts.probate.service.docmosis.assembler;

import org.junit.Test;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssembleEntitlementTest {

    private static final String YES = "Yes";
    private AssemblerBase assemblerBase = new AssemblerBase();

    private AssembleEntitlement assembleEntitlement = new AssembleEntitlement(assemblerBase);

    @Test
    public void testExecutorNotAccountedForSingleApplicant() {

        CaseData caseData = CaseData.builder().primaryApplicantForenames("primary fn").primaryApplicantSurname("primary sn").build();

        List<ParagraphDetail> response = assembleEntitlement.executorNotAccountedFor(ParagraphCode.IHT421Await, caseData);
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals("primary fn primary sn", response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void testExecutorNotAccountedForMultipleApplicant() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.executorNotAccountedFor(ParagraphCode.IHT421Await, caseData);
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals("primary fn primary sn, Bob Smith", response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementAttorneryAndExec() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementAttorneyAndExec(ParagraphCode.EntAttorney, caseData);
        assertEquals("EntAttorney", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00135.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals("Attorney and Executor cannot apply together", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementLeadingGrantApplication() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementLeadingGrantApplication(ParagraphCode.IHT421Await, caseData);
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementNoTitle() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementNoTitle(ParagraphCode.IHT421Await, caseData);
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementFamilyTree() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementFamilyTree(ParagraphCode.EntAttorney, caseData);
        assertEquals("EntAttorney", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00135.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals("Attorney and Executor cannot apply together", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementConfirmDeath() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementConfirmDeath(ParagraphCode.IHT421Await, caseData);
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementSubsitituteExec() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementSubstitutedExec(ParagraphCode.IHT421Await, caseData);
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementPrejudic() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementPrejudice(ParagraphCode.EntAttorney, caseData);
        assertEquals("EntAttorney", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00135.docx", response.get(0).getTemplateName());
        assertEquals("Static", response.get(0).getEnableType().name());
        assertEquals("Attorney and Executor cannot apply together", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementWrongExec() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementWrongExec(ParagraphCode.IHT421Await, caseData);
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

    @Test
    public void shouldGetEntitlementTwoApplications() {

        CollectionMember<AdditionalExecutorApplying> additionalExecutor =
                new CollectionMember<>(AdditionalExecutorApplying.builder().applyingExecutorName("Bob Smith")
                        .applyingExecutorAddress(SolsAddress.builder().addressLine1("123 Fake street")
                                .addressLine3("North West East Field")
                                .postCode("AB2 3CD")
                                .build()).build());
        List<CollectionMember<AdditionalExecutorApplying>> additionalExecutors = new ArrayList<>(1);
        additionalExecutors.add(additionalExecutor);

        CaseData caseData = CaseData.builder()
                .primaryApplicantForenames("primary fn")
                .primaryApplicantSurname("primary sn")
                .additionalExecutorsApplying(additionalExecutors)
                .build();


        List<ParagraphDetail> response = assembleEntitlement.entitlementTwoApplications(ParagraphCode.IHT421Await, caseData);
        assertEquals("IHT421Await", response.get(0).getCode());
        assertEquals("FL-PRB-GNO-ENG-00125.docx", response.get(0).getTemplateName());
        assertEquals("Text", response.get(0).getEnableType().name());
        assertEquals("Awaiting IHT421", response.get(0).getLabel());
        assertEquals(null, response.get(0).getTextValue());
        assertEquals(null, response.get(0).getTextAreaValue());
    }

}
