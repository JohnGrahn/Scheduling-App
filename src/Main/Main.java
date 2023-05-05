package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    /**
     * Launches the login screen of the application
     * @param primaryStage
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));
        primaryStage.setTitle("Scheduling App");
        primaryStage.setScene(new Scene(root, 380, 400));
        primaryStage.show();
    }

    /**
     * Connect to SQL database
     * @param args
     */
    public static void main(String[] args) {
        JDBC.makeConnection();
        launch(args);
    }
}
