package DAO;

import Main.JDBC;
import Model.Appointments;
import Model.Reports;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReportsDAO extends Appointments {
    public ReportsDAO(int appointmentID, String apptTitle, String apptDescription, String apptLocation, String apptType, LocalDateTime apptStart, LocalDateTime apptEnd, int customerID, int userID, int contactID) {
        super(appointmentID, apptTitle, apptDescription, apptLocation, apptType, apptStart, apptEnd, customerID, userID, contactID);
    }

    /**
     * SQL Query that pulls Countries and Appointments for Reports
     * @return
     * @throws SQLException
     */
    public static ObservableList<Reports> getCountries() throws SQLException {
        ObservableList<Reports> countriesObservableList = FXCollections.observableArrayList();
        String sqlSelect = "SELECT countries.Country, count(*) AS ctryTotal from customers INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID INNER JOIN countries ON countries.Country_ID = first_level_divisions.Country_ID WHERE customers.Division_ID = first_level_divisions.Division_ID GROUP BY first_level_divisions.Country_ID ORDER BY count(*) DESC";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String ctryName = resultSet.getString("Country");
            int ctryTotal = resultSet.getInt("ctryTotal");
            Reports report = new Reports(ctryName, ctryTotal);
            countriesObservableList.add(report);
        }
        return countriesObservableList;
    }
}
