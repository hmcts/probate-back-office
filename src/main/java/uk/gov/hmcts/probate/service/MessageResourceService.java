package uk.gov.hmcts.probate.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Data
@Component
public class MessageResourceService {

    @Qualifier("resourceMessageSource")
    private final MessageSource resourceMessageSource;

    public String getMessage(String id) {
        return resourceMessageSource.getMessage(id, null, null);
    }

}
