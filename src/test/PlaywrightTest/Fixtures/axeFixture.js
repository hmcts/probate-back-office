const base = require('@playwright/test');
const AxeBuilder = require('@axe-core/playwright').default;

// Extend the base test with our accessibility testing fixtures
const test = base.test.extend({
    // Original page fixture - making it available to be used in our a11yPage fixture
    page: async ({page}, use) => {
        await use(page);
    },

    // Create an accessibility-ready page fixture
    a11yPage: async ({page}, use) => {
        // Store the original goto function
        const originalGoto = page.goto.bind(page);

        // Override the goto function to return a chainable interface
        page.goto = async (url, options) => {
            await originalGoto(url, options);
            return page;
        };

        // Add the checkA11y method to the page
        page.checkA11y = async (options = {}) => {
            // Create axe builder with default configuration
            let axeBuilder = new AxeBuilder({page})
                .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa']);

            // Add any specific exclusions (you can customize this list)
            axeBuilder = axeBuilder.exclude('#known-issue-element');

            // Handle options if provided
            if (options.include) {
                axeBuilder = axeBuilder.include(options.include);
            }

            if (options.exclude) {
                axeBuilder = axeBuilder.exclude(options.exclude);
            }

            if (options.rules) {
                axeBuilder = axeBuilder.withRules(options.rules);
            }

            // Run the analysis
            const results = await axeBuilder.analyze();

            // Return the results
            return results;
        };

        await use(page);
    }
});

// Re-export the base expect
const expect = base.expect;

module.exports = {test, expect};
