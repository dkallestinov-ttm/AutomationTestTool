package automation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Stream;

import com.github.jankroken.commandline.*;
import com.squareup.okhttp.*;

public class AutomationTestTool {
    private static OkHttpClient client = new OkHttpClient();
    private static final String DATEFORMAT = "yyyy-MM-dd";
    private static String CURRENTDIRECTORYFILEPATH = System.getProperty("user.dir");
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASICAUTH = "Basic b21uaUFkYXB0ZXI6azN5U2l4cmlkMW9n";
    private static final String VALKYRIEQAURL = "https://valkyrie.qa.connectedfleet.io";
    private static final String APPLICATIONJSON = "application/json";
    private static final String CACHECONTROL = "Cache-Control";
    private static final String CONTENTTYPE = "Content-Type";
    private static final String NOCACHE = "no-cache";

    private boolean showHelp = false;

    //Date in UTC because Valkyrie needs UTC time to schedule an OTAP
    private static String getUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        return (utcTime+"T00:00:00Z");
    }

    //Scheduling Multiple DSNs
    private static String submitBatch(String[] dsns) throws IOException {
        String setCurrentDateAsScheduledDate = getUTCdatetimeAsString();
        String JSONToPost = "{\r\n  \"submittedBy\": \"Aditya Test via Java\",\r\n  \"scheduledDate\": \""+setCurrentDateAsScheduledDate+"\",\r\n  \"dsns\": [\r\n    "+String.join(",", dsns)+"\r\n  ],\r\n  \"profile\": {\r\n    \"id\": 650448,\r\n    \"name\": \"ASTest1\"\r\n  },\r\n  \"name\": \"API_Tool\"\r\n}";

        // POST Request to SUBMIT a batch to Valkyrie
        MediaType mediaType = MediaType.parse(APPLICATIONJSON);
        RequestBody body = RequestBody.create(mediaType, JSONToPost);
        Request request = new Request.Builder()
                .url(VALKYRIEQAURL+"/submitBatch")
                .post(body)
                .addHeader(CONTENTTYPE, APPLICATIONJSON)
                .addHeader(CACHECONTROL, NOCACHE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("submitBatch: "+response.code());
        return response.body().string();
    }

    //Scheduling Multiple DSNs
    private static String submitAndApproveBatch(String[] dsns) throws IOException {
        String setCurrentDateAsScheduledDate = getUTCdatetimeAsString();
        String JSONToPost = "{\r\n  \"submittedBy\": \"Aditya Test via Java\",\r\n  \"scheduledDate\": \""+setCurrentDateAsScheduledDate+"\",\r\n  \"dsns\": [\r\n    "+String.join(",", dsns)+"\r\n  ],\r\n  \"profile\": {\r\n    \"id\": 650448,\r\n    \"name\": \"ASTest1\"\r\n  },\r\n  \"name\": \"API_Tool\"\r\n}";

        // POST Request to SUBMIT a batch to Valkyrie
        MediaType mediaType = MediaType.parse(APPLICATIONJSON);
        RequestBody body = RequestBody.create(mediaType, JSONToPost);
        Request request = new Request.Builder()
                .url(VALKYRIEQAURL+"/submitAndApproveBatch")
                .post(body)
                .addHeader(CONTENTTYPE, APPLICATIONJSON)
                .addHeader(CACHECONTROL, NOCACHE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("submitAndApproveBatch: "+response.code());
        return response.body().string();
    }

    private static int nextPackages(String dsn) throws IOException {
        String nextPackagesURLString = VALKYRIEQAURL+"/nextPackages/"+dsn;

        // GET Request for nextPackages endpoint to Valkyrie
        Request request = new Request.Builder()
                .url(nextPackagesURLString)
                .get()
                .addHeader(AUTHORIZATION, BASICAUTH)
                .addHeader(CACHECONTROL, NOCACHE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("nextPackages: "+ response.code() +" ");
        //return response.body().string();
        return response.code();
    }

    private static int packagesReady(String dsn) throws IOException {
        String packagesReadyURLString=VALKYRIEQAURL+"/packagesReady/p.mtp."+dsn+".otap.module-ready";

        // PUT Request for nextPackages endpoint to Valkyrie
        Request request = new Request.Builder()
                .url(packagesReadyURLString)
                .put(null)
                .addHeader(AUTHORIZATION, BASICAUTH)
                .addHeader(CACHECONTROL, NOCACHE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("packagesReady: "+response.code() +" ");
        //return response.body().string();
        return response.code();
    }

    private static int otapCompleted(String dsn, String fileName) throws IOException {
        String otapCompletedURLString=VALKYRIEQAURL+"/otapCompleted/p.mop."+dsn+".210.icap-rom-version";

        // POST Request for nextPackages endpoint to Valkyrie
        MediaType mediaType = MediaType.parse("application/octet-stream");
        Request request = new Request.Builder()
                .url(otapCompletedURLString)
                .post(RequestBody.create(mediaType, Files.readAllBytes(Paths.get(fileName))))
                .addHeader(AUTHORIZATION, BASICAUTH)
                .addHeader(CACHECONTROL, NOCACHE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("otapCompleted: " + response.code() + " ");
        //return response.body().string();
        return response.code();
    }

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
        ArrayList<String> looseArgs = args.getLooseArgs();
        int profileId = Integer.parseInt(looseArgs.get(1));
        String[] dsns = new String[looseArgs.size() - 2];

        looseArgs.subList(2, looseArgs.size()).toArray(dsns);

        BinaryManager manager = BinaryManagerFactory.create(args);

        manager.writeBinaries(profileId, dsns);

        try {
            submitAndApproveBatch(dsns);
        } catch (Exception e) {
            System.err.println("Could not submit and approve batch");
            e.printStackTrace();
        }

        try {
            for (String dsn : dsns) {
                nextPackages(dsn);
            }
        } catch (Exception e) {
            System.err.println("Could not get next packages");
            e.printStackTrace();
        }

        try {
            for (String dsn : dsns) {
                packagesReady(dsn);
            }
        } catch (Exception e) {
            System.err.println("Could not mark packages as ready");
            e.printStackTrace();
        }

        try {
            for (String dsn : dsns) {
                otapCompleted(dsn, manager.getBaseFileName(profileId) + dsn + ".dat");
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