import { ApiService } from "./apiService.js";
import type { CaseEventResult } from "./apiTypes.js";

export class CaseProgressApiService extends ApiService {
  async selectForQa(
    userId: string,
    jurisdictionId: string,
    caseId: string
  ): Promise<CaseEventResult> {
    return super.submitCaseworkerEventWithExistingCaseData(
      userId,
      jurisdictionId,
      "GrantOfRepresentation",
      caseId,
      "selectForQa",
      "API SPIKE - Select for QA",
      "Submitting Select for QA using fetched case data"
    );
  }

  async generateGrantPreview(
    userId: string,
    jurisdictionId: string,
    caseId: string
  ): Promise<CaseEventResult> {
    return super.submitCaseworkerEventWithExistingCaseData(
      userId,
      jurisdictionId,
      "GrantOfRepresentation",
      caseId,
      "generateGrantPreview",
      "API SPIKE - Generate grant preview",
      "Submitting Generate grant preview using fetched case data"
    );
  }

  async submitGenericCaseProgressEvent(
    userId: string,
    jurisdictionId: string,
    caseId: string,
    eventId: string,
    summary: string,
    description: string
  ): Promise<CaseEventResult> {
    return super.submitCaseworkerEventWithExistingCaseData(
      userId,
      jurisdictionId,
      "GrantOfRepresentation",
      caseId,
      eventId,
      summary,
      description
    );
  }
}
