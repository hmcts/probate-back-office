function generateAccessibilityReport(reportJson) {
    consoleReport(reportJson);
}

function consoleReport(reportjson) {
    /* eslint-disable no-console */
    console.log('\t Total tests : ' + reportjson.tests.length);
    console.log('\t Passed tests : ' + reportjson.pass);
    console.log('\t Failed tests : ' + reportjson.fail);

    for (let count = 0; count < reportjson.tests.length; count++) {
        const test = reportjson.tests[count];
        if (test.status === 'failed') {
            const a11yIssues = test.a11yIssues;

            console.log('\t \t Page title : ' + test.documentTitle);
            console.log('\t \t Page url : ' + test.pageUrl);
            console.log('\t \t Screenshot of the page : ' + test.screenshot);
            console.log('\t \t Issues:');
            if (a11yIssues.length > 0) {
                for (let issueCounter = 0; issueCounter < a11yIssues.length; issueCounter++) {
                    console.log('\t \t \t ' + (issueCounter + 1) + '. ' + a11yIssues[issueCounter].code);
                    console.log('\t \t \t ' + a11yIssues[issueCounter].message);
                }
            } else {
                console.log('\t \t \t Error executing test steps');
            }
        }
        console.log('\t');
    }
}

module.exports = {generateAccessibilityReport};
