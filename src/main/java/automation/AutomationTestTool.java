package automation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Stream;
import com.squareup.okhttp.*;

public class AutomationTestTool {
    private OkHttpClient client = new OkHttpClient();
    private static final String DATEFORMAT = "yyyy-MM-dd";
    private static String CURRENTDIRECTORYFILEPATH = System.getProperty("user.dir");
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASICAUTH = "Basic b21uaUFkYXB0ZXI6azN5U2l4cmlkMW9n";
    private static final String VALKYRIEQAURL = "https://valkyrie.qa.connectedfleet.io";
    private static final String APPLICATIONJSON = "application/json";
    private static final String  CACHECONTROL = "Cache-Control";
    private static final String CONTENTTYPE = "Content-Type";
    private static final String NOCACHE = "no-cache";

    //Date in UTC because Valkyrie needs UTC time to schedule an OTAP
    private static String getUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        return (utcTime+"T00:00:00Z");
    }

    //Method to create DSN List and store the numbers with comma separation. This will be used while giving SubmitAndApprove batch an input of 50,000 dsns in a single API Call.
    private static void createFileWithDSNs(String filePath, int startDSN, int endDSN) {
        new File(filePath + File.separator + "Binaries").mkdirs();
        filePath = filePath + File.separator + "Binaries" + File.separator + "dsn-list.txt";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filePath, "UTF-8");
            for(int i = startDSN ; i <= endDSN ; i++) {
                if(i != endDSN) {
                    writer.println(i+",");
                } else {
                    writer.println(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.close();
    }

    //Returns a string of the DSN List File created by method createFileWithDSNs
    private static String readLineByLineFromFile(String filePath) {
        filePath = filePath + File.separator + "Binaries" + File.separator + "dsn-list.txt";
        System.out.println("Reading DSN List file from " + filePath);
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    //Scheduling Multiple DSNs
    private String submitBatch(String dsn) throws IOException {
        String setCurrentDateAsScheduledDate = getUTCdatetimeAsString();
        String JSONToPost = "{\r\n  \"submittedBy\": \"Aditya Test via Java\",\r\n  \"scheduledDate\": \""+setCurrentDateAsScheduledDate+"\",\r\n  \"dsns\": [\r\n    "+dsn+"\r\n  ],\r\n  \"profile\": {\r\n    \"id\": 650448,\r\n    \"name\": \"ASTest1\"\r\n  },\r\n  \"name\": \"API_Tool\"\r\n}";

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

    //Scheduling Single DSN
    private String submitBatch(int dsn) throws IOException {
        String setCurrentDateAsScheduledDate = getUTCdatetimeAsString();
        String JSONToPost = "{\r\n  \"submittedBy\": \"Aditya Test via Java\",\r\n  \"scheduledDate\": \""+setCurrentDateAsScheduledDate+"\",\r\n  \"dsns\": [\r\n    "+dsn+"\r\n  ],\r\n  \"profile\": {\r\n    \"id\": 650448,\r\n    \"name\": \"ASTest1\"\r\n  },\r\n  \"name\": \"API_Tool\"\r\n}";

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
    private String submitAndApproveBatch(String dsn) throws IOException {
        String setCurrentDateAsScheduledDate = getUTCdatetimeAsString();
        String JSONToPost = "{\r\n  \"submittedBy\": \"Aditya Test via Java\",\r\n  \"scheduledDate\": \""+setCurrentDateAsScheduledDate+"\",\r\n  \"dsns\": [\r\n    "+dsn+"\r\n  ],\r\n  \"profile\": {\r\n    \"id\": 650448,\r\n    \"name\": \"ASTest1\"\r\n  },\r\n  \"name\": \"API_Tool\"\r\n}";

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

    private int nextPackages(int dsn) throws IOException {
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

    private int packagesReady(int dsn) throws IOException {
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

    private int otapCompleted(int dsn, String profilename) throws IOException {
        String pathString = CURRENTDIRECTORYFILEPATH +File.separator+"Binaries"+File.separator+dsn+"_"+profilename+".dat";
        String otapCompletedURLString=VALKYRIEQAURL+"/otapCompleted/p.mop."+dsn+".210.icap-rom-version";

        // POST Request for nextPackages endpoint to Valkyrie
        MediaType mediaType = MediaType.parse("application/octet-stream");
        Request request = new Request.Builder()
                .url(otapCompletedURLString)
                .post(RequestBody.create(mediaType, Files.readAllBytes(Paths.get(pathString))))
                .addHeader(AUTHORIZATION, BASICAUTH)
                .addHeader(CACHECONTROL, NOCACHE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("otapCompleted: " + response.code() + " ");
        //return response.body().string();
        return response.code();
    }

    //this method is used to generate Binaries as payloads for Rom Version Mid
    private static void createBinaries(int dsn, String profileName, String romVersionMidArgumentsUpdateTypeIdAndTargetVersions) {
        RomVersionMid romVersionMid = new RomVersionMid();
        String DSN = Integer.toString(dsn);
        String[] romVersionMidName = {DSN+"_"+profileName+".dat",DSN};
        String[] updateTypeID_TargetVerdsions = romVersionMidArgumentsUpdateTypeIdAndTargetVersions.split(" ");
        String[] romVersionMidPayload = ValkyrieSQL.concatenateArrays(romVersionMidName,updateTypeID_TargetVerdsions);
        try {
            //generating Rom Version Mid binary payload
            romVersionMid.execute(romVersionMidPayload);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //This method sets the wheels in motion
    private static void beginOTAP(int startDSN, int endDSN, String profileName, String romVersionMidArguments_UpdateTypeID_AND_TargetVersions) {
        AutomationTestTool testObject = new AutomationTestTool();
        System.out.println("Working Directory = " + CURRENTDIRECTORYFILEPATH);

        //create DSN List
        createFileWithDSNs(CURRENTDIRECTORYFILEPATH,startDSN,endDSN);

        //retrieve DSN List to be given as input to /submitAndApproveBatch for scheduling multiple DSNs within the same calls
        String dsnList = readLineByLineFromFile(CURRENTDIRECTORYFILEPATH);

        //print DSN List
        System.out.println(dsnList);

        //submitAndApprove
        try {
            System.out.println(testObject.submitAndApproveBatch(dsnList));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int dsn = startDSN ; dsn <= endDSN ; dsn++){
            try {
                //create a Binary for the recently submitted dsn for OTAP
                createBinaries(dsn,profileName, romVersionMidArguments_UpdateTypeID_AND_TargetVersions);
                int nextPackagesCode = testObject.nextPackages(dsn);
                if(nextPackagesCode==200 || nextPackagesCode==204 || nextPackagesCode==304){
                    int packagesReadyCode = testObject.packagesReady(dsn);
                    if(packagesReadyCode == 200 || packagesReadyCode == 204){
                        int otapCompletedCode = testObject.otapCompleted(dsn,profileName);
                        if(otapCompletedCode==200){
                            System.out.print("otap Completed for dsn: "+ dsn + " ");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int startDSN = 10000000;
        int endDSN = startDSN + 1;
//        int profileID = 650448;
        int profileID = 16300006;
        String profileName = ValkyrieSQL.getProfileNameFromTable(profileID).toString();
        String romVersionMidArguments_UpdateTypeID_AND_TargetVersions = ValkyrieSQL.beginExecution(profileID);
        beginOTAP(startDSN,endDSN,profileName, romVersionMidArguments_UpdateTypeID_AND_TargetVersions);
//        System.out.println(" ");
//        System.out.println(" ");
//        System.out.println("update_type_id, target version: "+ValkyrieSQL.beginExecution(profileID));
    }
}