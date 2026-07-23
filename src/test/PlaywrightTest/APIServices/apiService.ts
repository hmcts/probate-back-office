import type { APIRequestContext } from "@playwright/test";
import {
  getAccessToken,
  getServiceAuthToken,
  buildAuthHeaders,
} from "../API/apiHelper.ts";

type EventSummary = {
  summary: string;
  description: string;
};

type StartEventResponse = Record<string, unknown>;
type SubmitEventResponse = Record<string, unknown>;
type CaseResponse = Record<string, unknown>;

export class apiService {
  constructor(private readonly request: APIRequestContext) {}

  private get env() {
    const idamUrl = process.env.IDAM_API_URL!;
    const s2sUrl = process.env.S2S_API_URL!;
    const ccdApiUrl = process.env.CCD_API_URL!;
    const cwEmail = process.env.CW_USER_EMAIL!;
    const cwPassword = process.env.CW_USER_PASSWORD!;
    const gatewayUrl =
      process.env.CCD_API_URL ??
      process.env.CCD_DATA_STORE_URL ??
      process.env.CCD_GATEWAY_URL;

    const caseworkerEmail = process.env.CW_USER_EMAIL;
    const caseworkerPassword = process.env.CW_USER_PASSWORD;
    const microservice = process.env.S2S_MICROSERVICE_NAME ?? "probate_backend";

    if (!idamUrl) {
      throw new Error("Missing IDAM_API_URL or IDAM_URL");
    }

    if (!s2sUrl) {
      throw new Error("Missing S2S_URL");
    }

    if (!gatewayUrl) {
      throw new Error(
        "Missing CCD_API_URL or CCD_DATA_STORE_URL or CCD_GATEWAY_URL"
      );
    }

    if (!caseworkerEmail) {
      throw new Error("Missing CW_USER_EMAIL");
    }

    if (!caseworkerPassword) {
      throw new Error("Missing CW_USER_PASSWORD");
    }

    return {
      idamUrl,
      s2sUrl,
      gatewayUrl: gatewayUrl.replace(/\/$/, ""),
      caseworkerEmail,
      caseworkerPassword,
      microservice,
    };
  }

  private async getAuthHeaders(
    userEmail?: string,
    userPassword?: string
  ): Promise<Record<string, string>> {
    const env = this.env;

    const authToken = await getAccessToken(
      env.idamUrl,
      userEmail ?? env.caseworkerEmail,
      userPassword ?? env.caseworkerPassword
    );

    const serviceAuthToken = await getServiceAuthToken(
      env.s2sUrl,
      env.microservice
    );

    return buildAuthHeaders(authToken, serviceAuthToken);
  }

  private getBaseCcdUrl(userId: string, jurisdictionId: string, caseTypeId: string): string {
    const env = this.env;
    return `${env.gatewayUrl}/caseworkers/${encodeURIComponent(
      userId
    )}/jurisdictions/${encodeURIComponent(
      jurisdictionId
    )}/case-types/${encodeURIComponent(caseTypeId)}`;
  }

  async getCaseData(
    userId: string,
    jurisdictionId: string,
    caseTypeId: string,
    caseId: string
  ): Promise<CaseResponse> {
    const headers = await this.getAuthHeaders();
    const url = `${this.getBaseCcdUrl(
      userId,
      jurisdictionId,
      caseTypeId
    )}/cases/${caseId}`;

    const response = await this.request.get(url, {
      headers,
      failOnStatusCode: false,
    });

    const bodyText = await response.text();

    console.log("GET CASE URL:", url);
    console.log("GET CASE STATUS:", response.status());
    console.log("GET CASE BODY:", bodyText);

    if (!response.ok()) {
      throw new Error(`getCaseData failed: ${response.status()} - ${bodyText}`);
    }

    return JSON.parse(bodyText);
  }

  async getCaveatCase(
    userId: string,
    jurisdictionId: string,
    caseId: string
  ): Promise<CaseResponse> {
    return this.getCaseData(userId, jurisdictionId, "Caveat", caseId);
  }

  async startEvent(
    userId: string,
    jurisdictionId: string,
    caseTypeId: string,
    caseId: string,
    eventId: string
  ): Promise<StartEventResponse> {
    const headers = await this.getAuthHeaders();
    const url = `${this.getBaseCcdUrl(
      userId,
      jurisdictionId,
      caseTypeId
    )}/cases/${caseId}/event-triggers/${eventId}/token`;

    const response = await this.request.get(url, {
      headers,
      failOnStatusCode: false,
    });

    const bodyText = await response.text();

    console.log("START EVENT URL:", url);
    console.log("START EVENT STATUS:", response.status());
    console.log("START EVENT BODY:", bodyText);

    if (!response.ok()) {
      throw new Error(`startEvent failed: ${response.status()} - ${bodyText}`);
    }

    return JSON.parse(bodyText);
  }

  async submitEvent(
    userId: string,
    jurisdictionId: string,
    caseTypeId: string,
    caseId: string,
    eventId: string,
    caseData: Record<string, unknown>,
    event: EventSummary
  ): Promise<SubmitEventResponse> {
    const headers = await this.getAuthHeaders();
    const startEventResponse = await this.startEvent(
      userId,
      jurisdictionId,
      caseTypeId,
      caseId,
      eventId
    );

    const token =
      (startEventResponse.token as string | undefined) ??
      ((startEventResponse as Record<string, unknown>).event_token as string | undefined);

    if (!token) {
      throw new Error(`No event token returned for event ${eventId}`);
    }

    const url = `${this.getBaseCcdUrl(
      userId,
      jurisdictionId,
      caseTypeId
    )}/cases/${caseId}/events`;

    const payload = {
      data: caseData,
      event: {
        id: eventId,
        summary: event.summary,
        description: event.description,
      },
      event_token: token,
      ignore_warning: false,
    };

    const response = await this.request.post(url, {
      headers,
      data: payload,
      failOnStatusCode: false,
    });

    const bodyText = await response.text();

    console.log("SUBMIT EVENT URL:", url);
    console.log("SUBMIT EVENT STATUS:", response.status());
    console.log("SUBMIT EVENT PAYLOAD:", JSON.stringify(payload, null, 2));
    console.log("SUBMIT EVENT BODY:", bodyText);

    if (!response.ok()) {
      throw new Error(`submitEvent failed: ${response.status()} - ${bodyText}`);
    }

    return JSON.parse(bodyText);
  }

  async createCaseAsCaseworker(
    userId: string,
    jurisdictionId: string,
    caseTypeId: string,
    eventId: string,
    caseData: Record<string, unknown>,
    summary: string = "API test case creation",
    description: string = "Created via Playwright API test"
  ): Promise<SubmitEventResponse> {
    const headers = await this.getAuthHeaders();

    const startUrl = `${this.getBaseCcdUrl(
      userId,
      jurisdictionId,
      caseTypeId
    )}/event-triggers/${eventId}/token`;

    const startResponse = await this.request.get(startUrl, {
      headers,
      failOnStatusCode: false,
    });

    const startBodyText = await startResponse.text();

    console.log("CREATE CASE START URL:", startUrl);
    console.log("CREATE CASE START STATUS:", startResponse.status());
    console.log("CREATE CASE START BODY:", startBodyText);

    if (!startResponse.ok()) {
      throw new Error(
        `createCase start failed: ${startResponse.status()} - ${startBodyText}`
      );
    }

    const startPayload = JSON.parse(startBodyText);
    const token =
      startPayload.token ?? startPayload.event_token;

    if (!token) {
      throw new Error(`No create-case token returned for event ${eventId}`);
    }

    const createUrl = `${this.getBaseCcdUrl(
      userId,
      jurisdictionId,
      caseTypeId
    )}/cases`;

    const payload = {
      data: caseData,
      event: {
        id: eventId,
        summary,
        description,
      },
      event_token: token,
      ignore_warning: false,
    };

    const createResponse = await this.request.post(createUrl, {
      headers,
      data: payload,
      failOnStatusCode: false,
    });

    const createBodyText = await createResponse.text();

    console.log("CREATE CASE URL:", createUrl);
    console.log("CREATE CASE STATUS:", createResponse.status());
    console.log("CREATE CASE PAYLOAD:", JSON.stringify(payload, null, 2));
    console.log("CREATE CASE BODY:", createBodyText);

    if (!createResponse.ok()) {
      throw new Error(
        `createCaseAsCaseworker failed: ${createResponse.status()} - ${createBodyText}`
      );
    }

    return JSON.parse(createBodyText);
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


