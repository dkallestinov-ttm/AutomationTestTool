package service;

import automation.AutomationTestTool;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.File;


/**
 * Created on 7/13/2018.
 */
public class MidPayloadSampler implements org.apache.jmeter.protocol.java.sampler.JavaSamplerClient {

    DataFileFactory dataFileFactory;

    @Override
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        dataFileFactory = new DataFileFactory(new File(javaSamplerContext.getJMeterVariables().get("datadir")));
    }

    @Override
    public org.apache.jmeter.samplers.SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        String[] args = new String[] {};
        new AutomationTestTool().setup(args);
        SampleResult sr =  new SampleResult();
        String datadir = javaSamplerContext.getJMeterVariables().get("datadir");
        try {
            dataFileFactory.createFile("testfile.mid");
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

    @Override
    public void teardownTest(JavaSamplerContext javaSamplerContext) {
        dataFileFactory = null;
    }

    @Override
    public org.apache.jmeter.config.Arguments getDefaultParameters() {
        return null;
    }
}
