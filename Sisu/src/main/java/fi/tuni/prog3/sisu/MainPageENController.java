package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Class for controlling the English main page.
 * This is additional part of the project.
 *
 * @author eevih and Johanna
 */
public class MainPageENController implements Initializable {

    private PrimaryController info;
    private String selectedDegree;
    private JsonData data;
    private HashMap<String, Course> courses;
    private HashMap<String, Course> selectedCourses = new HashMap<>();
    LinkedHashMap<String, String> map;
    private String degreeCredits;

    @FXML
    private Label courseName;
    @FXML
    private Label helloText;
    @FXML
    private Label opMaara;
    @FXML
    private Label opTotal;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private TextArea courseTextArea;
    @FXML
    private ComboBox<String> chooseDegreeBox;

    @FXML
    private void switchToSecondary() throws IOException {
        addCoursesToJson();
        Sisu.setRoot("secondary");
    }

    @FXML
    private void switchToStudent() throws IOException {
        Sisu.setRoot("studentEN");
    }

    @FXML
    private void exitProgram() {
        Platform.exit();
    }

    /*Adds student data into a json file for safekeeping
     */
    private void addCoursesToJson() throws IOException {
        try {

            ArrayList<String> info = getInfo();
            map = new LinkedHashMap<>();
            map.put("name", info.get(0));
            map.put("number", info.get(1));
            map.put("start", info.get(2));
            map.put("finish", info.get(3));

            StudentController student = new StudentController(info.get(0),
                info.get(1), info.get(2), info.get(3));

            for (Map.Entry<String, Course> entry : selectedCourses.entrySet()) {
                map.put(entry.getValue().getNameFI(), entry.getValue()
                    .getCredits());
            }
            System.out.println("map: " + map);
            Writer writer = new FileWriter("student.json");
            new Gson().toJson(map, writer);
            writer.close();
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleComboBoxAction() throws IOException {
        courses = new HashMap<>();

        treeView.setRoot(null);
        selectedDegree = chooseDegreeBox.getValue();
        data.parseId(selectedDegree);

        Id degree = data.getDegreeProgrammesByName().get(selectedDegree)
            .getDegree();

        TreeItem<String> root = new TreeItem<>(degree.getNameEN());
        degreeCredits = degree.getCreditUnitsEN();
        opTotal.setText(degreeCredits);

        for (var subject : data.getDegreeProgrammesByName().get(selectedDegree)
            .getDegree().getStudyModules()) {

            TreeItem<String> child = new TreeItem<>(subject.getNameEN()
                + subject.getCreditUnitsEN());

            for (var course : subject.getCourses()) {

                TreeItem<String> child1 = new TreeItem<>(course.getNameEN()
                    + course.getCreditUnitsEN());
                child.getChildren().add(child1);

                courses.put(course.getNameEN()
                    + course.getCreditUnitsEN(), course);
            }

            for (var group : subject.getGroupingModules()) {

                TreeItem<String> child2 = new TreeItem<>(group.getNameEN()
                    + group.getCreditUnitsEN());
                child.getChildren().add(child2);

                createItems(group, child2);
            }

            root.getChildren().add(child);
        }

        treeView.setRoot(root);

        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            try {
                handleMouseClicked(event);
            } catch (IOException ex) {
                Logger.getLogger(MainPageENController.class.getName())
                    .log(Level.SEVERE, null, ex);
            }
        };
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
        treeView.setCellFactory(e -> new MainPageENController.CustomCell());
    }

    private void createItems(GroupingModule group, TreeItem<String> parent) {
        for (var course : group.getCourses()) {
            TreeItem<String> child3 = new TreeItem<>(course
                .getNameEN()
                + course.getCreditUnitsEN());
            courses.put(course.getNameEN()
                + course.getCreditUnitsEN(), course);
            parent.getChildren().add(child3);
        }

        for (var group1 : group.getSubModules()) {

            TreeItem<String> child = new TreeItem<>(group1
                .getNameEN() + group1.getCreditUnitsEN());
            parent.getChildren().add(child);
            if (!(group1.getSubModules().isEmpty() && group1.getCourses()
                .isEmpty())) {
                createItems(group1, child);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ArrayList<String> list = new ArrayList<>();

            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("student.json"));
            LinkedHashMap<String, String> map = gson.fromJson(reader,
                LinkedHashMap.class);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(entry.getValue());
            }

            PrimaryController info = new PrimaryController();
            info.setName(list.get(0));

            String capName = info.getName().toLowerCase().substring(0, 1)
                .toUpperCase()
                + info.getName().substring(1);

            helloText.setText("Hello, " + capName + "!");
            loadDegreeData();

        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class
                .getName())
                .log(Level.SEVERE, null, ex);
        }
    }

    private void loadDegreeData() throws IOException {
        data = new JsonData();
        data.parseDegreeProgrammes();

        ObservableList<String> list = observableArrayList(
            data.getDegreeProgrammeNames());
        chooseDegreeBox.setItems(list);
    }

    /**
     * Reads student.json and saves text into ArrayList.
     *
     * @return ArrayList of Student info written in PrimariController.java
     * @throws FileNotFoundException if file is not found
     * @throws IOException
     */
    private ArrayList<String> getInfo()
        throws FileNotFoundException, IOException {

        ArrayList<String> list = new ArrayList<>();

        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("student.json"));
            LinkedHashMap<String, String> map = gson
                .fromJson(reader, LinkedHashMap.class);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(entry.getValue());
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return list;
    }

    private void handleMouseClicked(MouseEvent event) throws IOException {
        Node node = event.getPickResult().getIntersectedNode();

        ArrayList curriculumPeriods = new ArrayList<>();

        // Accept clicks only on node cell
        if (node instanceof Text || (node instanceof TreeCell
            && ((TreeCell) node).getText() != null)) {
            String name = (String) ((TreeItem) treeView.getSelectionModel()
                .getSelectedItem()).getValue();
            System.out.println("Node click: " + name);

            for (Map.Entry<String, Course> entry : courses.entrySet()) {

                String key = entry.getKey();
                Course course = entry.getValue();
                String courseText;

                if (key.equals(name)) {

                    courseText = (new StringBuilder())
                        .append("\n\n Curriculum"
                            + " period: ").toString();

                    for (String curriculumPeriod : course
                        .getCurriculumPeriods()) {
                        courseText += "\n   -" + curriculumPeriod + "\n";
                        curriculumPeriods.add(curriculumPeriod.substring(
                            curriculumPeriod.length() - 4,
                            curriculumPeriod.length()));
                    }
                    ArrayList<String> studentinfo = getInfo();
                    int currMin = Integer.valueOf(curriculumPeriods.get(0)
                        .toString());
                    int currMax = Integer.valueOf(curriculumPeriods
                        .get(curriculumPeriods.size() - 1)
                        .toString());
                    int studentMin = Integer.valueOf(studentinfo.get(2));
                    int studentMax = Integer.valueOf(studentinfo.get(3));

                    if (ValueRange.of(studentMin, studentMax)
                        .isValidIntValue(currMin)
                        || ValueRange.of(studentMin, studentMax)
                            .isValidIntValue(currMax)) {

                        String strippedLearningMaterial = course.
                            getLearningMaterialEN()
                            .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "\n");
                        String strippedContent = course
                            .getContentEN()
                            .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "\n");
                        String strippedOutcomes = course.getOutcomesEN()
                            .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "\n");
                        String strippedPrereqs = course.getPrerequisitesEN()
                            .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "\n");
                        courseName.setText(name);
                        courseTextArea.setText(courseText
                            + "\n Content: \n " + strippedContent
                            + "\n\n Outcome: \n " + strippedOutcomes
                            + "\n\n Learning material: \n"
                            + strippedLearningMaterial
                            + "\n\n Prerequisite knowledge: \n"
                            + strippedPrereqs
                        );
                    } else {
                        courseTextArea.setText("CURRICULUM NOT AVAILABLE FOR"
                            + "\n THE TIME BETWEEN YOUR STARTING YEAR"
                            + "\n AND GRADUATION YEAR");
                    }

                }
            }
        }
    }

    /**
     * A custom cell for the checkboxes in treeview
     */
    class CustomCell extends TreeCell<String> {

        CheckBox checkBox;

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (isEmpty()) {
                setGraphic(null);
                setText(null);
            } else {
                if (this.getTreeItem().isLeaf()) {
                    HBox cellBox = new HBox(10);

                    checkBox = new CheckBox();
                    /*
                    eventlistener for treeview checkbox activity
                     */
                    checkBox.selectedProperty().addListener(
                        new ChangeListener<Boolean>() {

                        @Override
                        public void changed(
                            ObservableValue<? extends Boolean> observable,
                            Boolean oldValue, Boolean newValue) {
                            /*
                            if checkbox is ticked, add the course to the map
                             */
                            if (newValue && !selectedCourses.containsKey(item)) {

                                System.out.println("item" + item);
                                courses.entrySet().forEach(entry -> {
                                    String key = entry.getKey();
                                    Course course = entry.getValue();
                                    if (key.equals(item) && !selectedCourses
                                        .containsKey(item)) {
                                        selectedCourses.put(key, course);
                                        int credits = Integer.valueOf(opMaara
                                            .getText()) + Integer.valueOf(
                                                course.getCredits());
                                        String textCredits = Integer.toString(
                                            credits);
                                        opMaara.setText(textCredits);
                                    }
                                });

                            }/*
                            if checkbox is ticked off, remove course from the map
                             */ else if (oldValue) {
                                int credits = 0;
                                if (selectedCourses.size() > 1) {
                                    for (Map.Entry<String, Course> entry
                                        : selectedCourses.entrySet()) {

                                        Course course = entry.getValue();

                                        if (selectedCourses.containsKey(item)) {
                                            selectedCourses.remove(item);
                                            if (Integer.valueOf(opMaara
                                                .getText()) >= Integer.valueOf(
                                                    course.getCredits())) {

                                                credits = Integer.valueOf(
                                                    opMaara.getText())
                                                    - Integer.valueOf(course
                                                        .getCredits());
                                            }

                                            String textCredits = Integer
                                                .toString(credits);
                                            opMaara.setText(textCredits);
                                        }
                                    }
                                } else if (selectedCourses.containsKey(item)) {
                                    selectedCourses.remove(item);
                                    opMaara.setText("0");
                                }
                            }
                        }
                    });
                    Label label = new Label(item);
                    label.prefHeightProperty().bind(checkBox.heightProperty());

                    cellBox.getChildren().addAll(checkBox, label);
                    setGraphic(cellBox);
                    setText(null);

                } else {
                    // If this is the root we just display the text.
                    setGraphic(null);
                    setText(item);
                }
            }
        }
    }
}
