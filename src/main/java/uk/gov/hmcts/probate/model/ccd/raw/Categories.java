package uk.gov.hmcts.probate.model.ccd.raw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
public class Categories {

    @CCD(
            label = "Select Category(s)",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "categoriesTypes"
    )
    private final List<String> selectedCategories = new ArrayList();
    @CCD(
            label = "Entitlement Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"Ent\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "EntParagraphs"
    )
    private final List<String> entSelectedParagraphs = new ArrayList();
    @CCD(
            label = "IHT Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"IHT\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "IHTParagraphs"
    )
    private final List<String> ihtSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Missing/Further information Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"MissInfo\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "MissInfoParagraphs"
    )
    private final List<String> missInfoSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Will Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"Will\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "WillParagraphs"
    )
    private final List<String> willSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Incapacity Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"Incap\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "IncapParagraphs"
    )
    private final List<String> incapacitySelectedParagraphs = new ArrayList();
    @CCD(
            label = "Foreign Domicile Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"ForDom\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "ForDomParagraphs"
    )
    private final List<String> forDomSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Life and minority Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"LifeMinor\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "LifeAndMinParagraphs"
    )
    private final List<String> lifeAndMinoritySelectedParagraphs = new ArrayList();
    @CCD(
            label = "SOT incomplete Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"SOT\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "SotParagraphs"
    )
    private final List<String> sotSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Witness Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"Witness\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "WitParagraphs"
    )
    private final List<String> witnessSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Solicitor General Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"SolGen\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "SolsGenParagraphs"
    )
    private final List<String> solsGeneralSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Solicitor Cert Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"SolCert\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "SolsCertsParagraphs"
    )
    private final List<String> solsCertsSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Solicitor Affidavit Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"SolAffidavit\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "SolsAffidParagraphs"
    )
    private final List<String> solsAffidavitSelectedParagraphs = new ArrayList();
    @CCD(
            label = "Solicitor redeclare Paragraphs",
            showCondition = "selectedCategoriesCONTAINS\"SolRedec\"",
            typeOverride = FieldType.MultiSelectList,
            typeParameterOverride = "SolsRedecParagraphs"
    )
    private final List<String> solsRedeclareSelectedParagraphs = new ArrayList();

    @JsonIgnore
    public List<List<String>> getAllSelectedCategories() {
        return Arrays.asList(entSelectedParagraphs, ihtSelectedParagraphs, missInfoSelectedParagraphs,
                willSelectedParagraphs, forDomSelectedParagraphs, incapacitySelectedParagraphs,
                lifeAndMinoritySelectedParagraphs, sotSelectedParagraphs, witnessSelectedParagraphs,
                solsGeneralSelectedParagraphs, solsCertsSelectedParagraphs, solsAffidavitSelectedParagraphs,
                solsRedeclareSelectedParagraphs);
    }
}
