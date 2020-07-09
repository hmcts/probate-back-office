package uk.gov.hmcts.probate.service.docmosis.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.ParagraphDetail;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.MISS_INFO_DECEASED_COUNTRY;
import static uk.gov.hmcts.probate.service.docmosis.assembler.ParagraphField.MISS_INFO_DECEASED_DATE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssembleMissingInformation {
    private static final String CONDITIONS_WILL = "Will / Codicil";
    private static final String CONDITIONS_DEATH_CERT = "The one supplied was unclear / One was not supplied";
    private final AssemblerBase assemblerBase;

    public List<ParagraphDetail> missingInfoFee(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> missingInfoWill(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(CONDITIONS_WILL));
    }

    public List<ParagraphDetail> missingInfoDeathCert(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(CONDITIONS_DEATH_CERT));
    }

    public List<ParagraphDetail> missingInfoChangeOfApplicant(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getTextParagraphDetailWithDefaultValue(paragraphCode, Arrays.asList(caseData.getPrimaryApplicantFullName()));
    }

    public List<ParagraphDetail> missingInfoDateOfRequest(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getDateParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> missingInfoAlias(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> missingInfoRenunWill(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> missingInfoGrantReq(ParagraphCode paragraphCode, CaseData caseData) {
        return assemblerBase.getStaticParagraphDetails(paragraphCode);
    }

    public List<ParagraphDetail> missingInfoDeceased(ParagraphCode paragraphCode, CaseData caseData) {
        List<ParagraphDetail> paragraphDetails = new ArrayList<>();
        for (ParagraphField paragraphField : paragraphCode.getParagraphFields()) {
            if (MISS_INFO_DECEASED_COUNTRY.name().equals(paragraphField.name())) {
                paragraphDetails.add(assemblerBase.getSingleTextParagraphDetails(paragraphField, paragraphCode.getTemplateName()));
            } else if (MISS_INFO_DECEASED_DATE.name().equals(paragraphField.name())) {
                paragraphDetails.add(assemblerBase.getSingleDateParagraphDetails(paragraphField, paragraphCode.getTemplateName()));
            }
        }
        return paragraphDetails;
    }

}
