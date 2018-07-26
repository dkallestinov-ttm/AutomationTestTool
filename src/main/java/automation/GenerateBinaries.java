package automation;

public class GenerateBinaries {
    public static void main(String[] args) {
        int profileId = Integer.parseInt(args[0]);
        String[] dsns = new String[args.length - 1];

        System.arraycopy(args, 1, dsns, 0, dsns.length);

        String updateTypeIdAndTargetVersion = ValkyrieSQL.execute(profileId);
        String profileName = ValkyrieSQL.getProfileNameFromTable(profileId);
        String baseFileName = profileName + "_";

        try {
            for (int i = 0; i < dsns.length ; i++) {
                int dsn = Integer.parseInt(dsns[i]);
                RomVersionMid mid = new RomVersionMid(baseFileName + dsn + ".dat", dsn, updateTypeIdAndTargetVersion.split(" "));
                mid.write();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
