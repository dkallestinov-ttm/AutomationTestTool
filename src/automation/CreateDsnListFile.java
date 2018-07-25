package automation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CreateDsnListFile {

    private static String CURRENTDIRECTORYFILEPATH = System.getProperty("user.dir");

    //Method to create DSN List and store the numbers with comma separation. This will be used while giving SubmitAndApprove batch an input of 50,000 dsns in a single API Call.
    public static void createFileWithDSNs(int startDSN, int endDSN) {
        new File(CURRENTDIRECTORYFILEPATH + File.separator + "Binaries").mkdirs();
        String filePath = CURRENTDIRECTORYFILEPATH + File.separator + "Binaries" + File.separator + "dsn-list.txt";
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
    public static String readLineByLineFromFile() {
        String filePath = CURRENTDIRECTORYFILEPATH + File.separator + "Binaries" + File.separator + "dsn-list.txt";
        System.out.println("Reading DSN List file from " + filePath);
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}