package automation;

import java.io.File;
import java.util.ArrayList;

public class BinaryManager {
    private ValkyrieDAO dao;
    private String binaryDirPath;
    private static final String DEFAULT_BINARY_DIR = System.getProperty("user.dir").replace("\\", "\\\\") + "\\Binaries\\";

    public BinaryManager(ValkyrieDAO dao) {
        this(DEFAULT_BINARY_DIR, dao);
    }

    public BinaryManager(String binaryDirPath) {
        this(binaryDirPath, new ValkyrieDAO());
    }

    public BinaryManager(String binaryDirPath, ValkyrieDAO dao) {
        this.binaryDirPath = binaryDirPath;
        this.dao = dao;
    }

    private void createBinaryDir() {
        File binaryDir = new File(this.binaryDirPath);

        if (!binaryDir.exists()) {
            binaryDir.mkdir();
        }
    }

    public String getPathToBinaries() {
        return this.binaryDirPath;
    }

    public String getBaseFileName(int profileId) {
        String profileName = this.dao.getProfileNameFromTable(profileId);

        return this.binaryDirPath + profileName + "_";
    }

    public void writeBinaries(int profileId, String[] dsns) {
        this.createBinaryDir();

        ArrayList<InstalledSoftware> installedSoftware = this.dao.getProfileUpdateTypeIdsAndVersions(profileId);
        String baseFileName = this.getBaseFileName(profileId);

        try {
            for (int i = 0; i < dsns.length ; i++) {
                int dsn = Integer.parseInt(dsns[i]);
                RomVersionMid mid = new RomVersionMid(baseFileName + dsn + ".dat", dsn, installedSoftware);
                mid.write();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanBinaryDir() {
        File binaryDir = new File(this.binaryDirPath);

        if (!binaryDir.exists()) {
            return;
        }

        String[] existingBinaries = binaryDir.list();

        for (String binaryName : existingBinaries) {
            File binary = new File(this.binaryDirPath, binaryName);
            binary.delete();
        }

        binaryDir.delete();
    }

}
