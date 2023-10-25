import views.GameBoard;

import java.awt.*;

public class Tetris
{
    /**
     * Function main begins with program execution
     *
     * @param args The command line args (not used in this program)
     */
    public static void main(String[] args) {

        // Use EventQueue to ensure the GUI components are created and updated on the Event Dispatch Thread.
        EventQueue.invokeLater(() -> {

            GameBoard game = new GameBoard();
            game.setVisible(true);
        });
    }
}