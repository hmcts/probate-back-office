'use strict';

const testConfig = require('src/test/config.js');
const caseDetailsConfig = require('./caseDetailsConfig.json');
const GenericHelper = new (require('src/test/end-to-end/helpers/GenericHelper'))();
const TestConfigurator = new (require('src/test/end-to-end/helpers/TestConfigurator'))();
const {forEach} = require('lodash');

module.exports = function (caseRef) {

    const I = this;

    I.waitForText(caseDetailsConfig.waitForText, testConfig.TestTimeToWaitForText);
    I.see(caseRef);

    caseDetailsConfig.tabsList.forEach(function (value ,key) {
        console.log('value>>', value);
        console.log('key>>>', key);
        I.click(value.tabName);

        value.fields.forEach(function (fieldsValue, fieldsKey) {
            console.log('fieldsKey>>>', fieldsKey);
            console.log('fieldsValue>>>', fieldsValue);
             I.see(fieldsValue);  // <----- Need to add the field data somehow.....
        });

        pause();
    });
};
