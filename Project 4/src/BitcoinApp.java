

import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class BitcoinApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root =
                    loader.load(getClass().getResource("BitcoinGUI.fxml").openStream());
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            root.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}