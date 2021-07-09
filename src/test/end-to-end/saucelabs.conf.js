const supportedBrowsers = require('../crossbrowser/supportedBrowsers.js');

const testConfig = require('../config');

const waitForTimeout = parseInt(process.env.WAIT_FOR_TIMEOUT) || 45000;
const smartWait = parseInt(process.env.SMART_WAIT) || 30000;
const browser = process.env.BROWSER_GROUP || 'chrome';
const defaultSauceOptions = {
    username: process.env.SAUCE_USERNAME,
    accessKey: process.env.SAUCE_ACCESS_KEY,
    tunnelIdentifier: process.env.TUNNEL_IDENTIFIER || 'reformtunnel',
    acceptSslCerts: true,
    windowSize: '1600x900',
    tags: ['probate_expertui']
};

function merge (intoObject, fromObject) {
    return Object.assign({}, intoObject, fromObject);
}

function getBrowserConfig(browserGroup) {
    const browserConfig = [];
    for (const candidateBrowser in supportedBrowsers[browserGroup]) {
        if (candidateBrowser) {
            const candidateCapabilities = supportedBrowsers[browserGroup][candidateBrowser];
            candidateCapabilities['sauce:options'] = merge(
                defaultSauceOptions, candidateCapabilities['sauce:options']
            );
            browserConfig.push({
                browser: candidateCapabilities.browserName,
                capabilities: candidateCapabilities
            });
        } else {
            // eslint-disable-next-line no-console
            console.error('ERROR: supportedBrowsers.js is empty or incorrectly defined');
        }
    }
    return browserConfig;
}

const setupConfig = {
    'tests': testConfig.TestPathToRun,
    'output': `${process.cwd()}/${testConfig.TestOutputDir}`,
    'helpers': {
        WebDriver: {
            browser,
            smartWait,
            waitForTimeout,
            cssSelectorsEnabled: 'true',
            host: 'ondemand.eu-central-1.saucelabs.com',
            port: 80,
            region: 'eu',
            capabilities: {}
        },
        WebDriverHelper: {
            require: './helpers/WebDriverHelper.js'
        },
        SauceLabsReportingHelper: {
            require: './helpers/SauceLabsReportingHelper.js'
        },
        JSWait: {
            require: './helpers/JSWait.js'
        },
        Mochawesome: {
            uniqueScreenshotNames: 'true'
        }
    },
    plugins: {
        retryFailedStep: {
            enabled: true,
            retries: 2
        },
        autoDelay: {
            enabled: testConfig.TestAutoDelayEnabled,
            delayAfter: 2000
        }
    },
    include: {
        I: './pages/steps.js'
    },

    mocha: {
        reporterOptions: {
            'codeceptjs-cli-reporter': {
                stdout: '-',
                options: {steps: true}
            },
            'mocha-junit-reporter': {
                stdout: '-',
                options: {mochaFile: `${testConfig.TestOutputDir}/result.xml`}
            },
            mochawesome: {
                stdout: testConfig.TestOutputDir + '/console.log',
                options: {
                    reportDir: testConfig.TestOutputDir,
                    reportName: 'index',
                    reportTitle: 'Crossbrowser results for: ' + browser.toUpperCase(),
                    inlineAssets: true
                }
            }
        }
    },
    multiple: {
        microsoft: {
            browsers: getBrowserConfig('microsoft')
        },
        chrome: {
            browsers: getBrowserConfig('chrome')
        },
        firefox: {
            browsers: getBrowserConfig('firefox')
        },
        safari: {
            browsers: getBrowserConfig('safari')
        }
    },
    name: 'Probate-ExpertUI Cross-Browser Tests'
};

exports.config = setupConfig;
