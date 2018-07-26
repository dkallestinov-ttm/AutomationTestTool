package automation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class RomVersionMid
{
    static String DSN_LIST_DIR = System.getProperty("user.dir").replace("\\", "\\\\");
    static int BLOB_MID_HEADER_MID_LENGTH = 64;
    static Random MESSAGE_ID_GENERATOR = new Random();

    String fileName;
    int dsn;
    short midHeaderMessageId, icapRomVersionMidHeaderMessageId;
    InstalledSoftware[] installedSoftware;

    public RomVersionMid(String fileName, int dsn, String[] args) throws Exception {
        this(fileName, dsn, parseInstalledSoftware(args));
    }

    public RomVersionMid(String fileName, int dsn, InstalledSoftware[] installedSoftware) throws Exception{
        this.fileName = fileName;
        this.dsn = dsn;
        this.installedSoftware = installedSoftware;
        this.midHeaderMessageId = this.getRandomId();
        this.icapRomVersionMidHeaderMessageId = this.getRandomId();
    }

    private static InstalledSoftware[] parseInstalledSoftware(String[] args) throws Exception {
        if (args.length % 2 == 1) {
            throw new IllegalArgumentException(String.format("Expected an even number of arguments, got %S", args.length));
        }
        InstalledSoftware[] parsed = new InstalledSoftware[args.length/2];
        int index = 0, updateTypeId = 0, version;

        for (int i=0; i < args.length; i++) {
            if (i % 2 == 0) {
                index = i / 2;
                updateTypeId = Integer.parseInt(args[i]);
            } else {
                version = Integer.parseInt(args[i]);
                parsed[index] = new InstalledSoftware(updateTypeId, version);
            }
        }
        return parsed;
    }

    private short getRandomId() {
        return (short) (MESSAGE_ID_GENERATOR.nextInt(Short.MAX_VALUE) & Integer.MAX_VALUE);
    }
    /*
    let updateTypeAndVersionParser = new Parser()
        .endianess('big')
        .uint32('updateType')
        .uint32('version')

    let parser = new Parser()
        .endianess('big')
        .uint32('dsn')
        .uint16('blobMidHeaderMidLength')
        .uint16('blobMidHeaderMessageId')
        .uint32('blobMidHeaderAsn')
        .string('blobMidHeaderSchedule', stringOptions)
        .int32('blobMidRecordType')
        .uint32('blobMidApplicationId')
        .uint32('blobMidHostId')
        .uint32('blobMidBlobInstance')
        .uint32('blobMidBlobPieceNumber')
        .uint32('blobMidEntireBlobSize')
        .uint32('blobMidBlobBytesFollowing')
        .uint16('icapRomVersionMidHeaderMidLength')
        .uint16('icapRomVersionMidHeaderMessageId')
        .uint32('icapRomVersionMidHeaderAsn')
        .string('icapRomVersionMidHeaderSchedule', stringOptions)
        .uint32('icapRomVersionMidUpdateCount')
        .array('updateTypesAndVersions', {
            type: updateTypeAndVersionParser,
            length: 'icapRomVersionMidUpdateCount'
    })
     */
    public void write() throws Exception {
        String binaryDirPath = DSN_LIST_DIR + "\\Binaries\\";
        File binaryDir = new File(binaryDirPath);

        if (!binaryDir.exists()) {
            binaryDir.mkdir();
        }

        String[] existingBinaries = binaryDir.list();

        for (String binaryName : existingBinaries) {
            File binary = new File(binaryDirPath, binaryName);
            binary.delete();
        }

        // https://confluence.tekla.com/display/PNETTECH/MID+210%3A+OBC+Services+BLOB+OBC+to+PFM
        // https://confluence.tekla.com/display/PNETTECH/MID+216%3A+Return+ICAP+ROM+Version

        // https://confluence.tekla.com/display/PNETTECH/Mid+Header+Definition
        // Note: If there is no schedule, the MID header will be 9 bytes long.
        /*
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
         */

        try (FileOutputStream fos = new FileOutputStream(binaryDirPath + this.fileName);
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
            //dos.writeUTF(""); // icapRomVersionMidHeaderSchedule
            dos.writeByte(0);

            dos.writeInt(this.installedSoftware.length); // icapRomVersionMidUpdateCount
            for (int i = 0; i< this.installedSoftware.length; i++) {
                dos.writeInt(this.installedSoftware[i].getUpdateTypeId());
                dos.writeInt(this.installedSoftware[i].getVersion());
            }
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace(System.err);
        }

        System.out.println("wrote dsn: " + this.dsn + " to file: " + this.fileName);
    }
}