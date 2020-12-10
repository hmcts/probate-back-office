const testConfig = require('src/test/config.js');

exports.config = {
    'tests': testConfig.TestPathToRun,
    'output': testConfig.TestOutputDir,
    'helpers': {
        'Puppeteer': {
            'url': testConfig.TestBackOfficeUrl,
            'waitForTimeout': 60000,
            'getPageTimeout': 60000,
            // 'waitForAction': 1,
            'show': testConfig.TestShowBrowserWindow,
            'waitForNavigation': ['domcontentloaded', 'networkidle0'],
            'chrome': {
                'ignoreHTTPSErrors': true,
                'ignore-certificate-errors': true,
                'defaultViewport': {
                    'width': 1280,
                    'height': 960
                },
                args: [
                    // '--headless', 
                    '--disable-gpu',
                    '--no-sandbox',
                    '--allow-running-insecure-content',
                    '--ignore-certificate-errors',
                    '--proxy-server=proxyout.reform.hmcts.net:8080',
                    '--proxy-bypass-list=*beta*LB.reform.hmcts.net',
                    '--window-size=1440,1400'
                ]
            },

        },
        'PuppeteerHelper': {
            'require': './helpers/PuppeteerHelper.js'
        },
        'JSWait': {
            require: './helpers/JSWait.js'
        },
    },
    'include': {
        'I': './pages/steps.js'
    },
    'plugins': {
        'autoDelay': {
            'enabled': testConfig.TestAutoDelayEnabled
        }
    },
    'multiple': {
        'parallel': {
            // Splits tests into 2 chunks
            'chunks': 2
        }
    },
    'mocha': {
        'reporterOptions': {
            'reportDir': testConfig.TestOutputDir,
            'reportName': 'index',
            'inlineAssets': true
        }
    },
    'name': 'Codecept Tests'
};
