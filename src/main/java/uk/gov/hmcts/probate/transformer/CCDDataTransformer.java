package uk.gov.hmcts.probate.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.DocumentType;
import uk.gov.hmcts.probate.model.ccd.CCDData;
import uk.gov.hmcts.probate.model.ccd.Deceased;
import uk.gov.hmcts.probate.model.ccd.Executor;
import uk.gov.hmcts.probate.model.ccd.Fee;
import uk.gov.hmcts.probate.model.ccd.InheritanceTax;
import uk.gov.hmcts.probate.model.ccd.Solicitor;
import uk.gov.hmcts.probate.model.ccd.raw.CodicilAddedDate;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.UploadDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CallbackRequest;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
public class CCDDataTransformer {

    public CCDData transform(CallbackRequest callbackRequest) {

        return buildCCDData(callbackRequest);
    }

    private CCDData buildCCDData(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        return CCDData.builder()
            .caseId(callbackRequest.getCaseDetails().getId())
            .solicitorReference(notNullWrapper(caseData.getSolsSolicitorAppReference()))
            .caseSubmissionDate(getCaseSubmissionDate(callbackRequest.getCaseDetails().getLastModified()))
            .solsWillType(callbackRequest.getCaseDetails().getData().getSolsWillType())
            .solsSolicitorIsExec(callbackRequest.getCaseDetails().getData().getSolsSolicitorIsExec())
            .solsSolicitorIsApplying(callbackRequest.getCaseDetails().getData().getSolsSolicitorIsApplying())
            .solsSolicitorNotApplyingReason(
                callbackRequest.getCaseDetails().getData().getSolsSolicitorNotApplyingReason())
            .solicitor(buildSolicitorDetails(caseData))
            .deceased(buildDeceasedDetails(caseData))
            .iht(buildInheritanceTaxDetails(caseData))
            .fee(buildFeeDetails(caseData))
            .solsAdditionalInfo(caseData.getSolsAdditionalInfo())
            .executors(getAllExecutors(caseData))
            .boExaminationChecklistQ1(notNullWrapper(caseData.getBoExaminationChecklistQ1()))
            .boExaminationChecklistQ2(notNullWrapper(caseData.getBoExaminationChecklistQ2()))
            .willHasCodicils(caseData.getWillHasCodicils())
            .iht217(caseData.getIht217())
            .hasUploadedLegalStatement(determineHasUploadedLegalStatement(caseData))
            .originalWillSignedDate(caseData.getOriginalWillSignedDate())
            .codicilAddedDateList(getCodicilAddedDates(caseData))
            .deceasedDateOfDeath(caseData.getDeceasedDateOfDeath())
            .solsCoversheetDocument(caseData.getSolsCoversheetDocument())
            .build();
    }

    private boolean determineHasUploadedLegalStatement(CaseData data) {
        if (data.getBoDocumentsUploaded() != null) {
            for (CollectionMember<UploadDocument> uploadDocument : data.getBoDocumentsUploaded()) {
                if (uploadDocument.getValue().getDocumentType().equals(DocumentType.UPLOADED_LEGAL_STATEMENT)) {
                    return true;
                }
            }
        }
        return false;
    }

    public CCDData transformEmail(CallbackRequest callbackRequest) {

        return buildEmailCCDData(callbackRequest);
    }

    private CCDData buildEmailCCDData(CallbackRequest callbackRequest) {
        CaseData caseData = callbackRequest.getCaseDetails().getData();

        return CCDData.builder()
            .solsSolicitorEmail(notNullWrapper(caseData.getSolsSolicitorEmail()))
            .primaryApplicantEmailAddress(notNullWrapper(caseData.getPrimaryApplicantEmailAddress()))
            .applicationType(notNullWrapper(caseData.getApplicationType().toString()))

            .build();
    }

    private Solicitor buildSolicitorDetails(CaseData caseData) {
        return Solicitor.builder()
            .firmName(caseData.getSolsSolicitorFirmName())
            .firmAddress(caseData.getSolsSolicitorAddress())
            .fullname(caseData.getSolsSOTName())
            .jobRole(caseData.getSolsSOTJobTitle())
            .build();
    }

    private Deceased buildDeceasedDetails(CaseData caseData) {
        return Deceased.builder()
            .firstname(caseData.getDeceasedForenames())
            .lastname(caseData.getDeceasedSurname())
            .dateOfBirth((caseData.getDeceasedDateOfBirth()))
            .dateOfDeath((caseData.getDeceasedDateOfDeath()))
            .address(caseData.getDeceasedAddress())
            .build();
    }

    private InheritanceTax buildInheritanceTaxDetails(CaseData caseData) {
        return InheritanceTax.builder()
            .formName(caseData.getIhtFormId())
            .netValue(caseData.getIhtNetValue())
            .grossValue(caseData.getIhtGrossValue())
            .build();
    }

    private Fee buildFeeDetails(CaseData caseData) {
        return Fee.builder()
            .extraCopiesOfGrant(caseData.getExtraCopiesOfGrant())
            .outsideUKGrantCopies(caseData.getOutsideUKGrantCopies())
            .paymentMethod(caseData.getSolsPaymentMethods())
            .solsPBANumber(caseData.getSolsPBANumber() == null 
                || caseData.getSolsPBANumber().getValue() == null ? null :
                caseData.getSolsPBANumber().getValue().getCode())
            .solsPBAPaymentReference(caseData.getSolsPBAPaymentReference())
            .applicationFee(caseData.getApplicationFee())
            .amount(caseData.getTotalFee())
            .feeForUkCopies(caseData.getFeeForUkCopies())
            .feeForNonUkCopies(caseData.getFeeForNonUkCopies())
            .build();
    }

    private List<CodicilAddedDate> getCodicilAddedDates(CaseData caseData) {
        final List<CollectionMember<CodicilAddedDate>> codicilDates = caseData.getCodicilAddedDateList();
        if (codicilDates == null) {
            return new ArrayList<>();
        }
        List<CodicilAddedDate> addedDates = new ArrayList<>();
        addedDates.addAll(
            codicilDates.stream()
            .map(CollectionMember::getValue)
            .collect(Collectors.toList()));
        return addedDates;
    }

    private List<Executor> getAllExecutors(CaseData caseData) {
        List<Executor> executors = new ArrayList<>();
        if (caseData.getSolsAdditionalExecutorList() != null) {
            executors.addAll(caseData.getSolsAdditionalExecutorList().stream()
                .map(CollectionMember::getValue)
                .map(executor -> Executor.builder()
                    .applying(YES.equals(executor.getAdditionalApplying()))
                    .address(executor.getAdditionalExecAddress())
                    .reasonNotApplying(executor.getAdditionalExecReasonNotApplying())
                    .forename(executor.getAdditionalExecForenames())
                    .lastname(executor.getAdditionalExecLastname())
                    .build())
                .collect(Collectors.toList()));
        }

        if (caseData.getAdditionalExecutorsTrustCorpList() != null) {
            executors.addAll(caseData.getAdditionalExecutorsTrustCorpList().stream()
                .map(CollectionMember::getValue)
                .map(executor -> Executor.builder()
                    .applying(true)
                    .address(caseData.getTrustCorpAddress())
                    .reasonNotApplying(null)
                    .forename(executor.getAdditionalExecForenames())
                    .lastname(executor.getAdditionalExecLastname())
                    .build())
                .collect(Collectors.toList()));
        }

        if (caseData.getOtherPartnersApplyingAsExecutors() != null) {
            executors.addAll(caseData.getOtherPartnersApplyingAsExecutors().stream()
                .map(CollectionMember::getValue)
                .map(executor -> Executor.builder()
                    .applying(true)
                    .address(executor.getAdditionalExecAddress())
                    .reasonNotApplying(null)
                    .forename(executor.getAdditionalExecForenames())
                    .lastname(executor.getAdditionalExecLastname())
                    .build())
                .collect(Collectors.toList()));
        }

        if (caseData.getPrimaryApplicantForenames() != null) {
            executors.add(Executor.builder()
                    .applying(caseData.isPrimaryApplicantApplying())
                    .address(caseData.getPrimaryApplicantAddress())
                    .reasonNotApplying(caseData.getSolsPrimaryExecutorNotApplyingReason())
                    .forename(caseData.getPrimaryApplicantForenames())
                    .lastname(caseData.getPrimaryApplicantSurname())
                    .build());
        }

        return executors;
    }

    private LocalDate getCaseSubmissionDate(String[] lastModified) {
        try {
            return LocalDate.of(parseInt(lastModified[0]), parseInt(lastModified[1]), parseInt(lastModified[2]));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | DateTimeException | NullPointerException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    public CCDData transformBulkPrint(String letterId) {

        return buildCCDDataBulkPrint(letterId);
    }

    private CCDData buildCCDDataBulkPrint(String letterId) {

        return CCDData.builder().sendLetterId(notNullWrapper(letterId))
            .build();
    }

    private String notNullWrapper(String nullableString) {
        return nullableString == null ? "" : nullableString;
    }
}
