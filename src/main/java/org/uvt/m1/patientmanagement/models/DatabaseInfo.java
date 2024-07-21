package org.uvt.m1.patientmanagement.models;

public class DatabaseInfo {
    public static class Patient{
        public static String[] columns = {"id", "firstName", "lastName", "birthday", "address", "contact"};
    }
    public static class Doctor{
        public static String[] columns = {"id", "firstName", "lastName", "address", "contact"};
    }
    public static class Room{
        public static String[] columns = {"id", "isBusy"};
    }
    public static class Admission{
        public static String[] columns = {"id", "patientId", "doctorId", "roomId", "arrivalDate", "exitDate", "report"};
    }
    public static class Consultation{
        public static String[] columns = {"id", "date", "objet", "note", "patientId", "doctorId"};
    }
    public static class Treatment{
        public static String[] columns = {"id", "drug", "dose", "duration", "consultationId"};
    }
}
