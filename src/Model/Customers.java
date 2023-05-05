package Model;

public class Customers {

    private int customerID;
    private String custName;
    private String custAddress;
    private String postalCode;
    private String phoneNumber;
    private int divisionID;
    private String divisionName;

    public Customers(int customerID, String custName, String custAddress, String postalCode, String phoneNumber, int divisionID, String divisionName) {
        this.customerID = customerID;
        this.custName = custName;
        this.custAddress = custAddress;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;
        this.divisionName = divisionName;

    }

    /**
     * @return the customerID
     */
    public Integer getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID set the customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * @return the customerName
     */
    public String getCustName() {
        return custName;
    }

    /**
     * @param customerName set the customerName
     */
    public void setCustName(String customerName) {
        this.custName = customerName;
    }

    /**
     * @return the customerAddress
     */
    public String getCustAddress() {
        return custAddress;
    }

    /**
     * @param customerAddress set the customerAddress
     */
    public void setCustAddress(String customerAddress) {
        this.custAddress = customerAddress;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode set the postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber set the phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the Customer divisionID
     */
    public Integer getCustomerDivisionID(){
        return divisionID;
    }

    /**
     * @param divisionID set the Customer divisionID
     */
    public void setCustomerDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * @return the divisionName
     */
    public String getDivisionName() {
        return divisionName;
    }
}
