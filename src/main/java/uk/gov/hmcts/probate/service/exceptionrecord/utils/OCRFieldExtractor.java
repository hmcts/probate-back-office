package uk.gov.hmcts.probate.service.exceptionrecord.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.BulkScanConfig;
import uk.gov.hmcts.probate.model.ocr.OCRField;
import uk.gov.hmcts.reform.probate.model.cases.CollectionMember;
import uk.gov.hmcts.reform.probate.model.cases.grantofrepresentation.ModifiedOCRField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OCRFieldExtractor {

    private static final String POSTCODE_REGEX_PATTERN = "^([A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}|GIR ?0A{2})$";

    private static BulkScanConfig bulkScanConfig;

    @Autowired
    private OCRFieldExtractor(BulkScanConfig bulkScanConfig) {
        OCRFieldExtractor.bulkScanConfig = bulkScanConfig;
    }

    public static String get(List<OCRField> ocrFields, String name) {
        String response = null;

        try {
            response = ocrFields
                    .stream()
                    .filter(it -> it.getName().equals(name))
                    .map(it -> it.getValue().trim())
                    .findFirst()
                    .orElse(null);
        } catch (NullPointerException npe) {
            log.warn("Unable to extract value for field '{}'", name);
        }

        return response;
    }

    public static String get(List<OCRField> ocrFields, String name1, String name2) {
        return (get(ocrFields, name1) + " "
                + get(ocrFields, name2)).replace("null", "").replaceAll("\\s{2,}", " ").trim();
    }

    public static String get(List<OCRField> ocrFields, String name1, String name2, String name3) {
        return (get(ocrFields, name1) + " "
                + get(ocrFields, name2) + " "
                + get(ocrFields, name3)).replace("null", "").replaceAll("\\s{2,}", " ").trim();
    }

    public static List<String> splitFullname(String fullName) {
        return new ArrayList<>(Arrays.asList(fullName.split(" ")));
    }

    public static String getDefaultPostcodeIfInvalid(List<OCRField> ocrFields, String name,
                                                List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response || isInvalidPostCode(response)) {
            log.warn("Invalid or missing postcode for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getDeceasedAddressPostCode();
        }

        return response;
    }

    //For any name, e.g sols, applicant, deceased, town
    public static String getDefaultNameIfInvalid(List<OCRField> ocrFields, String name,
                                                 List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing name for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getName();
        }

        return response;
    }

    //For any pairs of names, such as forenames
    public static String getDefaultNamesIfInvalid(List<OCRField> ocrFields, String name, String name2,
                                                  List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing names for fields '{}' and {}", name, name2);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .fieldName(name2)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getName();
        }

        return response;
    }

    //Serves all address lines
    public static String getDefaultAddressLineIfInvalid(List<OCRField> ocrFields, String name,
                                          List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing address line for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getAddressLine();
        }

        return response;
    }

    public static String getDefaultDateOfBirthIfInvalid(List<OCRField> ocrFields, String name,
                                          List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response || isInvalidDateOfBirth(response)) {
            log.warn("Invalid or missing date of birth for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getDob();
        }

        return response;
    }

    public static String getDefaultSolsAppReferenceIfInvalid(List<OCRField> ocrFields, String name,
                                                        List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing solicitor application reference for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getDob();
        }

        return response;
    }

    public static String getDefaultRepresentativeNameIfInvalid(List<OCRField> ocrFields, String name,
                                                               List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing representative name for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getName();
        }

        return response;
    }

    public static String getDefaultEmailIfInvalid(List<OCRField> ocrFields, String name,
                                                               List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing email for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getEmail();
        }

        return response;
    }

    public static String getDefaultPhoneNumberIfInvalid(List<OCRField> ocrFields, String name,
                                                               List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing phone number for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getSolsSolicitorPhoneNumber();
        }

        return response;
    }

    public static String getDefaultDomiciledInEngWalesIfInvalid(List<OCRField> ocrFields, String name,
                                                        List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing domiciled in Eng/Wales value for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getDeceasedDomicileInEngWales();
        }

        return response;
    }

    public static String getDefaultLegalRepresentativeIfInvalid(List<OCRField> ocrFields, String name,
                                                        List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing legal representative for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getLegalRepresentative();
        }

        return response;
    }

    public static String getDefaultSolsSolicitorAddressTownIfInvalid(List<OCRField> ocrFields, String name,
                                                        List<CollectionMember<ModifiedOCRField>> modifiedFields) {
        String response = ocrFields
                .stream()
                .filter(it -> it.getName().equals(name) && it.getValue() != null)
                .map(it -> it.getValue().trim().toUpperCase())
                .findFirst()
                .orElse(null);

        if (null == response) {
            log.warn("Invalid or missing town/city for field '{}'", name);
            ModifiedOCRField modifiedOCRField = ModifiedOCRField.builder()
                    .fieldName(name)
                    .originalValue(response)
                    .build();
            modifiedFields.add(new CollectionMember<>(null, modifiedOCRField));
            return bulkScanConfig.getLegalRepresentative();
        }

        return response;
    }

    private static boolean isInvalidPostCode(final String postCode) {
        return !postCode.matches(POSTCODE_REGEX_PATTERN);
    }

    private static boolean isInvalidDateOfBirth(final String dob) {
        return false;
        //return !dob.matches(DATE_OF_BIRTH_REGEX_PATTERN);
    }
}