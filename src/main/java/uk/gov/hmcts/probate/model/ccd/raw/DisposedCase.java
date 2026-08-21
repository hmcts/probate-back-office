package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DisposedCase {
    private String ccdId;
    private String caseData;
    private List<CollectionMember<String>> documentData;
}
