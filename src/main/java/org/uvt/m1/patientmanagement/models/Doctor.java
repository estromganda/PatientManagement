package org.uvt.m1.patientmanagement.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Doctor extends Model{
    private static final ArrayList<Doctor> doctors = new ArrayList<>();
    public static String[] columns = {"id", "firstName", "lastName", "address", "contact"};
    ArrayList<Consultation> consultations;

    public Doctor(){
        table = "Doctor";
        properties = new HashMap<>(Map.of());
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
            ArrayList<Model> models = this.hasMany("Consultation", "doctorId", "id", new String[]{"id", "date", "objet", "note", "patientId", "doctorId"});
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

    public static ArrayList<Doctor> getDoctors() {
        if(doctors.isEmpty()){
            loadDoctors();
        }
        return doctors;
    }

    public static void loadDoctors(){
        try {
            ArrayList<Model> models = Model.all("Doctor", Doctor.columns, "");
            doctors.clear();
            for(Model model: models){
                Doctor doctor = new Doctor();
                doctor.fill(model.getProperties());
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
