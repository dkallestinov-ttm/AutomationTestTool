package automation;

import Requests.SubmitBatchPayload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class APIClient {
    private static OkHttpClient client = new OkHttpClient();
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASICAUTH_HEADER_VALUE = "Basic b21uaUFkYXB0ZXI6azN5U2l4cmlkMW9n";
    private static final String APPLICATIONJSON_HEADER_VALUE= "application/json";
    private static final String CACHECONTROL_HEADER = "Cache-Control";
    private static final String CONTENTTYPE_HEADER = "Content-Type";
    private static final String NOCACHE_HEADER_VALUE = "no-cache";
    private static final String QAURL = "https://valkyrie.qa.connectedfleet.io";
    private static final String DEVURL = "https://valkyrie.dev.connectedfleet.io";
    private static final String LOCALURL = "https://localhost:8000";
    private String baseUrl;
    private GsonBuilder gsonBuilder;

    public APIClient(Environment env) throws IllegalArgumentException {
        switch (env) {
            case LOCAL:
                this.baseUrl = LOCALURL;
                break;
            case DEV:
                this.baseUrl = DEVURL;
                break;
            case QA:
                this.baseUrl = QAURL;
                break;
            default:
                throw new IllegalArgumentException("Unsupported Environment: LOCAL, DEV, or QA accepted");
        }
        this.gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    //Scheduling Multiple DSNs
    public String submitBatch(int profileId, String[] dsns) throws IOException {
        SubmitBatchPayload payload = new SubmitBatchPayload("SmokeTest", new Date(), dsns, profileId, "SmokeTestBatch");
        Gson gson = this.gsonBuilder.create();

        // POST Request to SUBMIT a batch to Valkyrie
        MediaType mediaType = MediaType.parse(APPLICATIONJSON_HEADER_VALUE);
        RequestBody body = RequestBody.create(mediaType, gson.toJson(payload));
        Request request = new Request.Builder()
                .url(this.baseUrl+"/submitBatch")
                .post(body)
                .addHeader(CONTENTTYPE_HEADER, APPLICATIONJSON_HEADER_VALUE)
                .addHeader(CACHECONTROL_HEADER, NOCACHE_HEADER_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("submitBatch: "+response.code());
        return response.body().string();
    }

    //Scheduling Multiple DSNs
    public String submitAndApproveBatch(int profileId, String[] dsns) throws IOException {
        SubmitBatchPayload payload = new SubmitBatchPayload("SmokeTest", new Date(), dsns, profileId, "SmokeTestBatch");
        Gson gson = this.gsonBuilder.create();

        // POST Request to SUBMIT a batch to Valkyrie
        MediaType mediaType = MediaType.parse(APPLICATIONJSON_HEADER_VALUE);
        RequestBody body = RequestBody.create(mediaType, gson.toJson(payload));
        Request request = new Request.Builder()
                .url(this.baseUrl+"/submitAndApproveBatch")
                .post(body)
                .addHeader(CONTENTTYPE_HEADER, APPLICATIONJSON_HEADER_VALUE)
                .addHeader(CACHECONTROL_HEADER, NOCACHE_HEADER_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("submitAndApproveBatch: "+response.code());
        return response.body().string();
    }

    public int nextPackages(String dsn) throws IOException {
        String nextPackagesURLString = this.baseUrl+"/nextPackages/"+dsn;

        // GET Request for nextPackages endpoint to Valkyrie
        Request request = new Request.Builder()
                .url(nextPackagesURLString)
                .get()
                .addHeader(AUTHORIZATION_HEADER, BASICAUTH_HEADER_VALUE)
                .addHeader(CACHECONTROL_HEADER, NOCACHE_HEADER_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("nextPackages: "+ response.code() +" ");
        //return response.body().string();
        return response.code();
    }

    public int packagesReady(String dsn) throws IOException {
        String packagesReadyURLString=this.baseUrl+"/packagesReady/p.mtp."+dsn+".otap.module-ready";

        // PUT Request for nextPackages endpoint to Valkyrie
        Request request = new Request.Builder()
                .url(packagesReadyURLString)
                .put(null)
                .addHeader(AUTHORIZATION_HEADER, BASICAUTH_HEADER_VALUE)
                .addHeader(CACHECONTROL_HEADER, NOCACHE_HEADER_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("packagesReady: "+response.code() +" ");
        //return response.body().string();
        return response.code();
    }

    public int otapCompleted(String dsn, String fileName) throws IOException {
        String otapCompletedURLString=this.baseUrl+"/otapCompleted/p.mop."+dsn+".210.icap-rom-version";

        // POST Request for nextPackages endpoint to Valkyrie
        MediaType mediaType = MediaType.parse("application/octet-stream");
        Request request = new Request.Builder()
                .url(otapCompletedURLString)
                .post(RequestBody.create(mediaType, Files.readAllBytes(Paths.get(fileName))))
                .addHeader(AUTHORIZATION_HEADER, BASICAUTH_HEADER_VALUE)
                .addHeader(CACHECONTROL_HEADER, NOCACHE_HEADER_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        System.out.print("otapCompleted: " + response.code() + " ");
        //return response.body().string();
        return response.code();
    }

}
