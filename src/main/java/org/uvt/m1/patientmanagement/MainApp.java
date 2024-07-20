package org.uvt.m1.patientmanagement;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.uvt.m1.patientmanagement.views.MainView;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        mainView.setPrimaryStage(primaryStage);
        Scene scene = new Scene(mainView, 1380, 700);
        primaryStage.setTitle("Patient management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
