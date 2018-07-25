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
        beginOTAP(integerStartDSN,integerEndDSN,profileName, updateTypeIdAndTargetVersion);
        System.out.println("update_type_id, target version: " + updateTypeIdAndTargetVersion);
    }

    //This method sets the wheels in motion
    public static void beginOTAP(int startDSN, int endDSN, String profileName, String updateTypeIdAndTargetVersion) {
        ApiCall testObject = new ApiCall();

        for(int dsn = startDSN ; dsn <= endDSN ; dsn++){
            try {
                //create a Binary for the recently submitted dsn for OTAP
                //createBinaries(dsn,profileName, romVersionMidArguments_UpdateTypeID_AND_TargetVersions);
                int nextPackagesCode = testObject.nextPackages(dsn);
                if(nextPackagesCode==200 || nextPackagesCode==204 || nextPackagesCode==304){
                    int packagesReadyCode = testObject.packagesReady(dsn);
                    if(packagesReadyCode == 200 || packagesReadyCode == 204){
                        int otapCompletedCode = testObject.otapCompleted(dsn,profileName);
                        if(otapCompletedCode==200){
                            System.out.print("otap Completed for dsn: "+ dsn + " ");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
