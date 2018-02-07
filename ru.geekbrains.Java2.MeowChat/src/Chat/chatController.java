package Chat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class chatController {
    @FXML
    TextArea textArea;

    @FXML
    TextArea textField;

    public void sendMsg() {
        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss zzz");
            String file = "src/Chat/resources/sounds/meow.mp3";
            Media sound = new Media(new File(file).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            if (!textField.getText().equals("")) {
                textArea.appendText("[" + simpleDateFormat.format(date) + "]: " + textField.getText() + "\n");
                mediaPlayer.play();
            }
            textField.clear();
            textField.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
