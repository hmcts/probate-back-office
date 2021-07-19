package uk.gov.hmcts.probate.caseactivity.befta;

import uk.gov.hmcts.befta.dse.ccd.DataLoaderToDefinitionStore;
import uk.gov.hmcts.befta.dse.ccd.CcdEnvironment;

public class HighLevelDataSetupApp extends DataLoaderToDefinitionStore {
    public HighLevelDataSetupApp(CcdEnvironment dataSetupEnvironment) {
        super(dataSetupEnvironment);
    }

    public static void main(String[] args) throws Throwable {

        main(HighLevelDataSetupApp.class, args);
    }
}
