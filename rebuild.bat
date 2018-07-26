START /WAIT mvn clean package dependency:copy-dependencies
xcopy /Y /exclude:excludeCopy.txt target\dependency\*.jar apache-jmeter-4.0\lib\  
xcopy /Y target\*.jar apache-jmeter-4.0\lib\ 