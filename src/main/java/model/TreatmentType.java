package model;

import java.math.BigDecimal;

/**
 * TreatmentType - lookup entity for treatment names and their consultation fees.
 * Maps to the 'treatment_type' table.
 */
public class TreatmentType {

    private String treatmentId;
    private String treatmentName;
    private BigDecimal consultationFee;

    public TreatmentType() {
    }

    public TreatmentType(String treatmentId, String treatmentName, BigDecimal consultationFee) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.consultationFee = consultationFee;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    /**
     * Returns the fee for this treatment. Used by Bill.calculateTotal()
     * as shown in the "Calculate and Print Bill" sequence diagram.
     */
    public BigDecimal getFee() {
        return consultationFee;
    }

    @Override
    public String toString() {
        return "TreatmentType{" +
                "treatmentId='" + treatmentId + '\'' +
                ", treatmentName='" + treatmentName + '\'' +
                ", consultationFee=" + consultationFee +
                '}';
    }
}