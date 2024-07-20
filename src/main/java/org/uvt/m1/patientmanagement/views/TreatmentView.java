package org.uvt.m1.patientmanagement.views;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.uvt.m1.patientmanagement.models.Consultation;
import org.uvt.m1.patientmanagement.models.Treatment;

import java.sql.SQLException;
import java.util.Objects;

public class TreatmentView extends VBox {

    Treatment currentSelected = null;
    private Consultation consultation;
    private TableView<Treatment> tableView;
    private VBox detailView;
    TextField drug;
    TextField dose;
    TextField duration;

    private Button btnAdd;
    private Button btnHide;
    private Button btnDelete;

    public TreatmentView(Consultation consultation){
        this.consultation = consultation;
        tableView = new TableView<>();
        detailView = new VBox();

        TableColumn<Treatment, String> idColumn = new TableColumn<>("Id");
        TableColumn<Treatment, String> drugColumn = new TableColumn<>("Médicament");
        TableColumn<Treatment, String> durationColumn = new TableColumn<>("Durée");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        drugColumn.setCellValueFactory(new PropertyValueFactory<>("drug"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(drugColumn);
        tableView.getColumns().add(durationColumn);
        tableView.getItems().addAll(consultation.getTreatments());
        create();

        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) change -> {
            ObservableList<? extends Treatment> list = change.getList();
            if(list.isEmpty()){
                return;
            }
            setCurrentSelected(list.get(0));
            btnDelete.setVisible(true);
            btnHide.setVisible(true);
        });
        detailView.setVisible(false);
        btnDelete.setVisible(false);
    }

    private void create(){
        Label label = new Label("Traitements");
        label.setFont(new Font(16));
        getChildren().add(label);
        getChildren().add(tableView);

        detailView = new VBox();
        drug = MainView.textField(600, "", "");
        dose = MainView.textField(600, "", "");
        duration = MainView.textField(600, "", "");
        detailView.getChildren().addAll(new Label("Médicament"), drug);
        detailView.getChildren().addAll(new Label("Dose"), dose);
        detailView.getChildren().addAll(new Label("Durée"), duration);

        getChildren().add(detailView);

        btnAdd = new Button("Ajouter");
        Button btnClear = new Button("Reset");
        btnDelete = new Button("Supprimer");
        btnHide = new Button("Afficher les details");

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(btnAdd, btnClear, btnDelete, btnHide);
        getChildren().add(buttonBox);

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                saveCurrent();
            }
        });
        btnClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clearInputs();
            }
        });
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    currentSelected.delete();
                    ObservableList<Treatment> items = tableView.getItems();
                    for (Treatment item: items){
                        if (currentSelected.getId().equals(item.getId())){
                            tableView.getItems().remove(item);
                            clearInputs();
                            tableView.refresh();
                            break;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        btnHide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(detailView.isVisible()){
                    detailView.setVisible(false);
                    btnHide.setText("Afficher les details");
                }
                else{
                    detailView.setVisible(true);
                    btnHide.setText("Masquer les details");
                }
            }
        });
    }

    private void saveCurrent(){
        boolean isUpdate = currentSelected != null;
        if(currentSelected == null) {
            currentSelected = new Treatment();
        }
        currentSelected.setDrug(drug.getText());
        currentSelected.setDose(dose.getText());
        currentSelected.setDuration(duration.getText());
        currentSelected.setConsultation(consultation);
        currentSelected.save();

        if(isUpdate){
            ObservableList<Treatment> items = tableView.getItems();
            for (Treatment item : items) {
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

    private void clearInputs(){
        currentSelected = null;
        drug.setText("");
        dose.setText("");
        duration.setText("");
        btnAdd.setText("Ajouter");
        btnDelete.setVisible(false);
        btnHide.setVisible(false);
    }

    private void setCurrentSelected(Treatment currentSelected){
        this.currentSelected = currentSelected;
        drug.setText(currentSelected.getDrug());
        dose.setText(currentSelected.getDose());
        duration.setText(currentSelected.getDuration());
        btnAdd.setText("Modifier");
    }

    public void setConsultation(Consultation consultation){
        if(this.consultation != consultation){
            this.consultation = consultation;
            if(!tableView.getItems().isEmpty()){
                tableView.getItems().clear();
            }
            tableView.getItems().addAll(consultation.getTreatments());
            tableView.refresh();
            clearInputs();
        }
    }
}
