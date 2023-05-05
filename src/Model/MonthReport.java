package Model;

public class MonthReport {
    public String apptMonth;
    public int apptTotal;

    /**
     * Store date for month report
     * @param apptMonth
     * @param apptTotal
     */
    public MonthReport(String apptMonth, int apptTotal){
        this.apptMonth = apptMonth;
        this.apptTotal = apptTotal;
    }

    /**
     * Get apptMonth
     * @return
     */
    public String getApptMonth() {
        return apptMonth;
    }

    /**
     * Get apptTotal
     * @return
     */
    public int getApptTotal() {
        return apptTotal;
    }
}
