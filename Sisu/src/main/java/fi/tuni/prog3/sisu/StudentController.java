/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
 import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * FXML Controller class
 * Class has txt.file-reding function for Arraylist initialization
 * And an initializing fucntion for FXML.
 * Also offers FXML-switch functions for functionality between FXML views.
 * @author Johanna
 */
public class StudentController implements Initializable {
    PrimaryController info;
    @FXML
    private Label ownName;
    @FXML
    private Label ownNumber;
    @FXML
    private Label ownStart;
    @FXML
    private Label ownFinish;
    private final String firstName;
    private final String surName;
    private final String startYear;
    private final String finishYear;
    
    public StudentController(String firstName, String surName, String startYear
                                    , String finishYear) {

        this.firstName = firstName;
        this.surName = surName;
        this.startYear = startYear;
        this.finishYear = finishYear;
    }
     /*
    * Sets the view back to mainPage.fxml
    */
    @FXML
    private void switchToMainPage() throws IOException{
        Sisu.setRoot("mainPage");
    }

    @FXML
    private void switchToPrimary() throws IOException {
        Sisu.setRoot("primary");
    }
    @FXML
    private void exitProgram() throws IOException{
        
        Platform.exit();
    }
    /**
     * Reads student.json  and saves text into ArrayList.
     * @return ArrayList of Student info written in PrimariController.java
     * @throws FileNotFoundException if file is not found
     * @throws IOException
     */
    private ArrayList<String> getInfo() 
            throws FileNotFoundException, IOException{
        
        ArrayList<String> lista = new ArrayList<>();
        
        try 
        {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("student.json"));
            LinkedHashMap <String, String> map = 
                    gson.fromJson(reader, LinkedHashMap.class);
            
            for(Map.Entry<String, String>entry : map.entrySet()){
                lista.add(entry.getValue());
            }
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Initialises a new PrimaryController object
     * and uses object setter functions to set attributes.
     * Sets label texts in student.fxml.
     * Calls {@link #getInfo()}
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ArrayList<String> list = getInfo();
            PrimaryController info = new PrimaryController();
            info.setName(list.get(0));
            info.setNumber(list.get(1));
            info.setStart(list.get(2));
            info.setFinish(list.get(3));
            
            ownName.setText(info.getName());
            ownNumber.setText(info.getNumber());
            ownStart.setText(info.getStart());
            ownFinish.setText(info.getFinish());
            
        } catch (IOException ex) {
            Logger.getLogger(StudentController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }    
    
}