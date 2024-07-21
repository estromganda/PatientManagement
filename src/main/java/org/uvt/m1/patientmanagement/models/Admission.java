package org.uvt.m1.patientmanagement.models;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Admission extends Model{
    Patient patient;
    Doctor doctor;
    Room room;

    public Admission(){
        table = "Admission";
        patient = null;
        doctor = null;
        properties = new HashMap<>(Map.of());
    }

    public BigInteger getPatientId(){
        Object id = properties.getOrDefault("patientId", "0");
        return BigInteger.valueOf(Integer.parseInt(id.toString()));
    }

    public BigInteger getRoomId(){
        Object id = properties.getOrDefault("roomId", "0");
        return BigInteger.valueOf(Integer.parseInt(id.toString()));
    }
    public void setRoomId(BigInteger id){
        properties.put("roomId", id);
    }

    public void setPatientId(BigInteger newId){
        properties.put("patientId", newId);
    }

    public Object getArrivalDate(){
        return properties.getOrDefault("arrivalDate", null);
    }

    public void setArrivalDate(Object newDate){
        properties.put("arrivalDate", newDate);
    }

    public Object getExitDate(){
        return properties.getOrDefault("exitDate", null);
    }

    public void setExitDate(Object newDate){
        properties.put("exitDate", newDate);
    }

    public String getSalle(){
        return properties.getOrDefault("salle", "").toString();
    }
    public void setSalle(String salle){
        properties.put("salle", salle);
    }

    public String getReport(){
        return (String) properties.getOrDefault("report", null);
    }

    public void setReport(String newReport){
        properties.put("report", newReport);
    }

    public BigInteger getDoctorId(){
        Object id = properties.getOrDefault("doctorId", "0");
        if(id instanceof String){
            return BigInteger.valueOf(Long.parseLong(id.toString()));
        }
        return (BigInteger) id;
    }

    public void setDoctorId(BigInteger newId){
        properties.put("doctorId", newId);
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
            doctor = new Doctor();
            String[] patientPpt = {"id", "firstName", "lastName", "birthday", "address", "contact"};
            try {
                Model model = Model.find("Patient", this.getPatientId(), patientPpt);
                doctor.fill(model.properties);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Room getRoom() {
        if(room == null){
            room = new Room();
            try {
                Model model = Model.find("Room", this.getRoomId(), DatabaseInfo.Room.columns);
                room.fill(model.properties);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return room;
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

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
