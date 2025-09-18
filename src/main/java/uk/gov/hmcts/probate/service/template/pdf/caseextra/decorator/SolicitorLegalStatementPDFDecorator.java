package uk.gov.hmcts.probate.service.template.pdf.caseextra.decorator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.businessrule.IhtEstateNotCompletedBusinessRule;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.service.template.pdf.LocalDateToWelshStringConverter;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.CodicilDateCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.IhtEstateConfirmCaseExtra;
import uk.gov.hmcts.probate.service.template.pdf.caseextra.WillDateCaseExtra;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.probate.model.Constants.IHT_ESTATE_CONFIRM;
import static uk.gov.hmcts.probate.model.Constants.YES;

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
        }
        if (caseData.getOriginalWillSignedDate() != null) {
            String welshWillFormattedDate = localDateToWelshStringConverter.convert(caseData.getOriginalWillSignedDate());
            WillDateCaseExtra willDateCaseExtra = WillDateCaseExtra.builder()
                .showWillDate(YES)
                .originalWillSignedDateWelshFormatted(welshWillFormattedDate)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                    caseExtraDecorator.decorate(willDateCaseExtra));
        }

        if (caseData.getCodicilAddedDateList() != null) {
            List<CollectionMember<String>> formattedCodicilDates = new ArrayList<>();
            caseData.getCodicilAddedDateList().forEach(date -> {
                String welshCodicilFormattedDate = localDateToWelshStringConverter.convert(date.getValue()
                        .getDateCodicilAdded());
                formattedCodicilDates.add(new CollectionMember<>(welshCodicilFormattedDate));
            });
            CodicilDateCaseExtra codicilDateCaseExtra = CodicilDateCaseExtra.builder()
                .showCodicilDate(YES)
                .codicilSignedDateWelshFormatted(formattedCodicilDates)
                .build();
            decoration = caseExtraDecorator.combineDecorations(decoration,
                    caseExtraDecorator.decorate(codicilDateCaseExtra));
        }
        return decoration;
    }
}
