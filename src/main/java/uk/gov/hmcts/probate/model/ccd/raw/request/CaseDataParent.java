package uk.gov.hmcts.probate.model.ccd.raw.request;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplyingPowerReserved;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorPartners;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorTrustCorps;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.DynamicList;
import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Jacksonized
@SuperBuilder
@Data
public class CaseDataParent {

    protected final String schemaVersion;
    protected final String registrySequenceNumber;
    protected final String deceasedDeathCertificate;
    protected final String deceasedDiedEngOrWales;
    protected final String deceasedForeignDeathCertInEnglish;
    protected final String deceasedForeignDeathCertTranslation;
    protected final String solsForenames;
    protected final String solsSurname;
    protected final String solsSolicitorWillSignSOT;
    protected final String dispenseWithNotice;
    protected final String titleAndClearingType;
    protected final String trustCorpName;
    protected SolsAddress trustCorpAddress;
    // Not final so field can be reset in CaseDataTransformer
    protected List<CollectionMember<AdditionalExecutorTrustCorps>> additionalExecutorsTrustCorpList;
    protected final String lodgementAddress;
    protected final LocalDate lodgementDate;
    protected final String nameOfFirmNamedInWill;
    protected SolsAddress addressOfFirmNamedInWill;
    protected final String nameOfSucceededFirm;
    protected SolsAddress addressOfSucceededFirm;
    protected String isSolThePrimaryApplicant;
    protected final String anyOtherApplyingPartners;
    protected final String anyOtherApplyingPartnersTrustCorp;
    protected final String furtherEvidenceForApplication;
    // Not final so field can be reset in CaseDataTransformer
    protected List<CollectionMember<AdditionalExecutorPartners>> otherPartnersApplyingAsExecutors;
    protected final String morePartnersHoldingPowerReserved;
    protected final String probatePractitionersPositionInTrust;
    protected final String dispenseWithNoticeLeaveGiven;
    protected final LocalDate dispenseWithNoticeLeaveGivenDate;
    // Not final as field set in CaseDataTransformer
    protected String dispenseWithNoticeLeaveGivenDateFormatted;
    // Not final as field set in CaseDataTransformer
    protected List<CollectionMember<String>> codicilAddedFormattedDateList;
    protected String originalWillSignedDateFormatted;
    protected final String dispenseWithNoticeOverview;
    protected final String dispenseWithNoticeSupportingDocs;
    // Not final so field can be reset in CaseDataTransformer
    protected List<CollectionMember<AdditionalExecutorNotApplyingPowerReserved>> dispenseWithNoticeOtherExecsList;
    protected final List<String> whoSharesInCompanyProfits;
    protected final String solsIdentifiedApplyingExecs;
    protected final String solsIdentifiedNotApplyingExecs;
    protected final String solsIdentifiedApplyingExecsCcdCopy;
    protected final String solsIdentifiedNotApplyingExecsCcdCopy;
    protected final String iht217;
    protected final String noOriginalWillAccessReason;
    protected final LocalDate originalWillSignedDate;
    protected final List<CollectionMember<CodicilAddedDate>> codicilAddedDateList;

    @Getter
    protected LocalDate authenticatedDate;

    protected String singularProfitSharingTextForLegalStatement;
    protected String pluralProfitSharingTextForLegalStatement;
    private final DynamicList solsPBANumber;
    private final String solsPBAPaymentReference;
    private final String solsOrgHasPBAs;
    private final String solsNeedsPBAPayment;

    private final String reissueDate;

    @Getter(lazy = true)
    private final String reissueDateFormatted = convertDate(reissueDate);

    public String convertDate(LocalDate dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        return convertDate(dateToConvert.toString());
    }

    public String convertDate(String dateToConvert) {
        if (dateToConvert == null || dateToConvert.equals("")) {
            return null;
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMMM yyyy");
        try {
            Date date = originalFormat.parse(dateToConvert);
            String formattedDate = targetFormat.format(date);
            int day = Integer.parseInt(formattedDate.substring(0, 2));
            switch (day) {
                case 1:
                case 21:
                case 31:
                    return day + "st " + formattedDate.substring(3);

                case 2:
                case 22:
                    return day + "nd " + formattedDate.substring(3);

                case 3:
                case 23:
                    return day + "rd " + formattedDate.substring(3);

                default:
                    return day + "th " + formattedDate.substring(3);
            }
        } catch (ParseException ex) {
            ex.getMessage();
            return null;
        }
    }
}
