package org.uvt.m1.patientmanagement.models;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Consultation extends Model{
    Patient patient = null;
    Doctor doctor = null;
    ArrayList<Treatment> treatments;
    public Consultation(){
        this.table = "Consultation";
        properties = new HashMap<>(Map.of());
        setDate(null);
    }

    public Object getDate(){
        return properties.getOrDefault("date", null);
    }

    public void setDate(Object date){
        properties.put("date", date);
    }

    public BigInteger getPatientId(){
        Object id = properties.getOrDefault("patientId", "0");
        return BigInteger.valueOf(Integer.parseInt(id.toString()));
    }

    public void setPatientId(BigInteger newId){
        properties.put("patientId", newId);
    }

    public BigInteger getDoctorId(){
        Object id = properties.getOrDefault("doctorId", "0");
        return BigInteger.valueOf(Integer.parseInt(id.toString()));
    }

    public void setDoctorId(BigInteger newId){
        properties.put("doctorId", newId);
    }

    public String getNote(){
        return (String) properties.getOrDefault("note", "");
    }

    public void setNote(String note){
        properties.put("consultationNote", note);
    }

    public Patient getPatient() {
        if(patient == null){
            patient = new Patient();
            String[] patientPpt = {"id", "firstName", "lastName", "birthday", "address", "contact"};
            try {
                Model model = Model.find("Patient", this.getPatientId(), patientPpt);
                patient.fill(model.properties);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return patient;
    }

    public Doctor getDoctor() {
        if(doctor == null){
            String[] doctorPpt = {"id", "firstName", "lastName", "address", "contact"};
            try {
                Model model = Model.find("Doctor", this.getDoctorId(), doctorPpt);
                doctor = new Doctor();
                doctor.fill(model.properties);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return doctor;
    }

    public ArrayList<Treatment> getTreatments(){
        if(this.treatments != null){
            return treatments;
        }
        treatments = new ArrayList<>();
        try {
            ArrayList<Model> lsModels = this.hasMany("Treatment", "consultationId", "id", DatabaseInfo.Treatment.columns);
            for (Model model: lsModels){
                Treatment treatment = new Treatment();
                treatment.fill(model.properties);
                treatments.add(treatment);
            }
        } catch (SQLException e) {
            treatments = null;
            throw new RuntimeException(e);
        }
        return treatments;
    }

    public String getObjet() {
        return (String) properties.getOrDefault("objet", "");
    }

    public void setPatient(Patient patient) {
        if(this.patient != patient){
            this.patient = patient;
            this.properties.put("patientId", patient.getId());
        }
    }

    public void setDoctor(Doctor doctor) {
        if(this.doctor != doctor){
            this.doctor = doctor;
            this.properties.put("doctorId", doctor.getId());
        }
    }

    public void setObjet(String text) {
        this.properties.put("objet", text);
    }

    public String getDoctorFullName(){
        Doctor doctor = getDoctor();
        if(doctor != null){
            return doctor.getFullName();
        }
        return "";
    }

    public String getPatientFullName(){
        Patient patient = getPatient();
        if(patient != null){
            return patient.getFullName();
        }
        return "";
    }
}
