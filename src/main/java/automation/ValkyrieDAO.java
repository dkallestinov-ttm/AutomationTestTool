package automation;

import java.sql.*;
import java.util.*;

public class ValkyrieDAO {
    private static final String LOCAL_CONN_URL = "jdbc:mysql://localhost:3306/valkyrie_db";
    private static final String DEV_CONN_URL = "jdbc:mysql://dev-security-user.ckcpzgmqvbbt.us-east-1.rds.amazonaws.com:3306/valkyrie_db";
    private static final String QA_CONN_URL = "jdbc:mysql://qa-security-user.ckcpzgmqvbbt.us-east-1.rds.amazonaws.com:3306/valkyrie_db";

    private String connectionUrl;
    private String dbUser;
    private String dbPassword;
    private String driver;

    public ValkyrieDAO() {
        this(Environment.LOCAL);
    }

    public ValkyrieDAO(Environment env) {
        this(env, "valkyrie_db", "valkyrie_db");
    }

    public ValkyrieDAO(String user, String password) {
        this(Environment.LOCAL, user, password);
    }

    public ValkyrieDAO(Environment env, String user, String password) {
        this(env, user, password, "com.mysql.cj.jdbc.Driver");
    }

    public ValkyrieDAO(Environment env, String user, String password, String driver) {
        switch (env) {
            case LOCAL:
                this.connectionUrl = LOCAL_CONN_URL;
                break;
            case DEV:
                this.connectionUrl = DEV_CONN_URL;
                break;
            case QA:
                this.connectionUrl = QA_CONN_URL;
                break;
            default:
                throw new IllegalArgumentException("Unrecognized Environment - cannot connect to database");
        }
        this.dbUser = user;
        this.dbPassword = password;
        this.driver = driver;
    }

    /**
     * Establishes and returns a connection to the database
     * @return a connection to the database
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(this.driver);
        return DriverManager.getConnection(this.connectionUrl, this.dbUser, this.dbPassword);
    }

    /**
     * Executes a select query with the connection given
     * @param conn db connection to use
     * @param sql the sql statement to execute
     * @return the ResultSet from the sql statement
     * @throws SQLException
     */
    private ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        Statement statement = conn.createStatement();
        return statement.executeQuery(sql);
    }

    /**
     * Returns the name of the profile for the given profileid
     * @param  profileId
     * @return profile name
     */
    public String getProfileNameFromTable(int profileId) {
        String sqlQuery = "SELECT name FROM valkyrie_db.profile WHERE id="+profileId+";";
        String profileName = null;

        try {
            Connection conn = this.getConnection();
            ResultSet rs = this.executeQuery(conn, sqlQuery);

            while(rs.next()) {
                profileName = rs.getString("name");
            }

            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return profileName;
    }


    /**
     * Gets the updateTypes and versions for a given profile
     * @param profileID
     * @return an ArrayList of InstalledSoftware objects
     */
    public ArrayList<InstalledSoftware> getProfileUpdateTypeIdsAndVersions(int profileID) {
        String sqlQuery = "SELECT " +
                "pkg.update_type_id, " +
                "pkg.version, " +
                "pkg_dep.update_type_id AS dependentUpdateTypeId, " +
                "pkg_dep.version AS dependentVersion " +
                "FROM valkyrie_db.profile p " +
                "JOIN valkyrie_db.profile_package pp ON pp.profile_id=p.id " +
                "JOIN valkyrie_db.package pkg ON pkg.id=pp.package_id " +
                "LEFT JOIN valkyrie_db.package_dependency pd ON pd.package_id=pp.package_id " +
                "LEFT JOIN valkyrie_db.package pkg_dep ON pkg_dep.id=pd.dependent_on_package_id " +
                "WHERE p.id=" + profileID + ";";
        ArrayList<InstalledSoftware> result = new ArrayList<>();

        try {
            Connection conn = this.getConnection();
            ResultSet resultSet = this.executeQuery(conn, sqlQuery);

            while(resultSet.next()) {
                int updateTypeId = resultSet.getInt("update_type_id");
                int version = resultSet.getInt("version");
                int dependentUpdateTypeId = resultSet.getInt("dependentUpdateTypeId");
                int dependentVersion = resultSet.getInt("dependentVersion");

                result.add(new InstalledSoftware(updateTypeId, version));
                result.add(new InstalledSoftware(dependentUpdateTypeId, dependentVersion));
            }
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}