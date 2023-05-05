package DAO;

import Main.JDBC;
import Model.Division;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DivisionDAO extends Division {
    public DivisionDAO(int divisionID, String divisionName, int country_ID) {
        super(divisionID, divisionName, country_ID);
    }

    /**
     * Selects every entry from first_level_divisions table
     * @return
     * @throws SQLException
     */
    public static ObservableList<DivisionDAO> getAllDivisions() throws SQLException {
        ObservableList<DivisionDAO> divisionObservableList = FXCollections.observableArrayList();
        String sqlSelect = "SELECT * FROM first_level_divisions";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int divisionID = resultSet.getInt("Division_ID");
            String divisionName = resultSet.getString("Division");
            int country_ID = resultSet.getInt("Country_ID");
            DivisionDAO division = new DivisionDAO(divisionID, divisionName, country_ID);
            divisionObservableList.add(division);


        }
        return divisionObservableList;
    }
}
