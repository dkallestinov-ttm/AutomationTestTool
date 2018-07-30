package automation;

import java.io.File;
import java.lang.reflect.Array;

public class CreateBinaries {
    private static String CURRENTDIRECTORYFILEPATH = System.getProperty("user.dir");
    public static String binariesFolderPath = CURRENTDIRECTORYFILEPATH + File.separator + "Binaries" + File.separator;

    //this method is used to generate Binaries as payloads for Rom Version Mid
    public static void createBinaries(int dsn, String profileName, String updateTypeIdAndTargetVersions) {
        RomVersionMid romVersionMid = new RomVersionMid();
        String DSN = Integer.toString(dsn);
        String[] romVersionMidName = {DSN+"_"+profileName+".dat",DSN};
        String[] updateTypeID_TargetVerdsions = updateTypeIdAndTargetVersions.split(" ");
        String[] romVersionMidPayload = concatenateArrays(romVersionMidName,updateTypeID_TargetVerdsions);
        try {
            //generating Rom Version Mid binary payload
            romVersionMid.execute(binariesFolderPath, romVersionMidPayload);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T[] concatenateArrays(T[] inputArrayA, T[] inputArrayB) {
        int aLen = inputArrayA.length;
        int bLen = inputArrayB.length;

        @SuppressWarnings("unchecked")
        T[] concatenatedArray = (T[]) Array.newInstance(inputArrayA.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(inputArrayA, 0, concatenatedArray, 0, aLen);
        System.arraycopy(inputArrayB, 0, concatenatedArray, aLen, bLen);
        return concatenatedArray;
    }
}
