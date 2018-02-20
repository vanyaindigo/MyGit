package Chat.Server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ServerThread {

    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private boolean adminStatus;
    private Server server;
    private Socket socket;

    public String getName() {
        return name;
    }

    public boolean getAdminStatus() {
        return adminStatus;
    }

    ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try {
                    while (true) {
                        String w = in.readUTF();
                        if (w.startsWith("/auth")) {
                            String[] parts = w.split("\\s");
                            String nick = AuthService.getNickByLoginAndPass(parts[1], parts[2]);
                            if (nick != null) {
                                if (!server.isNickBusy(nick)) {
                                    name = nick;
                                    adminStatus = AuthService.getAdminByNick(name);
                                    sendMsg("/authok " + adminStatus);
                                    server.broadcastMsg(this, name + " зашел в чат");
                                    sendMsg(name + ", вы подключились к серверу");
                                    server.subscribe(this);
                                    System.out.println("Клиент подключился " + adminStatus);
                                    break;
                                } else sendMsg("Учетная запись уже используется");
                            } else sendMsg("Неверные логин/пароль");
                        } if (w.startsWith("/register")) {
                            String[] parts = w.split("\\s");
                            if (AuthService.addUser(parts[1], parts[2], parts[3])) {
                                name=parts[3];
                                adminStatus = AuthService.getAdminByNick(name);
                                sendMsg("/registerok " + adminStatus);
                                server.fullClientList();
                                server.broadcastMsg(this, name + " зашел в чат");
                                sendMsg(name + ", вы подключились к серверу");
                                server.subscribe(this);
                                System.out.println("Клиент подключился");
                                break;
                            }
                        } else sendMsg("Вход не удался");
                    }
                    while (true) {
                        String w = in.readUTF();
                        if (w.startsWith("/")) {
                            if (w.startsWith("/end")) {
                                sendMsg("Вы отключились от сервера");
                                break;
                            } // реализация отправки в приват
                            if (w.startsWith("/w")) {
                                String[] parts = w.split("\\s");
                                server.sendPersonalMsg(this, parts[1], w.replaceAll(parts[0] + " " + parts[1], ""));
                            }
                            if (w.startsWith("/blacklist") && adminStatus) {
                                String[] parts = w.split("\\s");
                                if (AuthService.setBlacklistByNick(parts[1], true)) {
                                    sendMsg("Вы добавили пользователя " + parts[1] + " в черный список");
                                    server.fullClientList();
                                } else sendMsg("Ошибка добавления пользователя " + parts[1] + " в черный список");
                            }
                            if (w.startsWith("/blackremove") && adminStatus) {
                                String[] parts = w.split("\\s");
                                if (AuthService.setBlacklistByNick(parts[1], false)) {
                                    sendMsg("Вы удалили пользователя " + parts[1] + " из черного списка");
                                    server.fullClientList();
                                } else sendMsg("Ошибка удаления пользователя " + parts[1] + " из черного списка");
                            }
                            if (w.startsWith("/logout") && adminStatus) {
                                String[] parts = w.split("\\s");
                                if (server.kickUserByNick(parts[1])) {
                                    sendMsg("Вы удалили пользователя " + parts[1] + " из чата");
                                } else sendMsg("Ошибка удаления пользователя " + parts[1] + " из чата");
                            }
                            if (w.startsWith("/clientfull") && adminStatus) {
                                server.fullClientList();
                            }
                        } else server.broadcastMsg(this, name + ": " + w);
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка сервер.run");
                } finally {
                    disconnect(this);
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы создания обработчика клиента");
        }
    }

    private void disconnect(ServerThread o) {
        server.unsubscribe(o);
        server.broadcastMsg(o, o.getName() + " вышел из чата");
        try {
            out.close();
            in.close();
            socket.close();
            System.out.println("Клиент отключен");
        } catch (IOException exc) {
            System.out.println("Ошибка отключения клиента");
        }
    }

    public void sendMsg(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBlackList(String name) {
        try {
            return AuthService.getBlacklistByNick(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
