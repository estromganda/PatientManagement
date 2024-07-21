package org.uvt.m1.patientmanagement.models;

import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Patient extends Model{

    private static final ArrayList<Patient> patients = new ArrayList<>();
    public static final StringConverter<Patient> stringConverter = new StringConverter<>() {
        @Override
        public String toString(Patient patient) {
            return patient.getFirstName();
        }

        @Override
        public Patient fromString(String s) {
            return null;
        }
    };

    ArrayList<Consultation> consultations;
    ArrayList<Admission> admissions;

    public Patient(){
        table = "Patient";
        properties = new HashMap<>(Map.of());
        admissions = new ArrayList<>();
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getFirstName() {
        return (String) properties.getOrDefault("firstName", null);
    }

    public void setFirstName(String firstName) {
        properties.put("firstName", firstName);
    }

    public String getLastName() {
        return (String) properties.getOrDefault("lastName", null);
    }

    public void setLastName(String lastName) {
        properties.put("lastName", lastName);
    }

    public String getBirthday() {
        return (String) properties.getOrDefault("birthday", null);
    }

    public void setBirthday(String birthday) {
        properties.put("birthday", birthday);
    }

    public String getAddress() {
        return (String) properties.getOrDefault("address", null);
    }

    public void setAddress(String address) {
        properties.put("address", address);
    }

    public String getContact() {
        return (String) properties.getOrDefault("contact", null);
    }

    public void setContact(String contact) {
        properties.put("contact", contact);
    }

    public ArrayList<Consultation> getConsultations() {
        if(consultations != null){
            return consultations;
        }
        try {
            ArrayList<Model> models = this.hasMany("Consultation", "patientId", "id", new String[]{"id", "date", "objet", "note", "patientId", "doctorId"});
            consultations = new ArrayList<>();
            for (Model model: models){
                Consultation consultation = new Consultation();
                consultation.fill(model.properties);
                consultations.add(consultation);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return consultations;
    }

    public void setConsultations(ArrayList<Consultation> consultations) {
        if (consultations != this.consultations){
            this.consultations = consultations;
        }
    }

    public ArrayList<Admission> getAdmissions() {
        try {
            ArrayList<Model> models = this.hasMany("Admission", "patientId", "id", new String[]{"id", "patientId", "doctorId", "arrivalDate", "exitDate", "report"});
            admissions.clear();
            for (Model model: models){
                Admission admission = new Admission();
                admission.fill(model.properties);
                admissions.add(admission);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return admissions;
    }

    public void setAdmissions(ArrayList<Admission> admissions) {
        if(admissions != this.admissions){
            this.admissions = admissions;
        }
    }

    public static ArrayList<Patient> getPatients() {
        if(patients.isEmpty()){
            loadPatients();
        }
        return patients;
    }

    public static void loadPatients(){
        try {
            ArrayList<Model> models = Model.all("Patient", DatabaseInfo.Patient.columns, "");
            patients.clear();
            for(Model model: models){
                Patient patient = new Patient();
                patient.fill(model.getProperties());
                patients.add(patient);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
