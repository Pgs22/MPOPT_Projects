package model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.swing.ImageIcon;

/**
 * Encapsulated class that defines the type of entity that will manage the application.
 * @author Fran Perez
 * @version 1.1.0
 */
@Entity
public class Person implements Serializable{

    @Id 
    private String nif;
    private String name;
    private String email;
    private String phoneNumber;
    private String postalCode;
    private Date dateOfBirth;
    @Transient
    private ImageIcon photo;
    @Lob
    private byte[] photoOnlyJPA;

    public Person(){
        
    }
    
    /*
ok Añadir un nuevo campo "postalCode" al modelo "Persona".
Iok mplementar la validación con expresiones regulares para garantizar que el código postal tenga el formato correcto.
ok Actualizar la interfaz de usuario para gestionar la entrada y la validación del código postal al añadir o actualizar una persona.
ok Contexto adicional

ok Campo de código postal:
ok Añadir un atributo "postalCode" a la clase "Persona" con los métodos getter y setter necesarios.

ok Validación con expresiones regulares:
Usar un patrón de expresiones regulares para validar el formato del código postal. Por ejemplo, para un formato típico de código postal de EE. UU.:
String postalCodeRegex = "^(\d{5})(?:[-\s]?\d{4})?$";
Esto gestionará tanto códigos postales de 5 dígitos como formatos extendidos de 9 dígitos (ZIP+4).

ok Cambios en el menú o la app:
Actualice el formulario o el panel de entrada de la app o el menú para incluir el campo "Código postal".
Al añadir o editar una persona, valide el código postal con la expresión regular. Si el código postal no es válido, muestre un mensaje de error como "Formato de código postal no válido".

ok Gestión de errores en la app:
Si el código postal no supera la validación, impida que el usuario guarde o actualice el registro y muestre un mensaje solicitándole que introduzca un código postal válido.   
    */
    
    
    
    /**
     * Constructor to validate new person. Two persons cannot have the same NIF
     * @param nif 
     */
    public Person(String nif) {
        this.nif = nif;
    }
    
    /**
     * Constructor with mandatory data.
     * @author Fran Perez
     * @version 1.0
     */
    public Person(String name, String nif) {
        this.name = name;
        this.nif = nif;
    }

    /**
     * Constructor with all data
     * @author Fran Perez
     * @version 1.0
     * @param name
     * @param nif
     * @param dateOfBirth
     * @param photo
     * @param email
     * @param phoneNumber
     * @param postalCode
     */
    public Person(String name, String nif, Date dateOfBirth, ImageIcon photo, String email, String phoneNumber, String postalCode) {
        this.name = name;      
        this.nif = nif;
        this.dateOfBirth = dateOfBirth;
        this.photo = photo;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.postalCode = postalCode;
    }

//    public Person(String name, String nif, String email, Date dateOfBirth, ImageIcon photo) {
//        this.name = name;      
//        this.nif = nif;
//        this.email = email;
//        this.dateOfBirth = dateOfBirth;
//        this.photo = photo;
//    }
    
    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }    

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }      
    
    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ImageIcon getPhoto() {
        return photo;
    }

    public void setPhoto(ImageIcon photo) {
        this.photo = photo;
    }

    public byte[] getPhotoOnlyJPA() {
        return photoOnlyJPA;
    }

    public void setPhotoOnlyJPA(byte[] photoOnlyJPA) {
        this.photoOnlyJPA = photoOnlyJPA;
    }  
    
    /**
     * Function used to compare two Personas. There cannot be two or more people
     * with the same ID. Actually it isn't used in this project.
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.nif);
        return hash;
    }

    /**
     * Function used to compare two Personas in ArrayList and HashMap 
     * structures. There cannot be two or more people with the same ID.
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        return Objects.equals(this.hashCode(), other.hashCode());
    }

    
    /**
     * Function sed to show person's inform by console. Only for debugging 
     * pourposes.
     * @return 
     */
    @Override
    public String toString() {
        return "Person {" + "Name = " + name + ", NIF = " + nif
                + ", Email = " + email
                + ", Phone numer = " + phoneNumber
                + ", Postal code = " + postalCode
                + ", DateOfBirth = " + dateOfBirth + ", Photo = " + (photo!=null) + "}";
    }


}
