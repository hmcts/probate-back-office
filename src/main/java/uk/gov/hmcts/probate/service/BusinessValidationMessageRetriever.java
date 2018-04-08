package uk.gov.hmcts.probate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;

@Component
public class BusinessValidationMessageRetriever {

    @Resource
    @Qualifier("validationMessageSource")
    private ResourceBundleMessageSource validationMessageSource;

    public String getMessage(String code, String[] args, Locale locale) {
        return validationMessageSource.getMessage(code, args, locale);
    }

}
