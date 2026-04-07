export const refundReviewConfig = {
  rows: [
    {
      hasText: 'Refund reference',
      expectedText: '{{refundRef}}',
      useRegex: false
    },
    {
      hasText: 'Reason for refund',
      expectedText: '{{refundReason}}',
      useRegex: false
    },
    {
      hasText: 'Amount refunded',
      expectedText: '{{refAmount}}',
      useRegex: false
    },
  ]
};
