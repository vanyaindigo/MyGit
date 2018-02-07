package geekbrains.Lesson8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Map extends JPanel {
    public static final int MODE_H_V_A=0;
    public static final int MODE_H_V_H=1;
    private int[][] field;
    private int PLAYER_DOT=1;
    private int PLAYER1_DOT=3;
    private int PLAYER2_DOT=4;
    private int AI_DOT=2;
    private int EMPTY_DOT=0;
    private int stateGameOver;
    private int MODE;
    private int count;
    private static final int STATE_DRAW = 0;
    private static final int STATE_HUMAN_WIN = 1;
    private static final int STATE_HUMAN1_WIN = 3;
    private static final int STATE_HUMAN2_WIN = 4;
    private static final int STATE_AI_WIN = 2;
    private int fieldSizeX, fieldSizeY, winLength;
    private int cellHeight, cellWidth;
    private static Random random = new Random();
    private boolean isInitialized=false;
    private boolean gameOver=false;
    private static final String MSG_DRAW = "Ничья!";
    private static final String MSG_HUMAN_WIN = "Победил игрок!";
    private static final String MSG_HUMAN1_WIN = "Победил игрок 1!";
    private static final String MSG_HUMAN2_WIN = "Победил игрок 2!";
    private static final String MSG_AI_WIN = "Победил компьютер!";
    private final Font font = new Font("Times new roman", Font.BOLD, 48);

    Map() {
        setBackground(Color.white);
    }

    private void update(MouseEvent e) {
        if (gameOver) return;
        int cellX=e.getX()/cellWidth;
        int cellY=e.getY()/cellHeight;
        int dot;
        if (!isCellValid(cellY, cellX)||isFieldFull()) return;
        switch (MODE) {
            case 0:
                setSym(cellY, cellX, PLAYER_DOT);
                if (checkWin(PLAYER_DOT)) {
                    stateGameOver=STATE_HUMAN_WIN;
                    gameOver=true;
//                    System.out.println(MSG_HUMAN_WIN);
                    return;
                }
                if (isFieldFull()) {
                    stateGameOver=STATE_DRAW;
                    gameOver=true;
//                    System.out.println(MSG_DRAW);
                    return;
                }
                aiStep();
                repaint();
                if (checkWin(AI_DOT)) {
                    stateGameOver=STATE_AI_WIN;
                    gameOver=true;
//                    System.out.println(MSG_AI_WIN);
                    return;
                }
                if (isFieldFull()) {
                    stateGameOver=STATE_DRAW;
                    gameOver=true;
//                    System.out.println(MSG_DRAW);
                    return;
                }
                repaint();
                break;
            case 1:
                if (count%2!=0) {
                    dot=PLAYER1_DOT;
                } else {
                    dot=PLAYER2_DOT;
                }
                setSym(cellY, cellX, dot);
                count++;
                if (checkWin(dot)) {
                    if (dot==PLAYER1_DOT) {
                        stateGameOver=STATE_HUMAN1_WIN;
                        gameOver=true;
//                        System.out.println(MSG_HUMAN1_WIN);
                        return;
                    } else {
                        stateGameOver = STATE_HUMAN2_WIN;
                        gameOver = true;
//                        System.out.println(MSG_HUMAN2_WIN);
                        return;
                    }
                }
                if (isFieldFull()) {
                    stateGameOver=STATE_DRAW;
                    gameOver=true;
//                    System.out.println(MSG_DRAW);
                    return;
                }
                repaint();
                break;
        }
    }
    void startNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLen) {
//        System.out.println("mode "+ mode+"\n"+"fieldSizeX "+fieldSizeX+"\n"+"fieldSizeY "+fieldSizeY+"\n"+"winLen "+winLen);
        this.fieldSizeX=fieldSizeX;
        this.fieldSizeY=fieldSizeY;
        this.winLength=winLen;
        this.MODE=mode;
        count=3;
        field=new int[fieldSizeY][fieldSizeX];
        for (int i=0; i<fieldSizeY; i++) {
            for (int j=0; j<fieldSizeX; j++) {
                field[i][j]=EMPTY_DOT;
            }
        }
        isInitialized=true;
        gameOver=false;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                update(e);
                repaint();
            }
        });
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void render(Graphics g) {
        if (!isInitialized) return;
        int panelWidth=getWidth();
        int panelHeight=getHeight();
        cellHeight=panelHeight/fieldSizeY;
        cellWidth=panelWidth/fieldSizeX;
        for (int i=0; i<fieldSizeY; i++) {
            int y=i*cellHeight;
            g.drawLine(0, y, panelWidth, y);
        }
        for (int i=0; i<fieldSizeX; i++) {
            int x=i*cellWidth;
            g.drawLine(x, 0, x, panelHeight);
        }
        figure(g);
        if(gameOver){
            showMessageGameOver(g);
        }
    }
    private void showMessageGameOver(Graphics g){
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,200, getWidth(), 70);
        g.setColor(Color.YELLOW);
        g.setFont(font);
        switch (stateGameOver){
            case STATE_DRAW:
                g.drawString(MSG_DRAW, 180, getHeight() / 2);
                break;
            case STATE_HUMAN_WIN:
                g.drawString(MSG_HUMAN_WIN, 70, getHeight() / 2);
                break;
            case STATE_AI_WIN:
                g.drawString(MSG_AI_WIN, 20, getHeight() / 2);
                break;
            case STATE_HUMAN1_WIN:
                g.drawString(MSG_HUMAN1_WIN, 70, getHeight() / 2);
                break;
            case STATE_HUMAN2_WIN:
                g.drawString(MSG_HUMAN2_WIN, 70, getHeight() / 2);
                break;
            default:
                throw new RuntimeException("unexpected Game over state: " + stateGameOver);
        }
    }
    private void figure(Graphics g) {
        int x1, x2,y1, y2;
        switch (MODE) {
            case 0:
                for (int v=0; v<fieldSizeY; v++) {
                    for (int h=0; h<fieldSizeX; h++) {
                        if (field[v][h]==PLAYER_DOT) {
                            x1=h*cellWidth;
                            y1=v*cellHeight;
                            x2=(h+1)*cellWidth;
                            y2=(v+1)*cellHeight;
                            g.drawLine(x1, y1, x2, y2);
                            g.drawLine(x2, y1, x1, y2);
                        } else if (field[v][h]==AI_DOT) {
                            x1=h*cellWidth;
                            y1=v*cellHeight;
                            g.drawOval(x1, y1, cellWidth, cellHeight);
                        }
                    }
                }
                break;
            case 1:
                for (int v=0; v<fieldSizeY; v++) {
                    for (int h=0; h<fieldSizeX; h++) {
                        if (field[v][h]==PLAYER1_DOT) {
                            x1=h*cellWidth;
                            y1=v*cellHeight;
                            x2=(h+1)*cellWidth;
                            y2=(v+1)*cellHeight;
                            g.drawLine(x1, y1, x2, y2);
                            g.drawLine(x2, y1, x1, y2);
                        } else if (field[v][h]==PLAYER2_DOT) {
                            x1=h*cellWidth;
                            y1=v*cellHeight;
                            g.drawOval(x1, y1, cellWidth, cellHeight);
                        }
                    }
                }
                break;
        }

    }
    private boolean isCellValid(int y, int x) {
        if (x < 0 || y < 0 || x > fieldSizeX - 1 || y > fieldSizeY - 1) {
            return false;
        }
        return (field[y][x] == EMPTY_DOT);
    }

    private void setSym(int y, int x, int sym) {
        field[y][x] = sym;
    }
    private boolean checkWin(int sym) {
        for (int v = 0; v<fieldSizeY; v++){
            for (int h= 0; h<fieldSizeX; h++) {
                // проверяем сначала возможна ли победа
                if (h + winLength <= fieldSizeX) {
                    //по горизонтале
                    if (checkLineHorizon(v, h, sym) >= winLength) return true;
                    //вверх по диагонале
                    if (v - winLength > -2) {
                        if (checkDiaUp(v, h, sym) >= winLength) return true;
                    }
                    //вниз по диагонале
                    if (v + winLength <= fieldSizeY) {
                        if (checkDiaDown(v, h, sym) >= winLength) return true;
                    }
                }
                if (v + winLength <= fieldSizeY) {                       //по вертикале
                    if (checkLineVertical(v, h, sym) >= winLength) return true;
                }
            }
        }
        return false;
    }
    private boolean isFieldFull() {
        for(int i = 0; i < fieldSizeY; i++) {
            for(int j = 0; j < fieldSizeX; j++) {
                if (field[i][j] == EMPTY_DOT) {
                    return false;
                }
            }
        }
        return true;
    }
    //проверка заполнения всей линии по диагонале вверх
    private int checkDiaUp(int v, int h, int dot) {
        int count=0;
        for (int i = 0, j = 0; j < winLength; i--, j++) {
            if ((field[v+i][h+j] == dot)) count++;
        }
        return count;
    }
    //проверка заполнения всей линии по диагонале вниз
    private int checkDiaDown(int v, int h, int dot) {
        int count=0;
        for (int i = 0; i < winLength; i++) {
            if ((field[i+v][i+h] == dot)) count++;
        }
        return count;
    }
    //провека заполнения всей линии горизонтале
    private int checkLineHorizon(int v, int h, int dot) {
        int count=0;
        for (int j = h; j < winLength+h; j++) {
            if ((field[v][j] == dot)) count++;
        }
        return count;
    }
    //проверка заполнения всей линии по вертикале
    private int checkLineVertical(int v, int h, int dot) {
        int count=0;
        for (int i = v; i<winLength+v; i++) {
            if ((field[i][h] == dot)) count++;
        }
        return count;
    }
    // Ходы компьютера
    private void aiStep() {
        int x, y;
        //блокировка ходов человека
        for (int v = 0; v<fieldSizeY; v++) {
            for (int h = 0; h < fieldSizeX; h++) {
                //анализ наличие поля для проверки
                if (h+winLength<=fieldSizeX) {                           //по горизонтале
                    if (checkLineHorizon(v, h, PLAYER_DOT) == winLength - 1) {
                        if (MoveAiLineHorizon(v, h, AI_DOT)) return;
                    }

                    if (v - winLength > -2) {                            //вверх по диагонале
                        if (checkDiaUp(v, h, PLAYER_DOT) == winLength - 1) {
                            if (MoveAiDiaUp(v, h, AI_DOT)) return;
                        }
                    }
                    if (v + winLength <= fieldSizeY) {                       //вниз по диагонале
                        if (checkDiaDown(v, h, PLAYER_DOT) == winLength - 1) {
                            if (MoveAiDiaDown(v, h, AI_DOT)) return;
                        }
                    }
                }
                if (v+winLength<=fieldSizeY) {                       //по вертикале
                    if (checkLineVertical(v, h, PLAYER_DOT) == winLength-1) {
                        if(MoveAiLineVertical(v, h, AI_DOT)) return;
                    }
                }
            }
        }
        //игра на победу
        for (int v = 0; v<fieldSizeY; v++) {
            for (int h = 0; h < fieldSizeX; h++) {
                //анализ наличие поля для проверки
                if (h+winLength<=fieldSizeX) {                           //по горизонтале
                    if (checkLineHorizon(v, h, AI_DOT) == winLength-1) {
                        if (MoveAiLineHorizon(v, h, AI_DOT)) return;
                    }

                    if (v-winLength>-2) {                            //вверх по диагонале
                        if (checkDiaUp(v, h, AI_DOT) == winLength-1) {
                            if (MoveAiDiaUp(v, h, AI_DOT)) return;
                        }
                    }
                    if (v+winLength<=fieldSizeY) {                       //вниз по диагонале
                        if (checkDiaDown(v, h, AI_DOT) == winLength-1) {
                            if (MoveAiDiaDown(v, h, AI_DOT)) return;
                        }
                    }
                }
                if (v+winLength<=fieldSizeY) {                       //по вертикале
                    if (checkLineVertical(v, h, AI_DOT) == winLength-1) {
                        if(MoveAiLineVertical(v, h, AI_DOT)) return;
                    }
                }
            }
        }
        //случайный ход
        do {
            y = random.nextInt(fieldSizeY);
            x = random.nextInt(fieldSizeX);
        } while (!isCellValid(y,x));
        setSym(y, x, AI_DOT);
    }
    //ход компьютера по горизонтале
    private boolean MoveAiLineHorizon(int v, int h, int dot) {
        for (int j = h; j < winLength; j++) {
            if ((field[v][j] == EMPTY_DOT)) {
                field[v][j] = dot;
                return true;
            }
        }
        return false;
    }
    //ход компьютера по вертикале
    private boolean MoveAiLineVertical(int v, int h, int dot) {
        for (int i = v; i<winLength; i++) {
            if ((field[i][h] == EMPTY_DOT)) {
                field[i][h] = dot;
                return true;
            }
        }
        return false;
    }
    //ход компьютера проверка заполнения всей линии по диагонале вверх
    private boolean MoveAiDiaUp(int v, int h, int dot) {
        for (int i = 0, j = 0; j < winLength; i--, j++) {
            if ((field[v+i][h+j] == EMPTY_DOT)) {
                field[v+i][h+j] = dot;
                return true;
            }
        }
        return false;
    }
    //ход компьютера проверка заполнения всей линии по диагонале вниз
    private boolean MoveAiDiaDown(int v, int h, int dot) {
        for (int i = 0; i < winLength; i++) {
            if ((field[i+v][i+h] == EMPTY_DOT)) {
                field[i+v][i+h] = dot;
                return true;
            }
        }
        return false;
    }
}
