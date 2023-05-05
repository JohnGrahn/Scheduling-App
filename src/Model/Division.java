package Model;

public class Division {

    private int divisionID;
    private String divisionName;
    public int country_ID;

    /**
     * Stores first_level_division data
     * @param divisionID
     * @param divisionName
     * @param country_ID
     */
    public Division(int divisionID, String divisionName, int country_ID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.country_ID = country_ID;
    }

    /**
     * Get divisionID
     * @return
     */
        public int getDivisionID(){
        return divisionID;
        }

    /**
     * Get divisionName
     * @return
     */

        public String getDivisionName () {
            return divisionName;
        }

    /**
     * Get Country_ID
     * @return
     */
    public int getCountry_ID() {
        return country_ID;
    }
}
