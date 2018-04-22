package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameWindow extends JFrame {

    private static final int WINDOW_HEIGHT = 555;
    private static final int WINDOW_WIDTH = 507;
    private static final int WINDOW_POS_X = 100;
    private static final int WINDOW_POS_Y = 100;

    private Map map;
    private OptionsWindow optionsWindow;

    GameWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(WINDOW_POS_X, WINDOW_POS_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setTitle("TicTacToe");

        JButton btnNewGame = new JButton("New Game");
        btnNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionsWindow.setVisible(true);
            }
        });

        JButton btnExitGame = new JButton("Exit Game");
        btnExitGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        map = new Map();

        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new GridLayout(1, 2));
        panelBottom.add(btnNewGame);
        panelBottom.add(btnExitGame);

        add(map, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);

        optionsWindow = new OptionsWindow(this);

        setVisible(true);
    }

    void startNewGame(int mode, int sizeFieldX, int sizeFieldY, int winLength) {
        map.startNewGame(mode, sizeFieldX, sizeFieldY, winLength);
    }
}