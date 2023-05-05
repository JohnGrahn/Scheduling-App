package Model;

public class Contacts {
    public int contactID;
    public String contactName;
    public String contactEmailAddress;

    public Contacts(int contactID, String contactName, String contactEmailAddress){
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmailAddress = contactEmailAddress;
    }

    /**
     * @return the contactID
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * @return the contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @return the contactEmailAddress
     */
    public String getContactEmailAddress() {
        return contactEmailAddress;
    }
}
