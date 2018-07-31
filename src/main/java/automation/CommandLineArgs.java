package automation;

import com.github.jankroken.commandline.annotations.*;

import java.util.ArrayList;

public class CommandLineArgs {

    private boolean help;
    private String user = null;
    private String password = null;
    private String driver = null;
    private String filePath = null;
    private ArrayList<String> looseArgs = new ArrayList<>();
    private Environment env = null;

    @Option
    @ShortSwitch("h")
    @LongSwitch("help")
    @Toggle(true)
    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean showHelp() {
        return this.help;
    }

    @Option
    @ShortSwitch("u")
    @LongSwitch("user")
    @SingleArgument
    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }

    @Option
    @ShortSwitch("p")
    @LongSwitch("password")
    @SingleArgument
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    @Option
    @ShortSwitch("d")
    @LongSwitch("driver")
    @SingleArgument
    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return this.driver;
    }

    @Option
    @ShortSwitch("t")
    @LongSwitch("targetDir")
    @SingleArgument
    public void setFilePath(String path) {
        this.filePath = path;
    }

    public String getTargetDir() {
        return this.filePath;
    }

    @Option
    @ShortSwitch("e")
    @LongSwitch("env")
    @SingleArgument
    public void setEnv(String env) {
        switch (env) {
            case "local":
                this.env = Environment.LOCAL;
                break;
            case "dev":
                this.env = Environment.DEV;
                break;
            case "qa":
                this.env = Environment.QA;
                break;
        }
    }

    public Environment getEnv() {
        return env;
    }

    @Option
    @LooseArguments
    public void setLooseArgs(String arg) {
        this.looseArgs.add(arg);
    }

    public ArrayList<String> getLooseArgs() {
        return this.looseArgs;
    }

    public void printHelp() {
        StringBuffer helpMsgBuf = new StringBuffer();
        helpMsgBuf.append("Usage: \n");
        helpMsgBuf.append("\tjava -jar AutomationTestTool.jar [options] createBinaries [arguments] \n");
        helpMsgBuf.append("\tjava -jar AutomationTestTool.jar [options] smokeTest [arguments] \n");
        helpMsgBuf.append("\tjava -jar AutomationTestTool.jar [options] clean [arguments] \n");
        helpMsgBuf.append("\n");
        helpMsgBuf.append("Options:\n");
        helpMsgBuf.append("\t-u, --user        The User to connect to the database with. Defaults to \"valkyrie_db\"\n");
        helpMsgBuf.append("\t-p, --password    Password to use for the database connection. Defaults to the password for the valkyrie_db user\n");
        helpMsgBuf.append("\t-d, --driver      Driver to use for database connection. Defaults to \"com.mysql.cj.jdbc.Driver\"\n");
        helpMsgBuf.append("\t-t, --targetDir   Where to store the generated binary files. Defaults to ./Binaries\n");
        helpMsgBuf.append("\t-e, --env         What environment to use for API calls and database connections. 'local', 'dev', or 'qa'");
        helpMsgBuf.append("\n");
        helpMsgBuf.append("Actions:\n");
        helpMsgBuf.append("\tcreateBinaries    Create binaries for the given dsns that indicate successful installation of all packages for the given profile\n");
        helpMsgBuf.append("\tsmokeTest         Runs the given dsns through a lifecycle, including creation of mids\n");
        helpMsgBuf.append("\tclean             Cleans and deletes the binary directory\n");
        helpMsgBuf.append("\n");
        helpMsgBuf.append("Arguments:\n");
        helpMsgBuf.append("\tprofileId         The id of the profile to use for the test. This profile's dependencies will determine the contents of the binary files\n");
        helpMsgBuf.append("\tdsns              The remaining arguments: a list of dsns on which to perform the given action\n");

        System.out.print(helpMsgBuf.toString());
    }
}
