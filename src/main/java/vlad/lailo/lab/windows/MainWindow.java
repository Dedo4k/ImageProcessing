package vlad.lailo.lab.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vlad.lailo.lab.App;

import java.io.IOException;

public class MainWindow {

    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;

    public MainWindow(Stage stage, String title) throws IOException {
        this.stage = stage;
        loader = new FXMLLoader(App.class.getResource("main-window.fxml"));
        scene = new Scene(loader.load(), 1280, 720);
        stage.setTitle(title);
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }
}
