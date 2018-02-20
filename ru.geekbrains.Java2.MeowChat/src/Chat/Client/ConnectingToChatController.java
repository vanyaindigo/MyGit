package Chat.Client;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConnectingToChatController {

    @FXML
    TextField loginField;
    @FXML
    PasswordField passField;
    @FXML
    TextField loginFieldRegister;
    @FXML
    PasswordField passFieldRegister;
    @FXML
    TextField nickFieldRegister;

    private DataOutputStream outputStream;
    private ChatController chatController;
    private Stage dialogStage;


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void onAuthClick() {
        if (outputStream == null) {
            outputStream = chatController.getOutputStream();
        }
        try {
            outputStream.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
            dialogStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRegisterClick() {
        if (outputStream == null) {
            outputStream = chatController.getOutputStream();
        }
        try {
            outputStream.writeUTF("/register " + loginFieldRegister.getText() + " " + passFieldRegister.getText() + " " + nickFieldRegister.getText());
            loginFieldRegister.clear();
            passFieldRegister.clear();
            nickFieldRegister.clear();
            dialogStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
