import { APIRequestContext, expect } from "@playwright/test";

export class apiService {
  readonly request: APIRequestContext;

  constructor(request: APIRequestContext) {
    this.request = request;
  }

  async backdatePayment(env: string, caseRef: string, authToken: string, serviceAuthToken: string, backdateDate: string) {
    const paymentApiUrl = `http://payment-api-${env}.service.core-compute-${env}.internal/payments/ccd_case_reference/${caseRef}/lag_time/${backdateDate}`;
    const updatePaymentDateResponse = await this.request.patch(paymentApiUrl, {
      headers: {
        'Authorization': `${authToken}`,
        'ServiceAuthorization': `${serviceAuthToken}`,
        'Cookie': 'JSESSIONID=43DAA997EF6BFA65C030A37ECB2D78EB'
      }
    })
    expect(updatePaymentDateResponse.status()).toBe(204);
  }

  async refundsApprovalLiberata(env: string, refundRef: string, serviceAuthToken: string) {
    const paymentApiUrl = `http://ccpay-refunds-api-${env}.service.core-compute-${env}.internal/refund/${refundRef}`;
    const updatePaymentDateResponse = await this.request.patch(paymentApiUrl, {
      headers: {
        'ServiceAuthorization': `${serviceAuthToken}`,
        'Content-Type': 'application/json'
      },
      data: {
        reason: '',
        status: 'ACCEPTED'
      }
    })
    expect(updatePaymentDateResponse.status()).toBe(204);
  }
}


