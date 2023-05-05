package Controller;

import DAO.AppointmentsDAO;
import DAO.ContactsDAO;
import DAO.CustomersDAO;
import DAO.UsersDAO;
import Main.JDBC;
import Model.Appointments;
import Model.Contacts;
import Model.Customers;
import Model.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
 * NewAppointmentController class for creating new Appointments
 */
public class NewAppointmentController {
    @FXML
    private TextField newAppointmentID;
    @FXML
    private TextField newApptTitle;
    @FXML
    private TextField newApptDescription;
    @FXML
    private TextField newApptLocation;
    @FXML
    private TextField newApptType;
    @FXML
    private DatePicker newApptStartDate;
    @FXML
    private ComboBox<String> newApptStartTime;
    @FXML
    private DatePicker newApptEndDate;
    @FXML
    private ComboBox<String> newApptEndTime;
    @FXML
    private TextField newApptCustomerID;
    @FXML
    private TextField newApptUserID;
    @FXML
    private ComboBox<String> newApptContact;
    @FXML
    private Button newApptSaveButton;
    @FXML
    private Button newApptCancelButton;

    /**
     * Initializes lists and combo boxes for appointment times
     * Contains Lambda statement to replace loop for adding contName to List
     * @throws SQLException
     */
    @FXML
    void initialize() throws SQLException {
        ObservableList<Contacts> contObservableList = ContactsDAO.getAllContacts();
        ObservableList<String> allContNames = FXCollections.observableArrayList();

        contObservableList.forEach(conts -> allContNames.add(conts.getContactName()));
        ObservableList<String> apptTimes = FXCollections.observableArrayList();

        LocalTime firstAppt = LocalTime.MIN.plusHours(8);
        LocalTime lastAppt = LocalTime.MAX.minusHours(1).minusMinutes(45);

        if (!firstAppt.equals(0) || !lastAppt.equals(0)) {
            while (firstAppt.isBefore(lastAppt)) {
                apptTimes.add(String.valueOf(firstAppt));
                firstAppt = firstAppt.plusMinutes(15);
            }
        }
        newApptStartTime.setItems(apptTimes);
        newApptEndTime.setItems(apptTimes);
        newApptContact.setItems(allContNames);


    }

    /**
     * On button click saves new appointment
     * @param event
     * @throws IOException
     */
    @FXML
    void newApptSaveAction(ActionEvent event) throws IOException {
        try {
            Connection connection = JDBC.makeConnection();

            if (!newApptTitle.getText().isEmpty() && !newApptDescription.getText().isEmpty() && !newApptLocation.getText().isEmpty() && !newApptType.getText().isEmpty() && newApptStartDate.getValue() != null && newApptEndDate.getValue() != null && !newApptStartTime.getValue().isEmpty() && !newApptEndTime.getValue().isEmpty() && !newApptCustomerID.getText().isEmpty()) {
                ObservableList<Customers> getAllCustomers = CustomersDAO.getAllCustomers(connection);
                ObservableList<Integer> cacheCustIDs = FXCollections.observableArrayList();
                ObservableList<UsersDAO> getAllUsers = UsersDAO.getAllUsers();
                ObservableList<Integer> cacheUserIDs = FXCollections.observableArrayList();
                ObservableList<Appointments> getAllAppts = AppointmentsDAO.getAllAppointments();

                getAllCustomers.stream().map(Customers::getCustomerID).forEach(cacheCustIDs::add);
                getAllUsers.stream().map(Users::getUserID).forEach(cacheUserIDs::add);

                LocalDate localDateStart = newApptStartDate.getValue();
                LocalDate localDateEnd = newApptEndDate.getValue();

                DateTimeFormatter minuteHourFormat = DateTimeFormatter.ofPattern("HH:mm");
                String apptStartDate = newApptStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String apptStartTime = newApptStartTime.getValue();

                String apptEndDate = newApptEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String apptEndTime = newApptEndTime.getValue();

                LocalTime localTimeStart = LocalTime.parse(newApptStartTime.getValue(), minuteHourFormat);
                LocalTime localTimeEnd = LocalTime.parse(newApptEndTime.getValue(), minuteHourFormat);

                LocalDateTime localDateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                LocalDateTime localDateTimeEnd = LocalDateTime.of(localDateEnd, localTimeEnd);

                ZonedDateTime zoneDateTimeStart = ZonedDateTime.of(localDateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zoneDateTimeEnd = ZonedDateTime.of(localDateTimeEnd, ZoneId.systemDefault());

                ZonedDateTime estConvertStartTime = zoneDateTimeStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime estConvertEndTime = zoneDateTimeEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

                String utcStart = convertUTC(apptStartDate + " " + apptStartTime + ":00");
                String utcEnd = convertUTC(apptEndDate + " " + apptEndTime + ":00");

                LocalTime apptStartTimeConfirm = estConvertStartTime.toLocalTime();
                LocalTime apptEndTimeConfirm = estConvertEndTime.toLocalTime();

                DayOfWeek apptStartWeekDayConfirm = estConvertStartTime.toLocalDate().getDayOfWeek();
                DayOfWeek apptEndWeekDayConfirm = estConvertEndTime.toLocalDate().getDayOfWeek();

                int apptStartDayIntConfirm = apptStartWeekDayConfirm.getValue();
                int apptEndDayIntConfirm = apptEndWeekDayConfirm.getValue();

                int bizWorkDayStart = DayOfWeek.MONDAY.getValue();
                int bizWorkDayEnd = DayOfWeek.FRIDAY.getValue();

                LocalTime bizEstStartTime = LocalTime.of(8, 0, 0);
                LocalTime bizEstEndTime = LocalTime.of(22, 0, 0);

                if (apptStartDayIntConfirm < bizWorkDayStart || apptStartDayIntConfirm > bizWorkDayEnd || apptEndDayIntConfirm < bizWorkDayStart || apptEndDayIntConfirm > bizWorkDayEnd) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Day is not within business hours (Mon-Fri)");
                    Optional<ButtonType> confirm = alert.showAndWait();
                    System.out.println("not within business hours");
                    return;
                }

                if (apptStartTimeConfirm.isBefore(bizEstStartTime) || apptStartTimeConfirm.isAfter(bizEstEndTime) || apptEndTimeConfirm.isBefore(bizEstStartTime) || apptEndTimeConfirm.isAfter(bizEstEndTime)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is not within business hours (8am-10pm EST: " + apptStartTimeConfirm + " - " + apptEndTimeConfirm + " EST");
                    Optional<ButtonType> confirm = alert.showAndWait();
                    System.out.println("time not within business hours");
                    return;
                }

                int newApptID = Integer.parseInt(String.valueOf((int) (Math.random() * 100)));
                int custID = Integer.parseInt(newApptCustomerID.getText());

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

                for (Appointments appointments : getAllAppts) {
                    LocalDateTime confirmStart = appointments.getApptStart();
                    LocalDateTime confirmEnd = appointments.getApptEnd();

                    if ((custID == appointments.getCustomerID()) && (newApptID != appointments.getAppointmentID()) && (localDateTimeStart.isBefore(confirmStart)) && (localDateTimeEnd.isAfter(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Appointment overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Appointment overlaps with an existing appointment");
                        return;
                    }
                    if ((custID == appointments.getCustomerID()) && (newApptID != appointments.getAppointmentID()) && (localDateTimeStart.isEqual(confirmStart)) && (localDateTimeEnd.isEqual(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Appointment overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Appointment overlaps with an existing appointment");
                        return;
                    }
                    if ((custID == appointments.getCustomerID()) && (newApptID != appointments.getAppointmentID()) && (localDateTimeStart.isAfter(confirmStart)) && (localDateTimeStart.isBefore(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Start time overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Start time overlaps with an existing appointment");
                        return;
                    }
                    if ((custID == appointments.getCustomerID()) && (newApptID != appointments.getAppointmentID()) && (localDateTimeEnd.isAfter(confirmStart)) && (localDateTimeEnd.isBefore(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: End time overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("End time overlaps with an existing appointment");
                        return;
                    }
                    if ((custID == appointments.getCustomerID()) && (newApptID != appointments.getAppointmentID()) && (localDateTimeEnd.isBefore(confirmStart)) && (localDateTimeEnd.isEqual(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Appointment overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Appointment overlaps with an existing appointment");
                        return;
                    }
                    if ((custID == appointments.getCustomerID()) && (newApptID != appointments.getAppointmentID()) && (localDateTimeStart.isEqual(confirmStart)) && (localDateTimeEnd.isAfter(confirmEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Appointment overlaps with an existing appointment");
                        Optional<ButtonType> confirm = alert.showAndWait();
                        System.out.println("Appointment overlaps with an existing appointment");
                        return;
                    }
                }

                String sqlInsert = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                JDBC.makePreparedStatement(sqlInsert, JDBC.getConnection());
                PreparedStatement preparedStatement = JDBC.getPreparedStatement();
                preparedStatement.setInt(1, newApptID);
                preparedStatement.setString(2, newApptTitle.getText());
                preparedStatement.setString(3, newApptDescription.getText());
                preparedStatement.setString(4, newApptLocation.getText());
                preparedStatement.setString(5, newApptType.getText());
                preparedStatement.setString(6, utcStart);
                preparedStatement.setString(7, utcEnd);
                preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(9, "admin");
                preparedStatement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setInt(11, 1);
                preparedStatement.setInt(12, Integer.parseInt(newApptCustomerID.getText()));
                preparedStatement.setInt(13, Integer.parseInt(ContactsDAO.contIDSearch(newApptContact.getValue())));
                preparedStatement.setInt(14, Integer.parseInt(ContactsDAO.contIDSearch(newApptUserID.getText())));

                preparedStatement.execute();
            }
            Parent parent = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();


        } catch (SQLException exception) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Error: Customer ID and User ID must match values in database");
                Optional<ButtonType> confirm = alert.showAndWait();


            }

        }


    /**
     * On button click/ action the user is returned to the appointments screen
     * @throws IOException
     */
    @FXML public void newApptCancelAction(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}