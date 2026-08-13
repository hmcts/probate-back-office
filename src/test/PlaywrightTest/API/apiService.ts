import type { APIRequestContext, APIResponse } from "@playwright/test";
import { testConfig } from "../Configs/config.js";
import { buildAuthHeaders, getAccessToken, getServiceAuthToken } from "./apiHelper.js";
import type {
  DataContent,
  StartEventResponse,
  SubmitEventResponse,
  CaseEventResult,
} from "./apiTypes.js";

export class ApiService {
  protected authToken = "";
  protected serviceAuthToken = "";

  constructor(protected request: APIRequestContext) {}

  async initialiseTokens(username: string, password: string): Promise<void> {
    if (!this.authToken) {
      this.authToken = await getAccessToken(
        testConfig.TestBoIdamUrl,
        username,
        password
      );
    }

    if (!this.serviceAuthToken) {
      this.serviceAuthToken = await getServiceAuthToken(
        testConfig.TestS2sUrl,
        "probate_backend"
      );
    }
  }

  async initialiseCaseworkerTokens(): Promise<void> {
    await this.initialiseTokens(
      testConfig.TestEnvCwUser,
      testConfig.TestEnvCwPassword
    );
  }

  protected async getHeaders(): Promise<Record<string, string>> {
    await this.initialiseCaseworkerTokens();
    return buildAuthHeaders(this.authToken, this.serviceAuthToken);
  }

  protected async getSolicitorHeaders(): Promise<Record<string, string>> {
    const authToken = await getAccessToken(
      testConfig.TestBoIdamUrl,
      testConfig.TestEnvSolUser,
      testConfig.TestEnvSolPassword
    );

    const serviceAuthToken = await getServiceAuthToken(
      testConfig.TestS2sUrl,
      "probate_backend"
    );

    return buildAuthHeaders(authToken, serviceAuthToken);
  }

  protected async parseJsonResponse<T>(
    response: APIResponse,
    actionName: string,
    expectedStatus: number
  ): Promise<T> {
    const status = response.status();
    const contentType = response.headers()["content-type"] ?? "";
    const bodyText = await response.text();

    if (status !== expectedStatus) {
      throw new Error(
        `${actionName} failed: ${status} ${response.statusText()} | content-type=${contentType} | url=${response.url()} | body=${bodyText.slice(0, 500)}`
      );
    }

    if (!contentType.includes("application/json")) {
      throw new Error(
        `${actionName} returned non-JSON: ${status} ${response.statusText()} | content-type=${contentType} | url=${response.url()} | body=${bodyText.slice(0, 500)}`
      );
    }

    return JSON.parse(bodyText) as T;
  }

  async getCaseData(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    caseId: string
  ): Promise<SubmitEventResponse> {
    const response = await this.request.get(
      `${testConfig.TestBaseUrl}/caseworkers/${userId}/jurisdictions/${jurisdictionId}/case-types/${caseType}/cases/${caseId}`,
      {
        headers: await this.getHeaders(),
        failOnStatusCode: false,
      }
    );

    return this.parseJsonResponse<SubmitEventResponse>(response, "getCaseData", 200);
  }

  async getEventToken(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    caseId: string,
    eventId: string,
    ignoreWarning: boolean = false
  ): Promise<StartEventResponse> {
    const response = await this.request.get(
      `${testConfig.TestBaseUrl}/caseworkers/${userId}/jurisdictions/${jurisdictionId}/case-types/${caseType}/cases/${caseId}/event-triggers/${eventId}/token`,
      {
        headers: await this.getHeaders(),
        params: { "ignore-warning": ignoreWarning },
        failOnStatusCode: false,
      }
    );

    return this.parseJsonResponse<StartEventResponse>(response, "getEventToken", 200);
  }

  async submitEvent(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    caseId: string,
    dataContent: DataContent,
    ignoreWarning: boolean = false
  ): Promise<SubmitEventResponse> {
    const response = await this.request.post(
      `${testConfig.TestBaseUrl}/caseworkers/${userId}/jurisdictions/${jurisdictionId}/case-types/${caseType}/cases/${caseId}/events`,
      {
        headers: await this.getHeaders(),
        params: { "ignore-warning": ignoreWarning },
        data: dataContent,
        failOnStatusCode: false,
      }
    );

    return this.parseJsonResponse<SubmitEventResponse>(response, "submitEvent", 201);
  }

  async startCreateCaseEvent(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    eventId: string,
    ignoreWarning: boolean = false
  ): Promise<StartEventResponse> {
    const response = await this.request.get(
      `${testConfig.TestBaseUrl}/caseworkers/${userId}/jurisdictions/${jurisdictionId}/case-types/${caseType}/event-triggers/${eventId}/token`,
      {
        headers: await this.getHeaders(),
        params: { "ignore-warning": ignoreWarning },
        failOnStatusCode: false,
      }
    );

    return this.parseJsonResponse<StartEventResponse>(response, "startCreateCaseEvent", 200);
  }

  async startCreateCaseEventAsSolicitor(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    eventId: string,
    ignoreWarning: boolean = false
  ): Promise<StartEventResponse> {
    const response = await this.request.get(
      `${testConfig.TestBaseUrl}/caseworkers/${userId}/jurisdictions/${jurisdictionId}/case-types/${caseType}/event-triggers/${eventId}/token`,
      {
        headers: await this.getSolicitorHeaders(),
        params: { "ignore-warning": ignoreWarning },
        failOnStatusCode: false,
      }
    );

    return this.parseJsonResponse<StartEventResponse>(
      response,
      "startCreateCaseEventAsSolicitor",
      200
    );
  }

  async createCaseWithEvent(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    eventId: string,
    caseData: Record<string, unknown>,
    summary: string = "API test case creation",
    description: string = "Created via Playwright API test"
  ): Promise<SubmitEventResponse> {
    const start = await this.startCreateCaseEvent(
      userId,
      jurisdictionId,
      caseType,
      eventId
    );

    const payload = this.buildCreateCasePayload(
      start,
      caseData,
      summary,
      description
    );

    return this.createCase(
      userId,
      jurisdictionId,
      caseType,
      payload
    );
  }

  async createCase(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    dataContent: DataContent,
    ignoreWarning: boolean = false
  ): Promise<SubmitEventResponse> {
    const response = await this.request.post(
      `${testConfig.TestBaseUrl}/caseworkers/${userId}/jurisdictions/${jurisdictionId}/case-types/${caseType}/cases`,
      {
        headers: await this.getHeaders(),
        params: { "ignore-warning": ignoreWarning },
        data: dataContent,
        failOnStatusCode: false,
      }
    );

    return this.parseJsonResponse<SubmitEventResponse>(response, "createCase", 201);
  }

  buildEventPayload(
    startEventResponse: StartEventResponse,
    summary: string,
    description: string,
    mutate?: (currentData: Record<string, unknown>) => Record<string, unknown>
  ): DataContent {
    const currentData = (startEventResponse.case_details?.case_data ??
      {}) as Record<string, unknown>;

    return {
      event: {
        id: startEventResponse.event_id,
        summary,
        description,
      },
      data: mutate ? mutate(currentData) : currentData,
      event_token: startEventResponse.token,
      ignore_warning: false,
      security_classification:
        startEventResponse.case_details?.security_classification ?? "PUBLIC",
      supplementary_data_request: {},
    };
  }

  buildCreateCasePayload(
    startEventResponse: StartEventResponse,
    caseData: Record<string, unknown>,
    summary: string,
    description: string
  ): DataContent {
    return {
      event: {
        id: startEventResponse.event_id,
        summary,
        description,
      },
      data: caseData,
      event_token: startEventResponse.token,
      ignore_warning: false,
      security_classification:
        startEventResponse.case_details?.security_classification ?? "PUBLIC",
      supplementary_data_request: {},
    };
  }

  async createCaseAsCaseworker(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    eventId: string,
    caseData: Record<string, unknown>,
    summary: string = "API test case creation",
    description: string = "Created via Playwright API test"
  ): Promise<SubmitEventResponse> {
    const start = await this.startCreateCaseEvent(
      userId,
      jurisdictionId,
      caseType,
      eventId
    );

    const payload = this.buildCreateCasePayload(
      start,
      caseData,
      summary,
      description
    );

    return this.createCase(
      userId,
      jurisdictionId,
      caseType,
      payload
    );
  }

  async createCaseAsSolicitor(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    eventId: string,
    caseData: Record<string, unknown>,
    summary: string = "API test case creation",
    description: string = "Created via Playwright API test"
  ): Promise<SubmitEventResponse> {
    const start = await this.startCreateCaseEventAsSolicitor(
      userId,
      jurisdictionId,
      caseType,
      eventId
    );

    const payload = this.buildCreateCasePayload(
      start,
      caseData,
      summary,
      description
    );

    const response = await this.request.post(
      `${testConfig.TestBaseUrl}/caseworkers/${userId}/jurisdictions/${jurisdictionId}/case-types/${caseType}/cases`,
      {
        headers: await this.getSolicitorHeaders(),
        data: payload,
        failOnStatusCode: false,
      }
    );

    return this.parseJsonResponse<SubmitEventResponse>(
      response,
      "createCaseAsSolicitor",
      201
    );
  }

  async submitCaseworkerEventWithExistingCaseData(
    userId: string,
    jurisdictionId: string,
    caseType: string,
    caseId: string,
    eventId: string,
    summary: string,
    description: string,
    mutate?: (currentData: Record<string, unknown>) => Record<string, unknown>
  ): Promise<CaseEventResult> {
    const before = await this.getCaseData(userId, jurisdictionId, caseType, caseId);

    const startEvent = await this.getEventToken(
      userId,
      jurisdictionId,
      caseType,
      caseId,
      eventId
    );

    const payload = this.buildEventPayload(
      startEvent,
      summary,
      description,
      mutate
    );

    const submitResponse = await this.submitEvent(
      userId,
      jurisdictionId,
      caseType,
      caseId,
      payload
    );

    const after = await this.getCaseData(userId, jurisdictionId, caseType, caseId);

    return {
      before,
      startEvent,
      payload,
      submitResponse,
      after,
    };
  }
}
