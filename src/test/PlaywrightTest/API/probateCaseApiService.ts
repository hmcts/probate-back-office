import { ApiService } from "./apiService.js";
import type { SubmitEventResponse } from "./apiTypes.js";

export class ProbateCaseApiService extends ApiService {
  async getProbateCase(
    userId: string,
    jurisdictionId: string,
    caseId: string
  ): Promise<SubmitEventResponse> {
    return this.getCaseData(userId, jurisdictionId, "GrantOfRepresentation", caseId);
  }

  async getCaveatCase(
    userId: string,
    jurisdictionId: string,
    caseId: string
  ): Promise<SubmitEventResponse> {
    return this.getCaseData(userId, jurisdictionId, "Caveat", caseId);
  }

  async createProbateCaseAsCaseworker(
    userId: string,
    jurisdictionId: string,
    eventId: string,
    caseData: Record<string, unknown>,
    summary: string = "API test case creation",
    description: string = "Created via Playwright API test"
  ): Promise<SubmitEventResponse> {
    return this.createCaseAsCaseworker(
      userId,
      jurisdictionId,
      "GrantOfRepresentation",
      eventId,
      caseData,
      summary,
      description
    );
  }

  async createProbateCaseAsSolicitor(
    userId: string,
    jurisdictionId: string,
    eventId: string,
    caseData: Record<string, unknown>,
    summary: string = "API test case creation",
    description: string = "Created via Playwright API test"
  ): Promise<SubmitEventResponse> {
    return this.createCaseAsSolicitor(
      userId,
      jurisdictionId,
      "GrantOfRepresentation",
      eventId,
      caseData,
      summary,
      description
    );
  }

  async createCaveatCase(
    userId: string,
    jurisdictionId: string,
    eventId: string,
    caseData: Record<string, unknown>,
    summary: string = "API test case creation",
    description: string = "Created via Playwright API test"
  ): Promise<SubmitEventResponse> {
    return this.createCaseAsCaseworker(
      userId,
      jurisdictionId,
      "Caveat",
      eventId,
      caseData,
      summary,
      description
    );
  }
}
