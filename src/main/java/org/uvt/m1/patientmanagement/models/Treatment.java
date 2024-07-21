package org.uvt.m1.patientmanagement.models;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Treatment extends Model{
    private Consultation consultation;

    public Treatment(){
        consultation = null;
        table = "Treatment";
        properties = new HashMap<>(Map.of());
    }

    public BigInteger getConsultationId(){
        Object id = properties.getOrDefault("consultationId", "0");
        return BigInteger.valueOf(Integer.parseInt(id.toString()));
    }
    public void setConsultationId(BigInteger newId){
        properties.put("consultationId", "newId");
    }

    public String getDrug(){
        return (String) properties.getOrDefault("drug", null);
    }
    public void setDrug(String drug){
        properties.put("drug", drug);
    }

    public String getDose(){
        return (String) properties.getOrDefault("dose", null);
    }
    public void setDose(String dose){
        properties.put("dose", dose);
    }

    public String getDuration(){
        return (String) properties.getOrDefault("duration", null);
    }
    public void setDuration(String duration){
        properties.put("duration", duration);
    }

    public void setConsultation(Consultation consultation){
        if (this.consultation != consultation){
            this.consultation = consultation;
            this.properties.put("consultationId", consultation.getId());
        }
    }

    public Consultation getConsultation(){
        if(consultation != null){
            return consultation;
        }
        try {
            Model model = this.hasOne("Consultation", "id", "consultationId", DatabaseInfo.Consultation.columns);
            consultation = new Consultation();
            consultation.fill(model.getProperties());
        } catch (SQLException e) {
            consultation = null;
            throw new RuntimeException(e);
        }
        return consultation;
    }
}
