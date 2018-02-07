package geekbrains.Lesson8;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartNewGameWindow extends JFrame {
    private static final int WIN_HEIGHT=230; // высота окна
    private static final int WIN_WIDTH=350; // ширина окна
    private static final int MIN_WIN_LEN=3; // выигрышная длина
    private static final int MAX_WIN_LEN=10; // выигрышная длина
    private static final int MIN_FIELD_SIZE=3; // размер поля
    private static final int MAX_FIELD_SIZE=10; // размер поля
    private static final String STR_WIN_LEN="Winning Length: ";
    private static final String STR_FIELD_SIZE="Field size: ";
    private JSlider slFieldSize;
    private JSlider slWinLength;
    private final GameWindow gameWindow;
    private JRadioButton jrbHumVsAi=new JRadioButton("Human vs AI", true);
    private JRadioButton jrbHumVsHum=new JRadioButton("Human vs Human");
    private ButtonGroup gameMode=new ButtonGroup();

    StartNewGameWindow(GameWindow gameWindow) {
        this.gameWindow=gameWindow;
        setSize(WIN_WIDTH, WIN_HEIGHT); // задаем размер
        Rectangle gameWindowBounds=gameWindow.getBounds();
        int posX=(int) (gameWindowBounds.getCenterX()-WIN_WIDTH/2);
        int posY=(int) (gameWindowBounds.getCenterY()-WIN_HEIGHT/2);
        setLocation(posX, posY);
        setTitle("New game parameters");
        setResizable(false);
        setLayout(new GridLayout(10, 1)); // задаем таблицу настроек
        addGameControlMode();
        addGameSizeField();
        JButton btnStartGame=new JButton("Start new game");
        btnStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnStartGameClick();
            }
        });
        add(btnStartGame);
    }
    private void btnStartGameClick() {
        int gameMode;
        int fieldSize;
        int winLength;
        if (jrbHumVsAi.isSelected()) {
            gameMode=Map.MODE_H_V_A;
        } else if (jrbHumVsHum.isSelected()) {
            gameMode=Map.MODE_H_V_H;
        } else throw new RuntimeException("No button selected");
            fieldSize=slFieldSize.getValue();
            winLength=slWinLength.getValue();
        if (winLength>fieldSize) winLength=fieldSize;
        gameWindow.setStartNewGame(gameMode, fieldSize, fieldSize, winLength);
        setVisible(false);
    }
    private void addGameControlMode() {
        add(new JLabel("Choose game mode: "));
        gameMode.add(jrbHumVsAi);
        gameMode.add(jrbHumVsHum);
        add(jrbHumVsAi);
        add(jrbHumVsHum);
    }
    private void addGameSizeField() {
        add(new JLabel("Choose field size: "));
        final JLabel lblFieldSize=new JLabel(STR_FIELD_SIZE+MIN_FIELD_SIZE);
        add(lblFieldSize);
        slFieldSize=new JSlider(MIN_FIELD_SIZE, MAX_FIELD_SIZE, MIN_FIELD_SIZE); // слайдер размера поля
        slFieldSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int currentFieldSize=slFieldSize.getValue();
                lblFieldSize.setText(STR_FIELD_SIZE+currentFieldSize);
                slWinLength.setMaximum(currentFieldSize);
                slWinLength.setEnabled(true);
            }
        });
        add(slFieldSize);
        add(new JLabel("Choose win length: "));
        final JLabel lblWinLen=new JLabel(STR_WIN_LEN+MIN_WIN_LEN);
        add(lblWinLen);
        slWinLength=new JSlider(MIN_WIN_LEN, MAX_WIN_LEN, MIN_WIN_LEN); // слайдер выигрышной длины
        slWinLength.setEnabled(false);
        slWinLength.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lblWinLen.setText(STR_WIN_LEN+slWinLength.getValue());
            }
        });
        add(slWinLength);
    }

}
