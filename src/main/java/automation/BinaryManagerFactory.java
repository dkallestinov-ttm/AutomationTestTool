package automation;

public class BinaryManagerFactory {

    static BinaryManager create(CommandLineArgs args) {
        ValkyrieDAO dao;
        if (args.getEnv() != null) {
            if (args.getUser() != null) {
                if (args.getDriver() != null) {
                    dao = new ValkyrieDAO(args.getEnv(), args.getUser(), args.getPassword(), args.getDriver());
                } else {
                    dao = new ValkyrieDAO(args.getEnv(), args.getUser(), args.getPassword());
                }
            } else {
                dao = new ValkyrieDAO(args.getEnv());
            }
        } else {
            if (args.getUser() != null) {
                dao = new ValkyrieDAO(args.getUser(), args.getPassword());
            } else {
                dao = new ValkyrieDAO();
            }
        }

        if (args.getTargetDir() != null) {
            return new BinaryManager(args.getTargetDir(), dao);
        }

        return new BinaryManager(dao);
    }
}
