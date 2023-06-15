package DataBase;

import entity.Message;
import loginweb.LoginMessage;

import javax.servlet.http.HttpServlet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
//import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DbCtrl extends HttpServlet {
    static String url ="jdbc:mysql://127.0.0.1:3306/imsystem"+"?useUnicode=true&characterEncoding=utf8";
    //        jdbc:mysql 协议名
//        127.0.0.1/localhost 本机
    static String userName ="root";
    static String Passwd = "lmc.y.t.2002";
     static Connection connection;


    static {
        try {
            connection = getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
            // 注销驱动程序
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        synchronized (DbCtrl.class) {
            if (connection == null || connection.isClosed()) {
                connection = connectSql(url, userName, Passwd);
            }
            return connection;
        }
    }



    public static Connection connectSql(String sqlUrl, String Uname, String Passwd) throws ClassNotFoundException, SQLException {
        //通过反射加载驱动
        //获取数据库连接
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(sqlUrl,Uname,Passwd);
    }




    public static String getUserNameById(String userId) throws SQLException {
        String sql = "SELECT UserName FROM userinfo WHERE UserId = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, userId);

        ResultSet rs = stmt.executeQuery();
        String userName = null;
        if (rs.next()) {
            userName = rs.getString("UserName");
        }

        rs.close();
        stmt.close();
        return userName;
    }




    public static int Register(String userName, String userKey, String userEmail) {
        String sql = "INSERT INTO imsystem.userinfo (UserName, UserKey, UserEmail) VALUES (?, ?, ?)";
        String querySql = "SELECT UserId FROM imsystem.userinfo WHERE UserId = LAST_INSERT_ID()";
        try (
             PreparedStatement stmt = connection.prepareStatement(sql);PreparedStatement queryStmt = connection.prepareStatement(querySql)) {
            stmt.setString(1, userName);
            stmt.setString(2, userKey);
            stmt.setString(3, userEmail);
            stmt.executeUpdate();
            ResultSet rs = queryStmt.executeQuery();
            if (rs.next()){
                stmt.close();
                return rs.getInt("UserId");
            }else {
                return 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }



    public static LoginMessage loginUser(String userName, String userPassword) throws SQLException {
        String sql = "SELECT UserId, UserKey FROM imsystem.userinfo WHERE UserName = ? AND UserKey = ?";
        String updateSql = "UPDATE imsystem.userinfo SET LoginKey = ?, LastLoginTime = ? WHERE UserId = ?";
        try (
            PreparedStatement stmt2 = connection.prepareStatement(sql);PreparedStatement UpSql =connection.prepareStatement(updateSql)) {
            stmt2.setString(1, userName);
            stmt2.setString(2, userPassword);
            ResultSet rs= stmt2.executeQuery();
            if (rs.next()){
                String LoginKey = generateLoginKey(rs.getString("UserKey"));
                UpSql.setString(1,LoginKey);
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = dateFormat.format(date);
                date = (Date) dateFormat.parse(dateString);
                UpSql.setDate(2,new java.sql.Date(date.getTime()));
                UpSql.setInt(3,rs.getInt("UserId"));
                UpSql.executeUpdate();
                return new LoginMessage(rs.getInt("UserId"),LoginKey);
            }else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }




    public static boolean logout(String userId, String loginKey) throws SQLException {
        // 验证登录密钥是否有效
        if (!isLoginKeyValid(userId, loginKey)) {
            return false;
        }
        // 从数据库中删除登录密钥
        String sql = "UPDATE imsystem.userinfo SET LoginKey = NULL WHERE UserId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }




    public static boolean isLoginKeyValid(String userId, String loginKey) throws SQLException {
        String sql = "SELECT LoginKey FROM imsystem.userinfo WHERE UserId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedLoginKey = rs.getString("LoginKey");
                return loginKey.equals(storedLoginKey);
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    private static String generateLoginKey(String userKey) {
        long timestamp = System.currentTimeMillis();
        String data = userKey + timestamp;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

//    消息部分

    public static List<Message> getUserMessages(int userId,int chaUserid) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT MessageId, SendName, SendUserId, ReceiveUserName, ReceiveUserId, MessageRow, SendTime, IsDelete " +
                "FROM message " +
                "WHERE (SendUserId = ? AND ReceiveUserId = ? OR SendUserId=? AND ReceiveUserId=?) AND IsDelete <> 1";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, userId);
        pstmt.setInt(2, chaUserid);
        pstmt.setInt(3,chaUserid);
        pstmt.setInt(4,userId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int messageId = rs.getInt("MessageId");
            String sendName = rs.getString("SendName");
            int sendUserId = rs.getInt("SendUserId");
            String receiveUserName = rs.getString("ReceiveUserName");
            int receiveUserId = rs.getInt("ReceiveUserId");
            String messageRow = rs.getString("MessageRow");
            Timestamp sendTime = rs.getTimestamp("SendTime");
            int isDelete = rs.getInt("IsDelete");
            Message message = new Message(messageId, sendName, sendUserId, receiveUserName, receiveUserId,
                    messageRow, sendTime, isDelete);
            messages.add(message);
        }
        rs.close();
        pstmt.close();
        return messages;
    }


//    public static boolean sendMessage(String sendName, int sendUserId, String receiveUserName, int receiveUserId,String messageRow) {
//        String sql = "INSERT INTO message (SendName, SendUserId, ReceiveUserName, ReceiveUserId, MessageRow, SendTime) "
//                + "VALUES (?, ?, ?, ?, ?, ?)";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//
//            Date sendTime = new Date();
//
//            pstmt.setString(1, sendName);
//            pstmt.setInt(2, sendUserId);
//            pstmt.setString(3, receiveUserName);
//            pstmt.setInt(4, receiveUserId);
//            pstmt.setString(5, messageRow);
//            pstmt.setTimestamp(6, new java.sql.Timestamp(sendTime.getTime()));
//            int rowsInserted = pstmt.executeUpdate();
//            if (rowsInserted > 0) {
//                return true;
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//            return false;
//        }
//        return false;
//    }

    public static boolean sendMessage(int sendUserId, int receiveUserId, String messageRow) {
        // Retrieve the SendName and ReceiveUserName from the userinfo table
        String sendName = "";
        String receiveUserName = "";
        String sql = "SELECT UserName FROM userinfo WHERE UserId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, sendUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                sendName = rs.getString("UserName");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        sql = "SELECT UserName FROM userinfo WHERE UserId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, receiveUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                receiveUserName = rs.getString("UserName");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // Insert the message into the message table
        sql = "INSERT INTO message (SendName, SendUserId, ReceiveUserName, ReceiveUserId, MessageRow, SendTime) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            Date sendTime = new Date();
            pstmt.setString(1, sendName);
            pstmt.setInt(2, sendUserId);
            pstmt.setString(3, receiveUserName);
            pstmt.setInt(4, receiveUserId);
            pstmt.setString(5, messageRow);
            pstmt.setTimestamp(6, new java.sql.Timestamp(sendTime.getTime()));
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }


}
