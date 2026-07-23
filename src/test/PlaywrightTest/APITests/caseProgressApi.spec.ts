import { test, expect } from "../Fixtures/apiFixtures.ts";
import { CaseProgressApiService } from "../API/caseProgressApiService.js";

test.describe("Case Progress API SPIKE", () => {


  test("should submit case progress event with existing case data", async ({}) => {
    const userId = process.env.CW_USER_EMAIL!;
    const jurisdictionId = process.env.CCD_JURISDICTION ?? "PROBATE";
    const caseId = process.env.TEST_EXISTING_PROBATE_CASE_ID!;
    const eventId = process.env.TEST_CASE_PROGRESS_EVENT_ID ?? "selectForQa";

    const result = await CaseProgressApiService.submitGenericCaseProgressEvent(
      userId,
      jurisdictionId,
      caseId,
      eventId,
      `API SPIKE - ${eventId}`,
      `Submitting ${eventId} using fetched case data`
    );

    expect(result.before.id).toBeTruthy();
    expect(result.startEvent.token).toBeTruthy();
    expect(result.submitResponse.id).toBe(result.before.id);
    expect(result.after.id).toBe(result.before.id);
    expect(result.submitResponse.state || result.after.state).toBeTruthy();
  });
});
