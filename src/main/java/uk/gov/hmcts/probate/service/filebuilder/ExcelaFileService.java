package uk.gov.hmcts.probate.service.filebuilder;

import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.ScannedDocument;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelaFileService {

    private final TextFileBuilderService textFileBuilderService;
    private static final String DELIMITER = ",";
    private static final String DOC_SUBTYPE = "will";
    private Long id;

    public File createExcelaFile(CaseDetails ccdCase, String fileName) throws IOException {
        this.id = ccdCase.getId();
        return textFileBuilderService.createFile(prepareData(ccdCase.getData()), DELIMITER, fileName, true);
    }

    private List<String> prepareData(CaseData data) {
        ImmutableList.Builder<String> fileData = ImmutableList.builder();

        fileData.add(id.toString());
        fileData.add(data.getDeceasedSurname());
        fileData.add(getWillReferenceNumber(data));

        return fileData.build();
    }

    private String getWillReferenceNumber(CaseData data) {
        for (CollectionMember<ScannedDocument> document : data.getScannedDocuments()) {
            if (document.getValue().getSubtype().equals(DOC_SUBTYPE)) {
                return document.getValue().getControlNumber();
            }
        }
        return Strings.EMPTY;
    }
}