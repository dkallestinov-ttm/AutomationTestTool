package automation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import com.squareup.okhttp.*;

public class ApiCall {
    private static OkHttpClient client = new OkHttpClient();
    private static final String DATEFORMAT = "yyyy-MM-dd";
    private static String CURRENTDIRECTORYFILEPATH = System.getProperty("user.dir");
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASICAUTH = "Basic b21uaUFkYXB0ZXI6azN5U2l4cmlkMW9n";
    private static final String VALKYRIEQAURL = "https://valkyrie.qa.connectedfleet.io";
    private static final String APPLICATIONJSON = "application/json";
    private static final String  CACHECONTROL = "Cache-Control";
    private static final String CONTENTTYPE = "Content-Type";
    private static final String NOCACHE = "no-cache";
    private static String setCurrentDateAsScheduledDate = getUTCdatetimeAsString();

    //Date in UTC because Valkyrie needs UTC time to schedule an OTAP
    private static String getUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        return (utcTime+"T00:00:00Z");
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
    public static String submitAndApproveBatch(String dsn, String profileID) throws IOException {

        String jsonStringSubmitBatch = "{\n" +
                "  \"submittedBy\": \"TestTool\",\n" +
                "  \"scheduledDate\": \"" + setCurrentDateAsScheduledDate + "\",\n" +
                "  \"dsns\": [\n" +
                "    "+ dsn +"\n" +
                "  ],\n" +
                "  \"profile\": {\n" +
                "    \"id\": "+ profileID + "\n" +
                "  },\n" +
                "  \"name\": \"Batch1\"\n" +
                "}";

        MediaType mediaType = MediaType.parse(APPLICATIONJSON);
        RequestBody body = RequestBody.create(mediaType, jsonStringSubmitBatch);
        Request request = new Request.Builder()
                .url(VALKYRIEQAURL+"/submitAndApproveBatch")
                .post(body)
                .addHeader(CONTENTTYPE, APPLICATIONJSON)
                .addHeader(CACHECONTROL, NOCACHE)
                .build();

        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        System.out.print("submitBatch Response Body : " + responseString + " submitBatch ResponseCode : " + response.code());
        return responseString;
    }

    public int nextPackages(int dsn) throws IOException {
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

    public int packagesReady(int dsn) throws IOException {
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

    public int otapCompleted(int dsn, String profilename) throws IOException {
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
}