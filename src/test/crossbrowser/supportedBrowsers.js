const LATEST_MAC = 'macOS 10.15';
const LATEST_WINDOWS = 'Windows 10';

const supportedBrowsers = {
    microsoft: {
        ie11_win: {
            browserName: 'internet explorer',
            platformName: LATEST_WINDOWS,
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Back Office - XUI: IE11',
                screenResolution: '1400x1050'
            }
        },
        edge_win_latest: {
            browserName: 'MicrosoftEdge',
            platformName: LATEST_WINDOWS,
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Back Office - XUI: Edge_Win10'
            }
        }
    },
    webkit: {
        webkit_mac_latest: {
            browserName: 'webkit',
            platformName: 'macOS 11',
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Back Office - XUI: MAC_WEBKIT_LATEST',
                screenResolution: '1400x1050'
            }
        }
    },
    safari: {
        safari_mac_latest: {
            browserName: 'safari',
            platformName: 'macOS 10.14',
            browserVersion: 'latest',
            'sauce:options': {
                name: 'Probate Back Office - XUI: MAC_SAFARI',
                seleniumVersion: '3.141.59',
                screenResolution: '1376x1032'
            }
        }
    },
    chromium: {
        chrome_win_latest: {
            browserName: 'chromium',
            platformName: LATEST_WINDOWS,
            browserVersion: 'latest-1',
            'sauce:options': {
                name: 'Probate Back Office - XUI: WIN_CHROME_LATEST',
                screenResolution: '1600x1200'
            }
        },
        chrome_mac_latest: {
            browserName: 'chromium',
            platformName: LATEST_MAC,
            browserVersion: 'latest-1',
            'sauce:options': {
                name: 'Probate Back Office - XUI: MAC_CHROME_LATEST',
                screenResolution: '1600x1200'
            }
        }
    },
    firefox: {
        firefox_win_latest: {
            browserName: 'firefox',
            platformName: LATEST_WINDOWS,
            browserVersion: 'latest-1',
            'sauce:options': {
                name: 'Probate Back Office - XUI: WIN_FIREFOX_LATEST',
                screenResolution: '1600x1200'
            }
        },
        firefox_mac_latest: {
            browserName: 'firefox',
            platformName: LATEST_MAC,
            browserVersion: 'latest-1',
            'sauce:options': {
                name: 'Probate Back Office - XUI: MAC_FIREFOX_LATEST',
                screenResolution: '1600x1200'
            }
        }
    }
};

module.exports = supportedBrowsers;
