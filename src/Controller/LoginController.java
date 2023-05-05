package Controller;

import DAO.AppointmentsDAO;
import DAO.UsersDAO;
import Model.Appointments;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * LoginController confirms login data, writes login attempts to log, and changes locale/language based on OS settings
 */
public class LoginController implements Initializable {

    @FXML private Label loginLabel;
    @FXML private Label usernameLabel;
    @FXML private TextField loginUsername;
    @FXML private Label passwordLabel;
    @FXML private TextField loginPassword;
    @FXML private Label locationLabel;
    @FXML private TextField loginLocation;
    @FXML private Button loginButton;
    @FXML private Button exitButton;

    /**
     * Login button, on click confirms user and if valid enters program
     * @param event
     * @throws SQLException
     * @throws IOException
     * @throws Exception
     */
    @FXML private void loginButtonAction(ActionEvent event) throws SQLException, IOException, Exception {
        try {
            ObservableList<Appointments> getAllAppointments = AppointmentsDAO.getAllAppointments();
            LocalDateTime earlyApptTimeCheck = LocalDateTime.now().minusMinutes(15);
            LocalDateTime lateApptTimeCheck = LocalDateTime.now().plusMinutes(15);
            LocalDateTime apptStart;
            int getAppointmentID = 0;
            LocalDateTime timeView = null;
            boolean validApptTime = false;

            ResourceBundle resourceBundle = ResourceBundle.getBundle("Languages/login", Locale.getDefault());
            String userInput = loginUsername.getText();
            String passInput = loginPassword.getText();
            int userId = UsersDAO.userConfirm(userInput,passInput);

            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            PrintWriter printOutput = new PrintWriter(fileWriter);

            if (userId > 0) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/View/Main.fxml"));
                Parent parent = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.show();

                printOutput.print("User: " + userInput + " login successful: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

                for (Appointments appt: getAllAppointments) {
                    apptStart = appt.getApptStart();
                    if ((apptStart.isAfter(earlyApptTimeCheck) || apptStart.isEqual(earlyApptTimeCheck)) && (apptStart.isBefore(lateApptTimeCheck) || (apptStart.isEqual(lateApptTimeCheck)))) {
                        getAppointmentID = appt.getAppointmentID();
                        timeView = apptStart;
                        validApptTime = true;
                    }
                }
                if (validApptTime !=false){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment starts within 15 minutes: " + getAppointmentID + " appointment starts: " + timeView);
                    Optional<ButtonType> confirm = alert.showAndWait();
                    System.out.println("Appointment scheduled within 15 minutes");
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "There is not a scheduled appointment.");
                    Optional<ButtonType> confirm = alert.showAndWait();
                    System.out.println("There is not a scheduled appointment.");
                }
            } else if (userId < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(resourceBundle.getString("errorTitle"));
                alert.setContentText(resourceBundle.getString("errorText"));
                alert.show();

                printOutput.print("User: " + userInput + " login attempt failed at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");
            }
            printOutput.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Exit button, on click closes program
     * @param event
     */
    public void exitButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Initialize login screen
     * @param url
     * @param resourceBundle
     */
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);
            ZoneId zoneId = ZoneId.systemDefault();
            loginLocation.setText(String.valueOf(zoneId));

            resourceBundle = ResourceBundle.getBundle("Languages/login",Locale.getDefault());
            loginLabel.setText(resourceBundle.getString("login"));
            usernameLabel.setText(resourceBundle.getString("username"));
            passwordLabel.setText(resourceBundle.getString("password"));
            loginButton.setText(resourceBundle.getString("loginButton"));
            exitButton.setText(resourceBundle.getString("exitButton"));
            locationLabel.setText(resourceBundle.getString("location"));

        }catch (MissingResourceException e) {
            System.out.println("Resource Bundle missing: " + e);
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
