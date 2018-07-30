package service;


import automation.RomVersionMid;
import automation.ValkyrieSQL;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.protocol.java.test.JavaTest;
import org.apache.jmeter.samplers.SampleResult;

import


/**
 * Created on 7/13/2018.
 */
public class MidPayloadSampler implements org.apache.jmeter.protocol.java.sampler.JavaSamplerClient {

    private String url;
    private String username;
    private String password;

    @Override
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        url = "jdbc:mysql://localhost:3306/valkyrie_db";
        username = "valkyrie_db";
        password = "valkyrie_db";
    }

    @Override
    public org.apache.jmeter.samplers.SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        String[] args = new String[] {};

        SampleResult sr =  new SampleResult();
        String datadir = "C:\\Development\\oem\\development\\otap\\loadTest\\data\\"; //javaSamplerContext.getJMeterVariables().get("datadir");
        try {
            createBinaries(datadir,44600001,  new String[] {"999887777"});
        } catch (Exception e)
        {
            sr.setResponseCode("500");
            sr.setResponseCode(e.getMessage());
            return sr;
        }
        sr.setResponseMessage(datadir);
        sr.setResponseOK();

        return sr;
    }

    private void createBinaries(String dir, int profileId, String[] dsns) {
        String updateTypeIdAndTargetVersion =  new ValkyrieSQL(url, username, password).execute(profileId);
        String baseFileName = "profileId"+ profileId + "_dsn";

        try {
            for (int i = 0; i < dsns.length ; i++) {
                int dsn = Integer.parseInt(dsns[i]);
                RomVersionMid mid = new RomVersionMid(dir, baseFileName + dsn + ".dat", dsn, updateTypeIdAndTargetVersion.split(" "));
                mid.write();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void teardownTest(JavaSamplerContext javaSamplerContext) {
    }

    @Override
    public org.apache.jmeter.config.Arguments getDefaultParameters() {
        return null;
    }
}
