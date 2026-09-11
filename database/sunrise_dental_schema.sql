-- ============================================================
-- Sunrise Dental Clinic - Database Schema
-- CIS6003 Advanced Programming - WRIT1
-- ============================================================

DROP DATABASE IF EXISTS sunrise_dental;
CREATE DATABASE sunrise_dental;
USE sunrise_dental;

-- ------------------------------------------------------------
-- Table: staff
-- Stores login credentials for authorized clinic staff
-- ------------------------------------------------------------
CREATE TABLE staff (
    staff_id      VARCHAR(10)   NOT NULL PRIMARY KEY,
    username      VARCHAR(50)   NOT NULL UNIQUE,
    password      VARCHAR(255)  NOT NULL,   -- store a hashed password, not plain text
    full_name     VARCHAR(100)  NOT NULL,
    role          VARCHAR(30)   NOT NULL DEFAULT 'Receptionist'
);

-- ------------------------------------------------------------
-- Table: patient
-- ------------------------------------------------------------
CREATE TABLE patient (
    patient_id      VARCHAR(10)   NOT NULL PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    address         VARCHAR(255)  NOT NULL,
    contact_number  VARCHAR(20)   NOT NULL
);

-- ------------------------------------------------------------
-- Table: dentist
-- ------------------------------------------------------------
CREATE TABLE dentist (
    dentist_id      VARCHAR(10)   NOT NULL PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    specialization  VARCHAR(100)  NOT NULL
);

-- ------------------------------------------------------------
-- Table: treatment_type
-- Lookup table for treatment names + consultation fees
-- ------------------------------------------------------------
CREATE TABLE treatment_type (
    treatment_id       VARCHAR(10)     NOT NULL PRIMARY KEY,
    treatment_name     VARCHAR(100)    NOT NULL,
    consultation_fee   DECIMAL(10,2)   NOT NULL
);

-- ------------------------------------------------------------
-- Table: appointment
-- Core table linking patient, dentist, and treatment
-- ------------------------------------------------------------
CREATE TABLE appointment (
    appointment_number  VARCHAR(15)   NOT NULL PRIMARY KEY,
    patient_id          VARCHAR(10)   NOT NULL,
    dentist_id          VARCHAR(10)   NOT NULL,
    treatment_id        VARCHAR(10)   NOT NULL,
    appointment_date    DATE          NOT NULL,
    appointment_time    TIME          NOT NULL,
    status              VARCHAR(20)   NOT NULL DEFAULT 'Scheduled',

    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_appointment_dentist
        FOREIGN KEY (dentist_id) REFERENCES dentist(dentist_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_appointment_treatment
        FOREIGN KEY (treatment_id) REFERENCES treatment_type(treatment_id)
        ON DELETE RESTRICT
);

-- ------------------------------------------------------------
-- Table: bill
-- One bill generated per appointment
-- ------------------------------------------------------------
CREATE TABLE bill (
    bill_id             VARCHAR(15)     NOT NULL PRIMARY KEY,
    appointment_number  VARCHAR(15)     NOT NULL,
    total_amount        DECIMAL(10,2)   NOT NULL,
    issue_date          DATE            NOT NULL,

    CONSTRAINT fk_bill_appointment
        FOREIGN KEY (appointment_number) REFERENCES appointment(appointment_number)
        ON DELETE CASCADE
);

-- ============================================================
-- Sample seed data (useful for testing/demo screenshots)
-- ============================================================

INSERT INTO staff (staff_id, username, password, full_name, role) VALUES
('S001', 'admin', 'admin123', 'Nadeesha Perera', 'Receptionist');

INSERT INTO patient (patient_id, name, address, contact_number) VALUES
('P001', 'Kasun Fernando', '12 Galle Road, Colombo 03', '0771234567'),
('P002', 'Ishara Silva', '45 Kandy Road, Kadawatha', '0719876543');

INSERT INTO dentist (dentist_id, name, specialization) VALUES
('D001', 'Dr. Ramesh Gunawardena', 'General Dentistry'),
('D002', 'Dr. Anusha Jayawardena', 'Orthodontics');

INSERT INTO treatment_type (treatment_id, treatment_name, consultation_fee) VALUES
('T001', 'General Checkup', 1500.00),
('T002', 'Tooth Extraction', 3500.00),
('T003', 'Root Canal Treatment', 12000.00),
('T004', 'Teeth Cleaning', 2500.00),
('T005', 'Braces Consultation', 2000.00);

INSERT INTO appointment (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status) VALUES
('APT00001', 'P001', 'D001', 'T001', '2026-09-10', '09:30:00', 'Scheduled'),
('APT00002', 'P002', 'D002', 'T005', '2026-09-11', '14:00:00', 'Scheduled');