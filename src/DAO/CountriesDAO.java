package DAO;

import Main.JDBC;
import Model.Countries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountriesDAO extends Countries {

    public CountriesDAO(int countryID, String ctryName) {
        super(countryID, ctryName);
    }

    /**
     * Gets Country data from countries table within database and stores it in a list
     * @return
     * @throws SQLException
     */
    public static ObservableList<CountriesDAO> getAllCountries() throws SQLException {
        ObservableList<CountriesDAO> countriesObservableList = FXCollections.observableArrayList();
        String sqlSelect = "SELECT Country_ID, Country FROM countries";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int countryID = resultSet.getInt("Country_ID");
            String ctryName = resultSet.getString("Country");
            CountriesDAO ctry = new CountriesDAO(countryID, ctryName);
            countriesObservableList.add(ctry);
        }
        return countriesObservableList;
    }
}
