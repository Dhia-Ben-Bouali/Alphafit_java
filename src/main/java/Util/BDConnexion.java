package Util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDConnexion {
    static String url="jdbc:mysql://localhost:3306/gestuser";
    static   String login="root";
    static String pwd="";
    static String driver = "com.mysql.cj.jdbc.Driver";
    public static Connection getcon(){
        Connection con = null;
        try {
            Class.forName(driver);
            try {
                con = DriverManager.getConnection(url,login,pwd);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return con;
    }
}
