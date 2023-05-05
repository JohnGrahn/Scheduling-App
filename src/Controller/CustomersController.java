package Controller;

import DAO.AppointmentsDAO;
import DAO.CountriesDAO;
import DAO.CustomersDAO;
import DAO.DivisionDAO;
import Main.JDBC;
import Model.Appointments;
import Model.Countries;
import Model.Customers;
import Model.Division;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * CustomersController class has methods for creating, editing, and deleting customer data
 */

public class CustomersController implements Initializable {
    @FXML private TableView<Customers> customersTable;
    @FXML private TableColumn <?, ?> customerIDColumn;
    @FXML private TableColumn <?, ?> customerNameColumn;
    @FXML private TableColumn <?, ?> customerAddressColumn;
    @FXML private TableColumn <?, ?> customerPostalColumn;
    @FXML private TableColumn <?, ?> customerPhoneColumn;
    @FXML private TableColumn <?, ?> customerDivisionColumn;
    @FXML private TextField customerIdModify;
    @FXML private TextField customerNameModify;
    @FXML private TextField customerPhoneModify;
    @FXML private TextField customerAddressModify;
    @FXML private TextField customerPostalModify;
    @FXML private ComboBox<String> customerStateModify;
    @FXML private ComboBox<String> customerCountryModify;
    @FXML private Button addCustomerButton;
    @FXML private Button editCustomerButton;
    @FXML private Button deleteCustomerButton;
    @FXML private Button saveCustomerButton;
    @FXML private Button cancelCustomerButton;


    /**
     * Initialize customersTable
     * Lambda statement to populate allDivisionList with DivisionName
     * @param url
     * @param resourceBundle
     */
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = JDBC.makeConnection();

            ObservableList<CountriesDAO> allCountriesList = CountriesDAO.getAllCountries();
            ObservableList<String> ctryNames = FXCollections.observableArrayList();
            ObservableList<DivisionDAO> allDivisionsList = DivisionDAO.getAllDivisions();
            ObservableList<String> divisionsAllNames = FXCollections.observableArrayList();
            ObservableList<Customers> allCustList = CustomersDAO.getAllCustomers(connection);

            customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("custName"));
            customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("custAddress"));
            customerPostalColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            customerDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

            allCountriesList.stream().map(Countries::getCtryName).forEach(ctryNames::add);
            customerCountryModify.setItems(ctryNames);

            allDivisionsList.forEach(division -> divisionsAllNames.add(division.getDivisionName()));

            customerStateModify.setItems(divisionsAllNames);
            customersTable.setItems(allCustList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add customer to database
     * @param event
     */
    @FXML void addCustomerAction(ActionEvent event) {
        try {
            Connection connection = JDBC.makeConnection();

            if (!customerNameModify.getText().isEmpty() || !customerAddressModify.getText().isEmpty() || !customerAddressModify.getText().isEmpty() || !customerPostalModify.getText().isEmpty() || !customerPhoneModify.getText().isEmpty() || !customerCountryModify.getValue().isEmpty() || !customerStateModify.getValue().isEmpty()) {
                Integer newCustID = (int) (Math.random() * 100);
                int divisionName = 0;
                for (DivisionDAO division : DivisionDAO.getAllDivisions()) {
                    if (customerStateModify.getSelectionModel().getSelectedItem().equals(division.getDivisionName())) {
                        divisionName = division.getDivisionID();
                    }
                }
                String sqlInsert = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";
                JDBC.makePreparedStatement(sqlInsert, JDBC.getConnection());
                PreparedStatement preparedStatement = JDBC.getPreparedStatement();
                preparedStatement.setInt(1, newCustID);
                preparedStatement.setString(2, customerNameModify.getText());
                preparedStatement.setString(3, customerAddressModify.getText());
                preparedStatement.setString(4, customerPostalModify.getText());
                preparedStatement.setString(5, customerPhoneModify.getText());
                preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(7, "admin");
                preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(9, "admin");
                preparedStatement.setInt(10, divisionName);
                preparedStatement.execute();

                customerIdModify.clear();
                customerNameModify.clear();
                customerAddressModify.clear();
                customerPostalModify.clear();
                customerPostalModify.clear();

                ObservableList<Customers> renewCustList = CustomersDAO.getAllCustomers(connection);
                customersTable.setItems(renewCustList);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Edit selected customer data
     * @param event
     * @throws SQLException
     */
    @FXML void editCustomerAction(ActionEvent event) throws SQLException {
        try {
            JDBC.makeConnection();
            Customers selectedCust = (Customers) customersTable.getSelectionModel().getSelectedItem();
            String divisionName = "";
            String ctryName = "";
            if (selectedCust != null) {
                ObservableList<CountriesDAO> getAllCountries = CountriesDAO.getAllCountries();
                ObservableList<DivisionDAO> getDivisionNames = DivisionDAO.getAllDivisions();
                ObservableList<String> allDivisions = FXCollections.observableArrayList();

                customerStateModify.setItems(allDivisions);

                customerIdModify.setText(String.valueOf(selectedCust.getCustomerID()));
                customerNameModify.setText(selectedCust.getCustName());
                customerAddressModify.setText(selectedCust.getCustAddress());
                customerPostalModify.setText(selectedCust.getPostalCode());
                customerPhoneModify.setText(selectedCust.getPhoneNumber());

                for (Division division: getDivisionNames) {
                    allDivisions.add(division.getDivisionName());
                    int ctryIdModify = division.getCountry_ID();

                    if (division.getDivisionID() == selectedCust.getCustomerDivisionID()) {
                        divisionName = division.getDivisionName();

                        for (Countries ctry: getAllCountries) {
                            if (ctry.getCountryID() == ctryIdModify){
                                ctryName = ctry.getCtryName();
                            }
                        }
                    }
                }
                customerStateModify.setValue(divisionName);
                customerCountryModify.setValue(ctryName);

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete selected customer from database
     * @param event
     * @throws Exception
     */
    @FXML void deleteCustomerAction(ActionEvent event) throws Exception {
        Connection connection = JDBC.makeConnection();
        ObservableList<Appointments> getAllApptsList = AppointmentsDAO.getAllAppointments();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete customer and associated appointments?");
        Optional<ButtonType> confirm = alert.showAndWait();
        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            int deleteCustID = customersTable.getSelectionModel().getSelectedItem().getCustomerID();
            AppointmentsDAO.apptDelete(deleteCustID, connection);

            String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
            JDBC.makePreparedStatement(sqlDelete, JDBC.getConnection());

            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            int selectedCustTable = customersTable.getSelectionModel().getSelectedItem().getCustomerID();

            for (Appointments appt: getAllApptsList) {
                int selectedCustAppt = appt.getCustomerID();
                if (selectedCustTable == selectedCustAppt) {
                    String sqlApptDelete =  "DELETE FROM appointments WHERE Appointment_ID = ?";
                    JDBC.makePreparedStatement(sqlApptDelete, JDBC.getConnection());
                }
            }
            preparedStatement.setInt(1, selectedCustTable);
            preparedStatement.execute();
            ObservableList<Customers> renewCustList = CustomersDAO.getAllCustomers(connection);
            customersTable.setItems(renewCustList);
        }
    }

    /**
     * Populates combo boxes with first_level_division data
     * @param event
     * @throws SQLException
     */
    @FXML public void customerCountryModifyAction(ActionEvent event) throws SQLException {
        try {


            JDBC.makeConnection();

            String selectedCtry = customerCountryModify.getSelectionModel().getSelectedItem();
            ObservableList<DivisionDAO> allDivisions = DivisionDAO.getAllDivisions();
            ObservableList<String> divisionUS = FXCollections.observableArrayList();
            ObservableList<String> divisionUK = FXCollections.observableArrayList();
            ObservableList<String> divisionCanada = FXCollections.observableArrayList();

            allDivisions.forEach(division -> {
                if (division.getCountry_ID() == 1) {
                    divisionUS.add(division.getDivisionName());
                } else if (division.getCountry_ID() == 2) {
                    divisionUK.add(division.getDivisionName());
                } else if (division.getCountry_ID() == 3) {
                    divisionCanada.add(division.getDivisionName());
                }
            });
            if (selectedCtry.equals("U.S")) {
                customerStateModify.setItems(divisionUS);
            } else if (selectedCtry.equals("UK")) {
                customerStateModify.setItems(divisionUK);
            } else if (selectedCtry.equals("Canada")) {
                customerStateModify.setItems(divisionCanada);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Saves changes made
     * @param event
     */
    @FXML void saveCustomerAction(ActionEvent event) {
        try {
            Connection connection = JDBC.makeConnection();

            if (!customerNameModify.getText().isEmpty() || !customerAddressModify.getText().isEmpty() || !customerAddressModify.getText().isEmpty() || !customerPostalModify.getText().isEmpty() || !customerPhoneModify.getText().isEmpty() || !customerCountryModify.getValue().isEmpty() || !customerStateModify.getValue().isEmpty()) {

                int divisionName = 0;
                for (DivisionDAO division : DivisionDAO.getAllDivisions()) {
                    if (customerStateModify.getSelectionModel().getSelectedItem().equals(division.getDivisionName())) {
                        divisionName = division.getDivisionID();
                    }
                }
                String sqlUpdate =  "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
                JDBC.makePreparedStatement(sqlUpdate, JDBC.getConnection());
                PreparedStatement preparedStatement = JDBC.getPreparedStatement();
                preparedStatement.setInt(1, Integer.parseInt(customerIdModify.getText()));
                preparedStatement.setString(2, customerNameModify.getText());
                preparedStatement.setString(3, customerAddressModify.getText());
                preparedStatement.setString(4, customerPostalModify.getText());
                preparedStatement.setString(5, customerPhoneModify.getText());
                preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(7, "admin");
                preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(9, "admin");
                preparedStatement.setInt(10, divisionName);
                preparedStatement.setInt(11, Integer.parseInt(customerIdModify.getText()));
                preparedStatement.execute();

                customerIdModify.clear();
                customerNameModify.clear();
                customerAddressModify.clear();
                customerPostalModify.clear();
                customerPostalModify.clear();

                ObservableList<Customers> renewCustList = CustomersDAO.getAllCustomers(connection);
                customersTable.setItems(renewCustList);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Returns user to main screen
     * @param event
     * @throws IOException
     */
    @FXML public void cancelCustomerAction (ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/View/Main.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
