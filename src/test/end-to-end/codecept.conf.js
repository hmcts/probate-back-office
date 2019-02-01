const testConfig = require('src/test/config.js');

exports.config = {
    'tests': './paths/**/*.js',
    'output': './output',
    'helpers': {
        'Puppeteer': {
            'url': testConfig.TestE2EFrontendUrl || 'http://localhost:3000',
            'waitForTimeout': 60000,
            'getPageTimeout': 30000,
            'waitForAction': 1500,
            'show': true,
            'chrome': {
                'ignoreHTTPSErrors': true,
                'ignore-certificate-errors': true,
                'defaultViewport': {
                    'width': 1280,
                    'height': 960
                },
                args: [
                    '--no-sandbox',
                    '--proxy-server=proxyout.reform.hmcts.net:8080',
                    '--proxy-bypass-list=*beta*LB.reform.hmcts.net',
                    '--window-size=1440,1400'
                ]
            },
        },
        'PuppeteerHelper': {
            'require': './helpers/PuppeteerHelper.js'
        },
    },
    'include': {
        'I': './pages/steps.js'
    },
    'plugins': {
        'autoDelay': {
            'enabled': true
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
            'reportDir': process.env.E2E_OUTPUT_DIR || './output',
            'reportName': 'index',
            'inlineAssets': true
        }
    },
    'name': 'Codecept Tests'
};
