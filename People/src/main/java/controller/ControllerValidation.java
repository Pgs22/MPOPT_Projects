/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import model.dao.DAOSQLValidation;
import view.Login;

/**
 *
 * @author CassiusTedesco
 */
public class ControllerValidation {

//    Login login;
//    
//    private void handleLogin() {
//        login = new Login();
//        login.setVisible(true);
//        login.getLogin().addActionListener(this);
//    }
//    
//    private void validateLogin(){
//        String name = login.getUsername().getText();
//        char[] passwordChars = login.getPasswordField().getPassword();
//        String password = new String(passwordChars);
//        DAOSQLValidation daoValidation = new DAOSQLValidation();
//        
//        try {
//            if(daoValidation.validate(name, password)){
//                login.dispose();
//            }
//            else{
//                JOptionPane.showMessageDialog(login, "User not found. Closing application.", "People v1.1.0", JOptionPane.ERROR_MESSAGE);
//                System.exit(0);
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(login, "User not found. Closing application.", "People v1.1.0", JOptionPane.ERROR_MESSAGE);
//            System.exit(0);
//        }
//    }
}
