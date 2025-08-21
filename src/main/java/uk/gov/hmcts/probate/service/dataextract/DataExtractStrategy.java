package uk.gov.hmcts.probate.service.dataextract;

import uk.gov.hmcts.probate.model.DataExtractType;
import uk.gov.hmcts.probate.model.ccd.raw.request.ReturnedCaseDetails;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DataExtractStrategy {

    boolean matchesType(DataExtractType type);

    File generateZipFile(List<ReturnedCaseDetails> cases, String date) throws IOException;

    void uploadToBlobStorage(File file) throws IOException;

    DataExtractType getType();
}
