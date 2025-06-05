package uk.gov.hmcts.probate.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.config.notifications.NotificationStop;
import uk.gov.hmcts.probate.config.notifications.StopReasonCode;
import uk.gov.hmcts.probate.model.LanguagePreference;


@Slf4j
@RequiredArgsConstructor
@Service
public class StopReasonService {

    private final NotificationStop notificationStop;

    public String getStopReasonDescription(LanguagePreference languagePreference, String stopReasonCode) {
        StopReasonCode stopReasonCodes = notificationStop.getReasons().get(languagePreference);
        return switch (stopReasonCode) {
            case "DeceasedAddressMissing" -> stopReasonCodes.getDeceasedAddressMissing();
            case "Affidavit" -> stopReasonCodes.getAffidavit();
            case "AnyOtherWills" -> stopReasonCodes.getAnyOtherWills();
            case "ApplicantNameIssue" -> stopReasonCodes.getApplicantNameIssue();
            case "AwaitingUniqueIHTCode" -> stopReasonCodes.getAwaitingUniqueIHTCode();
            case "CaveatMatch" -> stopReasonCodes.getCaveatMatch();
            case "DatesDiffer" -> stopReasonCodes.getDatesDiffer();
            case "DeathCertIssue" -> stopReasonCodes.getDeathCertIssue();
            case "DeceasedName" -> stopReasonCodes.getDeceasedName();
            case "DocumentsRequired" -> stopReasonCodes.getDocumentsRequired();
            case "DuplicateEmailAddresses" -> stopReasonCodes.getDuplicateEmailAddresses();
            case "ExecNotAccountedFor" -> stopReasonCodes.getExecNotAccountedFor();
            case "Fee" -> stopReasonCodes.getFee();
            case "ForeignDomicile" -> stopReasonCodes.getForeignDomicile();
            case "IHT421Awaiting" -> stopReasonCodes.getIht421Awaiting();
            case "IHTFiguresIncorrect" -> stopReasonCodes.getIhtFiguresIncorrect();
            case "IHTIssue" -> stopReasonCodes.getIhtIssue();
            case "LostWill" -> stopReasonCodes.getLostWill();
            case "MatchingApplication" -> stopReasonCodes.getMatchingApplication();
            case "NoMinorityLife" -> stopReasonCodes.getNoMinorityLife();
            case "NotEligible" -> stopReasonCodes.getNotEligible();
            case "NotEntitled" -> stopReasonCodes.getNotEntitled();
            case "OriginalDocsRequired" -> stopReasonCodes.getOriginalDocsRequired();
            case "WillNotEnclosed" -> stopReasonCodes.getWillNotEnclosed();
            case "PermanentCaveat" -> stopReasonCodes.getPermanentCaveat();
            case "ProbateFiguresIncorrect" -> stopReasonCodes.getProbateFiguresIncorrect();
            case "RedeclareTitleClearing" -> stopReasonCodes.getRedeclareTitleClearing();
            case "SolicitorsLegalStatementNotSigned" -> stopReasonCodes.getSolicitorsLegalStatementNotSigned();
            case "TrustCorp" -> stopReasonCodes.getTrustCorp();
            case "WillCondition" -> stopReasonCodes.getWillCondition();
            case "WillContent" -> stopReasonCodes.getWillContent();
            case "WelshTranslationRequired" -> stopReasonCodes.getWelshTranslationRequired();
            case "Other" -> stopReasonCodes.getOther();
            case "Affidavits" -> stopReasonCodes.getAffidavits();
            case "CAPA5C" -> stopReasonCodes.getCapA5C();
            case "DecreeAbsolute" -> stopReasonCodes.getDecreeAbsolute();
            case "Grants" -> stopReasonCodes.getGrants();
            case "IHT205" -> stopReasonCodes.getIht205();
            case "IHT207" -> stopReasonCodes.getIht207();
            case "PA11" -> stopReasonCodes.getPa11();
            case "PA12" -> stopReasonCodes.getPa12();
            case "PA13" -> stopReasonCodes.getPa13();
            case "PA14" -> stopReasonCodes.getPa14();
            case "PA15" -> stopReasonCodes.getPa15();
            case "PA16" -> stopReasonCodes.getPa16();
            case "PA17" -> stopReasonCodes.getPa17();
            case "PA19" -> stopReasonCodes.getPa19();
            case "PowerOfAttorney" -> stopReasonCodes.getPowerOfAttorney();
            case "Renunciation" -> stopReasonCodes.getRenunciation();
            case "Resolutions" -> stopReasonCodes.getResolutions();
            default -> stopReasonCode;
        };
    }
}




