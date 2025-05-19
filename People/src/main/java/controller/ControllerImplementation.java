package controller;

import model.entity.Person;
import model.entity.PersonException;
import model.dao.DAOArrayList;
import model.dao.DAOFile;
import model.dao.DAOFileSerializable;
import model.dao.DAOHashMap;
import model.dao.DAOJPA;
import model.dao.DAOSQL;
import model.dao.IDAO;
import start.Routes;
import view.DataStorageSelection;
import view.Delete;
import view.Insert;
import view.Menu;
import view.Read;
import view.ReadAll;
import view.Update;
import view.Login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.persistence.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.dao.DAOSQLValidation;
import model.entity.User;
import org.jdatepicker.DateModel;
import static utils.Constants.ARRAY_LIST;
import static utils.Constants.FILE;
import static utils.Constants.FILE_SERIALIZATION;
import static utils.Constants.HASH_MAP;
import static utils.Constants.JPA_DATABASE;
import static utils.Constants.SQL_DATABASE;



/**
 * This class starts the visual part of the application and programs and manages
 * all the events that it can receive from it. For each event received the
 * controller performs an action.
 *
 * @author Francesc Perez
 * @version 1.1.0
 */
public class ControllerImplementation implements IController, ActionListener {

    //Instance variables used so that both the visual and model parts can be 
    //accessed from the Controller.
    private final DataStorageSelection dSS;
    private IDAO dao;
    private Login login;
    private Menu menu;
    private Insert insert;
    private Read read;
    private Delete delete;
    private Update update;
    private ReadAll readAll;
    public static User user;

    /**
     * This constructor allows the controller to know which data storage option
     * the user has chosen.Schedule an event to deploy when the user has made
     * the selection.
     *
     * @param dSS
     */
    public ControllerImplementation(DataStorageSelection dSS) {
        this.dSS = dSS;
        ((JButton) (dSS.getAccept()[0])).addActionListener(this);
    }

    /**
     * With this method, the application is started, asking the user for the
     * chosen storage system.
     */
    @Override
    public void start() {
        dSS.setVisible(true);
    }

    /**
     * This receives method handles the events of the visual part. Each event
     * has an associated action.
     *
     * @param e The event generated in the visual part
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dSS.getAccept()[0]) {
            handleDataStorageSelection();
        } else if(e.getSource() == login.getLogin()){
            validateLogin();
        } else if (e.getSource() == menu.getInsert()) {
            handleInsertAction();
        } else if (insert != null && e.getSource() == insert.getInsert()) {
            handleInsertPerson();
        } else if (e.getSource() == menu.getRead()) {
            handleReadAction();
        } else if (read != null && e.getSource() == read.getRead()) {
            handleReadPerson();
        } else if (e.getSource() == menu.getDelete()) {
            handleDeleteAction();
        } else if (delete != null && e.getSource() == delete.getDelete()) {
            handleDeletePerson();
        } else if (e.getSource() == menu.getUpdate()) {
            handleUpdateAction();
        } else if (update != null && e.getSource() == update.getRead()) {
            handleReadForUpdate();
        } else if (update != null && e.getSource() == update.getUpdate()) {
            handleUpdatePerson();
        } else if (e.getSource() == menu.getReadAll()) {
            handleReadAll();
        } else if (e.getSource() == menu.getDeleteAll()) {
            handleDeleteAll();
        }
    }

    private void handleDataStorageSelection() {
        String daoSelected = ((javax.swing.JCheckBox) (dSS.getAccept()[1])).getText();
        dSS.dispose();
        switch (daoSelected) {
            case ARRAY_LIST:
                dao = new DAOArrayList();
                break;
            case HASH_MAP:
                dao = new DAOHashMap();
                break;
            case FILE:
                setupFileStorage();
                break;
            case FILE_SERIALIZATION:
                setupFileSerialization();
                break;
            case SQL_DATABASE:
                setupSQLDatabase();
                break;
            case JPA_DATABASE:
                setupJPADatabase();
                break;
        }
           handleLogin();
    }

    private void setupFileStorage() {
        File folderPath = new File(Routes.FILE.getFolderPath());
        File folderPhotos = new File(Routes.FILE.getFolderPhotos());
        File dataFile = new File(Routes.FILE.getDataFile());
        folderPath.mkdir();
        folderPhotos.mkdir();
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dSS, "File structure not created. Closing application.", "File - People v1.1.0", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        dao = new DAOFile();
    }

    private void setupFileSerialization() {
        File folderPath = new File(Routes.FILES.getFolderPath());
        File dataFile = new File(Routes.FILES.getDataFile());
        folderPath.mkdir();
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dSS, "File structure not created. Closing application.", "FileSer - People v1.1.0", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        dao = new DAOFileSerializable();
    }

    private void setupSQLDatabase() {
        try {
            Connection conn = DriverManager.getConnection(Routes.DB.getDbServerAddress() + Routes.DB.getDbServerComOpt(),
                    Routes.DB.getDbServerUser(), Routes.DB.getDbServerPassword());
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("create database if not exists " + Routes.DB.getDbServerDB() + ";");
                stmt.executeUpdate("create table if not exists " + Routes.DB.getDbServerDB() + "." + Routes.DB.getDbServerTABLE() + "("
                        + "nif varchar(9) primary key not null, "
                        + "name varchar(50), "
                        + "email varchar(150), "
                        + "phoneNumer varchar(50), "
                        + "postalCode varchar(9), "
                        + "dateOfBirth DATE, "
                        + "photo varchar(200) );");
                stmt.close();
                conn.close();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dSS, "SQL-DDBB structure not created. Closing application.", "SQL_DDBB - People v1.1.0", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        dao = new DAOSQL();
    }

    private void setupJPADatabase() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(Routes.DBO.getDbServerAddress());
            EntityManager em = emf.createEntityManager();
            em.close();
            emf.close();
        } catch (PersistenceException ex) {
            JOptionPane.showMessageDialog(dSS, "JPA_DDBB not created. Closing application.", "JPA_DDBB - People v1.1.0", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        dao = new DAOJPA();
    }

    private void handleLogin() {
        login = new Login();
        login.setVisible(true);
        login.getLogin().addActionListener(this);
    }
    
    private void validateLogin(){
        String name = login.getUsername().getText();
        char[] passwordChars = login.getPasswordField().getPassword();
        String password = new String(passwordChars);
        DAOSQLValidation daoValidation = new DAOSQLValidation();
        try {
            this.user = daoValidation.validate(name, password);
            login.dispose();
            if(user != null){
                if(user.getRol().equals("admin")){
                    setupAdminMenu();
                }
                else{
                    setupEmployeeMenu();
                }
            }
            else{
                JOptionPane.showMessageDialog(login, "Invalid username or password.", "People v1.1.0", JOptionPane.ERROR_MESSAGE);
                resetLoginForm();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(login, "User not found. Closing application.", "People v1.1.0", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private void resetLoginForm(){
        login.getUsername().setText("");
        login.getPasswordField().setText("");
        login.setVisible(true);
    }

    
    private void setupAdminMenu() {
        menu = new Menu();
        menu.setVisible(true);
        menu.getInsert().addActionListener(this);
        menu.getRead().addActionListener(this);
        menu.getUpdate().addActionListener(this);
        menu.getDelete().addActionListener(this);
        menu.getReadAll().addActionListener(this);
        menu.getDeleteAll().addActionListener(this);
    }
    
    private void setupEmployeeMenu() {
        menu = new Menu();
        menu.setVisible(true);
        menu.getInsert().addActionListener(this);
        menu.getRead().addActionListener(this);
        menu.getUpdate().addActionListener(this);
        menu.getDelete().addActionListener(this);
        menu.getReadAll().addActionListener(this);
        menu.getDeleteAll().addActionListener(this);
        
        menu.getInsert().setEnabled(false);
        menu.getUpdate().setEnabled(false);
        menu.getDelete().setEnabled(false);
        menu.getDeleteAll().setEnabled(false);
    }

    private void handleInsertAction() {
        insert = new Insert(menu, true);
        insert.getInsert().addActionListener(this);
        insert.setVisible(true);
    }

    private void handleInsertPerson() {
        String name = insert.getNam().getText().equals("Enter full name") ? null : insert.getNam().getText();
        Person p = new Person(name, insert.getNif().getText());
        if(insert.getEmail().getText() != null && !insert.getEmail().getText().equals("Enter your email")){
            p.setEmail((String) insert.getEmail().getText());
        }
        if(insert.getPhoneNumber().getText() != null && !insert.getPhoneNumber().getText().equals("Enter your phone number")){
            p.setPhoneNumber((String) insert.getPhoneNumber().getText());
        }
        if(insert.getPostalCode().getText() != null && !insert.getPostalCode().getText().equals("Enter your postal code")){
            p.setPostalCode((String) insert.getPostalCode().getText());
        }
        if (insert.getDateOfBirth().getModel().getValue() != null) {
            p.setDateOfBirth(((GregorianCalendar) insert.getDateOfBirth().getModel().getValue()).getTime());
        }
        if (insert.getPhoto().getIcon() != null) {
            p.setPhoto((ImageIcon) insert.getPhoto().getIcon());
        }
        insert(p);
        insert.getReset().doClick();
    }

    private void handleReadAction() {
        read = new Read(menu, true);
        read.getRead().addActionListener(this);
        read.setVisible(true);
    }

    private void handleReadPerson() {
        Person p = new Person(read.getNif().getText());
        Person pNew = read(p);
        if (pNew != null) {
            read.getNam().setText(pNew.getName());
            if (pNew.getEmail() != null){
                read.getEmail().setText(pNew.getEmail());
            }
            if (pNew.getPhoneNumber() != null){
                read.getPhoneNumber().setText(pNew.getPhoneNumber());
            }
            if (pNew.getPostalCode() != null){
                read.getPostalCode().setText(pNew.getPostalCode());
            }
            if (pNew.getDateOfBirth() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(pNew.getDateOfBirth());
                DateModel<Calendar> dateModel = (DateModel<Calendar>) read.getDateOfBirth().getModel();
                dateModel.setValue(calendar);
            }
            //To avoid charging former images
            if (pNew.getPhoto() != null) {
                pNew.getPhoto().getImage().flush();
                read.getPhoto().setIcon(pNew.getPhoto());
            }
        } else {
            JOptionPane.showMessageDialog(read, p.getNif() + " doesn't exist.", read.getTitle(), JOptionPane.WARNING_MESSAGE);
            read.getReset().doClick();
        }
    }

    public void handleDeleteAction() {
        delete = new Delete(menu, true);
        delete.getDelete().addActionListener(this);
        delete.setVisible(true);
    }

    public void handleDeletePerson() {
        if (delete != null) {
            Person p = new Person(delete.getNif().getText());
            delete(p);
            delete.getReset().doClick();
        }
    }

    public void handleUpdateAction() {
        update = new Update(menu, true);
        update.getUpdate().addActionListener(this);
        update.getRead().addActionListener(this);
        update.setVisible(true);
    }

    public void handleReadForUpdate() {
        if (update != null) {
            Person p = new Person(update.getNif().getText());
            Person pNew = read(p);
            if (pNew != null) {
                update.getNam().setEnabled(true);
                update.getDateOfBirth().setEnabled(true);
                update.getPhoto().setEnabled(true);
                update.getEmail().setEnabled(true);
                update.getPhoneNumber().setEnabled(true);
                update.getPostalCode().setEnabled(true);
                update.getUpdate().setEnabled(true);
                update.getNam().setText(pNew.getName());
                if(pNew.getEmail() != null){
                    update.getEmail().setText(pNew.getEmail());
                }
                if(pNew.getPhoneNumber() != null){
                    update.getPhoneNumber().setText(pNew.getPhoneNumber());
                }
                if(pNew.getPostalCode() != null){
                    update.getPostalCode().setText(pNew.getPostalCode());
                }                
                if (pNew.getDateOfBirth() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(pNew.getDateOfBirth());
                    DateModel<Calendar> dateModel = (DateModel<Calendar>) update.getDateOfBirth().getModel();
                    dateModel.setValue(calendar);
                }
                if (pNew.getPhoto() != null) {
                    pNew.getPhoto().getImage().flush();
                    update.getPhoto().setIcon(pNew.getPhoto());
                    update.getUpdate().setEnabled(true);
                }
            } else {
                JOptionPane.showMessageDialog(update, p.getNif() + " doesn't exist.", update.getTitle(), JOptionPane.WARNING_MESSAGE);
                update.getReset().doClick();
            }
        }
    }

    public void handleUpdatePerson() {
        if (update != null) {
            Person p = new Person(update.getNam().getText(), update.getNif().getText());
            if ((update.getEmail().getText() != null)){
                p.setEmail(update.getEmail().getText());
            }
            if ((update.getPhoneNumber().getText() != null)){
                p.setPhoneNumber(update.getPhoneNumber().getText());
            }
            if ((update.getPostalCode().getText() != null)){
                p.setPostalCode(update.getPostalCode().getText());
            }                 
            if ((update.getDateOfBirth().getModel().getValue()) != null) {
                p.setDateOfBirth(((GregorianCalendar) update.getDateOfBirth().getModel().getValue()).getTime());
            }
            if ((ImageIcon) (update.getPhoto().getIcon()) != null) {
                p.setPhoto((ImageIcon) update.getPhoto().getIcon());
            }            
            update(p);
            update.getReset().doClick();
        }
    }

    public void handleReadAll() {
        ArrayList<Person> s = readAll();
        if (s.isEmpty()) {
            JOptionPane.showMessageDialog(menu, "There are not people registered yet.", "Read All - People v1.1.0", JOptionPane.WARNING_MESSAGE);
        } else {
            //readAll = new ReadAll(menu, true);
            readAll = new ReadAll(menu, true, this);
            DefaultTableModel model = (DefaultTableModel) readAll.getTable().getModel();
            for (int i = 0; i < s.size(); i++) {
                model.addRow(new Object[i]);
                model.setValueAt(s.get(i).getNif(), i, 0);
                model.setValueAt(s.get(i).getName(), i, 1);
                if (s.get(i).getDateOfBirth() != null) {
                    model.setValueAt(s.get(i).getDateOfBirth().toString(), i, 2);
                } else {
                    model.setValueAt("", i, 2);
                }
                if (s.get(i).getPhoto() != null) {
                    model.setValueAt("yes", i, 3);
                } else {
                    model.setValueAt("no", i, 3);
                }
                if (s.get(i).getEmail() != null){
                    model.setValueAt(s.get(i).getEmail(), i, 4);
                }
                else{
                    model.setValueAt("", i, 4);
                }
                if (s.get(i).getPhoneNumber() != null){
                    model.setValueAt(s.get(i).getPhoneNumber(), i, 5);
                }
                else{
                    model.setValueAt("", i, 5);
                }
                if (s.get(i).getPostalCode() != null){
                    model.setValueAt(s.get(i).getPostalCode(), i, 6);
                }
                else{
                    model.setValueAt("", i, 6);
                }
            }
            readAll.setVisible(true);
        }
    }
    
    public void handleExportData() {
        JFileChooser mySport = new JFileChooser(); // Para poder gestionar archivos
        int returnVal = mySport.showSaveDialog(readAll); // Usamos 'readAll' como padre
        if (returnVal == JFileChooser.APPROVE_OPTION) { // Para saber si ha pulsado guardar o cancelar
            File file = mySport.getSelectedFile(); //Método del objeto que le he llamado mySport para obtener referencia del archivo (Que aún no existe)
            // Para evitar que no guarde sin la extensión del archivo .csv, si no la tiene, se la añadimos
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            // Para obtener la fecha actual
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = currentDate.format(formatter);

            // Para añadir al nombre del archivo la fecha actual
            String baseFilename = file.getAbsolutePath();
            File fileWithDate = new File(baseFilename + "_" + formattedDate + ".csv");

            try {
                FileWriter writer = new FileWriter(fileWithDate); // Usamos el fichero con la fecha
                JTable table = readAll.getTable(); // Obtenemos la tabla de 'readAll'
                if (table != null) {
                    // Escribe los encabezados de las columnas
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        writer.append(table.getColumnName(i));
                        if (i < table.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");

                    // Escribe los datos de la tabla
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            Object value = model.getValueAt(i, j);
                            writer.append(value != null ? value.toString() : "");
                            if (j < table.getColumnCount() - 1) {
                                writer.append(",");
                            }
                        }
                        writer.append("\n"); // Después de cada fila, salto de línea
                    }
                    writer.flush(); // Escribe en el disco
                    writer.close();
                    JOptionPane.showMessageDialog(readAll, "Datos exportados correctamente a " + fileWithDate.getAbsolutePath(), "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(readAll, "No hay tabla para exportar.", "Error de Exportación", JOptionPane.ERROR_MESSAGE);
                    if (writer != null) {
                        writer.close(); // Cierre si hay error
                    }
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(readAll, "Error al exportar los datos: " + ex.getMessage(), "Error de Exportación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public void handleDeleteAll() {
        Object[] options = {"Yes", "No"};
        //int answer = JOptionPane.showConfirmDialog(menu, "Are you sure to delete all people registered?", "Delete All - People v1.1.0", 0, 0);
        int answer = JOptionPane.showOptionDialog(
        menu,
        "Are you sure you want to delete all registered people?", 
        "Delete All - People v1.1.0",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE,
        null,
        options,
        options[1] // Default selection is "No"
    );

        if (answer == 0) {
            deleteAll();
        }
    }
    
    /**
     * This function inserts the Person object with the requested NIF, if it
     * doesn't exist. If there is any access problem with the storage device,
     * the program stops.
     *
     * @param p Person to insert
     */
    @Override
    public void insert(Person p) {
        try {
            if (dao.read(p) == null) {
                dao.insert(p);
                JOptionPane.showMessageDialog(insert, "Person inserted successfully!");
            } else {
                throw new PersonException(p.getNif() + " is registered and can not "
                        + "be INSERTED.");
            }
        } catch (Exception ex) {
            //Exceptions generated by file read/write access. If something goes 
            // wrong the application closes.
            if (ex instanceof FileNotFoundException || ex instanceof IOException
                    || ex instanceof ParseException || ex instanceof ClassNotFoundException
                    || ex instanceof SQLException || ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(insert, ex.getMessage() + ex.getClass() + " Closing application.", insert.getTitle(), JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            if (ex instanceof PersonException) {
                JOptionPane.showMessageDialog(insert, ex.getMessage(), insert.getTitle(), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * This function updates the Person object with the requested NIF, if it
     * doesn't exist. NIF can not be aupdated. If there is any access problem
     * with the storage device, the program stops.
     *
     * @param p Person to update
     */
    @Override
    public void update(Person p) {
        try {
            dao.update(p);
            JOptionPane.showMessageDialog(update, "Person updated successfully!", update.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            //Exceptions generated by file read/write access. If something goes 
            // wrong the application closes.
            if (ex instanceof FileNotFoundException || ex instanceof IOException
                    || ex instanceof ParseException || ex instanceof ClassNotFoundException
                    || ex instanceof SQLException || ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(update, ex.getMessage() + ex.getClass() + " Closing application.", update.getTitle(), JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    /**
     * This function deletes the Person object with the requested NIF, if it
     * exists. If there is any access problem with the storage device, the
     * program stops.
     *
     * @param p Person to read
     */
    @Override
public void delete(Person p) {
    int opcion = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this person?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

    if (opcion == JOptionPane.YES_OPTION) {
        try {
            if (dao.read(p) != null) {
                dao.delete(p);
                JOptionPane.showMessageDialog(update, "Person deleted successfully!");
            } else {
                throw new PersonException(p.getNif() + " is not registered and can not be DELETED");
            }
        } catch (Exception ex) {
            // Exceptions generated by file, DDBB read/write access. If something goes wrong the application closes.
            if (ex instanceof FileNotFoundException || ex instanceof IOException
                    || ex instanceof ParseException || ex instanceof ClassNotFoundException
                    || ex instanceof SQLException || ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(read, ex.getMessage() + ex.getClass() + " Closing application.", "Insert - People v1.1.0", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            if (ex instanceof PersonException) {
                JOptionPane.showMessageDialog(read, ex.getMessage(), "Delete - People v1.1.0", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    // Si elige NO, no pasa nada. Simplemente vuelve al panel actual.
}



    /**
     * This function returns the Person object with the requested NIF, if it
     * exists. Otherwise it returns null. If there is any access problem with
     * the storage device, the program stops.
     *
     * @param p Person to read
     * @return Person or null
     */
    @Override
    public Person read(Person p) {
        try {
            Person pTR = dao.read(p);
            if (pTR != null) {
                return pTR;
            }
        } catch (Exception ex) {

            //Exceptions generated by file read access. If something goes wrong 
            //reading the file, the application closes.
            if (ex instanceof FileNotFoundException || ex instanceof IOException
                    || ex instanceof ParseException || ex instanceof ClassNotFoundException
                    || ex instanceof SQLException || ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(read, ex.getMessage() + " Closing application.", read.getTitle(), JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        return null;
    }

    /**
     * This function returns the people registered. If there is any access
     * problem with the storage device, the program stops.
     *
     * @return ArrayList
     */
    @Override
    public ArrayList<Person> readAll() {
        ArrayList<Person> people = new ArrayList<>();
        try {
            people = dao.readAll();
        } catch (Exception ex) {
            if (ex instanceof FileNotFoundException || ex instanceof IOException
                    || ex instanceof ParseException || ex instanceof ClassNotFoundException
                    || ex instanceof SQLException || ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(readAll, ex.getMessage() + " Closing application.", readAll.getTitle(), JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        return people;
    }

    /**
     * This function deletes all the people registered. If there is any access
     * problem with the storage device, the program stops.
     */
    @Override
    public void deleteAll() {
        try {
            dao.deleteAll();
            JOptionPane.showMessageDialog(menu, "All persons have been deleted successfully!");
        } catch (Exception ex) {
            if (ex instanceof FileNotFoundException || ex instanceof IOException
                    || ex instanceof ParseException || ex instanceof ClassNotFoundException
                    || ex instanceof SQLException || ex instanceof PersistenceException) {
                JOptionPane.showMessageDialog(menu, ex.getMessage() + " Closing application.", "Delete All - People v1.1.0", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

}
