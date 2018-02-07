package Chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MeowChat extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("chatWindow.fxml"));
        primaryStage.setTitle("Meow chat");
        primaryStage.getIcons().add(new Image(MeowChat.class.getResourceAsStream("resources/images/if_paw_1608784.png")));
        Scene scene = new Scene(root, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}
