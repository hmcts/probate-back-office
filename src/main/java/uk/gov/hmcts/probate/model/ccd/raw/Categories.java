package uk.gov.hmcts.probate.model.ccd.raw;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
@Builder
public class Categories {

    private final List<String> selectedCategories;
    private final List<String> entSelectedParagraphs;
    private final List<String> ihtSelectedParagraphs;
    private final List<String> missInfoSelectedParagraphs;
    private final List<String> willSelectedParagraphs;

    private List<CollectionMember<ParagraphDetail>> paragraphDetails;
}
