package Model;

public class TypeReport {
    public String apptType;
    public int apptTotal;

    /**
     * Method to store data for report
     * @param apptType
     * @param apptTotal
     */
    public TypeReport(String apptType, int apptTotal){
        this.apptType = apptType;
        this.apptTotal = apptTotal;
    }

    /**
     * Get apptType
     * @return
     */
    public String getApptType() {
        return apptType;
    }

    /**
     * Get apptTotal
     * @return
     */
    public int getApptTotal() {
        return apptTotal;
    }
}
