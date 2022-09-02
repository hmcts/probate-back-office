package uk.gov.hmcts.probate.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.bulkscan.enums.EnvelopeProcessStatus;
import uk.gov.hmcts.bulkscan.type.BulkScanEnvelopeProcessingResponse;
import uk.gov.hmcts.bulkscan.type.IEnvelopeReceiver;
import uk.gov.hmcts.bulkscan.type.ProcessedEnvelopeContents;

import static java.util.Collections.emptyList;

@Component
@Slf4j
public class BulkScanEnvelopeHandler implements IEnvelopeReceiver {

    @Override
    public BulkScanEnvelopeProcessingResponse onEnvelopeReceived(ProcessedEnvelopeContents envelopeContents) {

        // upload the pdfs, attach to a case etc
        log.info("Uploading contents of {}", envelopeContents.envelope().getFileName());
        envelopeContents.zipDetail().pdfFileNames.forEach(p -> {
            log.info("Uploading file {}", p);
            log.info("Attaching file {} to case {}", p, envelopeContents.inputEnvelope().caseNumber);
        });

        envelopeContents.inputEnvelope().payments.forEach(inputPayment -> {
            log.info("Process inputPayment DCN {}", inputPayment.documentControlNumber);
        });

        return new BulkScanEnvelopeProcessingResponse(
                envelopeContents.envelope().getEtag(),
                String.format("Successfully processed Envelope %s", envelopeContents.envelope().getEtag()),
                EnvelopeProcessStatus.SUCCESS,
                emptyList(),
                emptyList()
        );

    }
}
