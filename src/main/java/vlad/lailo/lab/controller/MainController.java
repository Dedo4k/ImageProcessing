package vlad.lailo.lab.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vlad.lailo.lab.utils.ImageUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainController {

    @FXML
    public ImageView imageView;

    @FXML
    public TextField p1i1;

    @FXML
    public TextField p2i1;

    @FXML
    public TextField p2i2;

    @FXML
    public CheckBox useCurrent;

    @FXML
    public TextField minAreaField;

    @FXML
    public TextField clustersField;

    @FXML
    public BarChart<String, Long> chart;

    @FXML
    public Pane pane;

    private Image originalImage;

    private Image currentImage;

    @FXML
    public void browseAction() throws MalformedURLException {
        File currentFile = getFileChooser("Open image",
                "E:/Vlad/7 sem/DIP/фигуры/easy",
                "Available extensions",
                "*.jpg", "*.png");
        if (currentFile == null) {
            return;
        }
        ((Stage) pane.getScene().getWindow()).setTitle(currentFile.getAbsolutePath());
        originalImage = new Image(currentFile.toURL().toString());
        currentImage = originalImage;
        loadImageAndHistogram(currentImage);
    }

    @FXML
    public void firstAction() {
        Image imageToProcess = originalImage;
        if (useCurrent.isSelected()) {
            imageToProcess = currentImage;
        }
        try {
            int threshold = Integer.parseInt(p1i1.getText());
            currentImage = ImageUtil.getPreparationA(imageToProcess, threshold);
        } catch (NumberFormatException ex) {
            currentImage = ImageUtil.getPreparationA(imageToProcess, 127);
        }
        loadImageAndHistogram(currentImage);
    }

    @FXML
    public void secondAction() {
        Image imageToProcess = originalImage;
        if (useCurrent.isSelected()) {
            imageToProcess = currentImage;
        }
        try {
            int thresholdLow = Integer.parseInt(p2i1.getText());
            int thresholdHigh = Integer.parseInt(p2i2.getText());
            currentImage = ImageUtil.getPreparationB(imageToProcess, thresholdLow, thresholdHigh);
        } catch (NumberFormatException ex) {
            currentImage = ImageUtil.getPreparationB(imageToProcess, 230, 255);
        }
        loadImageAndHistogram(currentImage);
    }

    @FXML
    public void thirdAction() {
        Image imageToProcess = originalImage;
        if (useCurrent.isSelected()) {
            imageToProcess = currentImage;
        }
        currentImage = ImageUtil.applySobelFilter(imageToProcess,
                new int[][]{{1, 0, -1},
                        {2, 0, -2},
                        {1, 0, -1}},
                new int[][]{{-1, -2, -1},
                        {0, 0, 0},
                        {1, 2, 1}});
        loadImageAndHistogram(currentImage);
    }

    @FXML
    public void clearAction() {
        currentImage = originalImage;
        loadImageAndHistogram(currentImage);
    }

    @FXML
    public void groupAction() {
        Image imageToProcess = originalImage;
        if (useCurrent.isSelected()) {
            imageToProcess = currentImage;
        }
        List<ImageUtil.Group> groups = null;
        int minArea;
        try {
            minArea = Integer.parseInt(minAreaField.getText());
        } catch (NumberFormatException ex) {
            minArea = 20;
        }
        groups = ImageUtil.getGroups(imageToProcess, minArea);
        int k;
        try {
            k = Integer.parseInt(clustersField.getText());
        } catch (NumberFormatException ex) {
            k = 1;
        }
        Map<Integer, Set<ImageUtil.Group>> grouped = ImageUtil.kAverage(groups, k);
        ImageUtil.highlightGroups(imageToProcess, grouped);
    }

    @FXML
    public void grayAction() {
        Image imageToProcess = originalImage;
        if (useCurrent.isSelected()) {
            imageToProcess = currentImage;
        }
        currentImage = ImageUtil.toGrayScale(imageToProcess);
        loadImageAndHistogram(currentImage);
    }

    private void loadImageAndHistogram(Image image) {
        if (image == null) {
            return;
        }
        imageView.setImage(image);
        chart.getData().clear();
        chart.getData().add(new XYChart.Series<>(FXCollections.observableArrayList(ImageUtil.getHistogramData(image))));
    }

    private File getFileChooser(String title, String srcDir, String description, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(srcDir));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(description, extensions));
        return fileChooser.showOpenDialog(new Stage());
    }
}
