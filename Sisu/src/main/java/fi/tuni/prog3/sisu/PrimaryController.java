package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * Class offers public and private getter and setter functions. Also offers
 * FXML-switch functions for functionality. This Controller reads student data
 * into JSON where we can get it to the Student info page too later in the
 * program. Controlled FXML asks for following user input:
 * <ul>
 * <li> User name: only letters allowed, length unlimited.</li>
 * <li>User number: only numbers allowed, length unlimited.</li>
 * <li> User studies start year: only numbers allowed, four digit limit</li>
 * <li>User studies finish year: only numbers allowed, four digit limit.</li>
 * </ul>
 *
 * @author Johanna
 */
public class PrimaryController implements Initializable {

    private String sname;
    private String snumber;
    private String sstart;
    private String sfinish;

    @FXML
    private TextField name;
    @FXML
    private TextField number;
    @FXML
    private ComboBox start;
    @FXML
    private ComboBox finish;

    /**
     * Returns a String for student name.
     *
     * @return String for student name.
     */
    public String getName() {
        return sname;
    }

    /**
     * Returns a String for student number.
     *
     * @return String for student number.
     */
    public String getNumber() {
        return snumber;
    }

    /**
     * Returns a String for start year.
     *
     * @return a String for start year.
     */
    public String getStart() {
        return sstart;
    }

    /**
     * Returns a String for graduation year.
     *
     * @return a String for graduation year.
     */
    public String getFinish() {
        return sfinish;
    }

    /**
     * Sets student name for student information.
     *
     * @param name student's name.
     */
    public void setName(String name) {
        this.sname = name;
    }

    /**
     * Sets student number for student information.
     *
     * @param number student number.
     */
    public void setNumber(String number) {
        this.snumber = number;
    }

    /**
     * Sets start year for student information.
     *
     * @param start starting year of studies.
     */
    public void setStart(String start) {
        this.sstart = start;
    }

    /**
     * Sets graduation year for student information.
     *
     * @param finish ending year of studies.
     */
    public void setFinish(String finish) {
        this.sfinish = finish;
    }

    @FXML
    private void switchToSecondary() throws IOException {
        Sisu.setRoot("secondary");
    }

    /**
     * Checks TextField input validity
     *
     * @param name student name for student information
     * @param number student number for student information
     * @param start student studies star year for student information
     * @param finish student studies graduation year for student information
     * @return int value: 1 if errors occur in input, 0 if zero errors found
     * @throws IOException if data cannot be read.
     */
    public int addTostudentData(String name,
        String number, String start, String finish) throws IOException {
        boolean errors = false;
        Pattern special = Pattern.compile("[^a-z0-9 ]",
            Pattern.CASE_INSENSITIVE);

        Matcher matcher = special.matcher(name);
        boolean constainsSymbols = matcher.find();

        if (Integer.valueOf(start) >= Integer.valueOf(finish)) {
            System.out.println("inputerror");
            errors = true;
            Sisu.setRoot("inputerror");
        }
        if (constainsSymbols == true) {
            System.out.println("inputerror");
            Sisu.setRoot("inputerror");
            errors = true;
        }
        //input check on name 
        if (name.matches("[0-9]+") || number.matches("[a-zA-Z]+")) {
            System.out.println("inputerror");
            Sisu.setRoot("inputerror");
            errors = true;
        }

        if (errors == true) {
            return 1;
        }
        return 0;
    }

    /*Adds student data into a json file for safekeeping
     */
    private void addToJson(String name, String number, String start,
        String finish) throws IOException {
        try {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("name", name);
            map.put("number", number);
            map.put("start", start);
            map.put("finish", finish);

            Writer writer = new FileWriter("student.json");

            new Gson().toJson(map, writer);
            writer.close();
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    /*In pressing button:
    * Writes TextField values in student.txt and calls
    * {@link #addTostudentData(String name,String number, String start, String finish)}
    * @throws IOException - if there is some kind of an IO error 
    * (e.g. if the specified file does not exist).
    * Then  sets view to mainPage.
     */
    private void switchToMainPage() throws IOException {

        int errors;

        if (name.getText() != null || number.getText() != null || start
            .getValue() != null
            || finish.getValue() != null) {
            errors = addTostudentData(name.getText(), number.getText(),
                start.getValue().toString(), finish.getValue().toString());

            if (errors == 0) {
                addToJson(name.getText(), number.getText(),
                    start.getValue().toString(), finish.getValue().toString());
                Sisu.setRoot("mainPage");
            }
        } else {
            Sisu.setRoot("inputerror");
        }

    }

    /**
     * Initializes the window and adds year for the drop down-selection.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> years = FXCollections.observableArrayList();
        for (int i = 1990; i < 2041; i++) {
            years.add(String.valueOf(i));
        }
        start.setItems(years);
        finish.setItems(years);
    }
}
