package uk.gov.hmcts.probate.service.template.pdf.caseextra;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class CodicilDateCaseExtra {

    private final String showCodicilDate;
    private final List<CollectionMember<String>> codicilSignedDateWelshFormatted;

}
