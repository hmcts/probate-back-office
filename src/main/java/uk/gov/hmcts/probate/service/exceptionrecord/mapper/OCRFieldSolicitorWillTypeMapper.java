package uk.gov.hmcts.probate.service.exceptionrecord.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.OCRMappingException;
import uk.gov.hmcts.probate.service.exceptionrecord.mapper.qualifiers.ToSolicitorWillType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.GrantType;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType;

import java.util.LinkedHashMap;
import java.util.Map;

import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.GRANT_TYPE_ADMON;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.GRANT_TYPE_INTESTACY;
import static uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.SolicitorWillType.GRANT_TYPE_PROBATE;


@Slf4j
@Component
public class OCRFieldSolicitorWillTypeMapper {

    private static final LinkedHashMap<String, SolicitorWillType> willTypeMap = new LinkedHashMap<>();
    static {
        willTypeMap.put("ADMON", GRANT_TYPE_ADMON);
        willTypeMap.put("ANNEXED", GRANT_TYPE_ADMON);
        willTypeMap.put("PROBATE", GRANT_TYPE_PROBATE);
        willTypeMap.put("ADMINISTRAT", GRANT_TYPE_ADMON);
        willTypeMap.put("GRANT", GRANT_TYPE_PROBATE);
    }

    @ToSolicitorWillType
    public static SolicitorWillType toSolicitorWillType(String ocrSolsWillType, GrantType grantType) {
        if (ocrSolsWillType == null || ocrSolsWillType.isEmpty()) {
            return null;
        }
        if (GrantType.INTESTACY.equals(grantType)) {
            log.info("Form type: PA1A, map solicitor will type to Intestacy");
            return GRANT_TYPE_INTESTACY;
        }

        String solsWillType = ocrSolsWillType.replaceAll("\\s+","").toUpperCase();
        log.info("Beginning mapping for Solicitor Will Type value: {}", solsWillType);


        for (Map.Entry<String, SolicitorWillType> entry : willTypeMap.entrySet()) {
            if (solsWillType.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        String errorMessage = "solsWillType: Probate, Admon or Intestacy expected but got '" + solsWillType + "'";
        log.error(errorMessage);
        throw new OCRMappingException(errorMessage);
    }
}
