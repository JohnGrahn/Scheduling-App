package Controller;

import DAO.AppointmentsDAO;
import DAO.ContactsDAO;
import DAO.CustomersDAO;
import DAO.ReportsDAO;
import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Month;
import java.util.Collection;
import java.util.Collections;

/**
 * ReportsController controller for display report information
 */
public class ReportsController {
    @FXML private TableView<Appointments> AppointmentsTable;
    @FXML private TableColumn<?, ?> appointmentIDColumn;
    @FXML private TableColumn<?, ?> appointmentTitleColumn;
    @FXML private TableColumn<?, ?> appointmentDescriptionColumn;
    @FXML private TableColumn<?, ?> appointmentLocationColumn;
    @FXML private TableColumn<?, ?> ContactColumn;
    @FXML private TableColumn<?, ?> appointmentTypeColumn;
    @FXML private TableColumn<?, ?> appointmentStartColumn;
    @FXML private TableColumn<?, ?> appointmentEndColumn;
    @FXML private TableColumn<?, ?> appointmentCustomerIDColumn;
    @FXML private TableColumn<?, ?> appointmentContactIDColumn;
    @FXML private ComboBox<String> contScheduleCombo;
    @FXML private Tab appointmentReportTotalTab;
    @FXML private TableView<TypeReport> appointmentReportTypeTotal;
    @FXML private TableColumn appointmentReportTypeColumn;
    @FXML private TableColumn appointmentReportTypeTotalColumn;
    @FXML private TableView<MonthReport> appointmentMonthTotal;
    @FXML private TableColumn appointmentReportMonthColumn;
    @FXML private TableColumn appointmentReportMonthTotalColumn;
    @FXML private Tab customerReportCountryTab;
    @FXML private TableView<Reports> customerCountryTotal;
    @FXML private TableColumn customerReportCountryColumn;
    @FXML private TableColumn customerReportCountryTotalColumn;
    @FXML private Button returnButton;

    public void initialize () throws SQLException {
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("apptTitle"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("apptDescription"));
        appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("apptLocation"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("apptStart"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("apptEnd"));
        appointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        appointmentContactIDColumn.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        appointmentReportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        appointmentReportTypeTotalColumn.setCellValueFactory(new PropertyValueFactory<>("apptTotal"));
        appointmentReportMonthColumn.setCellValueFactory(new PropertyValueFactory<>("apptMonth"));
        appointmentReportMonthTotalColumn.setCellValueFactory(new PropertyValueFactory<>("apptTotal"));
        customerReportCountryColumn.setCellValueFactory(new PropertyValueFactory<>("ctryName"));
        customerReportCountryTotalColumn.setCellValueFactory(new PropertyValueFactory<>("ctryTotal"));

        ObservableList<Contacts> contObservableList = ContactsDAO.getAllContacts();
        ObservableList<String> allContNames = FXCollections.observableArrayList();

        /**
         * Lambda expression
         */
        contObservableList.forEach(cont -> allContNames.add(cont.getContactName()));
        contScheduleCombo.setItems(allContNames);

    }

    /**
     * Fills table with contact schedule
     */
    @FXML public void apptContDataAction()  {
        try {
            int contID = 0;

            ObservableList<Appointments> getAllApptInfo = AppointmentsDAO.getAllAppointments();
            ObservableList<Appointments> apptInfo = FXCollections.observableArrayList();
            ObservableList<Contacts> getAllConts = ContactsDAO.getAllContacts();

            Appointments contApptInfo;
            String contName = contScheduleCombo.getSelectionModel().getSelectedItem();

            for (Contacts cont: getAllConts) {
                if(contName.equals(cont.getContactName())) {
                    contID = cont.getContactID();
                }
            }
            for(Appointments appt: getAllApptInfo) {
                if (appt.getContactID() == contID) {
                    contApptInfo = appt;
                    apptInfo.add(contApptInfo);
                }
            }
            AppointmentsTable.setItems(apptInfo);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Fills table with reports of appointment type, month, and totals
     * @throws SQLException
     */
    @FXML public void appointmentReportTotalTab() throws SQLException {
        try {
            ObservableList<Appointments> getAllAppts = AppointmentsDAO.getAllAppointments();
            ObservableList<Month> apptMonths = FXCollections.observableArrayList();
            ObservableList<Month> selectedApptMonth = FXCollections.observableArrayList();

            ObservableList<String> apptType = FXCollections.observableArrayList();
            ObservableList<String> selectedAppt = FXCollections.observableArrayList();

            ObservableList<TypeReport> typeReports = FXCollections.observableArrayList();
            ObservableList<MonthReport> monthReports = FXCollections.observableArrayList();

            getAllAppts.forEach(appts -> {
                apptType.add(appts.getApptType());
            });
            getAllAppts.stream().map(appt -> {
                return appt.getApptStart().getMonth();
            }).forEach(apptMonths::add);

            apptMonths.stream().filter(month -> {
               return  !selectedApptMonth.contains(month);
            }).forEach(selectedApptMonth::add);

            for (Appointments appt: getAllAppts){
                String apptApptType = appt.getApptType();
                if(!selectedAppt.contains(apptApptType)){
                    selectedAppt.add(apptApptType);
                }
            }
            for (Month month: selectedApptMonth){
                int monthTotals = Collections.frequency(apptMonths, month);
                String nameMonth = month.name();
                MonthReport apptMonth = new MonthReport(nameMonth, monthTotals);
                monthReports.add(apptMonth);
            }
            appointmentMonthTotal.setItems(monthReports);

            for (String selectedApptType: selectedAppt) {
                String apptTypeMod = selectedApptType;
                int totalType = Collections.frequency(apptType, selectedApptType);
                TypeReport apptTypes = new TypeReport(apptTypeMod, totalType);
                typeReports.add(apptTypes);
            }
            appointmentReportTypeTotal.setItems(typeReports);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills custom report of total number of appointments in each country
     * @throws SQLException
     */
    @FXML public void customerCountrySort() throws SQLException{
        try {
            ObservableList<Reports> countriesCache = ReportsDAO.getCountries();
            ObservableList<Reports> selectedCountries = FXCollections.observableArrayList();

            countriesCache.forEach(selectedCountries::add);
            customerCountryTotal.setItems(selectedCountries);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Button to return user to main screen
     * @param event
     * @throws IOException
     */
    @FXML public void returnButtonAction(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/View/Main.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
