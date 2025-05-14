const AxeBuilder = require('@axe-core/playwright').default;

/*
 * Performs accessibility testing on the current page
 * @param {Object} page - Playwright page object
 * @returns {Promise<Object>} Accessibility test results
 */
async function checkAccessibility(page, testInfo) {
    const axeBuilder = new AxeBuilder({page})
        .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
        .exclude('#known-issue-element');

    // Run analysis
    const results = await axeBuilder.analyze();

    // Attach results to test report
    await testInfo.attach('accessibility-results.json', {
        body: JSON.stringify(results, null, 2),
        contentType: 'application/json'
    });

    return results;
}

module.exports = {checkAccessibility};
