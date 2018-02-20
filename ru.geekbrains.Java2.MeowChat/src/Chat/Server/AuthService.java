package Chat.Server;


import java.sql.*;

class AuthService {
    private static Connection connection;
    private static Statement statement;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
                connection= DriverManager.getConnection("jdbc:sqlite:userDB.db");
                statement=connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(String login, String pass, String nick) {
        try {
            String query = "INSERT INTO main (login, password, nickname, admin, blacklist)" + "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, login);
            ps.setInt(2, pass.hashCode());
            ps.setString(3, nick);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT nickname, password FROM main WHERE login = '" + login + "'");
        int myHash = pass.hashCode();
        if (resultSet.next()) {
            String nick = resultSet.getString(1);
            int dbHash = resultSet.getInt(2);
            if (myHash == dbHash) return nick;
        }
        return null;
    }

    public static boolean getAdminByNick(String nick) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT admin FROM main WHERE nickname = '" + nick + "'");
        if (resultSet.next()) {
            int admin = resultSet.getInt(1);
            return admin == 1;
        }
        return false;
    }

    public static boolean getBlacklistByNick(String nick) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT blacklist FROM main WHERE nickname = '" + nick + "'");
        if (resultSet.next()) {
            int black = resultSet.getInt(1);
            return black == 1;
        }
        return false;
    }

    public static boolean setBlacklistByNick(String nick, boolean black) {
        try {
            String query;
            if (black) {
                query = "UPDATE main SET blacklist = 1 WHERE nickname = '" + nick + "'";
            } else query = "UPDATE main SET blacklist = 0 WHERE nickname = '" + nick + "'";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getFullUserList() throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT nickname, blacklist FROM main");
        StringBuilder sb = new StringBuilder();
        sb.append("/clientfull ");
        while (resultSet.next()) {
            sb.append(resultSet.getString("nickname")).append("_");
            sb.append(resultSet.getString("blacklist")).append(" ");
        }
        return sb.toString();
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
