package Chat.Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class AdminWindowController {

    private ChatController chatController;

    @FXML
    ListView<String> clientList;
    @FXML
    TextField nickField;

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    @FXML
    private void kickUser() {
        if (!nickField.getText().isEmpty()) {
            chatController.msgOut("/logout " + nickField.getText() );
            nickField.clear();
            nickField.requestFocus();
        }
    }

    public void updateClientList(String string) {
        String[] parts = string.split(" ");
        Platform.runLater(() -> {
            clientList.getItems().clear();
            for (int i = 1; i < parts.length; i++) {
                clientList.getItems().add(parts[i]);
            }
        });
    }

    @FXML
    private void moveToBlacklist() {
        if (!nickField.getText().isEmpty()) {
            chatController.msgOut("/blacklist " + nickField.getText() );
            nickField.clear();
            nickField.requestFocus();
        }
    }

    @FXML
    private void removeFromBlacklist() {
        if (!nickField.getText().isEmpty()) {
            chatController.msgOut("/blackremove " + nickField.getText() );
            nickField.clear();
            nickField.requestFocus();
        }
    }
}
