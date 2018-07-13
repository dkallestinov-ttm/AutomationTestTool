package automation;

import java.sql.*;

public class ValkyrieSQL {

    private static final String SQLCONNECTIONURL = "";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";

    //returns PackageIDs associated with the ProfileID which is given as input to this method
    public static StringBuilder profilePackageTable(int profileID) {
        String sqlQuery = "SELECT * FROM valkyrie_db.profile_package WHERE profile_id="+profileID+";";
        StringBuilder stringBuilder = new StringBuilder();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    SQLCONNECTIONURL,
                    USERNAME,
                    PASSWORD
            );
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnNumber = resultSetMetaData.getColumnCount();
            while(resultSet.next()) {
                for (int i = 1 ; i <= columnNumber ; i++) {
                    String columnValue = resultSet.getString(i);
                    if(i % 3 == 0) {
                        stringBuilder.append(columnValue);
                        stringBuilder.append(" ");
                    }
                }
            }
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringBuilder;
    }

    //returns UPDATE_TYPE_ID and TARGET_VERSION for the PackageID which is given as input to this method
    public static StringBuilder updateTypeIdAndTargetVersion(int packageID) {
        String sqlQuery = "SELECT update_type_id, version FROM valkyrie_db.package WHERE id=" + packageID + ";";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    SQLCONNECTIONURL,
                    USERNAME,
                    PASSWORD
            );
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnNumber = resultSetMetaData.getColumnCount();
            while(resultSet.next()){
                for (int i = 1 ; i <= columnNumber ; i++) {
                    String columnValue = resultSet.getString(i);
                    stringBuilder.append(columnValue);
                    stringBuilder.append(" ");
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }

    public static int[] arrayOfPackageIDs(String[] packageIDs){
        int[] arrayOfPackageIDs = new int[packageIDs.length];
        for (int i = 0; i < packageIDs.length; i++) {
            arrayOfPackageIDs[i] = Integer.parseInt(packageIDs[i]);
        }
        return arrayOfPackageIDs;
    }

    public static String[] stringArrayOfUpdateTypeIdsAndTargetVersions(int[] arrayOfPackageIDs){
        String[] uIdAndTargVer = new String[arrayOfPackageIDs.length];
        StringBuilder stringBuilderObject;
        for (int i = 0; i < arrayOfPackageIDs.length; i++) {
            stringBuilderObject = updateTypeIdAndTargetVersion(arrayOfPackageIDs[i]);
            uIdAndTargVer[i] = stringBuilderObject.toString();
        }
        return uIdAndTargVer;
    }

    public static String romVersionMidArgumentBuilder(String[] arguments, int profileID){
        String romVersionMidArgument= String.valueOf(profileID) + " ";
        for (int i = 0; i < arguments.length; i++) {
            romVersionMidArgument = romVersionMidArgument+arguments[i];
        }
        return trimTrailingBlanks(romVersionMidArgument);
    }

    public static String trimTrailingBlanks(String inputString) {
        if(inputString == null)
            return null;
        int len = inputString.length();
        for( ; len > 0; len--) {
            if(!Character.isWhitespace(inputString.charAt(len - 1)))
                break;
        }
        return inputString.substring(0, len);
    }

    //sort of like the main method of this program
    public static String beginExecution(int profileID) {
        StringBuilder profilePackageIDs;
        profilePackageIDs = profilePackageTable(profileID);

        String[] packageIDs = profilePackageIDs.toString().split(" ");
        int[] arrayOfPackageIDs = arrayOfPackageIDs(packageIDs);

        String[] update_type_id_AND_target_version = stringArrayOfUpdateTypeIdsAndTargetVersions(arrayOfPackageIDs);
        return romVersionMidArgumentBuilder(update_type_id_AND_target_version, profileID);
    }
}