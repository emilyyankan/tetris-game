package controllers;

import models.Shape;
import models.Shape.Tetronimo;
import views.GameBoard;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *The TetrisController class manages the game logic and user input for the Tetris game.
 *
 **/
public class GameController extends JPanel {

    private static final int LINE_CLEAR_SCORE = 100;

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    public final int PERIOD_INTERVAL = 300;

    // Timer for controlling game cycles
    private Timer timer;
    // Flags to manage game state
    private boolean fallingFinishState = false;
    private boolean isPaused = false;

    // Game statistics
    private int linesRemovedCount = 0;

    // Current piece position
    private int curX = 0;
    private int curY = 0;

    // UI element to display game status
    private JLabel statusbar;

    // The current falling piece
    private Shape curPiece;
    private Tetronimo[] board;

    /**
     * Constructs a TetrisController object.
     *
     * @param parent The TetrisBoard instance associated with this controller.
     */
    public GameController(GameBoard parent) {
        initBoard(parent);
    }

    // Initialize the game board
    private void initBoard(GameBoard parent) {

        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(PERIOD_INTERVAL, new GameCycle());

        setBackground(Color.BLACK);

        statusbar = parent.getStatusBar();
        addKeyListener(new TAdapter());
    }

    // Calculate the width of a single square
    private int squareWidth() {
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    // Calculate the height of a single square
    private int squareHeight() {
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    // Get the Tetronimo shape at a specific position on the board
    private Tetronimo shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }


    /**
     * Start the game by initializing the game board and starting the timer.
     */
    public void start() {
        curPiece = new Shape();
        board = new Tetronimo[BOARD_WIDTH * BOARD_HEIGHT];

        clearBoard();
        newPiece();

        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }

    /*
     * Pause or unpause the game.
     * <p>
     * When the game is paused, the statusbar text is updated accordingly.
     */
    private void pause() {

        isPaused = !isPaused;
        if (isPaused) {
            statusbar.setText("paused");
        } else {
            statusbar.setText(String.valueOf(linesRemovedCount));
        }
        repaint();
    }

    public void togglePauseGame() {
        isPaused = !isPaused;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    // Draws the Tetris game on the provided Graphics object.
    private void doDrawing(Graphics g) {

        // Calculate the top of the board based on the current size
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        // Draw the shapes on the board
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetronimo shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Tetronimo.NoShape) {
                    drawSquare(g, j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
                }
            }
        }

        //// Draw the current falling piece
        if (curPiece.getShape() != Tetronimo.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(),
                        boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    /*
     * Moves the current piece down until it cannot move any further,
     * then calls pieceDropped() to handle its placement on the board.
     */
    private void dropDown() {

        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            newY--;
        }
        pieceDropped();
    }

    /*
     * Moves the current piece one row down if possible, and calls
     * pieceDropped() if it cannot move any further.
     */
    private void oneLineDown() {

        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    // Clears the game board by setting all positions to NoShape.
    private void clearBoard() {

        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Tetronimo.NoShape;
        }
    }

    /*
     * Handles the placement of the current piece on the board after it's dropped,
     * removes full lines, and starts a new piece if needed.
     */
    private void pieceDropped() {
        // Place the piece on the board
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);

            if (y >= 0 && y < BOARD_HEIGHT && x >= 0 && x < BOARD_WIDTH) {
                board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
            }
        }

        // Remove full lines and start a new piece
        removeFullLines();
        if (!fallingFinishState) {
            newPiece();
        }
    }


    /*
     * Generates a new random piece and sets its initial position.
     * Ends the game if the new piece cannot be placed.
     */
    private void newPiece() {

        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        // End the game if the new piece cannot be placed
        if (!tryMove(curPiece, curX, curY)) {

            curPiece.setShape(Tetronimo.NoShape);
            timer.stop();

            String msg = String.format("Game over. Score: %d", linesRemovedCount);
            statusbar.setText(msg);
        }
    }

    /**
     * Attempts to move the provided piece to a new position on the game board.
     *
     * @param newPiece The piece to be moved.
     * @param newX     The new X-coordinate for the piece.
     * @param newY     The new Y-coordinate for the piece.
     * @return True if the move is valid and successful, false otherwise.
     */
    private boolean tryMove(Shape newPiece, int newX, int newY) {

        for (int i = 0; i < 4; i++) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            // Check if the new position is out of bounds or collides with existing shapes
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }
            if (shapeAt(x, y) != Tetronimo.NoShape) {
                return false;
            }
        }

        // Update the current piece's position and trigger a repaint
        curPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();

        return true;
    }

    /*
     * Removes any full lines on the game board and updates the score and state accordingly.
     */
    private void removeFullLines() {

        int fullLinesCount = 0;

        // Check each row for full lines and remove them
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Tetronimo.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            // If the line is full, remove it and shift rows above it down
            if (lineIsFull) {
                fullLinesCount++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        // Update score and game state if full lines were removed
        if (fullLinesCount > 0) {
            linesRemovedCount += fullLinesCount * LINE_CLEAR_SCORE;
            statusbar.setText(String.valueOf(linesRemovedCount));
            statusbar.setForeground(Color.WHITE);
            fallingFinishState = true;
            curPiece.setShape(Tetronimo.NoShape);
        }
    }

    /**
     * Draws a square with the specified shape, color, and dimensions.
     *
     * @param g      The Graphics object to draw on.
     * @param x      The X-coordinate of the top-left corner of the square.
     * @param y      The Y-coordinate of the top-left corner of the square.
     * @param shape  The shape to determine the color of the square.
     */
    private void drawSquare(Graphics g, int x, int y, Tetronimo shape) {
        Color[] colors = {
                new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        // Determine the color based on the shape's ordinal value
        Color color = colors[shape.ordinal()];

        // Draw the square with specified color and dimensions
        g.setColor(color);
        g.fillRect(x, y, squareWidth(), squareHeight());

        g.setColor(color.brighter());
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.drawLine(x, y, x, y + squareHeight() - 1);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    /**
     * ActionListener implementation that triggers the game cycle.
     */
    private class GameCycle implements ActionListener {

        /**
         * Invoked when an action occurs. Initiates the game cycle.
         *
         * @param e The action event.
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    /*
     * Performs a single game cycle, including updating the game state and triggering a repaint.
     */
    private void doGameCycle() {

        update();
        repaint();
    }

    /*
     * Updates the game state based on the current conditions.
     */
    private void update() {

        if (isPaused) {
            return;
        }
        if (fallingFinishState) {
            fallingFinishState = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    /**
     * KeyAdapter implementation for handling keyboard input during the game.
     */
    class TAdapter extends KeyAdapter {

        /**
         * Invoked when a key is pressed. Handles specific key events to control the game.
         *
         * @param e The key event.
         */
        @Override
        public void keyPressed(KeyEvent e) {

            if (curPiece.getShape() == Shape.Tetronimo.NoShape) {
                return;
            }
            int keycode = e.getKeyCode();

            switch (keycode) {
                case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT -> tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP -> tryMove(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_SPACE -> dropDown();
                case KeyEvent.VK_D -> oneLineDown();
            }
        }
    }
}
