const base = require('@playwright/test');
const AxeBuilder = require('@axe-core/playwright').default;

exports.test = base.test.extend({
    makeAxeBuilder: async ({ page }, use, testInfo) => {
        const makeAxeBuilder = () => new AxeBuilder({ page })
            .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
            .exclude('#commonly-reused-element-with-known-issue');

        await use(makeAxeBuilder);
    }
});
exports.expect = base.expect;
