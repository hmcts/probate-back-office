'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (caseRef, tabConfigFile, tabUpdates, tabUpdatesConfigFile) {

    const I = this;

    await I.see(caseRef);
    await I.clickTab(tabConfigFile.tabName);

    if (tabUpdates) {
        const updatedConfig = tabConfigFile[tabUpdates];

        for (let i = 0; i < updatedConfig.fields.length; i++) {
            // eslint-disable-next-line
            await I.waitForText(updatedConfig.fields[i]);
        }

        for (let i = 0; i < updatedConfig.dataKeys.length; i++) {
            // eslint-disable-next-line
            await I.waitForText(tabUpdatesConfigFile[updatedConfig.dataKeys[i]], testConfig.TestTimeToWaitForText || 60);
        }
    }
};
