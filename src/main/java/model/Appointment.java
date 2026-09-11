package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Appointment - the core entity linking a Patient, Dentist, and TreatmentType
 * for a specific date and time. Maps to the 'appointment' table.
 */
public class Appointment {

    private String appointmentNumber;
    private Patient patient;
    private Dentist dentist;
    private TreatmentType treatment;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

    public Appointment() {
    }

    public Appointment(String appointmentNumber, Patient patient, Dentist dentist,
                        TreatmentType treatment, LocalDate appointmentDate,
                        LocalTime appointmentTime, String status) {
        this.appointmentNumber = appointmentNumber;
        this.patient = patient;
        this.dentist = dentist;
        this.treatment = treatment;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }

    public TreatmentType getTreatment() {
        return treatment;
    }

    public void setTreatment(TreatmentType treatment) {
        this.treatment = treatment;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns a human-readable summary of the appointment,
     * used by the "Display Appointment Details" use case.
     */
    public String getAppointmentInfo() {
        return "Appointment No: " + appointmentNumber +
                "\nPatient: " + (patient != null ? patient.getName() : "N/A") +
                "\nDentist: " + (dentist != null ? dentist.getName() : "N/A") +
                "\nTreatment: " + (treatment != null ? treatment.getTreatmentName() : "N/A") +
                "\nDate: " + appointmentDate +
                "\nTime: " + appointmentTime +
                "\nStatus: " + status;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentNumber='" + appointmentNumber + '\'' +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", status='" + status + '\'' +
                '}';
    }
}