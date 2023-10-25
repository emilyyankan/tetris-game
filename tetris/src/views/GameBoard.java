package views;

import controllers.GameController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * TetrisBoard.java
 * Represents the main game board for the Tetris game.
 */
public class GameBoard extends JFrame {

    private JLabel statusbar;
    private JButton quitButton;

    /**
     * Constructor to initialize the board
     */
    public GameBoard() {
        initUI();
    }

    /**
     * Initializes the user interface and sets up the Tetris game board.
     */
    public void initUI() {

        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.WHITE);

        // Status bar to display game information
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.NORTH);
        GameController board = new GameController(this);
        add(board, BorderLayout.CENTER);

        // Quit button to exit the game
        var quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Close the application when the Quit button is clicked
            }
        });

        // Create a panel for buttons
        var buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(quitButton);
        buttonPanel.setBackground(Color.WHITE);

        add(buttonPanel, BorderLayout.SOUTH);

        board.start();

        var pauseButton = new JButton("Pause/Resume");
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.togglePauseGame();
                // You can also change the text of the status bar here to indicate the game is paused.
            }
        });
        buttonPanel.add(pauseButton);

        // Set frame properties
        setTitle("Tetris");
        setSize(250, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    /**
     * Retrieves the status bar label.
     *
     * @return The JLabel representing the status bar.
     */
    public JLabel getStatusBar() {
        return statusbar;
    }
}
