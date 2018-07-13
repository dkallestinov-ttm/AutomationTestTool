package service;

import automation.RomVersionMidArgs;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created on 7/18/2018.
 */
public class DataFileFactory {

    private File dataDirectory;

    public DataFileFactory(File dataDir) {
        dataDirectory = dataDir;
    }

    public int createFile( String filename ) throws Exception
    {
        File file = new File(filename);
        String[] arguments = new String[]{};

        RomVersionMidArgs romVersionMidArgs = new RomVersionMidArgs(arguments);
        file.mkdir();

        int blobMidHeaderMidLength = 64;
        int midHeaderMessageId = 210;
        int icapRomVersionMidHeaderMessageId = 216;

        try (FileOutputStream fos = new FileOutputStream(romVersionMidArgs.fileName);
             DataOutputStream dos = new DataOutputStream(fos)) {

            dos.writeInt(romVersionMidArgs.dsn);
            dos.writeShort(blobMidHeaderMidLength);
            dos.writeShort(midHeaderMessageId);
            dos.writeInt(0);
            //dos.writeUTF(""); // blobMidHeaderSchedule
            dos.writeByte(0);

            dos.writeInt(1); // blobMidRecordType
            dos.writeInt(1); // blobMidApplicationId
            dos.writeInt(1); // blobMidHostId
            dos.writeInt(1); // blobMidBlobInstance
            dos.writeInt(0); // blobMidBlobPieceNumber
            dos.writeInt(1); // blobMidEntireBlobSize
            dos.writeInt(1); // blobMidBlobBytesFollowing
            dos.writeShort(1); // icapRomVersionMidHeaderMidLength
            dos.writeShort(icapRomVersionMidHeaderMessageId); // icapRomVersionMidHeaderMessageId
            dos.writeInt(1); // icapRomVersionMidHeaderAsn
            //dos.writeUTF(""); // icapRomVersionMidHeaderSchedule
            dos.writeByte(0);

            dos.writeInt(romVersionMidArgs.versions.size()); // icapRomVersionMidUpdateCount
            for (int index = 0; index< romVersionMidArgs.versions.size(); index+=1) {
                dos.writeInt(romVersionMidArgs.updateTypeIds.get(index));
                dos.writeInt(romVersionMidArgs.versions.get(index));
            }
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
        }

        System.out.println("wrote dsn: " + romVersionMidArgs.dsn + " to file: " + romVersionMidArgs.fileName);
        return 1;
    }
}
