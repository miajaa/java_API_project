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
 * Class for controlling the main page.
 * HandleMouseClicked(MouseEvent event) is an additional part of the project.
 * It is supposed to show extra information of the chosen courses on the right.
 * @author eevih and Johanna
 */
public class MainPageController implements Initializable {

    private PrimaryController info;
    private String selectedDegree;
    private JsonData data;
    private HashMap<String, Course> courses;
    private final HashMap<String, Course> selectedCourses = new HashMap<>();
    private LinkedHashMap<String, String> map;
    private String degreeCredits;

    @FXML
    private Label helloText;
    @FXML
    private Label opMaara;
    @FXML
    private Label opTotal;
    @FXML
    private Label courseName;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private TextArea courseTextArea;
    @FXML
    private ComboBox<String> chooseDegreeBox;

    @FXML
    private void switchToPrimary() throws IOException {

        Sisu.setRoot("primary");
    }

    @FXML
    private void exitProgram() throws IOException {

        addCoursesToJson();
        Platform.exit();
    }

    @FXML
    private void switchToStudent() throws IOException {
        Sisu.setRoot("Student");
    }

    /*Adds student data into a json file for safekeeping
     */
    private void addCoursesToJson() throws IOException {
        try {

            ArrayList<String> info1;
            info1 = getInfo();
            map = new LinkedHashMap<>();
            map.put("name", info1.get(0));
            map.put("number", info1.get(1));
            map.put("start", info1.get(2));
            map.put("finish", info1.get(3));

            StudentController student = new StudentController(info1.get(0),
                info1.get(1), info1.get(2), info1.get(3));

            selectedCourses.entrySet().forEach(entry -> {
                map.put(entry.getValue().getNameFI(), entry.getValue()
                    .getCredits());
            });
            System.out.println("map: " + map);
            try (Writer writer = new FileWriter("student.json")) {
                new Gson().toJson(map, writer);
            }
        } catch (JsonIOException | IOException e) {
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

        TreeItem<String> root = new TreeItem<>(degree.getNameFI());
        degreeCredits = degree.getCreditUnitsFI();
        opTotal.setText(degreeCredits);
        data.getDegreeProgrammesByName().get(selectedDegree)
            .getDegree().getStudyModules().stream().map(subject -> {
                TreeItem<String> child = new TreeItem<>(subject.getNameFI()
                    + subject.getCreditUnitsFI());
                subject.getCourses().forEach(course -> {
                    TreeItem<String> child1 = new TreeItem<>(course.getNameFI()
                        + course.getCreditUnitsFI());
                    child.getChildren().add(child1);

                    courses.put(course.getNameFI()
                        + course.getCreditUnitsFI(), course);
                });
                subject.getGroupingModules().forEach(group -> {
                    TreeItem<String> child2 = new TreeItem<>(group.getNameFI()
                        + group.getCreditUnitsFI());
                    child.getChildren().add(child2);

                    createItems(group, child2);
                });
                return child;
            }).forEachOrdered(child -> {
            root.getChildren().add(child);
        });

        treeView.setRoot(root);
        treeView.setShowRoot(false);

        EventHandler<MouseEvent> mouseEventHandle;
        mouseEventHandle = (MouseEvent event) -> {
            try {
                handleMouseClicked(event);
            } catch (IOException ex) {
                Logger.getLogger(MainPageController.class
                    .getName()).log(Level.SEVERE, null, ex);
            }
        };
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
        treeView.setCellFactory(e -> new CustomCell());
    }

    private void createItems(GroupingModule group, TreeItem<String> parent) {
        group.getCourses().forEach(course -> {
            TreeItem<String> child3 = new TreeItem<>(course
                .getNameFI()
                + course.getCreditUnitsFI());
            courses.put(course.getNameFI()
                + course.getCreditUnitsFI(), course);
            parent.getChildren().add(child3);
        });

        group.getSubModules().forEach(group1 -> {
            TreeItem<String> child = new TreeItem<>(group1
                .getNameFI() + group1.getCreditUnitsFI());
            parent.getChildren().add(child);
            if (!(group1.getSubModules().isEmpty() && group1.getCourses()
                .isEmpty())) {
                createItems(group1, child);
            }
        });
    }

    /**
     * Initializer function for setting up the FXML
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ArrayList<String> list = new ArrayList<>();

            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("student.json"));
            LinkedHashMap<String, String> linkedMap;
            linkedMap = gson.fromJson(reader,
                LinkedHashMap.class);

            linkedMap.entrySet().forEach(entry -> {
                list.add(entry.getValue());
            });

            PrimaryController primaryControllerInfo;
            primaryControllerInfo = new PrimaryController();
            primaryControllerInfo.setName(list.get(0));
            String capName = primaryControllerInfo.getName().toLowerCase()
                .substring(0, 1)
                .toUpperCase()
                + primaryControllerInfo.getName().substring(1).toLowerCase();
            helloText.setText("Hei, " + capName + "!");
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
     * @return ArrayList of Student info written in PrimariController.java.
     * @throws FileNotFoundException if file is not found.
     * @throws IOException if data cannot be read.
     */
    private ArrayList<String> getInfo()
        throws FileNotFoundException, IOException {

        ArrayList<String> lista = new ArrayList<>();

        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("student.json"));
            LinkedHashMap<String, String> getInfoMap = gson
                .fromJson(reader, LinkedHashMap.class);

            getInfoMap.entrySet().forEach(entry -> {
                lista.add(entry.getValue());
            });
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
        return lista;
    }

    /*
    handle mouse click in treeview nodes to show courses
    * calls
    * {@link #setCourseDescription(Course course,int currMin, int currMax, 
                                        int studentMin,int studentMax
                                        , String name, String courseText)}
    *@param MouseEvent event
    * @throws IOException - if there is some kind of an IO error 
     */
    
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

                /*display course details in textarea next to the treeview
                * if the course is found from the map
                 */
                if (key.equals(name)) {
                    courseText = (new StringBuilder())
                        .append("\n\n Ajankohta: ").toString();

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
                            getLearningMaterialFI()
                            .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "\n");
                        String strippedContent = course
                            .getContentFI()
                            .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "\n");
                        String strippedOutcomes = course.getOutcomesFI()
                            .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "\n");
                        String strippedPrereqs = course.getPrerequisitesFI()
                            .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "\n");
                        courseName.setText(name);
                        courseTextArea.setText(courseText
                            + "\n Sisältö: \n " + strippedContent
                            + "\n\n Tavoitteet: \n " + strippedOutcomes
                            + "\n\n Opintomateriaalit: \n"
                            + strippedLearningMaterial
                            + "\n\n Esitietovaatimukset: \n"
                            + strippedPrereqs
                        );
                    } else {
                        courseTextArea.setText("KURSSIN AIKATAULUA"
                            + "\n EI OLE SAATAVILLA TEIDÄN ALOITUSVUOTENNE"
                            + "\n JA VALMISTUMISVUOTENNE VÄLILLÄ");
                    }
                }
            }
        }
    }

    /**
     * A custom cell for the CheckBoxes in TreeView.
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
                        (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
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
