import { APIRequestContext } from "@playwright/test";
import { testConfig } from "../Configs/config.js";
import {
  buildAuthHeaders,
  getAccessToken,
  getServiceAuthToken,
} from "../API/apiHelper.js";

type EventMeta = {
  summary: string;
  description: string;
};

export type RaiseCaveatData = {
  applicationType: string;
  registryLocation: string;
  deceasedForenames: string;
  deceasedSurname: string;
  deceasedDateOfDeath: string;
  deceasedAnyOtherNames: string;
  deceasedFullAliasNameList: Array<{
    id: string;
    value: {
      FullAliasName: string;
    };
  }>;
  deceasedAddress: {
    AddressLine1: string;
    AddressLine2?: string;
    AddressLine3?: string;
    PostTown: string;
    County?: string;
    PostCode: string;
    Country: string;
  };
  caveatorForenames: string;
  caveatorSurname: string;
  caveatorEmailAddress: string;
  solsSolicitorAppReference: string;
  caveatorAddress: {
    AddressLine1: string;
    AddressLine2?: string;
    AddressLine3?: string;
    PostTown: string;
    County?: string;
    PostCode: string;
    Country: string;
  };
  languagePreferenceWelsh: string;
  caveatRaisedEmailNotificationRequested: string;
};

export class CaseApiService {
  private authToken = "";
  private serviceAuthToken = "";

  constructor(private readonly request: APIRequestContext) {}

  private get baseUrl(): string {
    const url = testConfig.TestCcdDataStoreUrl;
    console.log("Request BaseURL event URL:", url);

    if (!url || !/^https?:\/\//i.test(url)) {
      throw new Error(
        `TestCcdDataStoreUrl is invalid: "${url}". Expected a full URL like https://manage-case.aat.platform.hmcts.net`
      );
    }

    return url.endsWith("/") ? url : `${url}/`;
  }

  private buildUrl(
    pathname: string,
    query?: Record<string, string | boolean>
  ): string {
    const url = new URL(pathname.replace(/^\/+/, ""), this.baseUrl);
    console.log("Request buildURL event URL:", url);

    if (query) {
      for (const [key, value] of Object.entries(query)) {
        url.searchParams.set(key, String(value));
      }
    }

    return url.toString();
  }

  private async initialiseCaseworkerTokens(): Promise<void> {
    if (!this.authToken) {
      this.authToken = await getAccessToken(
        testConfig.TestBoIdamUrl,
        testConfig.TestEnvCwUser!,
        testConfig.TestEnvCwPassword!
      );
    }

    if (!this.serviceAuthToken) {
      this.serviceAuthToken = await getServiceAuthToken(
        testConfig.TestS2sUrl,
        "probate_backend"
      );
    }
  }

  private async getCommonHeaders(
    accept: string
  ): Promise<Record<string, string>> {
    await this.initialiseCaseworkerTokens();

    return {
      ...buildAuthHeaders(this.authToken, this.serviceAuthToken),
      experimental: "true",
      "client-context":
        "eyJjbGllbnRfY29udGV4dCI6eyJ1c2VyX2xhbmd1YWdlIjp7Imxhbmd1YWdlIjoiZW4ifX19",
      accept,
    };
  }

  private async parseJsonResponse(
    response: Awaited<ReturnType<APIRequestContext["get"]>>,
    action: string
  ) {
    const responseText = await response.text();

    if (!response.ok()) {
      throw new Error(
        `${action} failed: ${response.status()} ${response.statusText()} - ${responseText}`
      );
    }

    const contentType = response.headers()["content-type"] || "";
    if (!contentType.toLowerCase().includes("json")) {
      throw new Error(
        `${action} expected JSON but got ${contentType} from ${response.url()} - ${responseText.slice(0, 500)}`
      );
    }

    return JSON.parse(responseText);
  }

  async getCaseData(caseId: string) {
    const rawCaseId = caseId.replace(/-/g, "");
    const url = this.buildUrl(`/data/internal/cases/${rawCaseId}`);

    const response = await this.request.get(url, {
      headers: await this.getCommonHeaders(
        "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json"
      ),
    });

    return this.parseJsonResponse(response, "getCaseData");
  }

  async getStartEventTrigger(caseTypeId: string, eventId: string) {
    const url = this.buildUrl(
      `/data/internal/case-types/${caseTypeId}/event-triggers/${eventId}`,
      { "ignore-warning": false }
    );

    const response = await this.request.get(url, {
      headers: await this.getCommonHeaders(
        "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8"
      ),
    });

    return this.parseJsonResponse(response, "getStartEventTrigger");
  }

  async getCaseEventTrigger(caseTypeId: string, caseId: string, eventId: string) {
    const rawCaseId = caseId.replace(/-/g, "");
    const url = this.buildUrl(
      `/data/internal/cases/${rawCaseId}/event-triggers/${eventId}`,
      { "ignore-warning": false }
    );

    const response = await this.request.get(url, {
      headers: await this.getCommonHeaders(
        "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8"
      ),
    });

    return this.parseJsonResponse(response, "getCaseEventTrigger");
  }

  async createCase(
    caseTypeId: string,
    eventId: string,
    data: Record<string, unknown>,
    meta: EventMeta
  ) {
    const trigger = await this.getStartEventTrigger(caseTypeId, eventId);
    const url = this.buildUrl(`/data/case-types/${caseTypeId}/cases`, {
      "ignore-warning": false,
    });

    const response = await this.request.post(url, {
      headers: await this.getCommonHeaders(
        "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8"
      ),
      data: {
        data,
        event: {
          id: eventId,
          summary: meta.summary,
          description: meta.description,
        },
        event_token: trigger.event_token ?? trigger.token,
        ignore_warning: false,
      },
    });

    return this.parseJsonResponse(response, "createCase");
  }

  async submitEvent(
    caseId: string,
    eventId: string,
    data: Record<string, unknown>,
    meta: EventMeta,
    caseTypeId: string = "Caveat"
  ) {
    const rawCaseId = caseId.replace(/-/g, "");
    const trigger = await this.getCaseEventTrigger(caseTypeId, rawCaseId, eventId);
    const url = this.buildUrl(`/data/cases/${rawCaseId}/events`);

    const response = await this.request.post(url, {
      headers: await this.getCommonHeaders(
        "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-content.v2+json;charset=UTF-8"
      ),
      data: {
        data,
        event: {
          id: eventId,
          summary: meta.summary,
          description: meta.description,
        },
        event_token: trigger.event_token ?? trigger.token,
        ignore_warning: false,
      },
    });

    return this.parseJsonResponse(response, "submitEvent");
  }

  async raiseCaveatCase(data: RaiseCaveatData) {
    return this.createCase(
      "Caveat",
      "raiseCaveat",
      data as Record<string, unknown>,
      {
        summary: "raise a caveat event summary text",
        description: "raise a caveat event description text",
      }
    );
  }
}
