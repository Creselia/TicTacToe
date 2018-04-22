package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Map extends JPanel {

    static final int GAME_MODE_HVA = 0;
    static final int GAME_MODE_HVH = 1;

    private static final int DOT_EMPTY = 0;
    private static final int DOT_HUMAN = 1;
    private static final int DOT_AI = 2;
    private static final int DOTS_PADDING = 5;

    private static final int STATE_DRAW = 0;
    private static final int STATE_HUMAN_WIN = 1;
    private static final int STATE_AI_WIN = 2;
    private int stateGameOver;

    private static final String MSG_DRAW = "Ничья";
    private static final String MSG_HUM_WIN = "Победил игрок";
    private static final String MSG_AI_WIN = "Победил компьютер";

    private static Random RANDOM = new Random();

    private int[][] field;

    private int sizeFieldX;
    private int sizeFieldY;
    private int winLength;
    private int cellHeight;
    private int cellWidth;
    private boolean initialized;
    private boolean gameOver;

    Map() {
        setBackground(Color.WHITE);
        initialized = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void update(MouseEvent e) {
        if (gameOver) return;
        int cellX = (e.getX() / cellWidth);
        int cellY = (e.getY() / cellHeight);
        if (!isValidCell(cellX, cellY) || !isEmptyCell(cellX, cellY)) return;
        field[cellY][cellX] = DOT_HUMAN;

        if (checkWin(DOT_HUMAN)) {
            stateGameOver = STATE_HUMAN_WIN;
            gameOver = true;
            repaint();
            return;
        }
        if (isMapFull()) {
            stateGameOver = STATE_DRAW;
            gameOver = true;
            repaint();
            return;
        }
        aiTurn();
        repaint();
        if (checkWin(DOT_AI)) {
            stateGameOver = STATE_AI_WIN;
            gameOver = true;
            return;
        }
        if (isMapFull()) {
            stateGameOver = STATE_DRAW;
            gameOver = true;
            return;
        }
    }

    void render(Graphics g) {
        if (!initialized) return;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        cellWidth = panelWidth / sizeFieldX;
        cellHeight = panelHeight / sizeFieldY;
        g.setColor(Color.BLACK);
        for (int i = 0; i < sizeFieldY; i++) {
            int y = i * cellHeight;
            g.drawLine(0, y, panelWidth, y);
        }
        for (int i = 0; i < sizeFieldX; i++) {
            int x = i * cellWidth;
            g.drawLine(x, 0, x, panelWidth);
        }
        for (int y = 0; y < sizeFieldY; y++) {
            for (int x = 0; x < sizeFieldX; x++) {
                if (isEmptyCell(x, y)) continue;
                if (field[y][x] == DOT_HUMAN) {
                    g.setColor(Color.BLUE);
                } else if (field[y][x] == DOT_AI) {
                    g.setColor(Color.RED);
                } else {
                    throw new RuntimeException("Can't recognize cell contents" + field[y][x]);
                }

                g.fillOval(x * cellWidth + DOTS_PADDING,
                        y * cellHeight + DOTS_PADDING,
                        cellWidth - DOTS_PADDING * 2,
                        cellHeight - DOTS_PADDING * 2);
            }
        }

        if (gameOver) {
            showMessageGameOver(g);
        }
    }

    void showMessageGameOver(Graphics g) {
        Font font = new Font("Times new roman", Font.BOLD, 48);
        int labelHeight = getHeight() / 2;
        g.setColor(Color.DARK_GRAY); //сделать окошко
        g.fillRect(0, 200, getWidth(), 70);
        g.setColor(Color.YELLOW);
        g.setFont(font);

        switch (stateGameOver) {
            case STATE_DRAW:
                g.drawString(MSG_DRAW, 170, labelHeight);
                break;
            case STATE_HUMAN_WIN:
                g.drawString(MSG_HUM_WIN, 90, labelHeight);
                break;
            case STATE_AI_WIN:
                g.drawString(MSG_AI_WIN, 20, labelHeight);
                break;
            default:
                throw new RuntimeException("Unexpected GameOver Status" + stateGameOver);
        }
    }

    void startNewGame(int mode, int sizeFieldX, int sizeFieldY, int winLength) {
        this.sizeFieldX = sizeFieldX;
        this.sizeFieldY = sizeFieldY;
        this.winLength = winLength;
        field = new int[sizeFieldY][sizeFieldX];
        initialized = true;
        gameOver = false;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                update(e);
            }

        });
        repaint();
    }

    private void aiTurn() {
        if (turnAIWinCell()) return;        // проверим, не выиграет-ли игрок на следующем ходу
        if (turnHumanWinCell()) return;    // проверим, не выиграет-ли комп на следующем ходу
        int x, y;
        do {                            // или комп ходит в случайную клетку
            x = RANDOM.nextInt(sizeFieldX);
            y = RANDOM.nextInt(sizeFieldY);
        } while (!isEmptyCell(x, y));
        field[y][x] = DOT_AI;
    }

    // Проверка, может ли выиграть комп
    private boolean turnAIWinCell() {
        for (int i = 0; i < sizeFieldY; i++) {
            for (int j = 0; j < sizeFieldX; j++) {
                if (isEmptyCell(j, i)) {                // поставим нолик в каждую клетку поля по очереди
                    field[i][j] = DOT_AI;
                    if (checkWin(DOT_AI))
                        return true;    // если мы выиграли, вернём истину, оставив нолик в выигрышной позиции
                    field[i][j] = DOT_EMPTY;            // если нет - вернём обратно пустоту в клетку и пойдём дальше
                }
            }
        }
        return false;
    }

    // Проверка, выиграет-ли игрок своим следующим ходом
    private boolean turnHumanWinCell() {
        for (int i = 0; i < sizeFieldY; i++) {
            for (int j = 0; j < sizeFieldX; j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = DOT_HUMAN;            // поставим крестик в каждую клетку по очереди
                    if (checkWin(DOT_HUMAN)) {            // если игрок победит
                        field[i][j] = DOT_AI;            // поставить на то место нолик
                        return true;
                    }
                    field[i][j] = DOT_EMPTY;            // в противном случае вернуть на место пустоту
                }
            }
        }
        return false;
    }

    // проверка на победу
    private boolean checkWin(int dot) {
        for (int i = 0; i < sizeFieldY; i++) {            // ползём по всему полю
            for (int j = 0; j < sizeFieldX; j++) {
                if (checkLine(i, j, 1, 0, winLength, dot)) return true;    // проверим линию по х
                if (checkLine(i, j, 1, 1, winLength, dot)) return true;    // проверим по диагонали х у
                if (checkLine(i, j, 0, 1, winLength, dot)) return true;    // проверим линию по у
                if (checkLine(i, j, 1, -1, winLength, dot)) return true;    // проверим по диагонали х -у
            }
        }
        return false;
    }

    // проверка линии
    private boolean checkLine(int x, int y, int vx, int vy, int len, int dot) {
        final int far_x = x + (len - 1) * vx;            // посчитаем конец проверяемой линии
        final int far_y = y + (len - 1) * vy;
        if (!isValidCell(far_x, far_y)) return false;    // проверим не выйдет-ли проверяемая линия за пределы поля
        for (int i = 0; i < len; i++) {                    // ползём по проверяемой линии
            if (field[y + i * vy][x + i * vx] != dot)
                return false;    // проверим одинаковые-ли символы в ячейках
        }
        return true;
    }

    // ничья?
    private boolean isMapFull() {
        for (int i = 0; i < sizeFieldY; i++) {
            for (int j = 0; j < sizeFieldX; j++) {
                if (field[i][j] == DOT_EMPTY) return false;
            }
        }
        return true;
    }

    // ячейка-то вообще правильная?
    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < sizeFieldX && y >= 0 && y < sizeFieldY;
    }

    // а пустая?
    private boolean isEmptyCell(int x, int y) {
        return field[y][x] == DOT_EMPTY;
    }
}
