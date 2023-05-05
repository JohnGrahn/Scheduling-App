package DAO;

import Main.JDBC;
import Model.Contacts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactsDAO {
    /**
     * Gets contact data from database and stores it in list
     * @return
     * @throws SQLException
     */
    public static ObservableList<Contacts> getAllContacts() throws SQLException {
        ObservableList<Contacts> contObservableList = FXCollections.observableArrayList();
        String sqlSelect = "SELECT * FROM contacts";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int contactID = resultSet.getInt("Contact_ID");
            String contactName = resultSet.getString("Contact_Name");
            String contactEmailAddress = resultSet.getString("Email");
            Contacts contact = new Contacts(contactID, contactName, contactEmailAddress);
            contObservableList.add(contact);
        }
        return contObservableList;
    }

    /**
     * Gets Contact_ID of given Contact_Name
     * @param contID
     * @return
     * @throws SQLException
     */
    public static String contIDSearch(String contID) throws SQLException{
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement("SELECT * FROM contacts WHERE Contact_Name = ?");
        preparedStatement.setString(1, contID);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            contID = resultSet.getString("Contact_ID");
        }
        return contID;
    }
}
