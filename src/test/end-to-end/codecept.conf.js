const testConfig = require('src/test/config.js');

exports.config = {
    'tests': testConfig.TestPathToRun,
    'output': testConfig.TestOutputDir,
    'helpers': {
        'Playwright': {
            'waitForTimeout': 60000,
            'getPageTimeout': 60000,
            'waitForAction': 1000,
            'show': testConfig.TestShowBrowserWindow,
            'waitForNavigation': 'domcontentloaded',
            'headless': 'true',
            video: {
                enabled: true,
                dir: `${testConfig.TestOutputDir}/`,
                // Only keep videos for failed tests
                keepVideoForPassedTests: false,
                // Specify size if needed
                size: { width: 1280, height: 720 }
            },
            'chrome': {
                'ignoreHTTPSErrors': true,
                'ignore-certificate-errors': true,
                'defaultViewport': {
                    'width': 1280,
                    'height': 960
                },
                args: [
                    '--headless=new',
                    '--disable-gpu',
                    '--no-sandbox',
                    '--allow-running-insecure-content',
                    '--ignore-certificate-errors',
                    // '--proxy-server=proxyout.reform.hmcts.net:8080',
                    // '--proxy-bypass-list=*beta*LB.reform.hmcts.net',
                    '--window-size=1440,1400'
                ]
            }
        },
        'PlaywrightHelper': {
            'require': './helpers/PlaywrightHelper.js'
        },
        'JSWait': {
            require: './helpers/JSWait.js'
        },
        'Mochawesome': {
            uniqueScreenshotNames: 'true'
        }
    },
    'include': {
        'I': './pages/steps.js'
    },
    'plugins': {
        'autoDelay': {
            'enabled': testConfig.TestAutoDelayEnabled
        },
        screenshotOnFail: {
            enabled: true,
            fullPageScreenshots: 'true'
        }
    },
    'mocha': {
        'reporterOptions': {
            'codeceptjs-cli-reporter': {
                stdout: '-',
                options: {steps: true}
            },
            'mocha-junit-reporter': {
                stdout: `${testConfig.TestOutputDir}/console.log`,
                options: {
                    'reportDir': testConfig.TestOutputDir,
                    'reportName': 'index',
                    'inlineAssets': true
                }
            },
            'mochawesome': {
                stdout: `${testConfig.TestOutputDir}/console.log`,
                options: {
                    'reportDir': testConfig.TestOutputDir,
                    'reportName': 'index',
                    'inlineAssets': true
                }
            }
        }
    },
    'name': 'Codecept Tests'
};
