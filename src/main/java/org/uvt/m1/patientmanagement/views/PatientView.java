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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class PatientView extends SplitPane {

    protected TextField firstName;
    protected TextField lastName;
    protected TextField address;
    protected DatePicker birthday;
    protected TextField contact;
    protected Label labelAddNew;

    private Button btnAdd;
    private Button btnClear;
    private Button btnDelete;
    Patient currentSelected = null;

    TextField searchField;
    Node editVew;
    VBox consultationView;
    Node admissionView;
    TableView<Admission> admissionTableView;
    private TableView<Patient> tableView;

    public PatientView(){

        firstName = MainView.textField(300, "", "Nom");
        lastName = MainView.textField(300, "", "Prénom");
        address = MainView.textField(300, "", "Adresse");
        birthday = new DatePicker(); //MainView.textField(600, "");
        birthday.setValue(LocalDate.now());
        birthday.setMinWidth(600);
        birthday.setPadding(new Insets(5, 10, 10, 5));
        contact = MainView.textField(300, "", "Contact");

        createLeftSide();
        createEditView();
        createRightSide();
        createAdmissionView();
        createConsultationView();
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
        Label label = new Label("Patient");
        label.setFont(new Font("", 18));
        searchField = new TextField();
        searchField.setPadding(new Insets(7, 7, 7, 7));
        searchField.setMinWidth(350);
        Button btnSearch = new Button("Search");
        HBox searchContainer = new HBox();
        searchContainer.getChildren().addAll(searchField, btnSearch);
        searchContainer.setAlignment(Pos.CENTER);

        tableView = new TableView<>();
        TableColumn<Patient, String> idColumn = new TableColumn<>("Id");
        TableColumn<Patient, String> firstNameColumn = new TableColumn<>("First name");
        TableColumn<Patient, String> lastNameColumn = new TableColumn<>("Last name");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(firstNameColumn);
        tableView.getColumns().add(lastNameColumn);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, searchContainer, tableView);

        vBox.setAlignment(Pos.TOP_CENTER);
        tableView.setPrefHeight(900);
        this.getItems().add(vBox);
        setMinWidth(500);

        tableView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Patient>() {
            @Override
            public void onChanged(Change<? extends Patient> change) {
                if(change.getList().isEmpty()){
                    return;
                }
                firstName.setText(change.getList().get(0).getFirstName());
                lastName.setText(change.getList().get(0).getLastName());
                address.setText(change.getList().get(0).getAddress());
                contact.setText(change.getList().get(0).getContact());
                setCurrentSelected(change.getList().get(0));
                labelAddNew.setText("Information du patient");

                editVew.setVisible(true);
                btnAdd.setText("Modifier");
                btnDelete.setVisible(true);
                try {
                    birthday.setValue(LocalDate.parse(change.getList().get(0).getBirthday()));
                }catch (DateTimeParseException e){
                    e.printStackTrace();
                    birthday.setAccessibleText(change.getList().get(0).getBirthday());
                }
                showCurrentAdmission();
                showCurrentConsultation();
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
        labelAddNew = new Label("Add new patient");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setMinWidth(600);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(new Label("First name"), 0, 0);
        gridPane.add(firstName, 0, 1);
        gridPane.add(new Label("Last name"), 0, 2);
        gridPane.add(lastName, 0, 3);
        gridPane.add(new Label("Birthday"), 0, 4);
        gridPane.add(birthday, 0, 5);
        gridPane.add(new Label("Address"), 0, 6);
        gridPane.add(address, 0, 7);
        gridPane.add(new Label("Contact"), 0, 8);
        gridPane.add(contact, 0, 9);

        btnAdd = new Button("Ajouter");
        btnClear = new Button("Reset");
        btnDelete = new Button("Supprimer");
        btnDelete.setVisible(false);
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(btnAdd, btnClear, btnDelete);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        gridPane.add(buttonBox, 0, 10);


        labelAddNew.setFont(new Font("", 18));
        VBox vBox = new VBox();
        vBox.getChildren().addAll(labelAddNew, gridPane);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(10);

        createAdmissionView();
        createConsultationView();
        vBox.getChildren().add(admissionView);
        vBox.getChildren().add(consultationView);
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
                    ObservableList<Patient> patients = tableView.getItems();
                    for (Patient patient: patients){
                        if (currentSelected.getId().equals(patient.getId())){
                            boolean result = tableView.getItems().remove(patient);
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
            ArrayList<Model> lsModel =  Model.all("Patient", Patient.columns, "");
            if (!tableView.getItems().isEmpty()){
                tableView.getItems().clear();
            }
            for (Model model: lsModel){
                Patient patient = new Patient();
                patient.fill(model.getProperties());
                tableView.getItems().add(patient);
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void search(){
        try {
            Model.initConnection();
            ArrayList<Model> lsModel =  Model.search("Patient", searchField.getText(), Patient.columns);
            if (!tableView.getItems().isEmpty()){
                tableView.getItems().clear();
            }
            for (Model model: lsModel){
                Patient patient = new Patient();
                patient.fill(model.getProperties());
                tableView.getItems().add(patient);
            }
        }
        catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    public Node getEditVew() {
        return editVew;
    }

    public Patient getCurrentSelected() {
        return currentSelected;
    }

    public void setCurrentSelected(Patient currentSelected) {
        this.currentSelected = currentSelected;
    }

    public void saveCurrent(){
        boolean isUpdate = currentSelected != null;
        if(currentSelected == null) {
            currentSelected = new Patient();
        }
        currentSelected.setFirstName(firstName.getText());
        currentSelected.setLastName(lastName.getText());
        currentSelected.setAddress(address.getText());
        currentSelected.setContact(contact.getText());
        currentSelected.setBirthday(birthday.getValue().format(DateTimeFormatter.ofPattern("y-M-d")));
        currentSelected.save();

        if(isUpdate){
            ObservableList<Patient> items = tableView.getItems();
            for (Patient item : items) {
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
        firstName.setText("");
        lastName.setText("");
        birthday.setValue(LocalDate.now());
        address.setText("");
        contact.setText("");
        btnDelete.setVisible(false);
        btnAdd.setText("Ajouter");

        if(admissionTableView.getItems().size() >0){
            admissionTableView.getItems().clear();
        }
        ObservableList<Node> observableList = consultationView.getChildren();
        ArrayList<Node> removableItems = new ArrayList<>();
        for (Node node: observableList){
            if(node instanceof VBox){
                removableItems.add(node);
            }
        }
        if(!removableItems.isEmpty()){
            observableList.removeAll(removableItems);
        }
    }

    private void createConsultationView() {
        if (consultationView != null){
            return;
        }
        Label label = new Label("Consultations");
        label.setFont(new Font("", 16));
        label.setPadding(new Insets(0, 20, 20, 0));
        consultationView = new VBox();
        consultationView.getChildren().add(label);
    }

    private void showCurrentConsultation(){
        ObservableList<Node> observableList = consultationView.getChildren();
        ArrayList<Node> removableItems = new ArrayList<>();
        for (Node node: observableList){
            if(node instanceof VBox){
                removableItems.add(node);
            }
        }
        if(!removableItems.isEmpty()){
            observableList.removeAll(removableItems);
        }
        if(currentSelected == null){
            return;
        }
        ArrayList<Consultation> consultations = currentSelected.getConsultations();
        for(Consultation consultation: consultations){
            Doctor doctor = consultation.getDoctor();
            VBox gridPane = new VBox();
            Label label = new Label("Consultation du " + consultation.getDate());
            gridPane.getChildren().add(label);
            gridPane.getChildren().add(new Label("Objet: " + consultation.getObjet()));
            gridPane.getChildren().add(new Label("Docteur:" + doctor.getFullName()));
            gridPane.getChildren().add(new Label("Note"));

            TextField notText = new TextField(consultation.getNote());
            notText.setMinHeight(50);
            notText.setEditable(false);
            gridPane.getChildren().add(notText);
            gridPane.getChildren().add(new Label("Traitements"));

            ArrayList<Treatment> treatments = consultation.getTreatments();
            TableView <Treatment> treatmentTableView = new TableView<>();
            TableColumn<Treatment, String> drugColumn = new TableColumn<>("Medicament");
            TableColumn<Treatment, String> doseColumn = new TableColumn<>("Dose");
            TableColumn<Treatment, String> durationColumn = new TableColumn<>("Durée");

            drugColumn.setCellValueFactory(new PropertyValueFactory<>("drug"));
            doseColumn.setCellValueFactory(new PropertyValueFactory<>("dose"));
            durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

            treatmentTableView.getColumns().add(drugColumn);
            treatmentTableView.getColumns().add(doseColumn);
            treatmentTableView.getColumns().add(durationColumn);
            treatmentTableView.getItems().addAll(treatments);

            gridPane.getChildren().add(treatmentTableView);
            //treatmentTableView.setMinWidth(500);
            consultationView.getChildren().add(gridPane);
        }
    }

    private void showCurrentAdmission(){
        ArrayList<Admission> admissions = currentSelected.getAdmissions();
        if(!admissionTableView.getItems().isEmpty()){
            admissionTableView.getItems().clear();
        }
        admissionTableView.getItems().addAll(admissions);
    }

    private void createAdmissionView() {
        if(admissionTableView != null){
            return;
        }
        admissionTableView = new TableView<>();
        admissionTableView.getColumns().add(new TableColumn<>("Id"));
        admissionTableView.getColumns().add(new TableColumn<>("Salle"));
        admissionTableView.getColumns().add(new TableColumn<>("Date d'admission"));
        admissionTableView.getColumns().add(new TableColumn<>("Date de sortie"));

        admissionTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        admissionTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("salle"));
        admissionTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        admissionTableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("exitDate"));
        VBox vBox = new VBox();
        Label label = new Label("Admissions");
        label.setFont(new Font("", 16));

        vBox.getChildren().add(label);
        vBox.getChildren().add(admissionTableView);
        admissionView = vBox;
    }
}
