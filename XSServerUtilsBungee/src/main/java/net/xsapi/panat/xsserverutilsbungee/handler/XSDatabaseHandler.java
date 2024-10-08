package net.xsapi.panat.xsserverutilsbungee.handler;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.xsapi.panat.xsserverutilsbungee.config.mainConfig;
import net.xsapi.panat.xsserverutilsbungee.core;
import net.xsapi.panat.xsserverutilsbungee.discord.verifyHandler;
import net.xsapi.panat.xsserverutilsbungee.discord.verifyUser;
import net.xsapi.panat.xsserverutilsbungee.objects.XSBanplayers;
import net.xsapi.panat.xsserverutilsbungee.objects.XSMuteplayers;
import net.xsapi.panat.xsserverutilsbungee.scp.scpUsers;

import java.sql.*;
import java.util.UUID;

public class XSDatabaseHandler {

    private static String JDBC_URL;
    private static String USER;
    private static String HOST;
    private static int PORT;
    private static String PASS;
    private static String DB_NAME;
    private static String BAN_TABLE = "xsutils_data_ban";
    private static String MUTE_TABLE = "xsutils_data_mute";
    private static String MAIN_TABLE = "xsutils_users";
    private static String SCP_USER_TABLE = "xsutils_scp_users";

    public static String getBantable() {
        return BAN_TABLE;
    }
    public static String getMainTable() { return MAIN_TABLE; }

    public static String getScpUserTable() { return  SCP_USER_TABLE; }

    public static String getMuteTable() {
        return MUTE_TABLE;
    }

    private final static String BAN_SQL_QUERY = " ("
            + "id SERIAL PRIMARY KEY, "
            + "idRef INTEGER REFERENCES " + getMainTable() + "(id), "
            + "reason TEXT, "
            + "creation_date DOUBLE PRECISION, "
            + "end_date DOUBLE PRECISION, "
            + "banner TEXT"
            + ")";

    private final static String MUTE_SQL_QUERY = " ("
            + "id SERIAL PRIMARY KEY, "
            + "idRef INTEGER REFERENCES " + getMainTable() + "(id), "
            + "reason TEXT, "
            + "creation_date DOUBLE PRECISION, "
            + "end_date DOUBLE PRECISION, "
            + "muter TEXT"
            + ")";

    private final static String MAIN_SQL_QUERY = " ("
            + "id SERIAL PRIMARY KEY, "
            + "uuid TEXT, "
            + "username VARCHAR(16), "
            + "warnPoints INTEGER, "
            + "discordID TEXT, "
            + "verifyDate DOUBLE PRECISION"
            + ")";

    public static void loadSCPUsers() {
        try (Connection connection = establishConnection()) {
            querySCPUsers(connection,getScpUserTable());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadBanList() {
        try (Connection connection = establishConnection()) {
            queryBanFromDatabase(connection,getBantable());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadMuteList() {
        try (Connection connection = establishConnection()) {
            queryMuteFromDatabase(connection,getMuteTable());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadVerifyUser() {
        Connection connection;
        String checkPlayerQuery = "SELECT * FROM " + getMainTable() + " WHERE discordID IS NOT NULL AND discordID <> ''";
        try {
            connection = establishConnection();
            PreparedStatement preparedCheckStatement = connection.prepareStatement(checkPlayerQuery);
            ResultSet resultSet = preparedCheckStatement.executeQuery();

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String username = resultSet.getString("username");
                String discordID = resultSet.getString("discordID");
                long verifyDate = resultSet.getLong("verifyDate");

                verifyUser verifyUser = new verifyUser(username,discordID,verifyDate);

                verifyHandler.getVerifyUser().put(UUID.fromString(uuid),verifyUser);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertIntoDatabaseUser(ProxiedPlayer player)  {

        Connection connection;
        String checkPlayerQuery = "SELECT EXISTS(SELECT * FROM " + getMainTable() + " WHERE uuid = ?) AS exist";
        try {
            connection = establishConnection();
            PreparedStatement preparedCheckStatement = connection.prepareStatement(checkPlayerQuery);
            preparedCheckStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedCheckStatement.executeQuery();

            if (resultSet.next()) {
                boolean exists = resultSet.getBoolean("exist");
                if(!exists) {
                    String insetrQuery = "INSERT INTO " + getMainTable() + " (uuid, username, warnPoints, discordID) "
                            + "VALUES(?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insetrQuery);
                    preparedStatement.setString(1, player.getUniqueId().toString());
                    preparedStatement.setString(2, player.getName());
                    preparedStatement.setInt(3, 0);
                    preparedStatement.setString(4, "");
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUserIdFromUsername(String name) {
        int userId = -1;
        String targetUUID = "";
        String result;

        String query = "SELECT * FROM " + getMainTable() + " WHERE username = ?";

        try (Connection connection = establishConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("id");
                    targetUUID = resultSet.getString("uuid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        result = userId+":"+targetUUID;

        return result;
    }

    public static void updateVerifyUser(String discordID,String uuid)  {

        String updateQuery = "UPDATE " + getMainTable() + " SET discordID = ?, verifyDate = ? WHERE uuid = ?";
        try (PreparedStatement preparedStatement = establishConnection().prepareStatement(updateQuery)) {
            preparedStatement.setString(1, discordID);
            preparedStatement.setLong(2, System.currentTimeMillis());
            preparedStatement.setString(3, uuid);

            preparedStatement.executeUpdate();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateSCPUsersLogout(String username,long onlinetime,String server)  {

        String updateQuery = "UPDATE " + getScpUserTable() + " SET online_time = ?, server = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = establishConnection().prepareStatement(updateQuery)) {
            preparedStatement.setLong(1, onlinetime);
            preparedStatement.setString(2, server);
            preparedStatement.setString(3, username);

            preparedStatement.executeUpdate();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateSecretSCPUsers(String username,String secret)  {
        String updateQuery = "UPDATE " + getScpUserTable() + " SET secret = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = establishConnection().prepareStatement(updateQuery)) {
            preparedStatement.setString(1, secret);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void insertIntoDatabaseBan(int idRef,String reason,double creation_date,double end_date,String banner)  {
        String insetrQuery = "INSERT INTO " + getBantable() + " (idRef, reason, creation_date, end_date, banner) "
                + "VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = establishConnection().prepareStatement(insetrQuery)) {
            preparedStatement.setInt(1, idRef);
            preparedStatement.setString(2, reason);
            preparedStatement.setDouble(3, creation_date);
            preparedStatement.setDouble(4, end_date);
            preparedStatement.setString(5, banner);

            preparedStatement.executeUpdate();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoDatabaseMute(int idRef,String reason,double creation_date,double end_date,String muter)  {
        String insetrQuery = "INSERT INTO " + getMuteTable() + " (idRef, reason, creation_date, end_date, muter) "
                + "VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = establishConnection().prepareStatement(insetrQuery)) {
            preparedStatement.setInt(1, idRef);
            preparedStatement.setString(2, reason);
            preparedStatement.setDouble(3, creation_date);
            preparedStatement.setDouble(4, end_date);
            preparedStatement.setString(5, muter);

            preparedStatement.executeUpdate();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBanPlayerFromDatabase(int idRef)  {
        String deleteQuery = "DELETE FROM " + getBantable() + " WHERE idRef = ?";
        try (PreparedStatement preparedStatement = establishConnection().prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, idRef);
            preparedStatement.executeUpdate();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMutePlayerFromDatabase(int idRef)  {
        String deleteQuery = "DELETE FROM " + getMuteTable() + " WHERE idRef = ?";
        try (PreparedStatement preparedStatement = establishConnection().prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, idRef);
            preparedStatement.executeUpdate();
        }  catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void querySCPUsers(Connection connection, String table) throws SQLException {
        String selectQuery = "SELECT * FROM " + table;
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String server = resultSet.getString("server");
                long onlineTime = resultSet.getLong("online_time");
                String rank = resultSet.getString("rank");

                scpUsers scpUser = new scpUsers(username,server,onlineTime,rank);

                core.getPlugin().getLogger().info("[SCP] Added " + username + " from database ");

                XSHandler.getScpUsers().put(username,scpUser);
            }
        }

        XSHandler.updateSCPUser();
    }

    public static void queryBanFromDatabase(Connection connection, String table) throws SQLException {
        String selectQuery = "SELECT * FROM " + table;
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
               int idRef = resultSet.getInt("idRef");
               String reason = resultSet.getString("reason");
               double creation_date = resultSet.getDouble("creation_date");
               double end_date = resultSet.getDouble("end_date");
               String banner = resultSet.getString("banner");

               String result = queryGetName(connection,getMainTable(),idRef);
               String uuid = result.split(":")[0];
               String name = result.split(":")[1];
               XSBanplayers xsBanplayers = new XSBanplayers(uuid,idRef,reason,creation_date,end_date,banner);

               XSHandler.getBanList().put(name,xsBanplayers);
            }
        }
    }

    public static void queryMuteFromDatabase(Connection connection, String table) throws SQLException {
        String selectQuery = "SELECT * FROM " + table;
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int idRef = resultSet.getInt("idRef");
                String reason = resultSet.getString("reason");
                double creation_date = resultSet.getDouble("creation_date");
                double end_date = resultSet.getDouble("end_date");
                String muter = resultSet.getString("muter");

                String result = queryGetName(connection,getMainTable(),idRef);
                String uuid = result.split(":")[0];
                String name = result.split(":")[1];
                XSMuteplayers xsMuteplayers = new XSMuteplayers(uuid,idRef,reason,creation_date,end_date,muter);

                XSHandler.getMuteList().put(name,xsMuteplayers);
            }
        }
    }

    public static String queryGetName(Connection connection, String table, int idRef) throws SQLException {
        String selectQuery = "SELECT username, uuid FROM " + table + " WHERE id = ?";
        String result = "";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, idRef);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    String uuid = resultSet.getString("uuid");
                    String targetName = resultSet.getString("username");

                    result = uuid+":"+targetName;
                }
            }
        }
        return result;
    }

    public static Connection establishConnection() throws SQLException {
        HOST = mainConfig.getConfig().getString("database.host");
        PORT = mainConfig.getConfig().getInt("database.port");
        DB_NAME = mainConfig.getConfig().getString("database.dbName");
        USER = mainConfig.getConfig().getString("database.user");
        PASS = mainConfig.getConfig().getString("database.password");

        JDBC_URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME + "?user=" + USER + "&password=" + PASS + "&sslmode=disable";

        return DriverManager.getConnection(JDBC_URL);
    }

    public static void createTable(Connection connection, String table, String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + table + query;
            statement.executeUpdate(createTableQuery);
        }
    }

    public static void sqlConnection(String table, String query) {
        try (Connection connection = establishConnection()) {
            createTable(connection, table, query);
            core.getPlugin().getLogger().info("[XSUTILS] Database : Connected " + table);
        } catch (SQLException e) {
            core.getPlugin().getLogger().info("[XSUTILS] Database : Not Connected" + table);
            e.printStackTrace();
        }
    }

    public static void createSQLDatabase() {
        sqlConnection(getMainTable(), MAIN_SQL_QUERY);
        sqlConnection(getBantable(), BAN_SQL_QUERY);
        sqlConnection(getMuteTable(), MUTE_SQL_QUERY);
    }

}
