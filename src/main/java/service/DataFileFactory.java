package service;

import automation.InstalledSoftware;
import automation.RomVersionMid;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created on 7/18/2018.
 */
public class DataFileFactory {

    private File dataDirectory;

    public DataFileFactory(File dataDir) {
        dataDirectory = dataDir;
    }

    public void createFile( String filename ) throws Exception
    {

        ArrayList<InstalledSoftware> installedSoftware = new ArrayList<>();
        installedSoftware.add(new InstalledSoftware(5, 0 ));
        RomVersionMid mid = new RomVersionMid(filename, 10032, installedSoftware);

        mid.write();
    }
}
