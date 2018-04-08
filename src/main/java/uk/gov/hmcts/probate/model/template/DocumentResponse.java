package uk.gov.hmcts.probate.model.template;

import lombok.Data;

@Data
public class DocumentResponse {

    private final String name;

    private final String type;

    private final String url;
}
