const Helper = require('@codeceptjs/helper');

class CustomHelper extends Helper {

  // before/after hooks
  /**
   * @protected
   */
  _before() {
    // remove if not used
  }

  /**
   * @protected
   */
  _after() {
    // remove if not used
  }

  // add custom methods here
  async findPageElement(locator) {
    const { WebDriver } = this.helpers['Puppeteer'];
    await WebDriver._locate(locator);
  }

  /**
   * If you need to access other helpers
   * use: this.helpers['helperName']
   */

}

module.exports = CustomHelper;
