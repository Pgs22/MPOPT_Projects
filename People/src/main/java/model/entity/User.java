/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;


public class User {
    
    private String name;
    private String password;
    private String rol;

    public User(String name, String password, String rol){
        this.name = name;
        this.password = password;
        this.rol = rol;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the rol
     */
    public String getRol() {
        return rol;
    }
    
}
