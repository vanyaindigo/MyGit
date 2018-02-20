package Chat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;


public class Server {

    private Vector<ServerThread> clients;
    private ServerSocket serverSocket = null;
    private Socket socket = null;

    Server() throws SQLException {
        clients = new Vector<>();
        try {
            AuthService.connect();
            serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен, ожидаем подключения...");
            while (true) {
                socket = serverSocket.accept();
                new ServerThread(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка сервера");
        }
    }


    // Рассылаем сообщение по всем клиентам
    public void broadcastMsg(ServerThread from, String msg) {
        for (ServerThread o : clients) {
            if (!o.checkBlackList(from.getName())) {
                try {
                    o.sendMsg(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Ошибка отправки сообщения");
                }
            }
        }
    }

    private void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        for (ServerThread o : clients) {
            sb.append(o.getName()).append(" ");
        }
        String out = sb.toString();
        for (ServerThread o : clients) {
            o.sendMsg(out);
        }
    }

    public void fullClientList() throws SQLException {
        for (ServerThread o : clients) {
            if (o.getAdminStatus()) o.sendMsg(AuthService.getFullUserList());
        }
    }

    // Завершаем работу сервереа
    private void closeServer() {
        try {
            assert socket != null;
            socket.close();
            serverSocket.close();
            AuthService.disconnect();
            System.out.println("Сервер отключен");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Ошибка отключения сервера");
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ServerThread o : clients) {
            if (o.getName().equals(nick)) return true;
        }
        return false;
    }

    public synchronized boolean kickUserByNick(String nick) {
        for (ServerThread o : clients) {
            if (o.getName().equals(nick)) {
                o.sendMsg("/end");
                return true;
            }
        }
        return false;
    }

    public synchronized void unsubscribe(ServerThread o) {
        clients.remove(o);
        broadcastClientList();
        if (clients.isEmpty()) closeServer();
    }

    public synchronized void subscribe(ServerThread o) {
        clients.add(o);
        broadcastClientList();
    }

    public void sendPersonalMsg(ServerThread from, String nickTo, String msg) {
        for (ServerThread o : clients) {
            if (o.getName().equals(nickTo)) {
                o.sendMsg("From " + from.getName() + ": " + msg);
                from.sendMsg("To " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден в чате");
    }
}
