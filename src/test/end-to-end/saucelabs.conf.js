const supportedBrowsers = require('../crossbrowser/supportedBrowsers.js');

const browser = process.env.SAUCELABS_BROWSER || 'chrome';
const tunnelName = process.env.TUNNEL_IDENTIFIER || '';

const setupConfig = {
    'tests': './paths/*.js',
    'output': './output',
    'timeout': 20000,
    'helpers': {
        WebDriverIO: {
            url: process.env.TEST_E2E_FRONTEND_URL || 'https://localhost:3000',
            browser: supportedBrowsers[browser].browserName,
            waitforTimeout: 60000,
            cssSelectorsEnabled: 'true',
            windowSize: '1600x900',
            timeouts: {
                script: 60000,
                'page load': 60000,
                implicit: 20000
            },
            'host': 'ondemand.saucelabs.com',
            'port': 80,
            'user': process.env.SAUCE_USERNAME,
            'key': process.env.SAUCE_ACCESS_KEY,
            desiredCapabilities: getDesiredCapabilities()
        },

        'JSWait': {
            'require': './helpers/JSWait.js'
        },

        'SauceLabsReportingHelper': {
            'require': './helpers/SauceLabsReportingHelper.js'
        }
    },
    'include': {
        'I': './pages/steps.js'
    },
    'mocha': {
        'reporterOptions': {
            'reportDir': process.env.E2E_CROSSBROWSER_OUTPUT_DIR || './output',
            'reportName': browser + '_report',
            'reportTitle': 'Crossbrowser results for: ' + browser.toUpperCase(),
            'inlineAssets': true
        }
    },
    'name': 'frontEnd Tests'
};

function getDesiredCapabilities() {
    const desiredCapability = supportedBrowsers[browser];
    desiredCapability.tunnelIdentifier = tunnelName;
    desiredCapability.tags = ['probate'];
    return desiredCapability;
}

exports.config = setupConfig;
