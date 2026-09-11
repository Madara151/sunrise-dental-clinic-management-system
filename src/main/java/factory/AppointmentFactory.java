package factory;

import dao.AppointmentDAO;
import dao.DentistDAO;
import dao.PatientDAO;
import dao.TreatmentTypeDAO;
import model.Appointment;
import model.Dentist;
import model.Patient;
import model.TreatmentType;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * AppointmentFactory - Factory design pattern.
 *
 * Centralizes the construction of a complete, valid Appointment object:
 * - Generates the unique appointment number
 * - Resolves the Patient, Dentist, and TreatmentType by their IDs
 * - Assembles them into a ready-to-persist Appointment
 *
 * Why Factory here:
 * - Keeps object-assembly logic out of the Servlet (Controller), which
 *   should only orchestrate the request/response flow.
 * - If a second appointment "type" is ever needed (e.g. walk-in vs.
 *   scheduled), a new creation method can be added here without
 *   changing any calling code (Open/Closed Principle).
 */
public class AppointmentFactory {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DentistDAO dentistDAO = new DentistDAO();
    private final TreatmentTypeDAO treatmentTypeDAO = new TreatmentTypeDAO();

    /**
     * Builds a new Appointment ready to be saved, given raw ID inputs
     * typically collected from a registration form.
     *
     * @param patientId    ID of an existing patient
     * @param dentistId    ID of an existing dentist
     * @param treatmentId  ID of an existing treatment type
     * @param date         appointment date
     * @param time         appointment time
     * @return a fully-populated Appointment object, or null if any
     *         referenced entity (patient/dentist/treatment) does not exist
     */
    public Appointment createAppointment(String patientId, String dentistId,
                                          String treatmentId, LocalDate date, LocalTime time) {

        Patient patient = patientDAO.getPatientById(patientId);
        Dentist dentist = dentistDAO.getDentistById(dentistId);
        TreatmentType treatment = treatmentTypeDAO.getTreatmentById(treatmentId);

        if (patient == null || dentist == null || treatment == null) {
            // One or more referenced entities do not exist - cannot build a valid appointment
            return null;
        }

        String appointmentNumber = appointmentDAO.generateNextAppointmentNumber();

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setStatus("Scheduled");

        return appointment;
    }
}