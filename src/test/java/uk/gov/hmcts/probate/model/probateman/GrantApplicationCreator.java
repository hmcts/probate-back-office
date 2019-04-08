package uk.gov.hmcts.probate.model.probateman;

import java.time.LocalDate;

public class GrantApplicationCreator {

    public static GrantApplication create() {
        GrantApplication grantApplication = new GrantApplication();
        grantApplication.setId(1234L);
        grantApplication.setProbateNumber("gaProbateNumber");
        grantApplication.setProbateVersion(99999L);
        grantApplication.setRegistryName("Oxford");
        grantApplication.setSubregistryName("Carlisle");
        grantApplication.setRegistryCode(999L);
        grantApplication.setDeceasedId(66L);
        grantApplication.setDeceasedForenames("gaDeceasedForenames");
        grantApplication.setDeceasedSurname("gaDeceasedSurname");
        grantApplication.setDateOfBirth(LocalDate.of(1919, 1, 1));
        grantApplication.setDateOfDeath1(LocalDate.of(2019, 1, 1));
        grantApplication.setDeceasedAddress("gaDeceasedAddress");
        grantApplication.setDeceasedText("gaDeceasedText");
        grantApplication.setAliasNames("gaAliasNames");
        grantApplication.setGrantApplicationText("gaGrantApplicationText");
        grantApplication.setApplicationEventText("gaApplicationEventText");
        grantApplication.setOathText("gaOathText");
        grantApplication.setExecutorText("gaExecutorText");
        grantApplication.setOtherInformationText("gaOtherInformationText");
        grantApplication.setLinkedDeceasedIds("gaLinkedDeceasedIds");
        grantApplication.setCcdCaseNo("234495436595");
        grantApplication.setDnmInd("N");
        grantApplication.setDeceasedAgeAtDeath(100L);
        grantApplication.setDeceasedDeathType("gaDeceasedDeathType");
        grantApplication.setDeceasedDomicile("gaDeceasedDomicile");
        grantApplication.setDeceasedDomicileInWelsh("gaDeceasedDomicileInWelsh");
        grantApplication.setDeceasedDomicileWelsh("gaDeceasedDomicileWelsh");
        grantApplication.setDeceasedHonours("gaDeceasedHonours");
        grantApplication.setDeceasedSex("gaDeceasedSex");
        grantApplication.setDeceasedTitle("gaDeceasedTitle");
        grantApplication.setAppAdminClauseLimitation("gaAppAdminClauseLimitation");
        grantApplication.setAppAdminClauseLimitnWelsh("gaAppAdminClauseLimitnWelsh");
        grantApplication.setAppCaseType("gaAppCaseType");
        grantApplication.setAppExecutorLimitation("gaAppExecutorLimitation");
        grantApplication.setAppExecutorLimitationWelsh("gaAppExecutorLimitationWelsh");
        grantApplication.setAppReceivedDate(LocalDate.of(2019, 1, 2));
        grantApplication.setApplicantAddress("gaApplicantAddress");
        grantApplication.setApplicantDxExchange("gaApplicantDxExchange");
        grantApplication.setApplicantDxNumber("gaApplicantDxNUmber");
        grantApplication.setApplicantForenames("gaApplicantForenames");
        grantApplication.setApplicantHonours("gaApplicantHonours");
        grantApplication.setApplicantSurname("gaApplicantSurname");
        grantApplication.setApplicantTitle("gaApplicantTitle");
        grantApplication.setGrantWelshLanguageInd(false);
        grantApplication.setGrantWillType("gaGrantWillType");
        grantApplication.setGrantWillTypeWelsh("gaGrantWillTypeWelsh");
        grantApplication.setExceptedEstateInd("N");
        grantApplication.setFileslipSignal(false);
        grantApplication.setGrantApplicantType("gaGrantApplicantType");
        grantApplication.setGrantConfirmedDate(LocalDate.of(2019, 2, 2));
        grantApplication.setGrantIssuedDate(LocalDate.of(2019, 3, 3));
        grantApplication.setGrantIssuedSignal(false);
        grantApplication.setGrantLimitation("gaGrantLimiting");
        grantApplication.setGrantLimitationWelsh("gaGrantLimitationWelsh");
        grantApplication.setGrantPowerReserved("gaGrantPiwerReserved");
        grantApplication.setGrantSolId("gaGrantSolId");
        grantApplication.setGrantType("gaGrantType");
        grantApplication.setGrantVersionDate(LocalDate.of(2019, 4, 4));

        grantApplication.setGrantee1Address("gaGrantee1Address");
        grantApplication.setGrantee1Forenames("gaGrantee1Forenames");
        grantApplication.setGrantee1Honours("gaGrantee1Honours");
        grantApplication.setGrantee1Surname("gaGrantee1Surname");
        grantApplication.setGrantee1Title("gaGrantee1Title");

        grantApplication.setGrantee2Address("gaGrantee2Address");
        grantApplication.setGrantee2Forenames("gaGrantee2Forenames");
        grantApplication.setGrantee2Honours("gaGrantee2Honours");
        grantApplication.setGrantee2Surname("gaGrantee2Surname");
        grantApplication.setGrantee2Title("gaGrantee2Title");

        grantApplication.setGrantee3Address("gaGrantee3Address");
        grantApplication.setGrantee3Forenames("gaGrantee3Forenames");
        grantApplication.setGrantee3Honours("gaGrantee3Honours");
        grantApplication.setGrantee3Surname("gaGrantee3Surname");
        grantApplication.setGrantee3Title("gaGrantee3Title");

        grantApplication.setGrantee3Address("gaGrantee3Address");
        grantApplication.setGrantee3Forenames("gaGrantee3Forenames");
        grantApplication.setGrantee3Honours("gaGrantee3Honours");
        grantApplication.setGrantee3Surname("gaGrantee3Surname");
        grantApplication.setGrantee3Title("gaGrantee3Title");

        grantApplication.setGrantee4Address("gaGrantee4Address");
        grantApplication.setGrantee4Forenames("gaGrantee4Forenames");
        grantApplication.setGrantee4Honours("gaGrantee4Honours");
        grantApplication.setGrantee4Surname("gaGrantee4Surname");
        grantApplication.setGrantee4Title("gaGrantee4Title");

        grantApplication.setGrossEstateValue(1000000L);
        grantApplication.setNetEstateValue(900000L);
        grantApplication.setPlaceOfOriginalGrant("gaPlaceOfOriginalGrant");
        grantApplication.setPlaceOfOriginalGrantWelsh("gaPlaceOfOriginalGrantWelsh");
        grantApplication.setPowerReservedWelsh("gaPowerReservedWelsh");
        grantApplication.setResealDate(LocalDate.of(2019, 4, 4));
        grantApplication.setSolicitorReference("gaSolicitorReference");
        return grantApplication;
    }

    private GrantApplicationCreator() {
    }
}
