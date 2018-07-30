package automation;

public class StartProcess {

    public static void main(String[] args) {
        String startDSN = "9999998";
        String endDSN = "9999999";
        String profileID = "104";
        String updateTypeIdAndTargetVersion = ValkyrieSQL.execute(Integer.parseInt(profileID));
        int integerStartDSN = Integer.parseInt(startDSN);
        int integerEndDSN = Integer.parseInt(endDSN);
        String profileName = ValkyrieSQL.getProfileNameFromTable(Integer.parseInt(profileID));
        try {
            CreateDsnListFile.createFileWithDSNs(integerStartDSN,integerEndDSN);
            String dsnForSubmittingBatch = CreateDsnListFile.readLineByLineFromFile();
            System.out.println(dsnForSubmittingBatch);
            for (int i = integerStartDSN; i <= integerEndDSN; i++) {
                CreateBinaries.createBinaries(i,profileName,updateTypeIdAndTargetVersion);
            }
            ApiCall.submitAndApproveBatch(dsnForSubmittingBatch,profileID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        beginOTAP(integerStartDSN,integerEndDSN,profileName);
        System.out.println("update_type_id, target version: " + updateTypeIdAndTargetVersion);
    }

    //This method takes care of making api calls to /nextPackages /packagesReady and /otapsCompleted for all DSNs
    public static void beginOTAP(int startDSN, int endDSN, String profileName) {
        ApiCall apiCall = new ApiCall();
        System.out.println("");
        for(int dsn = startDSN ; dsn <= endDSN ; dsn++){
            try {
                int nextPackagesCode = apiCall.nextPackages(dsn);
                if(nextPackagesCode==200 || nextPackagesCode==204 || nextPackagesCode==304){
                    int packagesReadyCode = apiCall.packagesReady(dsn);
                    if(packagesReadyCode == 200 || packagesReadyCode == 204){
                        int otapCompletedCode = apiCall.otapCompleted(dsn,profileName);
                        if(otapCompletedCode==200){
                            System.out.print("otap Completed for dsn: "+ dsn + " ");
                        }
                    }
                }
                System.out.println("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}