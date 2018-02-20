package Chat.Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatController {

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;
    private boolean authorised;
    private PrivateWindowController privateWindowController;
    private MeowChat meowChat;
    private boolean adminStatus;
    private AdminWindowController adminWindowController;



    @FXML
    TextArea textArea;

    @FXML
    TextArea textField;

    @FXML
    HBox bottomPanel;

    @FXML
    ListView<String> clientList;

    public void setMeowChat(MeowChat meowChat) {
        this.meowChat = meowChat;
    }

    public void setPrivateWindowController(PrivateWindowController privateWindowController) {
        this.privateWindowController = privateWindowController;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public boolean getAuthorised() {
        return authorised;
    }

    private void setAuthorised(boolean authorised) {
        this.authorised = authorised;
        if (!authorised) {
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
        } else {
            clientList.setVisible(true);
            clientList.setManaged(true);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
        }
    }

    public void disconnect() {
        try {
            if (!socket.isClosed()) {
                outputStream.writeUTF("/end");
                System.out.println("Клиент отключился");
                setAuthorised(false);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка отключения клиента");
        }
    }


    @FXML
    private void initialize() {
        final String ipAddress = "localhost";
        final int port = 8189;
        try {
            socket = new Socket(ipAddress, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            setAuthorised(false);
            Thread t = new Thread(() -> {
                String string;
                try {
                    while (true) {
                        string = inputStream.readUTF();
                        if (string.startsWith("/authok") || string.startsWith("/registerok")) {
                            String[] parts = string.split(" ");
                            adminStatus = parts[1].equals("true");
                            System.out.println("Клиент подключился " + adminStatus);
                            setAuthorised(true);
                            break;
                        }
                        messageOnScreen(string);
                    }
                    while (true) {
                        string = inputStream.readUTF();
                        if (string.startsWith("/")) {
                            if (string.startsWith("/end")) {
                                disconnect();
                                break;
                            }
                            if (string.startsWith("/clientlist")) {
                                String[] parts = string.split(" ");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < parts.length; i++) {
                                        clientList.getItems().add(parts[i]);
                                    }
                                });
                            }
                            if (string.startsWith("/clientfull")) {
                                adminWindowController.updateClientList(string);
                            }
                        } else if (string.startsWith("From") || string.startsWith("To")) {
                            if (!meowChat.isPrivateClosed()) {
                                privateWindowController.messageOnScreen(string);
                            } else {
                                String finalString = string;
                                Platform.runLater(() -> {
                                    meowChat.showPrivateWindow();
                                    privateWindowController.messageOnScreen(finalString);
                                });
                            }
                        } else messageOnScreen(string);
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка чтения потока");
                }
                finally {
                    try {
                        if (!socket.isClosed()) {
                            socket.close();
                            System.out.println("Клиент отключился");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Ошибка отключения клиента");
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    // Вывод поступившего сообщения в окно чата
    private void messageOnScreen(String text) {
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

    // Отправка сообщения на сервер
    private void msgOut() {
        try {
            outputStream.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void msgOut(String string) {
        try {
            outputStream.writeUTF(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Отправка сообщения по нажатию кнопки "Send"
    public void sendMsg() {
        if (!textField.getText().isEmpty()) {
            msgOut();
        }
    }

    // Отправка сообщения по нажатию Alt+Enter в строке ввода
    public void sendMsgEnter(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ENTER) && e.isAltDown() && !textField.getText().isEmpty()) {
            msgOut();
        }
    }

    public boolean getAdminStatus() {
        return adminStatus;
    }

    public void setAdminWindowController(AdminWindowController adminWindowController) {
        this.adminWindowController = adminWindowController;
    }
}
