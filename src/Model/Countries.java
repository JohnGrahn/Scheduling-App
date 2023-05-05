package Model;

public class Countries {
    private int countryID;
    private String ctryName;

    public Countries(int countryID, String ctryName) {
        this.countryID = countryID;
        this.ctryName = ctryName;
    }

    /**
     * @return the countryID
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * @return the countryName
     */
    public String getCtryName() {
        return ctryName;
    }
}
