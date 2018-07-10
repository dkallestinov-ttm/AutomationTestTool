# AutomationTestTool
This program will be used to test Valkyrie by making API calls to endpoints and seeing an OTAP go through end-to-end

Automation Test Tool first makes a file of the DSNs with comma seperation and stores it as dsn-list which will be used to submit and approve a batch of DSNs by making a REST call to Valkyrie's /submitAndApproveBatch endpoint. Once the batch of DSNs are submitted the program starts making RomVersionMids for every DSN submitted in the batch. Then makes REST calls to the following Valkyrie endpoints 
1. /nextPackages
2. /packagesReady and
3. /otapCompleted respectively

The binary generated while creating the RomVersionMid is submitted as a payload while making the REST call to /otapCompleted. This happens iteratively via AutomationTestTool until all the OTAPs are completed.
