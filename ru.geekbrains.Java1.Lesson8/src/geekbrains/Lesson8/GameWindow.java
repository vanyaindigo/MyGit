package geekbrains.Lesson8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {
    private static final int WIN_HEIGHT=555; // высота окна
    private static final int WIN_WIDTH=507; // ширина окна
    private static final int WIN_POS_X=800; // начальная координата Х
    private static final int WIN_POS_Y=300; // начальная координата Y
    private static Map field; // создаем переменную поля
    private static StartNewGameWindow startNewGameWindow;
    GameWindow() {
        setTitle("Tic Tac Toe"); // заголовок
        setBounds(WIN_POS_X, WIN_POS_Y, WIN_WIDTH, WIN_HEIGHT); // установка координат
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // закрытие по крестику
        setResizable(false); // запрет изменения
        JPanel bottomPanel= new JPanel(new GridLayout(1,2)); // панель с кнопками
        JButton btnNewGame= new JButton("Start new game");
        JButton btnExit=new JButton("Exit");
        bottomPanel.add(btnNewGame); // Добавляем кнопку на панель
        bottomPanel.add(btnExit); // Добавляем кнопку на панель
        add(bottomPanel, BorderLayout.SOUTH); // Добавляем панель с кнопками в окно вниз
        btnExit.addActionListener(new ActionListener() { // прописываем функционал кнопки Exit
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnNewGame.addActionListener(new ActionListener() { // прописываем функцию кнопки New game
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGameWindow.setVisible(true); // проявляем второе окно
            }
        });
        startNewGameWindow=new StartNewGameWindow(this);
        field=new Map(); // создаем игровое поле
        add(field, BorderLayout.CENTER); // добавляем его на экран
        setVisible(true);
    }
    void setStartNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLen) {
        field.startNewGame(mode, fieldSizeX, fieldSizeY, winLen);
    }
}
