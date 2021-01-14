const LATEST_MAC = 'macOS 10.15';
const LATEST_WINDOWS = 'Windows 10';

const supportedBrowsers = {
    microsoft: {
        ie11_win: {
            browserName: 'internet explorer',
            platformName: LATEST_WINDOWS,
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Caveats: IE11',
                screenResolution: '1400x1050'
            }
        },
        edge_win_latest: {
            browserName: 'MicrosoftEdge',
            platformName: LATEST_WINDOWS,
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Caveats: Edge_Win10'
            }
        }
    },
    safari: {
        safari_mac: {
            browserName: 'safari',
            platformName: 'macOS 10.14',
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Caveats: MAC_SAFARI',
                seleniumVersion: '3.141.59',
                screenResolution: '1400x1050'
            }
        }
    },
    chrome: {
        chrome_win_latest: {
            browserName: 'chrome',
            platformName: LATEST_WINDOWS,
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Caveats: WIN_CHROME_LATEST'
            }
        },
        chrome_mac_latest: {
            browserName: 'chrome',
            platformName: LATEST_MAC,
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Caveats: MAC_CHROME_LATEST'
            }
        }
    },
    firefox: {
        firefox_win_latest: {
            browserName: 'firefox',
            platformName: LATEST_WINDOWS,
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Caveats: WIN_FIREFOX_LATEST'
            }
        },
        firefox_mac_latest: {
            browserName: 'firefox',
            platformName: LATEST_MAC,
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Caveats: MAC_FIREFOX_LATEST'
            }
        }
    }
};

module.exports = supportedBrowsers;
