package automation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class RomVersionMid
{
    static int BLOB_MID_HEADER_MID_LENGTH = 64;
    static Random MESSAGE_ID_GENERATOR = new Random();

    String fileName;
    int dsn;
    short midHeaderMessageId, icapRomVersionMidHeaderMessageId;
    ArrayList<InstalledSoftware> installedSoftware;

    public RomVersionMid(String fileName, int dsn, String[] args) throws Exception {
        this(fileName, dsn, parseInstalledSoftware(args));
    }

    public RomVersionMid(String fileName, int dsn, ArrayList<InstalledSoftware> installedSoftware) throws Exception{
        this.fileName = fileName;
        this.dsn = dsn;
        this.installedSoftware = installedSoftware;
        this.midHeaderMessageId = this.getRandomId();
        this.icapRomVersionMidHeaderMessageId = this.getRandomId();
    }

    private static ArrayList<InstalledSoftware> parseInstalledSoftware(String[] args) throws Exception {
        if (args.length % 2 == 1) {
            throw new IllegalArgumentException(String.format("Expected an even number of arguments, got %S", args.length));
        }
        ArrayList<InstalledSoftware> parsed = new ArrayList<>();
        int updateTypeId = 0, version;

        for (int i=0; i < args.length; i++) {
            if (i % 2 == 0) {
                updateTypeId = Integer.parseInt(args[i]);
            } else {
                version = Integer.parseInt(args[i]);
                parsed.add(new InstalledSoftware(updateTypeId, version));
            }
        }
        return parsed;
    }

    private short getRandomId() {
        return (short) (MESSAGE_ID_GENERATOR.nextInt(Short.MAX_VALUE) & Integer.MAX_VALUE);
    }

    /**
     *  https://confluence.tekla.com/display/PNETTECH/MID+210%3A+OBC+Services+BLOB+OBC+to+PFM
        https://confluence.tekla.com/display/PNETTECH/MID+216%3A+Return+ICAP+ROM+Version

        https://confluence.tekla.com/display/PNETTECH/Mid+Header+Definition
        Note: If there is no schedule, the MID header will be 9 bytes long.
        Sample:
            006C267D004000D20000000000000000000A9B8D2A048C43795928A895000000000000
            001D0000001D001B00D80000000000000000020000138800003F9A000002C200003F9B
            dsn                                006C267D :    7087741
            blobMidHeaderMidLength                 0040 :         64
            blobMidHeaderMessageId                 00D2 :        210
            blobMidHeaderAsn                   00000000 :          0
            blobMidHeaderSchedule                    00 :         ''
            blobMidRecordType                  00000000 :          0
            blobMidApplicationId               0A9B8D2A :  177966378
            blobMidHostId                      048C4379 :   76301177
            blobMidBlobInstance                5928A895 : 1495836821
            blobMidBlobPieceNumber             00000000 :          0
            blobMidEntireBlobSize              0000001D :         29
            blobMidBlobBytesFollowing          0000001D :         29
            icapRomVersionMidHeaderMidLength       001B :         27
            icapRomVersionMidHeaderMessageId       00D8 :        216
            icapRomVersionMidHeaderAsn         00000000 :          0
            icapRomVersionMidHeaderSchedule          00 :         ''
            icapRomVersionMidUpdateCount       00000002 :          2
            updateType                         00001388 :       5000
            version                            00003F9A :      16282
            updateType                         000002C2 :        706
            version                            00003F9B :      16283
     * @throws Exception
     */
    public void write() throws Exception {
        try (FileOutputStream fos = new FileOutputStream(this.fileName);
             DataOutputStream dos = new DataOutputStream(fos)) {

            dos.writeInt(this.dsn);
            dos.writeShort(BLOB_MID_HEADER_MID_LENGTH);
            dos.writeShort(this.midHeaderMessageId);
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
            dos.writeShort(this.icapRomVersionMidHeaderMessageId); // icapRomVersionMidHeaderMessageId
            dos.writeInt(1); // icapRomVersionMidHeaderAsn
//            dos.writeUTF(""); // icapRomVersionMidHeaderSchedule
            dos.writeByte(0);

            dos.writeInt(this.installedSoftware.size()); // icapRomVersionMidUpdateCount
            for (int i = 0; i< this.installedSoftware.size(); i++) {
                dos.writeInt(this.installedSoftware.get(i).getUpdateTypeId());
                dos.writeInt(this.installedSoftware.get(i).getVersion());
            }
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
        }

        System.out.println("wrote dsn: " + this.dsn + " to file: " + this.fileName);
    }
}