'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (caseRef, tabConfigFile, tabUpdates, tabUpdatesConfigFile, forUpdateApplication) {

    const I = this;

    await I.see(caseRef);
    await I.clickTab(tabConfigFile.tabName);
    await I.runAccessibilityTest();

    if (tabUpdates) {
        const updatedConfig = tabConfigFile[tabUpdates];
        let fields = updatedConfig.fields;
        let keys = updatedConfig.dataKeys;
        if (forUpdateApplication) {
            fields = fields.concat(updatedConfig.updateAppFields);
            keys = keys.concat(updatedConfig.updateAppDataKeys);
        }

        for (let i = 0; i < fields.length; i++) {
            // eslint-disable-next-line
            await I.waitForText(fields[i]);
        }

        for (let i = 0; i < keys.length; i++) {
            // eslint-disable-next-line
            await I.waitForText(tabUpdatesConfigFile[keys[i]], testConfig.WaitForTextTimeout || 60);
        }
    }
};
