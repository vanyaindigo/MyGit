package Chat.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;


public class RootLayoutController {

    private MeowChat meowChat;
    private ChatController chatController;

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void setMeowChat(MeowChat meowChat) {
        this.meowChat=meowChat;
    }

    @FXML
    private void getConnect() {
        if (!chatController.getAuthorised()) {
            meowChat.showConnectWindow();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Meow Chat");
            alert.setHeaderText("Вы уже авторизованы");
            alert.setContentText("Повторная авторизация недопустима");
            alert.showAndWait();
        }
    }

    @FXML
    private void administer() {
        meowChat.showAdministerWindow();
        chatController.msgOut("/clientfull");
    }

    @FXML
    public void privateMessages() {
        meowChat.showPrivateWindow();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Meow Chat");
        alert.setHeaderText("About");
        alert.setContentText("Author: Ivan Mishchenko\nCopyright (c) 2018");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        chatController.disconnect();
    }
}
