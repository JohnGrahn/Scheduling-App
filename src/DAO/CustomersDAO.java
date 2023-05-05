package DAO;

import Main.JDBC;
import Model.Customers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomersDAO {
    /**
     * Stores all customer data in list
     * @param connection
     * @return
     * @throws SQLException
     */

    public static ObservableList<Customers> getAllCustomers(Connection connection) throws SQLException {
        String sqlSelect = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Division_ID, first_level_divisions.Division FROM customers INNER JOIN  first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();
        ObservableList<Customers> custObservableList = FXCollections.observableArrayList();
        while (resultSet.next()) {
         int customerID = resultSet.getInt("Customer_ID");
         String custName = resultSet.getString("Customer_Name");
         String custAddress = resultSet.getString("Address");
         String postalCode = resultSet.getString("Postal_Code");
         String phoneNumber = resultSet.getString("Phone");
         int divisionID = resultSet.getInt("Division_ID");
         String divisionName = resultSet.getString("Division");
         Customers cust = new Customers(customerID, custName, custAddress, postalCode, phoneNumber, divisionID, divisionName);
         custObservableList.add(cust);

        }
        return custObservableList;
    }
}
