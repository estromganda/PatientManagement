package org.uvt.m1.patientmanagement.views;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.uvt.m1.patientmanagement.models.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class AdmissionView extends SplitPane {

    ComboBox<Patient> patientComboBox;
    ComboBox<Doctor> doctorComboBox;
    ComboBox<Room> roomComboBox;
    DatePicker arrivalDate;
    DatePicker exitDate;
    TextArea report;
    protected Label labelAddNew;

    private Button btnAdd;
    private Button btnDelete;
    Admission currentSelected = null;

    TextField searchField;
    Node editVew;

    private TableView<Admission> tableView;
    public AdmissionView(){
        patientComboBox = MainView.comboBox(600, "", "Patient");
        doctorComboBox = MainView.comboBox(600, "", "Docteur");
        roomComboBox = MainView.comboBox(600, "", "Salle");
        arrivalDate = new DatePicker(); arrivalDate.setMinWidth(600);
        exitDate = new DatePicker(); exitDate.setMinWidth(600);
        exitDate.setPadding(new Insets(5, 10, 10, 5));
        arrivalDate.setPadding(new Insets(5, 10, 10, 5));
        report = new TextArea();
        report.setWrapText(true);

        createLeftSide();
        createEditView();
        createRightSide();
        createContextMenu();
    }

    private void createContextMenu(){
        this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent contextMenuEvent) {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem refreshItem = new MenuItem("Actualiser");
                MenuItem addNew = new MenuItem("Ajouter un nouveau");
                MenuItem reset = new MenuItem("Reset");
                contextMenu.getItems().addAll(refreshItem, addNew, reset);
                contextMenu.show(getScene().getWindow());

                refreshItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        refresh();
                    }
                });
                addNew.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        clearInput();
                    }
                });
                reset.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        clearInput();
                    }
                });
            }
        });
    }

    protected void createLeftSide(){
        Label label = new Label("Admission");
        label.setFont(new Font("", 18));
        searchField = new TextField();
        searchField.setPadding(new Insets(7, 7, 7, 7));
        searchField.setMinWidth(350);
        Button btnSearch = new Button("Search");
        HBox searchContainer = new HBox();
        searchContainer.getChildren().addAll(searchField, btnSearch);
        searchContainer.setAlignment(Pos.CENTER);

        tableView = new TableView<>();
        TableColumn<Admission, String> idColumn = new TableColumn<>("Id");
        TableColumn<Admission, String> patientColumn = new TableColumn<>("Patient");
        TableColumn<Admission, String> doctorColumn = new TableColumn<>("Docteur");
        TableColumn<Admission, String> arrivalDateColumn = new TableColumn<>("Date d'arrivée");
        TableColumn<Admission, String> exitDateColumn = new TableColumn<>("Date de sortie");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientFullName"));
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorFullName"));
        arrivalDateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        exitDateColumn.setCellValueFactory(new PropertyValueFactory<>("exitDate"));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(patientColumn);
        tableView.getColumns().add(doctorColumn);
        tableView.getColumns().add(arrivalDateColumn);
        tableView.getColumns().add(exitDateColumn);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, searchContainer, tableView);

        vBox.setAlignment(Pos.TOP_CENTER);
        tableView.setPrefHeight(900);
        this.getItems().add(vBox);
        setMinWidth(500);

        tableView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Admission>() {
            @Override
            public void onChanged(Change<? extends Admission> change) {
                if(change.getList().isEmpty()){
                    return;
                }
                Admission admission = change.getList().get(0);

                setCurrentSelected(admission);
                labelAddNew.setText("Information de l' admission");

                editVew.setVisible(true);
                btnAdd.setText("Modifier");
                btnDelete.setVisible(true);
            }
        });

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                search();
            }
        });
    }
    protected void createEditView(){
        labelAddNew = new Label("Add new admission");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setMinWidth(600);
        gridPane.setAlignment(Pos.CENTER);
        int rowIndex = 0;
        gridPane.add(new Label("Patient"), 0, rowIndex);
        gridPane.add(patientComboBox, 0, ++rowIndex);
        gridPane.add(new Label("Docteur"), 0, ++rowIndex);
        gridPane.add(doctorComboBox, 0, ++rowIndex);
        gridPane.add(new Label("Salle"), 0, ++rowIndex);
        gridPane.add(roomComboBox, 0, ++rowIndex);
        gridPane.add(new Label("Date d'arrivée"), 0, ++rowIndex);
        gridPane.add(arrivalDate, 0, ++rowIndex);
        gridPane.add(new Label("Date de sortie"), 0, ++rowIndex);
        gridPane.add(exitDate, 0, ++rowIndex);
        gridPane.add(new Label("Rapport"), 0, ++rowIndex);
        gridPane.add(report, 0, ++rowIndex);

        btnAdd = new Button("Ajouter");
        Button btnClear = new Button("Reset");
        btnDelete = new Button("Supprimer");
        btnDelete.setVisible(false);
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(btnAdd, btnClear, btnDelete);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        gridPane.add(buttonBox, 0, ++rowIndex);


        labelAddNew.setFont(new Font("", 18));
        VBox vBox = new VBox();
        vBox.getChildren().addAll(labelAddNew, gridPane);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);

        editVew = vBox;

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(editVew);
        getItems().add(scrollPane);

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                saveCurrent();
            }
        });
        btnClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clearInput();
            }
        });
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    currentSelected.delete();
                    ObservableList<Admission> admissions = tableView.getItems();
                    for (Admission admission: admissions){
                        if (currentSelected.getId().equals(admission.getId())){
                            boolean result = tableView.getItems().remove(admission);
                            System.out.println("On remove item: " + currentSelected.getId());
                            clearInput();
                            tableView.refresh();
                            break;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    protected void createRightSide(){

    }

    public void refresh(){
        try {
            Model.initConnection();
            ArrayList<Model> lsModel =  Model.all("Admission", Admission.columns, "");
            if (!tableView.getItems().isEmpty()){
                tableView.getItems().clear();
            }
            for (Model model: lsModel){
                Admission admission = new Admission();
                admission.fill(model.getProperties());
                tableView.getItems().add(admission);
            }
            Patient.loadPatients();
            if(!patientComboBox.getItems().isEmpty()){
                patientComboBox.getItems().clear();
            }
            patientComboBox.getItems().addAll(Patient.getPatients());

            Doctor.loadDoctors();
            if(!doctorComboBox.getItems().isEmpty()){
                doctorComboBox.getItems().clear();
            }
            doctorComboBox.getItems().addAll(Doctor.getDoctors());

            Room.loadRooms();
            if(!roomComboBox.getItems().isEmpty()){
                roomComboBox.getItems().clear();
            }
            roomComboBox.getItems().addAll(Room.getRooms());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void search(){
        try {
            Model.initConnection();
            ArrayList<Model> lsModel =  Model.search("Admission", searchField.getText(), Admission.columns);
            if (!tableView.getItems().isEmpty()){
                tableView.getItems().clear();
            }
            for (Model model: lsModel){
                Admission admission = new Admission();
                admission.fill(model.getProperties());
                tableView.getItems().add(admission);
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Node getEditVew() {
        return editVew;
    }

    public Admission getCurrentSelected() {
        return currentSelected;
    }

    public void setCurrentSelected(Admission admission) {
        this.currentSelected = admission;
        patientComboBox.setValue(admission.getPatient());
        doctorComboBox.setValue(admission.getDoctor());
        roomComboBox.setValue(admission.getRoom());
        arrivalDate.setValue(LocalDate.parse(admission.getArrivalDate().toString()));
        Object exitDateValue = admission.getExitDate();
        if(exitDateValue != null){
            exitDate.setValue(LocalDate.parse(exitDateValue.toString()));
        }
        report.setText(admission.getReport());
    }

    public void saveCurrent(){
        boolean isUpdate = currentSelected != null;
        if(currentSelected == null) {
            currentSelected = new Admission();
        }

        Patient patient = patientComboBox.getValue();
        if(patient != null){
            currentSelected.setPatient(patient);
            currentSelected.setPatientId(patient.getId());
        }
        Doctor doctor = doctorComboBox.getValue();
        if(doctor != null){
            currentSelected.setDoctorId(doctor.getId());
        }
        Room room = roomComboBox.getValue();
        if(room != null){
            currentSelected.setRoomId(room.getId());
        }
        currentSelected.setArrivalDate(arrivalDate.getValue());
        currentSelected.setExitDate(exitDate.getValue());
        currentSelected.setReport(report.getText());
        currentSelected.save();

        if(isUpdate){
            ObservableList<Admission> items = tableView.getItems();
            for (Admission item : items) {
                if (item.getId() == currentSelected.getId()) {
                    item.fill(currentSelected.getProperties());
                    break;
                }
            }
        }
        else{
            tableView.getItems().add(currentSelected);
        }
        tableView.refresh();
    }

    void clearInput(){
        currentSelected = null;
        labelAddNew.setText("Add new");
        patientComboBox.setValue(null);
        doctorComboBox.setValue(null);
        roomComboBox.setValue(null);
        arrivalDate.setValue(LocalDate.now());
        exitDate.setValue(null);
        report.setText("");

        btnDelete.setVisible(false);
        btnAdd.setText("Ajouter");
    }
}
