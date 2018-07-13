package automation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class RomVersionMid
{
    static String DSNLISTFILEPATH = System.getProperty("user.dir").replace("\\", "\\\\");
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
    public static void execute( String[] arguments ) throws Exception
    {
        RomVersionMidArgs romVersionMidArgs = new RomVersionMidArgs(arguments);
        String filePath=DSNLISTFILEPATH+"\\Binaries\\";
        new File(filePath).mkdir();

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
        int blobMidHeaderMidLength = 64;
        int midHeaderMessageId = 210;
        int icapRomVersionMidHeaderMessageId = 216;

        try (FileOutputStream fos = new FileOutputStream(filePath+ romVersionMidArgs.fileName);
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
    }
}