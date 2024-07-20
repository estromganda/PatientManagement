package org.uvt.m1.patientmanagement.views;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.ListActionView;

public class MainView extends BorderPane {
    protected PatientView patientView;
    protected DoctorView doctorView;
    protected AdmissionView admissionView;
    protected ConsultationView consultationView;

    Stage primaryStage;
    Menu patient;
    public MainView(){
        patientView = new PatientView();
        doctorView = new DoctorView();
        admissionView = new AdmissionView();
        consultationView = new ConsultationView();
        createMenu();
        setCenter(consultationView);
        createSidebar();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    void createMenu(){
        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");
        MenuItem menuItemClose = new MenuItem("Close");
        file.getItems().add(menuItemClose);

        Menu doctor = new Menu("Doctor");
        MenuItem allDoctor = new MenuItem("All doctors");
        MenuItem addDoctor = new MenuItem("Add new");
        MenuItem refreshDoctor = new MenuItem("Refresh");
        doctor.getItems().addAll(allDoctor, addDoctor, refreshDoctor);

        patient = new Menu("Patient");
        MenuItem allPatient = new MenuItem("All patients");
        MenuItem addPatient = new MenuItem("Add new");
        MenuItem refreshPatient = new MenuItem("Refresh");
        patient.getItems().addAll(allPatient, addPatient, refreshPatient);

        Menu admission = new Menu("Admission");
        admission.getItems().add(new MenuItem("All admissions"));
        admission.getItems().add(new MenuItem("Add new"));
        admission.getItems().add(new MenuItem("Refresh"));

        admission.getItems().get(0).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != admissionView){
                    setCenter(admissionView);
                }
            }
        });
        admission.getItems().get(1).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != admissionView){
                    setCenter(admissionView);
                }
                admissionView.clearInput();
            }
        });
        admission.getItems().get(2).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != admissionView){
                    setCenter(admissionView);
                }
                admissionView.refresh();
            }
        });

        Menu consultation = new Menu("Consultation");
        consultation.getItems().add(new MenuItem("All Consultation"));
        consultation.getItems().add(new MenuItem("Add new"));
        consultation.getItems().add(new MenuItem("Refresh"));
        consultation.getItems().get(0).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != consultationView){
                    setCenter(consultationView);
                }
            }
        });
        consultation.getItems().get(1).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != consultationView){
                    setCenter(consultationView);
                }
                consultationView.clearInput();
            }
        });
        consultation.getItems().get(2).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != consultationView){
                    setCenter(consultationView);
                }
                consultationView.refresh();
            }
        });

        menuBar.getMenus().addAll(file, doctor, patient, admission, consultation);
        this.setTop(menuBar);

        menuItemClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                primaryStage.close();
            }
        });

        allPatient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != patientView){
                    setCenter(patientView);
                }
            }
        });
        refreshPatient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                patientView.refresh();
            }
        });
        refreshDoctor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doctorView.refresh();
            }
        });
        allDoctor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != doctorView){
                    setCenter(doctorView);
                }
            }
        });
        addDoctor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != doctorView){
                    setCenter(doctorView);
                }
                doctorView.clearInput();
            }
        });
        addPatient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(getCenter() != patientView){
                    setCenter(patientView);
                }
                patientView.clearInput();
            }
        });
    }

    private void createSidebar(){
        ListActionView<String> listActionView = new ListActionView<>();
        listActionView.getItems().add("Patient");
        listActionView.getItems().add("Doctor");
        listActionView.getItems().add("Consultation");

        listActionView.setScaleZ(2);
        listActionView.setStyle("-fx-font-size: 16px;");

        listActionView.getItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> change) {
                System.out.println(change);
            }
        });
        setLeft(listActionView);
    }

    public static TextField textField(double minWidth, String text, String placeholder){
        TextField textField = new TextField(text);
        textField.setMinWidth(minWidth);
        textField.setPadding(new Insets(5, 10, 10, 5));
        textField.setPromptText(placeholder);
        return textField;
    }

    public static <T> ComboBox comboBox(double minWidth, String text, String placeholder){
        ComboBox comboBox = new ComboBox<T>();
        comboBox.setMinWidth(minWidth);
        comboBox.setPadding(new Insets(5, 10, 10, 5));
        comboBox.setPromptText(placeholder);
        return comboBox;
    }
}
