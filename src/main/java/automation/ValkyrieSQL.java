package automation;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ValkyrieSQL {

    private static final String JDBCDRIVER = "com.mysql.cj.jdbc.Driver";
    private static final int updateTypeIdIndex = 1;
    private static final int versionIndex = 2;
    private static final int dependentUpdateTypeIdIndex = 3;
    private static final int dependentVersionIndex = 4;
    private static String sqlQueryForGettingUpdateTypeIdAndTargetVersion = "SELECT\n" +
            "pkg.update_type_id,\n" +
            "pkg.version,\n" +
            "pkg_dep.update_type_id AS dependentUpdateTypeId,\n" +
            "pkg_dep.version AS dependentVersion\n" +
            "FROM valkyrie_db.profile p\n" +
            "JOIN valkyrie_db.profile_package pp ON pp.profile_id=p.id\n" +
            "JOIN valkyrie_db.package pkg ON pkg.id=pp.package_id\n" +
            "LEFT JOIN valkyrie_db.package_dependency pd ON pd.package_id=pp.package_id\n" +
            "LEFT JOIN valkyrie_db.package pkg_dep ON pkg_dep.id=pd.dependent_on_package_id\n" +
            "WHERE p.id=";

    private String url;
    private String username;
    private String password;
    private Connection connection;

    public ValkyrieSQL(String connectionURL, String dbUsername, String dbPassword) {
        url = connectionURL;
        username = dbUsername;
        password = dbPassword;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String execute(int profileID) {
        Map<Integer,Integer> map = getAllUpdateTypeIdsAndVersions(profileID);
        return convertHashMapToString(map);
    }


    public  Map getAllUpdateTypeIdsAndVersions(int profileID) {
        sqlQueryForGettingUpdateTypeIdAndTargetVersion = sqlQueryForGettingUpdateTypeIdAndTargetVersion + profileID + ";";
        Map<Integer,Integer> map = new HashMap<>();
        try{
            Class.forName(JDBCDRIVER);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQueryForGettingUpdateTypeIdAndTargetVersion);
            while(resultSet.next()) {
                Integer updateTypeId = resultSet.getInt(updateTypeIdIndex);
                Integer version = resultSet.getInt(versionIndex);
                Integer dependentUpdateTypeId = resultSet.getInt(dependentUpdateTypeIdIndex);
                Integer dependentVersion = resultSet.getInt(dependentVersionIndex);
                map.putIfAbsent(updateTypeId,version);
                map.putIfAbsent(dependentUpdateTypeId,dependentVersion);
            }
            map.remove(0);
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }

    private String convertHashMapToString(Map map){
        StringBuilder keyValuePair = new StringBuilder();
        Iterator keyIterator = map.keySet().iterator();
        Iterator valueIterator = map.values().iterator();
        while (keyIterator.hasNext() && valueIterator.hasNext()){
            int key = (int) keyIterator.next();
            int value = (int) valueIterator.next();
            keyValuePair.append(key);
            keyValuePair.append(" ");
            keyValuePair.append(value);
            keyValuePair.append(" ");
        }
        return keyValuePair.toString().trim();
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
}