package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    public static DBconnection dBconnection;
    public Connection connection;

    private DBconnection(){


        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist", "root", "");

        } catch (ClassNotFoundException | SQLException e) {

            e.printStackTrace();
        }

    }

    public static DBconnection getInstance(){

        return(dBconnection==null) ? dBconnection = new DBconnection() : dBconnection;
    }

    public Connection getConnection(){
        return connection;
    }
}
