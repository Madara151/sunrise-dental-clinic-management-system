package model;

/**
 * Patient - represents a registered patient at Sunrise Dental Clinic.
 * Maps to the 'patient' table.
 */
public class Patient {

    private String patientId;
    private String name;
    private String address;
    private String contactNumber;

    public Patient() {
    }

    public Patient(String patientId, String name, String address, String contactNumber) {
        this.patientId = patientId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}