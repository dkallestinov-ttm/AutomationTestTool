package automation;

import java.util.ArrayList;
import java.util.Arrays;

public class RomVersionMidArgs {
    public String fileName;
    public int dsn;
    public ArrayList<Integer> updateTypeIds;
    public ArrayList<Integer> versions;

    public RomVersionMidArgs(String[] args) throws Exception {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));

        fileName = getFileName(arguments);
        dsn = getDsn(arguments);
        setUpdateTypesAndVersions(arguments);
    }

    private boolean IsIntegralValue(String s) {
        try {
            Integer.parseInt(s, 10);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getFileName(ArrayList<String> args) {
        if (args.isEmpty() || IsIntegralValue(args.get(0))) {
            return "someFile.dat";
        } else {
            return args.remove(0);
        }
    }

    private int getDsn(ArrayList<String> args) {
        int dsn = 1000000;
        if (!args.isEmpty()) {
            String s = args.remove(0);
            try {
                dsn = Integer.parseInt(s, 10);
            } catch (NumberFormatException e) {
                System.err.println("could not parse '" + s + "' as dsn");
            }
        }
        return dsn;
    }

    private void setUpdateTypesAndVersions(ArrayList<String> args) throws Exception {
        if (args.size() % 2 == 1) {
            throw new Exception(
                    "must have equal number of args for UpdateTypeIds and Versions - found " + args.size()
            );
        }

        updateTypeIds = new ArrayList<>();
        versions = new ArrayList<>();

        while (!args.isEmpty()) {
            updateTypeIds.add(Integer.parseInt(args.remove(0)));
            versions.add(Integer.parseInt(args.remove(0)));
        }
    }
}