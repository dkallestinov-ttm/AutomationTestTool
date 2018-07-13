package automation;

import java.lang.reflect.Array;
import java.sql.*;

public class ValkyrieSQL {

    private static final String SQLCONNECTIONURL = "jdbc:mysql://qa-security-user.ckcpzgmqvbbt.us-east-1.rds.amazonaws.com";
    private static final String USERNAME = "valkyrie_db";
    private static final String PASSWORD = "valkyrie_db";

    //returns Profile Name associated with the ProfileID
    public static StringBuilder getProfileNameFromTable(int profileID) {
        String sqlQuery = "SELECT name FROM valkyrie_db.profile WHERE id="+profileID+";";
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
                    stringBuilder.append(columnValue);
                }
            }
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringBuilder;
    }

    //returns PackageIDs associated with the ProfileID
    public static StringBuilder getPackageID(int profileID) {
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

    //returns Profile Name associated with the ProfileID
    public static StringBuilder getDependentPackageIDs(int packageID) {
        String sqlQuery = "SELECT dependent_on_package_id FROM valkyrie_db.package_dependency WHERE package_id = "+packageID+";";
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
                    stringBuilder.append(columnValue);
                }
            }
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringBuilder;
    }

    public static StringBuilder getUpdateTypeIdAndTargetVersion(int packageID) {
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
            stringBuilderObject = getUpdateTypeIdAndTargetVersion(arrayOfPackageIDs[i]);
            uIdAndTargVer[i] = stringBuilderObject.toString();
        }
        return uIdAndTargVer;
    }

    public static String romVersionMidArgumentBuilder(String[] arguments){
        String romVersionMidArgument="";
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

    public static StringBuilder helpGetDependentPackageIDs(int[] packageIDs) {
        StringBuilder dependentPackageIDs = new StringBuilder();
        System.out.println(dependentPackageIDs);
        for (int i = 0; i < packageIDs.length; i++) {
            dependentPackageIDs = dependentPackageIDs.append(getDependentPackageIDs(packageIDs[i]));
            dependentPackageIDs.append(" ");
        }
//        System.out.println("Dependent Package IDs are : "+dependentPackageIDs);
        return dependentPackageIDs;
    }

    public static String[] stringToStringArray(int dsn, String inputString, int profileID, String profileName) {
        String DSN = Integer.toString(dsn);
        String[] first = {DSN+"_"+profileName+".dat",DSN};
        String[] second = inputString.split(" ");
        String[] resultString = concatenateArrays(first,second);
        return (resultString);
    }

    public static void printArray(String[] arrayInput){
        for (int i = 0; i < arrayInput.length; i++) {
            System.out.print(arrayInput[i]+" ");
        }
        System.out.println();
    }

    public static <T> T[] concatenateArrays(T[] inputArrayA, T[] inputArrayB) {
        int aLen = inputArrayA.length;
        int bLen = inputArrayB.length;

        @SuppressWarnings("unchecked")
        T[] concatenatedArray = (T[]) Array.newInstance(inputArrayA.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(inputArrayA, 0, concatenatedArray, 0, aLen);
        System.arraycopy(inputArrayB, 0, concatenatedArray, aLen, bLen);
        return concatenatedArray;
    }

    public static String beginExecution(int profileID) {
        StringBuilder profilePackageIDs;
        profilePackageIDs = getPackageID(profileID);

        String[] packageIDs = profilePackageIDs.toString().split(" ");
        int[] arrayOfPackageIDs = arrayOfPackageIDs(packageIDs);
        String[] stringArrayOfDependentPackageIDs = helpGetDependentPackageIDs(arrayOfPackageIDs).toString().split(" ");
        int[] arrayOfDependentPackageIDs = arrayOfPackageIDs(stringArrayOfDependentPackageIDs);

        String[] update_type_id_AND_target_version = stringArrayOfUpdateTypeIdsAndTargetVersions(arrayOfPackageIDs);
        String[] dependentPackageIDs_update_type_id_AND_target_version = stringArrayOfUpdateTypeIdsAndTargetVersions(arrayOfDependentPackageIDs);
        String[] ALL_UID_AND_TargV = concatenateArrays(update_type_id_AND_target_version,dependentPackageIDs_update_type_id_AND_target_version);

        return (romVersionMidArgumentBuilder(ALL_UID_AND_TargV));
    }
}