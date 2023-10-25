package models;

import java.util.Random;

public class Shape {

    /**
     * Enumeration of different Tetronimo shapes.
     */
    public enum Tetronimo {
        NoShape, ZShape, SShape, LineShape,
        TShape, SquareShape, LShape, MirroredLShape
    }

    public Tetronimo pieceShape;
    public int[][] coords;

    /**
     * Constructs a new Shape object with default values.
     */
    public Shape() {

        coords = new int[4][2];
        setShape(Tetronimo.NoShape);
    }

    /**
     * Sets the shape of the Tetris piece.
     *
     * @param shape The shape to set the Tetris piece to.
     */
    public void setShape(Tetronimo shape) {

        int[][][] coordsTable = new int[][][]{
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}},
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
                {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
                {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}}
        };

        for (int i = 0; i < 4; i++) {
            System.arraycopy(coordsTable[shape.ordinal()], 0, coords, 0, 4);
        }
        pieceShape = shape;
    }

    /**
     * Sets the x-coordinate of a specific point of the piece.
     *
     * @param index The index of the point.
     * @param x     The new x-coordinate value.
     */
    public void setX(int index, int x) {

        coords[index][0] = x;
    }

    /**
     * Sets the y-coordinate of a specific point of the piece.
     *
     * @param index The index of the point.
     * @param y     The new y-coordinate value.
     */
    public void setY(int index, int y) {

        coords[index][1] = y;
    }

    /**
     * Retrieves the x-coordinate of a specific point of the piece.
     *
     * @param index The index of the point.
     * @return The x-coordinate of the point.
     */
    public int x(int index) {

        return coords[index][0];
    }

    /**
     * Retrieves the y-coordinate of a specific point of the piece.
     *
     * @param index The index of the point.
     * @return The y-coordinate of the point.
     */
    public int y(int index) {

        return coords[index][1];
    }

    /**
     * Retrieves the current shape of the Tetris piece.
     *
     * @return The current shape.
     */
    public Tetronimo getShape() {

        return pieceShape;
    }

    /**
     * Sets the shape of the Tetris piece to a random shape.
     */
    public void setRandomShape() {

        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;

        Tetronimo[] values = Tetronimo.values();
        setShape(values[x]);
    }

    /**
     * Retrieves the minimum x-coordinate of the Tetris piece.
     *
     * @return The minimum x-coordinate.
     */
    public int minX() {

        int m = coords[0][0];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][0]);
        }
        return m;
    }

    /**
     * Retrieves the minimum y-coordinate of the Tetris piece.
     *
     * @return The minimum y-coordinate.
     */
    public int minY() {

        int m = coords[0][1];

        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }

    /**
     * Rotates the Tetris piece counterclockwise (left).
     *
     * @return The new rotated shape.
     */
    public Shape rotateLeft() {

        if (pieceShape == Tetronimo.SquareShape) {
            return this;
        }
        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; i++) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    /**
     * Rotates the Tetris piece clockwise (right).
     *
     * @return The new rotated shape.
     */
    public Shape rotateRight() {

        if (pieceShape == Tetronimo.SquareShape) {
            return this;
        }
        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; i++) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}
