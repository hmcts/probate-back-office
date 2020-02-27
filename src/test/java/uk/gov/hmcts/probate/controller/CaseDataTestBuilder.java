package uk.gov.hmcts.probate.controller;

import uk.gov.hmcts.probate.model.ccd.raw.SolsAddress;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CaseDataTestBuilder {

    public static final LocalDate DOB = LocalDate.of(1990, 4, 4);
    public static final LocalDate DOD = LocalDate.of(2017, 4, 4);
    public static final Long ID = 1L;
    public static final String[] LAST_MODIFIED = {"2018", "1", "1", "0", "0", "0", "0"};
    public static final String FORNAME = "Andy";
    public static final String SURANME = "Michael";
    public static final String SOLICITOR_APP_REFERENCE = "Reff";
    public static final String SOLICITOR_FIRM_NAME = "Legal Service Ltd";
    public static final String SOLICITOR_FIRM_LINE1 = "Sols Add Line1";
    public static final String SOLICITOR_FIRM_POSTCODE = "SW1E 6EA";
    public static final String IHT_FORM = "IHT207";
    public static final String SOLICITOR_NAME = "Peter Crouch";
    public static final String SOLICITOR_JOB_TITLE = "Lawyer";
    public static final String PAYMENT_METHOD = "Cheque";
    public static final String WILL_HAS_CODICLIS = "Yes";
    public static final String NUMBER_OF_CODICLIS = "1";
    public static final BigDecimal NET = BigDecimal.valueOf(1000f);
    public static final BigDecimal GROSS = BigDecimal.valueOf(900f);
    public static final Long EXTRA_UK = 1L;
    public static final Long EXTRA_OUTSIDE_UK = 2L;
    public static final String DECEASED_ADDRESS_L1 = "DECL1";
    public static final String DECEASED_ADDRESS_PC = "DECPC";
    public static final SolsAddress DECEASED_ADDRESS = SolsAddress.builder().addressLine1(DECEASED_ADDRESS_L1)
            .postCode(DECEASED_ADDRESS_PC).build();
    public static final String PRIMARY_ADDRESS_L1 = "PRML1";
    public static final String PRIMARY_ADDRESS_PC = "PRMPC";
    public static final SolsAddress PRIMARY_ADDRESS = SolsAddress.builder().addressLine1(PRIMARY_ADDRESS_L1)
            .postCode(PRIMARY_ADDRESS_PC).build();
    public static final String PRIMARY_APPLICANT_APPLYING = "Yes";
    public static final String PRIMARY_APPLICANT_HAS_ALIAS = "No";
    public static final String PRIMARY_APPLICANT_EMAIL_ADDRESS = "test@test.com";
    public static final String OTHER_EXEC_EXISTS = "No";
    public static final String WILL_EXISTS = "Yes";
    public static final String WILL_TYPE = "WillLeft";
    public static final String WILL_ACCESS_ORIGINAL = "Yes";
    public static final String PRIMARY_FORENAMES = "ExFN";
    public static final String PRIMARY_SURNAME = "ExSN";
    public static final String DECEASED_OTHER_NAMES = "No";
    public static final String DECEASED_DOM_UK = "Yes";
    public static final String SOT_NEED_TO_UPDATE = "Yes";

    public static final BigDecimal APPLICATION_FEE = BigDecimal.TEN;
    public static final BigDecimal FEE_FOR_UK_COPIES = BigDecimal.TEN;
    public static final BigDecimal FEE_FOR_NON_UK_COPIES = BigDecimal.TEN;
    public static final BigDecimal TOTAL_FEE = BigDecimal.TEN;
    public static final String NEED_TO_UPDATE = "No";

    public static CaseData.CaseDataBuilder withDefaults() {

        SolsAddress solsAddress = getSolsAddress();

        return getCaseDataBuilder(solsAddress, PRIMARY_APPLICANT_EMAIL_ADDRESS);
    }

    private static SolsAddress getSolsAddress() {
        return SolsAddress.builder()
                .addressLine1(SOLICITOR_FIRM_LINE1)
                .postCode(SOLICITOR_FIRM_POSTCODE)
                .build();
    }

    public static CaseData.CaseDataBuilder withDefaultsAndNoPrimaryApplicantEmailAddress() {

        SolsAddress solsAddress = getSolsAddress();

        return getCaseDataBuilder(solsAddress, null);
    }

    private static CaseData.CaseDataBuilder getCaseDataBuilder(SolsAddress solsAddress, String primaryApplicantEmailAddress) {
        return CaseData.builder()
                .solsSolicitorFirmName(SOLICITOR_FIRM_NAME)
                .solsSolicitorAddress(solsAddress)
                .solsSolicitorAppReference(SOLICITOR_APP_REFERENCE)
                .deceasedDateOfBirth(DOB)
                .deceasedDateOfDeath(DOD)
                .deceasedForenames(FORNAME)
                .deceasedSurname(SURANME)
                .deceasedAddress(DECEASED_ADDRESS)
                .deceasedAnyOtherNames(DECEASED_OTHER_NAMES)
                .deceasedDomicileInEngWales(DECEASED_DOM_UK)
                .primaryApplicantForenames(PRIMARY_FORENAMES)
                .primaryApplicantSurname(PRIMARY_SURNAME)
                .primaryApplicantAddress(PRIMARY_ADDRESS)
                .primaryApplicantIsApplying(PRIMARY_APPLICANT_APPLYING)
                .primaryApplicantHasAlias(PRIMARY_APPLICANT_HAS_ALIAS)
                .primaryApplicantEmailAddress(primaryApplicantEmailAddress)
                .otherExecutorExists(OTHER_EXEC_EXISTS)
                .solsWillType(WILL_TYPE)
                .willExists(WILL_EXISTS)
                .willAccessOriginal(WILL_ACCESS_ORIGINAL)
                .ihtNetValue(NET)
                .ihtGrossValue(GROSS)
                .solsSOTNeedToUpdate(SOT_NEED_TO_UPDATE)
                .willHasCodicils(WILL_HAS_CODICLIS)
                .willNumberOfCodicils(NUMBER_OF_CODICLIS)
                .ihtFormId(IHT_FORM)
                .solsSOTNeedToUpdate(NEED_TO_UPDATE)
                .solsSOTName(SOLICITOR_NAME)
                .solsSOTJobTitle(SOLICITOR_JOB_TITLE)
                .solsPaymentMethods(PAYMENT_METHOD)
                .applicationFee(APPLICATION_FEE)
                .feeForUkCopies(FEE_FOR_UK_COPIES)
                .feeForNonUkCopies(FEE_FOR_NON_UK_COPIES)
                .extraCopiesOfGrant(EXTRA_UK)
                .outsideUKGrantCopies(EXTRA_OUTSIDE_UK)
                .totalFee(TOTAL_FEE);
    }

}
