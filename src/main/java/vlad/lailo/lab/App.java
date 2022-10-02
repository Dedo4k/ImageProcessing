package vlad.lailo.lab;

import javafx.application.Application;
import javafx.stage.Stage;
import vlad.lailo.lab.windows.MainWindow;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainWindow mainWindow = new MainWindow(stage, "Image Processing");
        mainWindow.show();
    }

    public static void main(String[] args) {
        launch();
    }
}