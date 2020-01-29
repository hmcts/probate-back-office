package uk.gov.hmcts.probate.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatCallbackRequest;
import uk.gov.hmcts.probate.model.ccd.caveat.request.CaveatData;

import java.time.DateTimeException;
import java.time.LocalDate;

import static java.lang.Integer.parseInt;

@Slf4j
@Component
public class CaveatDataTransformer {

    public CaveatData transformCaveats(CaveatCallbackRequest callbackRequest) {

        return buildCCDDataCaveats(callbackRequest);
    }

    public CaveatData transformSolsCaveats(CaveatCallbackRequest caveatCallbackRequest) {

        return buildCCDDataSolsCaveats(caveatCallbackRequest);
    }

    private CaveatData buildCCDDataCaveats(CaveatCallbackRequest callbackRequest) {
        CaveatData caseData = callbackRequest.getCaseDetails().getData();

        return CaveatData.builder()
            .caveatorEmailAddress(notNullWrapper(caseData.getCaveatorEmailAddress()))
            .expiryDate(caseData.getExpiryDate())
            .build();
    }

    private CaveatData buildCCDDataSolsCaveats(CaveatCallbackRequest caveatCallbackRequest) {
        CaveatData caveatData = caveatCallbackRequest.getCaseDetails().getData();

        return CaveatData.builder()
                .registryLocation(notNullWrapper(caveatData.getRegistryLocation()))
                .solsSolicitorAppReference(notNullWrapper(caveatData.getSolsSolicitorAppReference()))
                .applicationSubmittedDate(getCaseSubmissionDate(caveatCallbackRequest.getCaseDetails()
                        .getLastModified()))
                .caveatorEmailAddress(notNullWrapper(caveatData.getCaveatorEmailAddress()))
                .solsPaymentMethods(notNullWrapper(caveatData.getSolsPaymentMethods()))
                .solsFeeAccountNumber(notNullWrapper(caveatData.getSolsFeeAccountNumber()))
                .build();
    }

    private String notNullWrapper(String nullableString) {
        return nullableString == null ? "" : nullableString;
    }

    private LocalDate getCaseSubmissionDate(String[] lastModified) {
        try {
            return LocalDate.of(parseInt(lastModified[0]), parseInt(lastModified[1]), parseInt(lastModified[2]));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | DateTimeException | NullPointerException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }


}
