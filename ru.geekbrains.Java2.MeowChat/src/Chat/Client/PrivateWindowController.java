package Chat.Client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrivateWindowController {

    @FXML
    TextArea textArea;
    @FXML
    TextField nickField;
    @FXML
    TextField msgField;
    private ChatController chatController;

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void messageOnScreen(String text) {
        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss zzz");
            String file = "src/Chat/Client/resources/sounds/meow.mp3";
            Media sound = new Media(new File(file).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            textArea.appendText("[" + simpleDateFormat.format(date) + "]: " + text + "\n");
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        if (!msgField.getText().isEmpty() && !nickField.getText().isEmpty()) {
            chatController.msgOut("/w " + nickField.getText() + " " + msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        }
    }
}
