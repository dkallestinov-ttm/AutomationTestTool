set "CURRENT_DIR=%cd%"
set "JMETER_HOME=%CURRENT_DIR%\apache-jmeter-4.0\"
%JMETER_HOME%\bin\jmeter.bat -n -t valkyrie.jmx -o out\ -p default.properties
