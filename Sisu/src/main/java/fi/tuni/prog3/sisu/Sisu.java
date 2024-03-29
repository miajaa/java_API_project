package fi.tuni.prog3.sisu;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX Sisu
 */
public class Sisu extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 1400, 780);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Sisu.class.getResource(fxml
            + ".fxml"));
        return fxmlLoader.load();
    }
/**
 * Launches the application.
 * @param args command line arguments.
 */
    public static void main(String[] args) {
        launch();
    }

}
