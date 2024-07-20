CREATE DATABASE IF NOT EXISTS PatientManagement;
USE PatientManagement;

CREATE TABLE IF NOT EXISTS Patient(
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(32),
    lastName VARCHAR(32),
    birthday DATE DEFAULT NULL,
    address VARCHAR(64),
    contact VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS Doctor(
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(32),
    lastName VARCHAR(32),
    address VARCHAR(64),
    contact VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS Room(
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    isBusy BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS Admission(
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    patientId BIGINT UNSIGNED,
    doctorId BIGINT UNSIGNED,
    roomId BIGINT UNSIGNED,
    arrivalDate DATETIME,
    exitDate DATETIME DEFAULT NULL,
    report LONGTEXT,
    CONSTRAINT admissionRoomIdFk FOREIGN KEY(roomId) REFERENCES Room(id),
    CONSTRAINT admissionPatientIdFk FOREIGN KEY(patientId) REFERENCES Patient(id),
    CONSTRAINT admissionDoctorIdFk FOREIGN KEY(doctorId) REFERENCES Doctor(id)
);

CREATE TABLE IF NOT EXISTS Consultation(
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    objet VARCHAR(127),
    `date` DATETIME,
    note LONGTEXT DEFAULT NULL,
    patientId BIGINT UNSIGNED,
    doctorId BIGINT UNSIGNED,
    CONSTRAINT consultationPatientIdFk FOREIGN KEY(patientId) REFERENCES Patient(id),
    CONSTRAINT consultationDoctorIdFk FOREIGN KEY(doctorId) REFERENCES Doctor(id)
);

CREATE TABLE IF NOT EXISTS Treatment(
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    drug VARCHAR(64),
    dose VARCHAR(64),
    duration VARCHAR(32),
    consultationId BIGINT UNSIGNED,
    CONSTRAINT treatmentConsultationIddFk FOREIGN KEY(consultationId) REFERENCES Consultation(id)
);
