/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import start.Routes;

/**
 *
 * @author CassiusTedesco
 */
public class DAOSQLValidation {
    
    private final String SQL_SELECT_ALL = "SELECT * FROM " + Routes.VALIDATION.getDbServerDB() + "." + Routes.VALIDATION.getDbServerTABLE() + ";";
    private final String SQL_SELECT = "SELECT * FROM " + Routes.VALIDATION.getDbServerDB() + "." + Routes.VALIDATION.getDbServerTABLE() + " WHERE (name = ?) AND (password = ?);";
    
    
    public Connection connect() throws SQLException {
        Connection conn;
        conn = DriverManager.getConnection(Routes.DB.getDbServerAddress() + Routes.DB.getDbServerComOpt(), Routes.DB.getDbServerUser(), Routes.DB.getDbServerPassword());
        return conn;
    }

    public void disconnect(Connection conn) throws SQLException {
        conn.close();
    }
    
    public boolean validate(String name, String password) throws SQLException{
        Connection conn;
        PreparedStatement instruction;
        ResultSet rs;
        conn = connect();
        instruction = conn.prepareStatement(SQL_SELECT);
        instruction.setString(1, name);
        instruction.setString(2, password);
        rs = instruction.executeQuery();
        while (rs.next()) {
            return true;
        }
        rs.close();
        instruction.close();
        disconnect(conn);
        return false;
    }
}
