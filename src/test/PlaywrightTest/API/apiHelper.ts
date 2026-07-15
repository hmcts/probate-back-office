import { request } from "@playwright/test";

export const getAccessToken = async (
  idamUrl: string,
  email: string,
  password: string
): Promise<string> => {
  const requestContext = await request.newContext();

  try {
    const response = await requestContext.post(
      `${idamUrl}/loginUser?username=${email}&password=${password}`,
      {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        failOnStatusCode: false,
      }
    );

    if (response.status() !== 200) {
      throw new Error(`Auth failed: ${response.status()} - ${await response.text()}`);
    }

    const data = await response.json();
    return data.access_token;
  } finally {
    await requestContext.dispose();
  }
};

export const getServiceAuthToken = async (
  s2sUrl: string,
  microserviceName: string
): Promise<string> => {
  const requestContext = await request.newContext();

  try {
    const response = await requestContext.post(s2sUrl, {
      headers: {
        accept: "*/*",
        "Content-Type": "application/json",
      },
      data: {
        microservice: microserviceName,
      },
      failOnStatusCode: false,
    });

    const rawBody = await response.text();

    console.log("S2S url:", s2sUrl);
    console.log("S2S status:", response.status());
    console.log("S2S headers:", JSON.stringify(response.headers(), null, 2));
    console.log("S2S raw body:", rawBody);

    if (response.status() !== 200) {
      throw new Error(`S2S auth failed: ${response.status()} - ${rawBody}`);
    }

    console.log("Service auth token obtained");
    return rawBody;
  } finally {
    await requestContext.dispose();
  }
};

export const buildAuthHeaders = (
  authToken: string,
  serviceAuthToken?: string
): Record<string, string> => {
  const authorization = authToken.startsWith("Bearer ")
    ? authToken
    : `Bearer ${authToken}`;

  const serviceAuthorization = serviceAuthToken
    ? serviceAuthToken.startsWith("Bearer ")
      ? serviceAuthToken
      : `Bearer ${serviceAuthToken}`
    : undefined;

  return {
    Authorization: authorization,
    ...(serviceAuthorization
      ? { ServiceAuthorization: serviceAuthorization }
      : {}),
    "Content-Type": "application/json",
    Accept: "application/json",
  };
};
