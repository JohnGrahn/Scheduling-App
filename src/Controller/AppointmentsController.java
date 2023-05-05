package Controller;

import DAO.AppointmentsDAO;
import DAO.ContactsDAO;
import DAO.CustomersDAO;
import DAO.UsersDAO;
import Main.JDBC;
import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static Main.timeUtil.convertUTC;

/**
 * AppointmentsController class has methods for editing, organizing appointments by week and month, and check for overlapping appointments
 */
public class AppointmentsController {

    @FXML private RadioButton allRadioButton;
    @FXML private RadioButton monthRadioButton;
    @FXML private RadioButton weekRadioButton;
    @FXML private TableView<Appointments> AppointmentsTable;
    @FXML private TableColumn <?,?> appointmentIDColumn;
    @FXML private TableColumn <?,?> TitleColumn;
    @FXML private TableColumn <?,?> DescriptionColumn;
    @FXML private TableColumn <?,?> LocationColumn;
    @FXML private TableColumn <?,?> ContactIDColumn;
    @FXML private TableColumn <?,?> TypeColumn;
    @FXML private TableColumn <?,?> StartColumn;
    @FXML private TableColumn <?,?> EndColumn;
    @FXML private TableColumn <?,?> CustomerIDColumn;
    @FXML private TableColumn <?,?> UserIDColumn;
    @FXML private TextField updateAppointmentID;
    @FXML private TextField updateTitle;
    @FXML private TextField updateDescription;
    @FXML private TextField updateLocation;
    @FXML private TextField updateType;
    @FXML private TextField updateCustomerID;
    @FXML private DatePicker newStartDate;
    @FXML private ComboBox <String> newStartTime;
    @FXML private ComboBox <String> newContact;
    @FXML private DatePicker newEndDate;
    @FXML private ComboBox <String> newEndTime;
    @FXML private TextField newUserID;
    @FXML private Button updateAppointmentButton;
    @FXML private Button newAppointmentButton;
    @FXML private Button deleteAppointmentButton;
    @FXML private Button returnMainButton;

    /**
     * Initializes Appointments Table
     * @throws SQLException
     */
    public void initialize() throws SQLException{
        ObservableList<Appointments> allApptsList = AppointmentsDAO.getAllAppointments();

        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        TitleColumn.setCellValueFactory(new PropertyValueFactory<>("apptTitle"));
        DescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("apptDescription"));
        LocationColumn.setCellValueFactory(new PropertyValueFactory<>("apptLocation"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        StartColumn.setCellValueFactory(new PropertyValueFactory<>("apptStart"));
        EndColumn.setCellValueFactory(new PropertyValueFactory<>("apptEnd"));
        CustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        ContactIDColumn.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        UserIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        AppointmentsTable.setItems(allApptsList);
    }

    /**
     * On button click changes are updated and saved to appointment
     * @param event
     */
    @FXML void updateAppointmentAction(ActionEvent event) {
        try {
            Connection connection = JDBC.makeConnection();

            if (!updateTitle.getText().isEmpty() && !updateDescription.getText().isEmpty() && !updateLocation.getText().isEmpty() && !updateType.getText().isEmpty() && newStartDate.getValue() != null && newEndDate.getValue() != null && !newStartTime.getValue().isEmpty() && !newEndTime.getValue().isEmpty() && !updateCustomerID.getText().isEmpty()) {
                ObservableList<Customers> getAllCustomers = CustomersDAO.getAllCustomers(connection);
                ObservableList<Integer> cacheCustIDs = FXCollections.observableArrayList();
                ObservableList<UsersDAO> getAllUsers = UsersDAO.getAllUsers();
                ObservableList<Integer> cacheUserIDs = FXCollections.observableArrayList();
                ObservableList<Appointments> getAllAppts = AppointmentsDAO.getAllAppointments();

                getAllCustomers.stream().map(Customers::getCustomerID).forEach(cacheCustIDs::add);
                getAllUsers.stream().map(Users::getUserID).forEach(cacheUserIDs::add);

                LocalDate localDateStart = newStartDate.getValue();
                LocalDate localDateEnd = newEndDate.getValue();

                DateTimeFormatter minuteHourFormat = DateTimeFormatter.ofPattern("HH:mm");

                LocalTime localTimeStart = LocalTime.parse(newStartTime.getValue(), minuteHourFormat);
                LocalTime localTimeEnd = LocalTime.parse(newEndTime.getValue(), minuteHourFormat);

                LocalDateTime localDateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                LocalDateTime localDateTimeEnd = LocalDateTime.of(localDateEnd, localTimeEnd);

                ZonedDateTime zoneDateTimeStart = ZonedDateTime.of(localDateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zoneDateTimeEnd = ZonedDateTime.of(localDateTimeEnd, ZoneId.systemDefault());

                ZonedDateTime estConvertStartTime = zoneDateTimeStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime estConvertEndTime = zoneDateTimeEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

                if (estConvertStartTime.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) || estConvertStartTime.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) || estConvertEndTime.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) || estConvertEndTime.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue())){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Day is not within business hours (Mon-Fri)");
                    Optional<ButtonType> confirm = alert.showAndWait();
                    System.out.println("not within business hours");
                    return;
                }
                if (estConvertStartTime.toLocalTime().isBefore(LocalTime.of(8,0,0)) || estConvertStartTime.toLocalTime().isAfter(LocalTime.of(22,0,0)) || estConvertEndTime.toLocalTime().isBefore(LocalTime.of(8,0,0)) || estConvertEndTime.toLocalTime().isAfter(LocalTime.of(22,0,0))) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is not within business hours (8am-10pm EST: " + estConvertStartTime.toLocalTime() + " - " + estConvertEndTime.toLocalTime() + " EST");
                    Optional<ButtonType> confirm = alert.showAndWait();
                    System.out.println("time not within business hours");
                    return;
                }
                int updateCustID = Integer.parseInt(updateCustomerID.getText());
                int apptID = Integer.parseInt(updateAppointmentID.getText());

                if (localDateTimeStart.isAfter(localDateTimeEnd)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start time cannot be after end time");
                    Optional<ButtonType> confirm = alert.showAndWait();
                    System.out.println("Start time cannot be after end time");
                    return;
                }
                if (localDateTimeStart.isEqual(localDateTimeEnd)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start time and end time cannot be equal");
                    Optional<ButtonType> confirm = alert.showAndWait();
                    System.out.println("Start time and end time cannot be equal");
                    return;
                }


                for(Appointments appointments: getAllAppts) {
                    LocalDateTime confirmStart = appointments.getApptStart();
                    LocalDateTime confirmEnd = appointments.getApptEnd();

                    if ((updateCustID == appointments.getCustomerID()) && (apptID != appointments.getAppointmentID()) && (localDateTimeStart.isBefore(confirmStart)) && (localDateTimeEnd.isAfter(confirmEnd))){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Appointment overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Appointment overlaps with an existing appointment");
                        return;
                    }
                    if ((updateCustID == appointments.getCustomerID()) && (apptID != appointments.getAppointmentID()) && (localDateTimeStart.isEqual(confirmStart)) && (localDateTimeEnd.isEqual(confirmEnd))){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Appointment overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Appointment overlaps with an existing appointment");
                        return;
                    }
                    if ((updateCustID == appointments.getCustomerID()) && (apptID != appointments.getAppointmentID()) && (localDateTimeStart.isAfter(confirmStart)) && (localDateTimeStart.isBefore(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Start time overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Start time overlaps with an existing appointment");
                        return;
                    }
                    if((updateCustID == appointments.getCustomerID()) && (apptID != appointments.getAppointmentID()) && (localDateTimeEnd.isAfter(confirmStart)) && (localDateTimeEnd.isBefore(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: End time overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("End time overlaps with an existing appointment");
                        return;
                    }
                    if ((updateCustID == appointments.getCustomerID()) && (apptID != appointments.getAppointmentID()) && (localDateTimeEnd.isBefore(confirmStart)) && (localDateTimeEnd.isEqual(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Appointment overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Appointment overlaps with an existing appointment");
                        return;
                    }
                    if ((updateCustID == appointments.getCustomerID()) && (apptID != appointments.getAppointmentID()) && (localDateTimeStart.isEqual(confirmStart)) && (localDateTimeEnd.isAfter(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Appointment overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Appointment overlaps with an existing appointment");
                        return;
                    }
                }
                String apptStartDate = newStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String apptStartTime = newStartTime.getValue();
                String apptEndDate = newEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String apptEndTime = newEndTime.getValue();
                String utcStart = convertUTC(apptStartDate + " " + apptStartTime + ":00");
                String utcEnd = convertUTC(apptEndDate + " " + apptEndTime + ":00");

                String sqlUpdate = "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

                JDBC.makePreparedStatement(sqlUpdate, JDBC.getConnection());
                PreparedStatement preparedStatement = JDBC.getPreparedStatement();
                preparedStatement.setInt(1, Integer.parseInt(updateAppointmentID.getText()));
                preparedStatement.setString(2, updateTitle.getText());
                preparedStatement.setString(3, updateDescription.getText());
                preparedStatement.setString(4, updateLocation.getText());
                preparedStatement.setString(5, updateType.getText());
                preparedStatement.setString(6, utcStart);
                preparedStatement.setString(7, utcEnd);
                preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(9, "admin");
                preparedStatement.setInt(10, Integer.parseInt(updateCustomerID.getText()));
                preparedStatement.setInt(11, Integer.parseInt(newUserID.getText()));
                preparedStatement.setInt(12, Integer.parseInt(ContactsDAO.contIDSearch(newContact.getValue())));
                preparedStatement.setInt(13, Integer.parseInt(updateAppointmentID.getText()));

                System.out.println("ps " + preparedStatement);
                preparedStatement.execute();
                ObservableList<Appointments> allApptsList = AppointmentsDAO.getAllAppointments();
                AppointmentsTable.setItems(allApptsList);

            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Customer ID and User ID must match values in database");
            Optional<ButtonType> confirm = alert.showAndWait();
        }
    }

    /**
     * Loads selected appointment to be edited or deleted
     * Lambda statement to populate allContNames with contact data
     */
    @FXML void loadAppointments() {
        try {
            JDBC.makeConnection();
            Appointments selectedAppt = AppointmentsTable.getSelectionModel().getSelectedItem();

            if (selectedAppt != null) {
                ObservableList<Contacts> contObservableList = ContactsDAO.getAllContacts();
                ObservableList<String> allContNames = FXCollections.observableArrayList();
                String displayContName = "";

                contObservableList.forEach(contacts -> allContNames.add(contacts.getContactName()));
                newContact.setItems(allContNames);

                for (Contacts cont: contObservableList) {
                    if (selectedAppt.getContactID() == cont.getContactID()) {
                        displayContName = cont.getContactName();
                    }
                }
                updateAppointmentID.setText(String.valueOf(selectedAppt.getAppointmentID()));
                updateTitle.setText(String.valueOf(selectedAppt.getApptTitle()));
                updateDescription.setText(String.valueOf(selectedAppt.getApptDescription()));
                updateLocation.setText(String.valueOf(selectedAppt.getApptLocation()));
                updateType.setText(String.valueOf(selectedAppt.getApptType()));
                updateCustomerID.setText(String.valueOf(selectedAppt.getCustomerID()));
                newStartDate.setValue(selectedAppt.getApptStart().toLocalDate());
                newEndDate.setValue(selectedAppt.getApptEnd().toLocalDate());
                newStartTime.setValue(String.valueOf(selectedAppt.getApptStart().toLocalTime()));
                newEndTime.setValue(String.valueOf(selectedAppt.getApptEnd().toLocalTime()));
                newUserID.setText(String.valueOf(selectedAppt.getUserID()));
                newContact.setValue(displayContName);

                ObservableList<String> apptTimes = FXCollections.observableArrayList();

                LocalTime firstAppt = LocalTime.MIN.plusHours(8);
                LocalTime lastAppt = LocalTime.MAX.minusHours(1).minusMinutes(45);

                if (!firstAppt.equals(0) || !lastAppt.equals(0)) {
                    while (firstAppt.isBefore(lastAppt)) {
                        apptTimes.add(String.valueOf(firstAppt));
                        firstAppt = firstAppt.plusMinutes(15);
                    }
                }
                newStartTime.setItems(apptTimes);
                newEndTime.setItems(apptTimes);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads New Appointment screen for new appointment creation
     * @param event
     * @throws IOException
     */
    @FXML void createNewAppointmentAction(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/View/NewAppointment.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes selected appointment
     * @param event
     * @throws IOException
     */
    @FXML void deleteAppointmentAction(ActionEvent event) throws IOException {
        try {
            Connection connection = JDBC.makeConnection();
            int deleteApptID = AppointmentsTable.getSelectionModel().getSelectedItem().getAppointmentID();
            String deleteApptType = AppointmentsTable.getSelectionModel().getSelectedItem().getApptType();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete appointment associated with appointment ID: " + deleteApptID + " and appointment type " + deleteApptType);
            Optional<ButtonType> confirm = alert.showAndWait();
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                AppointmentsDAO.apptDelete(deleteApptID, connection);

                ObservableList<Appointments> AppointmentsList = AppointmentsDAO.getAllAppointments();
                AppointmentsTable.setItems(AppointmentsList);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Radio selection to show all appointments
     * @param event
     * @throws SQLException
     */
    @FXML void selectAllAppointments(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allApptsList = AppointmentsDAO.getAllAppointments();

            if (allApptsList != null) for (Appointments appts : allApptsList) {
                AppointmentsTable.setItems(allApptsList);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Radio selection to show appointments by month
     * @param event
     * @throws SQLException
     */
    @FXML void selectMonthAppointments(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allApptsList = AppointmentsDAO.getAllAppointments();
            ObservableList<Appointments> apptsMonth = FXCollections.observableArrayList();

            LocalDateTime selectedMonthStart = LocalDateTime.now().minusMonths(1);
            LocalDateTime selectedMonthEnd = LocalDateTime.now().plusMonths(1);

            if (allApptsList != null) allApptsList.forEach(appt -> {
                if (appt.getApptEnd().isAfter(selectedMonthStart) && appt.getApptEnd().isBefore(selectedMonthEnd)) {
                    apptsMonth.add(appt);
                }
                AppointmentsTable.setItems(apptsMonth);
            });

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Radio button to view appointment week
     * @param event
     * @throws SQLException
     */
    @FXML void selectWeekAppointments(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointments> allApptsList = AppointmentsDAO.getAllAppointments();
            ObservableList<Appointments> apptsWeek = FXCollections.observableArrayList();

            LocalDateTime selectedWeekStart = LocalDateTime.now().minusWeeks(1);
            LocalDateTime selectedWeekEnd = LocalDateTime.now().plusWeeks(1);

            if (allApptsList != null) allApptsList.forEach(appt -> {
                if (appt.getApptEnd().isAfter(selectedWeekStart) && appt.getApptEnd().isBefore(selectedWeekEnd)) {
                    apptsWeek.add(appt);
                }
                AppointmentsTable.setItems(apptsWeek);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Button to return to Main Menu
     * @param event
     * @throws IOException
     */
    @FXML void returnMainScreen(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/View/Main.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
