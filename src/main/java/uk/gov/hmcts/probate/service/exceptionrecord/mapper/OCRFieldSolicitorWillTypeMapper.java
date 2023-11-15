package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.model.exceptionrecord.ExceptionRecordOCRFields;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToSolicitorWillType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType;

import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.Constants.GRANT_TYPE_ADMON_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.Constants.GRANT_TYPE_INTESTACY_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.Constants.GRANT_TYPE_PROBATE_VALUE;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.GRANT_TYPE_PROBATE;


@Slf4j
@Component
public class OCRFieldSolicitorWillTypeMapper {

    private static final String GRANT = "GRANT";
    private static final String PROBATE = "PROBATE";
    private static final String ADMON = "ADMON";
    private static final String INTESTACY = "INTESTACY";

    @ToSolicitorWillType
    public SolicitorWillType toSolicitorWillType(ExceptionRecordOCRFields ocrFields) {
        if (ocrFields.getSolsWillType() == null || ocrFields.getSolsWillType().isEmpty()) {
            return null;
        }
        String solsWillType = ocrFields.getSolsWillType().replaceAll("\\s+","").toUpperCase();
        log.info("Beginning mapping for Solicitor Will Type value: {}", solsWillType);

        if (solsWillType.contains(GRANT) || solsWillType.contains(PROBATE)) {
            return GRANT_TYPE_PROBATE;
        } else if (solsWillType.contains(ADMON) || solsWillType.contains(GRANT_TYPE_ADMON_VALUE.toUpperCase())) {
            return GRANT_TYPE_ADMON;
        } else if (solsWillType.contains(INTESTACY)
                || solsWillType.contains(GRANT_TYPE_INTESTACY_VALUE.toUpperCase())) {
            return GRANT_TYPE_INTESTACY;
        } else {
            String errorMessage = "solsWillType: WillLeft/Probate, WillLeftAnnexed/Admon or NoWill/Intestacy "
                + "expected but got '" + solsWillType + "'";
            log.error(errorMessage);
            throw new OCRMappingException(errorMessage);
        }
    }
}
