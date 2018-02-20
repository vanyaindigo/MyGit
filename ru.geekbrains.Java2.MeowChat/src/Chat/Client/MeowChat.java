package Chat.Client;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MeowChat extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ChatController chatController;
    private RootLayoutController rootLayoutController;
    private boolean privateClosed = true;


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Meow chat");
        this.primaryStage.getIcons().add(new Image(MeowChat.class.getResourceAsStream("resources/images/if_paw_1608784.png")));
        initRootLayout();
        showChatWindow();
    }
// Меню окна чата
    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MeowChat.class.getResource("RootLayout.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            rootLayoutController = loader.getController();
            rootLayoutController.setMeowChat(this);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// Основное окно чата
    private void showChatWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MeowChat.class.getResource("chatWindow.fxml"));
            AnchorPane page = loader.load();
            chatController = loader.getController();
            chatController.setMeowChat(this);
            rootLayoutController.setChatController(chatController);
            rootLayout.setCenter(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// Отображение окна подключения (регистрация/логин)
    public void showConnectWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MeowChat.class.getResource("ConnectingToChat.fxml"));
            AnchorPane page = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Подключение к серверу");
            stage.getIcons().add(new Image(MeowChat.class.getResourceAsStream("resources/images/if_paw_1608784.png")));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            ConnectingToChatController connectingToChatController = loader.getController();
            connectingToChatController.setChatController(chatController);
            connectingToChatController.setDialogStage(stage);
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.show();
            PauseTransition delay = new PauseTransition(Duration.seconds(120));
            delay.setOnFinished( event -> {
                if (!chatController.getAuthorised()) {
                    stage.close();
                    chatController.disconnect();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Время истекло =(");
                    alert.setHeaderText("Вы не авторизованы");
                    alert.setContentText("Время для авторизации истекло");
                    alert.show();
                }
            } );
            delay.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// Окно приватных сообщений
    public void showPrivateWindow() {
        if (!chatController.getAuthorised()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Окно приватных сообщений");
            alert.setHeaderText("Вы не авторизованы");
            alert.setContentText("Для отправки приватных сообщений требуется авторизация");
            alert.showAndWait();
        } else try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MeowChat.class.getResource("PrivateWindow.fxml"));
            AnchorPane page = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Окно приватных сообщений");
            stage.getIcons().add(new Image(MeowChat.class.getResourceAsStream("resources/images/if_paw_1608784.png")));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            PrivateWindowController privateWindowController = loader.getController();
            privateWindowController.setChatController(chatController);
            chatController.setPrivateWindowController(privateWindowController);
            Scene scene = new Scene(page);
            stage.setScene(scene);
            setPrivateClosed(false);
            stage.show();
            stage.setOnCloseRequest(event -> setPrivateClosed(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// Окно администратора
    public void showAdministerWindow() {
        if (!chatController.getAuthorised() || !chatController.getAdminStatus()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Админка");
            alert.setHeaderText("Недостаточно прав");
            alert.setContentText("Для работы в админке у вас должны быть права администратора");
            alert.show();
        } else try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MeowChat.class.getResource("AdminWindow.fxml"));
            AnchorPane page = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Админка");
            stage.getIcons().add(new Image(MeowChat.class.getResourceAsStream("resources/images/if_paw_1608784.png")));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            AdminWindowController adminWindowController = loader.getController();
            adminWindowController.setChatController(chatController);
            chatController.setAdminWindowController(adminWindowController);
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        launch(args);
    }

    private void setPrivateClosed(boolean privateClosed) {
        this.privateClosed = privateClosed;
    }

    public boolean isPrivateClosed() {
        return privateClosed;
    }
}
