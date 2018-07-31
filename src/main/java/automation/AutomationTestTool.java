package automation;

import java.util.ArrayList;
import com.github.jankroken.commandline.*;

public class AutomationTestTool {
    //this method is used to generate Binaries as payloads for Rom Version Mid
    private static void createBinaries(CommandLineArgs args) {
        ArrayList<String> looseArgs = args.getLooseArgs();
        int profileId = Integer.parseInt(looseArgs.get(1));
        String[] dsns = new String[looseArgs.size() - 2];

        looseArgs.subList(2, looseArgs.size()).toArray(dsns);

        BinaryManager manager = BinaryManagerFactory.create(args);

        manager.writeBinaries(profileId, dsns);
    }

    private static void smokeTest(CommandLineArgs args) {
        if (args.getEnv() == null) {
            throw new IllegalArgumentException("Environment undefined - use 'local', 'dev', or 'qa'");
        }

        APIClient client = new APIClient(args.getEnv());

        ArrayList<String> looseArgs = args.getLooseArgs();
        int profileId = Integer.parseInt(looseArgs.get(1));
        String[] dsns = new String[looseArgs.size() - 2];

        looseArgs.subList(2, looseArgs.size()).toArray(dsns);

        BinaryManager manager = BinaryManagerFactory.create(args);

        manager.writeBinaries(profileId, dsns);

        try {
            client.submitAndApproveBatch(profileId, dsns);
        } catch (Exception e) {
            System.err.println("Could not submit and approve batch");
            e.printStackTrace();
        }

        try {
            for (String dsn : dsns) {
                client.nextPackages(dsn);
            }
        } catch (Exception e) {
            System.err.println("Could not get next packages");
            e.printStackTrace();
        }

        try {
            for (String dsn : dsns) {
                client.packagesReady(dsn);
            }
        } catch (Exception e) {
            System.err.println("Could not mark packages as ready");
            e.printStackTrace();
        }

        try {
            for (String dsn : dsns) {
                client.otapCompleted(dsn, manager.getBaseFileName(profileId) + dsn + ".dat");
            }
        } catch (Exception e) {
            System.err.println("Could not complete otap");
            e.printStackTrace();
        }
    }

    private static void clean(CommandLineArgs args) {
        BinaryManager manager = BinaryManagerFactory.create(args);

        manager.cleanBinaryDir();
    }

    public static void main(String[] args) throws Exception {
        try {
            CommandLineArgs clArgs = CommandLineParser.parse(CommandLineArgs.class, args, OptionStyle.LONG_OR_COMPACT);

            if (clArgs.showHelp()) {
                clArgs.printHelp();
                return;
            }

            if (clArgs.getLooseArgs().size() == 0) {
                throw new IllegalArgumentException("Command undefined. Use --help for available commands");
            }

            String cmd = clArgs.getLooseArgs().get(0);

            switch(cmd) {
                case "createBinaries":
                    createBinaries(clArgs);
                    break;
                case "smokeTest":
                    smokeTest(clArgs);
                    break;
                case "clean":
                    clean(clArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported command " + cmd +". Use --help for supported commands.");
            }

        } catch (Exception clException) {
            clException.printStackTrace();
            System.exit(1);
        }
    }


}