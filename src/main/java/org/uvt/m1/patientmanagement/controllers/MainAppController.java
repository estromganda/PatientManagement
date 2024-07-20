package org.uvt.m1.patientmanagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainAppController {

    protected BorderPane mainView;
    @FXML
    private Label welcomeText;

    public BorderPane getMainView() {
        return mainView;
    }

    public void setMainView(BorderPane mainView) {
        this.mainView = mainView;
    }
    @FXML
    protected void setDoctorView(){

    }
    public void setPatientView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainAppController.class.getResource("patient-view.xml"));
        mainView.setCenter(fxmlLoader.load());
    }
}
