package uk.gov.hmcts.probate.dmnutils;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_TYPE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.CASE_TYPE_VALUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DECEASED_FORENAMES;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DECEASED_FORENAMES_VALUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DECEASED_SURNAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.DECEASED_SURNAME_VALUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.LOCATION_NAME_VALUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.LOCATION_VALUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REGION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REGION_NATIONAL_VALUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REGISTRY_LOCATION;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.REGISTRY_LOCATION_VALUE;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ROLE_CATEGORY;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.ROLE_CATEGORY_CTSC;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.TASK_LOCATION_EPIMMS_ID;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.TASK_LOCATION_NAME;
import static uk.gov.hmcts.probate.dmnutils.TaskAttributeConstants.TASK_REGION;

public class CaseDataBuilder {

    Map<String,Object> caseData;

    private CaseDataBuilder(Map<String,Object> caseData) {
        this.caseData = caseData;
    }

    public static CaseDataBuilder defaultWaCase() {
        Map<String,Object> caseData = new HashMap<>();
        caseData.put("caseNameHmctsInternal", "Joe Blogs");
        caseData.put("isUrgent", "No");
        caseData.put(CASE_TYPE, CASE_TYPE_VALUE);
        caseData.put(DECEASED_FORENAMES, DECEASED_FORENAMES_VALUE);
        caseData.put(DECEASED_SURNAME, DECEASED_SURNAME_VALUE);
        caseData.put(REGION, REGION_NATIONAL_VALUE);
        caseData.put(ROLE_CATEGORY, ROLE_CATEGORY_CTSC);
        caseData.put(REGISTRY_LOCATION, REGISTRY_LOCATION_VALUE);
        caseData.put(TASK_LOCATION_EPIMMS_ID, LOCATION_VALUE);
        caseData.put(TASK_LOCATION_NAME, LOCATION_NAME_VALUE);
        caseData.put(TASK_REGION, REGION_NATIONAL_VALUE);
        return new CaseDataBuilder(caseData);
    }

    public Map<String,Object> build() {
        return caseData;
    }

    public CaseDataBuilder isUrgent() {
        caseData.put("isUrgent", "Yes");
        return this;
    }

    public CaseDataBuilder setSelectForQAUserIdamId(String idamId) {
        caseData.put("selectForQAUserIdamId", idamId);
        return this;
    }

    public CaseDataBuilder setMoveToCWEscalationUserIdamId(String idamId) {
        caseData.put("moveToCWEscalationUserIdamId", idamId);
        return this;
    }

    public CaseDataBuilder setEscalateToRegistrarUserIdamId(String idamId) {
        caseData.put("escalateToRegistrarUserIdamId", idamId);
        return this;
    }

    public CaseDataBuilder setRedeclarationUserIdamId(String idamId) {
        caseData.put("redeclarationUserIdamId", idamId);
        return this;
    }

    public CaseDataBuilder setResolveStoppedCaseUserIdamId(String idamId) {
        caseData.put("resolveStoppedCaseUserIdamId", idamId);
        return this;
    }

    public CaseDataBuilder setResolveRedeclarationUserIdamId(String idamId) {
        caseData.put("resolveRedeclarationUserIdamId", idamId);
        return this;
    }

    public CaseDataBuilder setTaskLocationEpimmsId(String taskLocationEpimmsId) {
        caseData.put("taskLocationEpimmsId", taskLocationEpimmsId);
        return this;
    }

    public CaseDataBuilder setTaskLocationName(String taskLocationName) {
        caseData.put("taskLocationName", taskLocationName);
        return this;
    }

    public CaseDataBuilder setTaskRegion(String taskRegion) {
        caseData.put("taskRegion", taskRegion);
        return this;
    }
}
