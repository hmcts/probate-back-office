package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.CodicilDateCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstateConfirmCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.ProfitSharingCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.WillDateCaseExtra;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.GRANT_TYPE_PROBATE;
import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_CONFIRM;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.EN_TO_WELSH;

@Slf4j
@Component
@AllArgsConstructor
public class SolicitorLegalStatementPDFDecorator {
    private final CaseExtraDecorator caseExtraDecorator;
    private final IhtEstateNotCompletedBusinessRule ihtEstateNotCompletedBusinessRule;
    private final LocalDateToWelshStringConverter localDateToWelshStringConverter;

    public String decorate(CaseData caseData) {
        String decoration = "";
        if (ihtEstateNotCompletedBusinessRule.isApplicable(caseData)) {
            IhtEstateConfirmCaseExtra ihtEstateConfirmCaseExtra = IhtEstateConfirmCaseExtra.builder()
                .showIhtEstate(YES)
                .ihtEstateText(IHT_ESTATE_CONFIRM)
                .build();
            decoration = caseExtraDecorator.decorate(ihtEstateConfirmCaseExtra);
            log.info("IHT estate not completed decoration: {}", decoration);
        }
        if (null != caseData.getOriginalWillSignedDate()) {
            String welshWillFormattedDate = localDateToWelshStringConverter
                    .convert(caseData.getOriginalWillSignedDate());
            WillDateCaseExtra willDateCaseExtra = WillDateCaseExtra.builder()
                .showWillDate(YES)
                .originalWillSignedDateWelshFormatted(welshWillFormattedDate)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                    caseExtraDecorator.decorate(willDateCaseExtra));
            log.info("Will signed date decoration: {}", decoration);
        }

        if (null != caseData.getCodicilAddedDateList()) {
            List<CollectionMember<String>> formattedCodicilDates = new ArrayList<>();
            caseData.getCodicilAddedDateList().forEach(date -> {
                String welshCodicilFormattedDate = localDateToWelshStringConverter.convert(date.getValue()
                        .getDateCodicilAdded());
                formattedCodicilDates.add(new CollectionMember<>(welshCodicilFormattedDate));
            });
            log.info("codicil added date list: {}", formattedCodicilDates);
            CodicilDateCaseExtra codicilDateCaseExtra = CodicilDateCaseExtra.builder()
                .showCodicilDate(YES)
                .codicilSignedDateWelshFormatted(formattedCodicilDates)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                    caseExtraDecorator.decorate(codicilDateCaseExtra));
            log.info("Codicil signed date decoration: {}", decoration);
        }

        if (GRANT_TYPE_PROBATE.equals(caseData.getSolsWillType()) && null != caseData.getWhoSharesInCompanyProfits()) {
            ProfitSharingCaseExtra profitSharingCaseExtra = ProfitSharingCaseExtra.builder()
                .welshSingularProfitSharingText(getWelshProfitSharingText(
                        caseData.getWhoSharesInCompanyProfits(), false))
                .welshPluralProfitSharingText(getWelshProfitSharingText(
                        caseData.getWhoSharesInCompanyProfits(), true))
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                    caseExtraDecorator.decorate(profitSharingCaseExtra));
            log.info("Welsh Profit sharing decoration: {}", decoration);
        }
        return decoration;
    }

    private static String getWelshConjunction(String nextWord) {
        // "ac" before a, e, i, o, w, y; "a" otherwise
        char first = Character.toLowerCase(nextWord.charAt(0));
        return "aeiowy".indexOf(first) >= 0 ? "ac" : "a";
    }

    private String getWelshProfitSharingText(List<String> whoShares, boolean plural) {
        log.info("who shares: {}", whoShares);
        if (whoShares == null || whoShares.isEmpty()) {
            return "";
        }

        StringBuilder profitText = new StringBuilder();
        for (int i = 0; i < whoShares.size(); i++) {
            String en = whoShares.get(i).toLowerCase();
            String[] welshForms = EN_TO_WELSH.get(en);
            if (welshForms == null) {
                continue;
            }
            String welsh = plural ? welshForms[1] : welshForms[0];

            if (i > 0) {
                profitText.append(" ");
                profitText.append(getWelshConjunction(welsh));
                profitText.append(" ");
            }
            profitText.append(welsh);
        }
        log.info("Profit sharing decoration: {}", profitText);
        return profitText.toString();
    }
}
