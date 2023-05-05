package Model;

import java.time.LocalDateTime;

public class Appointments {
    private int appointmentID;
    private String apptTitle;
    private String apptDescription;
    private String apptLocation;
    private String apptType;
    private LocalDateTime apptStart;
    private LocalDateTime apptEnd;
    public int customerID;
    public int userID;
    public int contactID;

    /**
     * Store appointment data
     * @param appointmentID
     * @param apptTitle
     * @param apptDescription
     * @param apptLocation
     * @param apptType
     * @param apptStart
     * @param apptEnd
     * @param customerID
     * @param userID
     * @param contactID
     */
    public Appointments(int appointmentID, String apptTitle, String apptDescription, String apptLocation, String apptType,
                        LocalDateTime apptStart, LocalDateTime apptEnd, int customerID, int userID, int contactID) {
        this.appointmentID = appointmentID;
        this.apptTitle = apptTitle;
        this.apptDescription = apptDescription;
        this.apptLocation = apptLocation;
        this.apptType = apptType;
        this.apptStart = apptStart;
        this.apptEnd = apptEnd;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * Get appointmentID
     * @return
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /**
     * Get apptTitle
     * @return
     */
    public String getApptTitle() {

        return apptTitle;
    }

    /**
     * Get apptDescription
     * @return
     */
    public String getApptDescription() {
        return apptDescription;
    }

    /**
     * Get apptLocation
     * @return
     */
    public String getApptLocation() {
        return apptLocation;
    }

    /**
     * Get apptType
     * @return
     */
    public String getApptType() {
        return apptType;
    }

    /**
     * Get apptStart
     * @return
     */
    public LocalDateTime getApptStart() {
        System.out.println("Start : " + apptStart);
        return apptStart;
    }

    /**
     * Get apptEnd
     * @return
     */
    public LocalDateTime getApptEnd() {
        return apptEnd;
    }

    /**
     * Get customerID
     * @return
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Get userID
     * @return
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Get contactID
     * @return
     */
    public int getContactID() {
        return contactID;
    }
}
