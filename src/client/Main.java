package client;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    private File file;
    private boolean isSelected = false;
    private Parent root;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML
    void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(primaryStage);
        if (file != null)
            isSelected = true;
}

    @FXML
    void sendFile() {
        Task<Void> sendFileTask;
        if (isSelected) {
            sendFileTask = new SendFileTask(file);
            statusLabel.textProperty().bind(sendFileTask.messageProperty());
            progressBar.progressProperty().bind(sendFileTask.progressProperty());
            executor.submit(sendFileTask);
        }
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
