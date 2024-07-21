package org.uvt.m1.patientmanagement.views;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.uvt.m1.patientmanagement.models.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class ConsultationView extends SplitPane {

    private final DatePicker date;
    private final TextField objet;
    private final ComboBox<Patient> patientComboBox;
    private final ComboBox<Doctor> doctorComboBox;
    private final TextArea note;
    private Label labelAddNew;
    private TreatmentView treatmentView;


    private Button btnAdd;
    private Button btnDelete;
    Consultation currentSelected = null;

    TextField searchField;
    VBox editVew;

    private TableView<Consultation> tableView;

    public ConsultationView(){
        date = new DatePicker(); date.setMinWidth(600);
        date.setPadding(new Insets(5, 10, 10, 5));
        objet = MainView.textField(600, "", "Objet de consultation");
        patientComboBox = MainView.comboBox(600, "Patient");
        doctorComboBox = MainView.comboBox(600, "Docteur");
        patientComboBox.setConverter(Patient.stringConverter);
        doctorComboBox.setConverter(Doctor.stringConverter);
        note = new TextArea();
        note.setWrapText(true);

        createLeftSide();
        createEditView();
        createContextMenu();
    }

    private void createContextMenu(){
        this.setOnContextMenuRequested(contextMenuEvent -> {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem refreshItem = new MenuItem("Actualiser");
            MenuItem addNew = new MenuItem("Ajouter un nouveau");
            MenuItem reset = new MenuItem("Reset");
            contextMenu.getItems().addAll(refreshItem, addNew, reset);
            contextMenu.show(getScene().getWindow());

            refreshItem.setOnAction(actionEvent -> refresh());
            addNew.setOnAction(actionEvent -> clearInput());
            reset.setOnAction(actionEvent -> clearInput());
        });
    }

    protected void createLeftSide(){
        Label label = new Label("Consultation");
        label.setFont(new Font("", 18));
        searchField = new TextField();
        searchField.setPadding(new Insets(7, 7, 7, 7));
        searchField.setMinWidth(350);
        Button btnSearch = new Button("Search");
        HBox searchContainer = new HBox();
        searchContainer.getChildren().addAll(searchField, btnSearch);
        searchContainer.setAlignment(Pos.CENTER);

        tableView = new TableView<>();
        TableColumn<Consultation, String> idColumn = new TableColumn<>("Id");
        TableColumn<Consultation, String> patientColumn = new TableColumn<>("Patient");
        TableColumn<Consultation, String> doctorColumn = new TableColumn<>("Docteur");
        TableColumn<Consultation, String> dateColumn = new TableColumn<>("Date");
        TableColumn<Consultation, String> objetColumn = new TableColumn<>("Objet");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientFullName"));
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorFullName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        objetColumn.setCellValueFactory(new PropertyValueFactory<>("objet"));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(patientColumn);
        tableView.getColumns().add(doctorColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(objetColumn);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, searchContainer, tableView);

        vBox.setAlignment(Pos.TOP_CENTER);
        tableView.setPrefHeight(900);
        this.getItems().add(vBox);
        setMinWidth(500);

        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Consultation>) change -> {
            if(change.getList().isEmpty()){
                return;
            }
            Consultation consultation = change.getList().get(0);

            setCurrentSelected(consultation);
            labelAddNew.setText("Information de la consultation");

            editVew.setVisible(true);
            btnAdd.setText("Modifier");
            btnDelete.setVisible(true);
        });

        btnSearch.setOnAction(actionEvent -> search());
    }
    protected void createEditView(){
        labelAddNew = new Label("Add new consultation");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setMinWidth(600);
        gridPane.setAlignment(Pos.CENTER);
        int rowIndex = 0;
        gridPane.add(new Label("Objet de consultation"), 0, rowIndex);
        gridPane.add(objet, 0, ++rowIndex);
        gridPane.add(new Label("Date"), 0, ++rowIndex);
        gridPane.add(date, 0, ++rowIndex);
        gridPane.add(new Label("Patient"), 0, ++rowIndex);
        gridPane.add(patientComboBox, 0, ++rowIndex);
        gridPane.add(new Label("Docteur"), 0, ++rowIndex);
        gridPane.add(doctorComboBox, 0, ++rowIndex);

        gridPane.add(new Label("Note"), 0, ++rowIndex);
        gridPane.add(note, 0, ++rowIndex);

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
        scrollPane.setMaxWidth(650);
        scrollPane.setContent(editVew);
        getItems().add(scrollPane);

        btnAdd.setOnAction(actionEvent -> saveCurrent());
        btnClear.setOnAction(actionEvent -> clearInput());
        btnDelete.setOnAction(actionEvent -> {
            try {
                currentSelected.delete();
                ObservableList<Consultation> consultations = tableView.getItems();
                for (Consultation consultation: consultations){
                    if (currentSelected.getId().equals(consultation.getId())){
                        tableView.getItems().remove(consultation);
                        System.out.println("On remove item: " + currentSelected.getId());
                        clearInput();
                        tableView.refresh();
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void refresh(){
        try {
            Model.initConnection();
            ArrayList<Model> lsModel =  Model.all("Consultation", DatabaseInfo.Consultation.columns, "");
            if (!tableView.getItems().isEmpty()){
                tableView.getItems().clear();
            }
            for (Model model: lsModel){
                Consultation consultation = new Consultation();
                consultation.fill(model.getProperties());
                tableView.getItems().add(consultation);
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
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void search(){
        try {
            Model.initConnection();
            ArrayList<Model> lsModel =  Model.search("Consultation", searchField.getText(), DatabaseInfo.Consultation.columns);
            if (!tableView.getItems().isEmpty()){
                tableView.getItems().clear();
            }
            for (Model model: lsModel){
                Consultation consultation = new Consultation();
                consultation.fill(model.getProperties());
                tableView.getItems().add(consultation);
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCurrentSelected(Consultation consultation) {
        this.currentSelected = consultation;
        objet.setText(consultation.getObjet());
        note.setText(consultation.getNote());
        patientComboBox.setValue(consultation.getPatient());
        doctorComboBox.setValue(consultation.getDoctor());

        Object consultationDate = consultation.getDate();
        if(consultationDate != null){
            date.setValue(LocalDate.parse(consultationDate.toString()));
        }

        if(treatmentView == null){
            treatmentView = new TreatmentView(consultation);
            editVew.getChildren().add(treatmentView);
        }
        else{
            treatmentView.setConsultation(consultation);
        }
    }

    public void saveCurrent(){
        boolean isUpdate = currentSelected != null;
        if(currentSelected == null) {
            currentSelected = new Consultation();
        }

        currentSelected.setDate(date.getValue());
        currentSelected.setObjet(objet.getText());
        currentSelected.setNote(note.getText());
        Patient patient = patientComboBox.getValue();
        if(patient != null){
            currentSelected.setPatient(patient);
        }
        Doctor doctor = doctorComboBox.getValue();
        if(doctor != null){
            currentSelected.setDoctor(doctor);
        }
        currentSelected.save();

        if(isUpdate){
            ObservableList<Consultation> items = tableView.getItems();
            for (Consultation item : items) {
                if (Objects.equals(item.getId(), currentSelected.getId())) {
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

        date.setValue(LocalDate.now());
        objet.setText("");
        note.setText("");
        patientComboBox.setValue(null);
        doctorComboBox.setValue(null);

        btnDelete.setVisible(false);
        btnAdd.setText("Ajouter");
    }
}
