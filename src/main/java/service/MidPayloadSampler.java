package service;


import automation.BinaryManager;
import automation.ValkyrieDAO;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

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
        BinaryManager bm = new BinaryManager(dir, new ValkyrieDAO(url, username, password));

        bm.writeBinaries(profileId, dsns);
    }

    @Override
    public void teardownTest(JavaSamplerContext javaSamplerContext) {
    }

    @Override
    public org.apache.jmeter.config.Arguments getDefaultParameters() {
        return null;
    }
}
