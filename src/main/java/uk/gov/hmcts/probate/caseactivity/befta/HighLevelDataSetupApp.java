package uk.gov.hmcts.probate.caseactivity.befta;

public class HighLevelDataSetupApp extends DataLoaderToDefinitionStore {
    public HighLevelDataSetupApp(CcdEnvironment dataSetupEnvironment) {
        super(dataSetupEnvironment);
    }

    public static void main(String[] args) throws Throwable {

        main(HighLevelDataSetupApp.class, args);
    }
}
