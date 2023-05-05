package DAO;

import Main.JDBC;
import Model.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDAO extends Users {
    public UsersDAO(int userID, String userName, String userPass) {

    }

    /**
     * Confirm the user for login
     * @param user
     * @param pass
     * @return
     */
    public static int userConfirm (String user, String pass){
        try {
            String sqlSelect = "SELECT * FROM users WHERE user_name = '" + user + "' AND password = '" + pass +"'";
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlSelect);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                if (resultSet.getString("User_Name").equals(user)) {
                    if (resultSet.getString("Password").equals(pass)) {
                        return resultSet.getInt("User_ID");
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Gets all user data
     * @return
     * @throws SQLException
     */
    public static ObservableList<UsersDAO> getAllUsers() throws SQLException {
        ObservableList<UsersDAO> userObservableList = FXCollections.observableArrayList();
        String sqlSelect = "SELECT * FROM users";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlSelect);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int userID = resultSet.getInt("User_ID");
            String userName = resultSet.getString("User_Name");
            String userPass = resultSet.getString("Password");
            UsersDAO user = new UsersDAO(userID, userName, userPass);
            userObservableList.add(user);
        }
        return  userObservableList;
    }
}
