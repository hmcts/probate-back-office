package uk.gov.hmcts.probate.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.probate.config.notifications.NotificationStop;
import uk.gov.hmcts.probate.config.notifications.StopReasonCode;
import uk.gov.hmcts.probate.model.LanguagePreference;

import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopReasonServiceTest {

    @Mock
    private NotificationStop notificationStop;

    @Mock
    private StopReasonCode stopReasonCodesMock;

    @InjectMocks
    private StopReasonService stopReasonService;


    @Test
    void returnsStopReasonCodeWhenCodeIsNotMapped() {
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodesMock));

        String result = stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "UnknownCode");

        assertEquals("UnknownCode", result);
    }

    @Test
    void throwsExceptionWhenLanguagePreferenceIsNotMapped() {
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.WELSH, stopReasonCodesMock));

        assertThrows(NullPointerException.class, () ->
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "DeceasedAddressMissing"));
    }

    @Test
    void returnsCorrectDescriptionForDeceasedAddressMissing() {
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodesMock));
        when(stopReasonCodesMock.getDeceasedAddressMissing()).thenReturn("Deceased address missing");

        String result = stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "DeceasedAddressMissing");

        assertEquals("Deceased address missing", result);
    }

    @Test
    void returnsMappedDescriptionForDocumentsRequired() {
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodesMock));
        when(stopReasonCodesMock.getDocumentsRequired()).thenReturn("Documents required");

        String result = stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "DocumentsRequired");

        assertEquals("Documents required", result);
    }

    @Test
    void returnsMappedDescriptionForAffidavit() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setAffidavit("Affidavit");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Affidavit", stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "Affidavit"));
    }

    @Test
    void returnsMappedDescriptionForAnyOtherWills() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setAnyOtherWills("Any other wills");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Any other wills", stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "AnyOtherWills"));
    }

    @Test
    void returnsMappedDescriptionForApplicantNameIssue() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setApplicantNameIssue("Applicant Name (missing, spelling etc)");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Applicant Name (missing, spelling etc)",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "ApplicantNameIssue"));
    }

    @Test
    void returnsMappedDescriptionForAwaitingUniqueIHTCode() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setAwaitingUniqueIHTCode("Awaiting unique probate IHT code");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Awaiting unique probate IHT code",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "AwaitingUniqueIHTCode"));
    }

    @Test
    void returnsMappedDescriptionForCaveatMatch() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setCaveatMatch("Caveat match");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Caveat match", stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "CaveatMatch"));
    }

    @Test
    void returnsMappedDescriptionForDatesDiffer() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setDatesDiffer("Dates differ (will, Birth, Death)");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Dates differ (will, Birth, Death)",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "DatesDiffer"));
    }

    @Test
    void returnsMappedDescriptionForDeathCertIssue() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setDeathCertIssue("Death Cert (missing, not original, unreadable)");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Death Cert (missing, not original, unreadable)",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                "DeathCertIssue"));
    }

    @Test
    void returnsMappedDescriptionForDeceasedName() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setDeceasedName("Deceased Name");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Deceased Name",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "DeceasedName"));
    }

    @Test
    void returnsMappedDescriptionForDuplicateEmailAddresses() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setDuplicateEmailAddresses("Duplicate email addresses");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Duplicate email addresses",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "DuplicateEmailAddresses"));
    }

    @Test
    void returnsMappedDescriptionForExecNotAccountedFor() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setExecNotAccountedFor("Executor not accounted for");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Executor not accounted for",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "ExecNotAccountedFor"));
    }

    @Test
    void returnsMappedDescriptionForFee() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setFee("Fee");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Fee",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "Fee"));
    }

    @Test
    void returnsMappedDescriptionForForeignDomicile() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setForeignDomicile("Foreign Domicile");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Foreign Domicile",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "ForeignDomicile"));
    }

    @Test
    void returnsMappedDescriptionForIht421Awaiting() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setIht421Awaiting("IHT 421 awaiting");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("IHT 421 awaiting",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "IHT421Awaiting"));
    }

    @Test
    void returnsMappedDescriptionForIhtFiguresIncorrect() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setIhtFiguresIncorrect("IHT figures incorrect");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("IHT figures incorrect",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "IHTFiguresIncorrect"));
    }

    @Test
    void returnsMappedDescriptionForIhtIssue() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setIhtIssue("IHT forms not received");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("IHT forms not received",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "IHTIssue"));
    }

    @Test
    void returnsMappedDescriptionForLostWill() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setLostWill("Lost Will");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Lost Will",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "LostWill"));
    }

    @Test
    void returnsMappedDescriptionForMatchingApplication() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        StringBuilder description = new StringBuilder();
        description.append("Matching application – (informing Sol or citizen that there is a");
        description.append(" matching application so their case cannot proceed)");
        stopReasonCodes.setMatchingApplication(description.toString());
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals(description.toString(),
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "MatchingApplication"));
    }

    @Test
    void returnsMappedDescriptionForNoMinorityLifen() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        StringBuilder description = new StringBuilder();
        description.append("No minority or life interest – we ask solicitors to certify");
        description.append(" this if missing on the Legal Statement");
        stopReasonCodes.setNoMinorityLife(description.toString());
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals(description.toString(),
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "NoMinorityLife"));
    }

    @Test
    void returnsMappedDescriptionForNotEligible() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setNotEligible("Not eligible for digital process");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Not eligible for digital process",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "NotEligible"));
    }

    @Test
    void returnsMappedDescriptionForNotEntitled() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setNotEntitled("Not entitled to make application");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Not entitled to make application",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "NotEntitled"));
    }

    @Test
    void returnsMappedDescriptionForOriginalDocsRequired() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setOriginalDocsRequired("Original Docs required");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Original Docs required",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "OriginalDocsRequired"));
    }

    @Test
    void returnsMappedDescriptionForWillNotEnclosed() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setWillNotEnclosed("Original will not enclosed");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Original will not enclosed",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "WillNotEnclosed"));
    }

    @Test
    void returnsMappedDescriptionForPermanentCaveat() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPermanentCaveat("Permanent Caveat");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Permanent Caveat",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PermanentCaveat"));
    }

    @Test
    void returnsMappedDescriptionForProbateFiguresIncorrect() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setProbateFiguresIncorrect("Probate figures incorrect");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Probate figures incorrect",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "ProbateFiguresIncorrect"));
    }

    @Test
    void returnsMappedDescriptionForRedeclareTitleClearing() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        StringBuilder description = new StringBuilder();
        description.append("Re-declare Legal Statement due to Title/Clearing –");
        description.append(" this would only be on Solicitor applications");
        stopReasonCodes.setRedeclareTitleClearing(description.toString());
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals(description.toString(),
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "RedeclareTitleClearing"));
    }

    @Test
    void returnsMappedDescriptionForSolicitorsLegalStatementNotSigned() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setSolicitorsLegalStatementNotSigned("Solicitors Legal Statement Not Signed");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Solicitors Legal Statement Not Signed",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "SolicitorsLegalStatementNotSigned"));
    }

    @Test
    void returnsMappedDescriptionForTrustCorp() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setTrustCorp("Trust Corp");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Trust Corp",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "TrustCorp"));
    }

    @Test
    void returnsMappedDescriptionForWillCondition() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setWillCondition("Will (condition)");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Will (condition)",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "WillCondition"));
    }

    @Test
    void returnsMappedDescriptionForWillContent() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setWillContent("Will (content, inc not signed)");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Will (content, inc not signed)",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "WillContent"));
    }

    @Test
    void returnsMappedDescriptionForWelshTranslationRequired() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setWelshTranslationRequired("Welsh Translation Required");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Welsh Translation Required",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "WelshTranslationRequired"));
    }

    @Test
    void returnsMappedDescriptionForOther() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setOther("*** NOT TO BE USED (Other) ***");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("*** NOT TO BE USED (Other) ***",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "Other"));
    }

    @Test
    void returnsMappedDescriptionForAffidavits() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setAffidavits("Affidavits");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Affidavits",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "Affidavits"));
    }

    @Test
    void returnsMappedDescriptionForCapA5C() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setCapA5C("CAP A5C");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("CAP A5C",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "CAPA5C"));
    }

    @Test
    void returnsMappedDescriptionForDecreeAbsolute() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setDecreeAbsolute("Decree Absolute");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Decree Absolute",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "DecreeAbsolute"));
    }

    @Test
    void returnsMappedDescriptionForGrants() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setGrants("Grants");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Grants",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "Grants"));
    }

    @Test
    void returnsMappedDescriptionForIht205() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setIht205("IHT 205");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("IHT 205",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "IHT205"));
    }

    @Test
    void returnsMappedDescriptionForIht207() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setIht207("IHT 207");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("IHT 207",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "IHT207"));
    }

    @Test
    void returnsMappedDescriptionForPA11() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPa11("PA11");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("PA11",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PA11"));
    }

    @Test
    void returnsMappedDescriptionForPA12() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPa12("PA12");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("PA12",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PA12"));
    }

    @Test
    void returnsMappedDescriptionForPA13() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPa13("PA13");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("PA13",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PA13"));
    }

    @Test
    void returnsMappedDescriptionForPA14() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPa14("PA14");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("PA14",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PA14"));
    }

    @Test
    void returnsMappedDescriptionForPA15() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPa15("PA15");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("PA15",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PA15"));
    }

    @Test
    void returnsMappedDescriptionForPA16() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPa16("PA16");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("PA16",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PA16"));
    }

    @Test
    void returnsMappedDescriptionForPA17() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPa17("PA17");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("PA17",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PA17"));
    }

    @Test
    void returnsMappedDescriptionForPA19() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPa19("PA19");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("PA19",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PA19"));
    }

    @Test
    void returnsMappedDescriptionForPowerOfAttorney() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setPowerOfAttorney("Power of attorney");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Power of attorney",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "PowerOfAttorney"));
    }

    @Test
    void returnsMappedDescriptionForRenunciation() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setRenunciation("Renunciation");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Renunciation",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "Renunciation"));
    }

    @Test
    void returnsMappedDescriptionForResolutions() {
        StopReasonCode stopReasonCodes = new StopReasonCode();
        stopReasonCodes.setResolutions("Resolutions");
        when(notificationStop.getReasons()).thenReturn(Map.of(LanguagePreference.ENGLISH, stopReasonCodes));

        assertEquals("Resolutions",
                stopReasonService.getStopReasonDescription(LanguagePreference.ENGLISH,
                        "Resolutions"));
    }
}