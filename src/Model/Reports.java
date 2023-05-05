package Model;

public class Reports {
    private int ctryTotal;
    private String ctryName;
    public String apptMonth;
    public int apptTotal;

    /**
     * Stores data for custom report
     * @param ctryName
     * @param ctryTotal
     */
    public Reports(String ctryName, int ctryTotal) {
        this.ctryName = ctryName;
        this.ctryTotal = ctryTotal;
    }

    /**
     * Get ctryName
     * @return
     */
    public String getCtryName(){
        return ctryName;
    }

    /**
     * Get ctryTotal
     * @return
     */
    public int getCtryTotal(){
        return ctryTotal;
    }
}
