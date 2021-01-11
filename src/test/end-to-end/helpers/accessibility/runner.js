const HTMLCS = require('html_codesniffer');
const testConfig = require('src/test/config');
const fs = require('fs');

const result = {
    PASSED: 'passed',
    FAILED: 'failed',
}

let resultObj = {
    appName:'Manage cases',
    pass: 0,
    fail:0,
    tests:[] 
}

async function runAccessibility(url, page) {
    //Add HMTL code sniffer script
    await page.addScriptTag({
        path: 'node_modules/html_codesniffer/build/HTMLCS.js'
    });

    const screenshotPath =  process.env.PWD  + '/functional-output/assets';
    const screenshotName = Date.now() + '.png';
    const screenshotReportRef = 'assets/' + screenshotName;

    const accessibilityErrorsOnThePage = await page.evaluate(() => {
            var processIssue = function(issue) {
                return {
                    code: issue.code,
                    message: issue.msg,
                    type: 'error',
                    element: issue.element, 
                    runner: 'htmlcs'
                };
            }

            var STANDARD = 'WCAG2AA';
            let messages;

            HTMLCS.process(STANDARD, window.document, function () {
                                            messages = HTMLCS
                                                            .getMessages()
                                                            .filter(function (m) {
                                                                return m.type === HTMLCS.ERROR
                                                            })
                                                            .map(processIssue);
            });

            return messages;
    });

    try {
        await page.screenshot({path: screenshotPath + screenshotName , fullPage: true });
    } catch(err) {
        fs.mkdirSync(screenshotPath);
        await page.screenshot({path: screenshotPath + screenshotName , fullPage: true });
    }
    
    
    updateResultObject(url, await page.title(), screenshotReportRef, accessibilityErrorsOnThePage);
}

function updateResultObject(url, pageTitle, screenshotReportRef, accessibilityErrorsOnThePage) {
    const isPageAccessible = accessibilityErrorsOnThePage.length === 0 ? result.PASSED : result.FAILED;

    if (isPageAccessible === result.PASSED) {
        resultObj.pass++;
    } else {
        resultObj.fail++; 
    }

    resultObj.tests.push({
        pageUrl: url,
        documentTitle: pageTitle,
        status: isPageAccessible,
        screenshot: screenshotReportRef,
        a11yIssues: accessibilityErrorsOnThePage
    });
}

function getAccessibilityTestResult() {
    return resultObj;
}

module.exports = { runAccessibility, getAccessibilityTestResult }