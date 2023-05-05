package DAO;
import Main.JDBC;
import Model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AppointmentsDAO {

    /**
     * Gets Appointments table from database and stores it in list
     * @return
     * @throws SQLException
     */
    public static ObservableList<Appointments> getAllAppointments() throws SQLException {
        ObservableList<Appointments> apptsObservableList = FXCollections.observableArrayList();
        String sqlSelect = "SELECT * FROM appointments";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {

            int appointmentID = resultSet.getInt("Appointment_ID");
            String apptTitle = resultSet.getString("Title");
            String apptDescription = resultSet.getString("Description");
            String apptLocation = resultSet.getString("Location");
            String apptType = resultSet.getString("Type");
            LocalDateTime apptStart = resultSet.getTimestamp("Start").toLocalDateTime();
            LocalDateTime apptEnd = resultSet.getTimestamp("End").toLocalDateTime();
            int customerID = resultSet.getInt("Customer_ID");
            int userID = resultSet.getInt("User_ID");
            int contactID = resultSet.getInt("Contact_ID");
            Appointments appt = new Appointments(appointmentID, apptTitle, apptDescription, apptLocation, apptType, apptStart, apptEnd, customerID, userID, contactID);
            apptsObservableList.add(appt);
        }
        return  apptsObservableList;
    }

    /**
     * Deletes selected appointment with with Appointment_ID
     * @param customer
     * @param connection
     * @return
     * @throws SQLException
     */
    public static int apptDelete(int customer, Connection connection) throws SQLException{
        String sqlDelete = "DELETE FROM appointments WHERE Appointment_ID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete);
        preparedStatement.setInt(1, customer);
        int deleteResult = preparedStatement.executeUpdate();
        preparedStatement.close();
        return deleteResult;
    }
}
