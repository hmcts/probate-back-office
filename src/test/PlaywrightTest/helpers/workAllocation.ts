import { testConfig } from "../../PlaywrightTest/Configs/config.ts";
import * as path from "path";
import * as fs from "fs";
import * as yaml from "yaml";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export class WorkAllocation {
  //this has been written to pull the env var PROBATE_WA_ENABLED from config charts from the position java.environment.PROBATE_WA_ENABLED
  //it is unclear if this will be the final path for this env var so may need updating in the future
  static async isWaEnabled(): Promise<boolean> {
    //relative paths to aat and preview charts
    const aatValuesPath =
      "../../../../charts/probate-back-office/values.aat.template.yaml";
    const previewValuesPath =
      "../../../../charts/probate-back-office/values.preview.template.yaml";

    //get url from test config
    const envUrl = testConfig.TestBackOfficeUrl.toLowerCase();

    //use url to determine which environemnt being used and select right path for chart
    const chartConfigPath = path.resolve(
      __dirname,
      envUrl.includes("pr") ? previewValuesPath : aatValuesPath,
    );

    try {
      //read chart contents
      const chartConfigContents = await fs.promises.readFile(
        chartConfigPath,
        "utf8",
      );

      //grab java envs from chart as this is where PROBATE_WA_ENABLED lives
      const config = yaml.parse(chartConfigContents) as {
        java?: { environment?: { PROBATE_WA_ENABLED?: boolean } };
      };

      //return the value for PROBATE_WA_ENABLED or return false, if somethings fails also return false
      //BEST GUESS FOR LOCATION OF PROBATE_WA_ENABLED FOR NOW
      return config?.java?.environment?.PROBATE_WA_ENABLED ?? false;
    } catch (error) {
      console.error(
        `Failed to read or parse chart config at ${chartConfigPath}:`,
        error,
      );
      return false;
    }
  }
}
