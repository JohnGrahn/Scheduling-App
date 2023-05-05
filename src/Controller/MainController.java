package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


import java.io.IOException;

/**
 * MainController controls the main screen of the program with navigation buttons for Appointments, Customers, and Reports
 */

public class MainController {
    @FXML private Button mainAppointmentButton;
    @FXML private Button mainCustomerButton;
    @FXML private Button mainReportButton;
    @FXML private Button mainExitButton;

    /**
     * Loads Appointments Screen
     * @param event
     * @throws IOException
     */
   @FXML void MainAppointmentAction(ActionEvent event) throws IOException {
        Parent apptView = FXMLLoader.load(getClass().getResource("/View/Appointments.fxml"));
        Scene scene = new Scene(apptView);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads Customers Screen
     * @param event
     * @throws IOException
     */
   @FXML void mainCustomerAction(ActionEvent event) throws IOException{
       Parent custView = FXMLLoader.load(getClass().getResource("/View/Customers.fxml"));
       Scene scene = new Scene(custView);
       Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
       stage.setScene(scene);
       stage.show();
    }

    /**
     * Loads Reports Screen
     * @param event
     * @throws IOException
     */
    @FXML void mainReportAction(ActionEvent event) throws IOException {
        Parent reportView = FXMLLoader.load(getClass().getResource("/View/Reports.fxml"));
        Scene scene = new Scene(reportView);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Closes program
     * @param eventExit
     */
    @FXML void mainExitAction(ActionEvent eventExit) {
       Stage stage = (Stage) ((Node) eventExit.getSource()).getScene().getWindow();
       stage.close();
    }
}
