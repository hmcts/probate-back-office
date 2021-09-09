package uk.gov.hmcts.probate.transformer.assembly;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.Categories;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.LimitationSentenceType;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleCaseworker;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleEntitlement;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleForeignDomicile;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleFreeText;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleIHT;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleIncapacity;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleLifeAndMinorityInterest;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleMissingInformation;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSOTIncomplete;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSolicitorAffidavit;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSolicitorCert;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSolicitorGeneral;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleSolicitorRedeclaration;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleWill;
import uk.gov.hmcts.probate.service.docmosis.assembler.AssembleWitness;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode;
import uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.AdmonLife;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.AdmonMinor;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.Caseworker;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntAttorney;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntDeathPa;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntExecNoAcc;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntFamTree;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntLeadingApp;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntNoTitle;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntPrejudiced;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntSubExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntTwoApps;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.EntWrongExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.ForDomAffidavit;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.ForDomInitialEnq;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.FreeText;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT205GrossEstateOver;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT205Miss;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT205NoAssets;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT217Miss;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHT421Await;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IHTIHT400;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapGen;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapInstitutedExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapMedical;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IncapOneExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IntLife;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IntLifeAndMin;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IntMinor;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.IntParental;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoAlias;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoAwaitResponse;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoChangeApp;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoDeathCert;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoDeceased;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoGrantReq;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoRenunWill;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.MissInfoWill;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotNotSigned;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ2;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ3;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ4;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ5;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aQ6;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1aRedec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ2;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ3;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ4;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ5;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ6;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pQ7;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.SotPa1pRedec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillAnyOther;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillFiat;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillList;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillLost;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillPlight;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillRevoked;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillSepPages;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WillStaple;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WitnessConsent;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WitnessDate;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WitnessExecution;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.WitnessSignature;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidAlias;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidAliasInt;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidAlterations;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidDate;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidHandwriting;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidIdentity;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidKnowledge;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidRecital;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsAffidSearch;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertAlias;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertDOB;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertDOD;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertDeceasedAdd;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertDeponentAdd;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertDivorce;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertDivorceDissolve;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertEpaLpa;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertExecName;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertExecNotAcc;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertFirmSucc;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertLifeMinority;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertOtherWill;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertPartnersDod;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertPlight;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertPowerReserved;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertSettledLand;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertSpouse;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertSurvivalExec;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertTrustCorp;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsCertWillSepPages;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsGenAuthorityPartners;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsGenExtendedRenun;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsGenPowerAttorney;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsGenVoid;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecClearing;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecCodicil;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecDomicile;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecIntForDom;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecMinority;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecNetEstate;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecSotDate;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecSotSigned;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.solsRedecTitle;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphCode.soplsRedecWillsForDom;

@Slf4j
@Component
@RequiredArgsConstructor
public class LimitationsTransformer {

    private static final String NEW_LINE = "\n";

    public void setupCombinedLimitationsText(@Valid CaseDetails caseDetails,
                                               ResponseCaseData.ResponseCaseDataBuilder<?, ?> responseCaseDataBuilder) {

        CaseData caseData = caseDetails.getData();
        StringBuilder allText = new StringBuilder();
        for (List<String> limitationGroup : caseData.getLimitations().getAllSelectedLimitations()) {
            for (String limitation : limitationGroup) {
                allText.append(LimitationSentenceType.valueOf(limitation).getLabel());
                allText.append(NEW_LINE);
            }
        }
        responseCaseDataBuilder.selectedLimitationsText(allText.toString());
    }
}
