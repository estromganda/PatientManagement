module org.uvt.m1.patientmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.java;
    requires jdk.jshell;

    opens org.uvt.m1.patientmanagement to javafx.fxml;
    exports org.uvt.m1.patientmanagement;

    opens org.uvt.m1.patientmanagement.controllers to javafx.fxml;
    exports org.uvt.m1.patientmanagement.controllers;

    opens org.uvt.m1.patientmanagement.models to javafx.base;
    exports org.uvt.m1.patientmanagement.models;
}