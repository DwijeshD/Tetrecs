package uk.ac.soton.comp1206.game;

import java.util.Random;

/**
 * Instances of GamePiece Represents the model of a specific Game Piece with it's block makeup.
 *
 * The GamePiece class also contains a factory for producing a GamePiece of a particular shape, as specified by it's
 * number.
 */
public class GamePiece {

    /**
     * The total number of pieces in this game
     */
    public static final int PIECES = 15;

    /**
     * The 2D grid representation of the shape of this piece
     */
    private int[][] blocks;

    /**
     * The value of this piece
     */
    private final int value;

    /**
     * The name of this piece
     */
    private final String name;

    private static Random random = new Random();

    private static final int lowerBound = 1;
    private static final int upperBound = 15;

    /**
     * Create a new GamePiece of the specified piece number
     * @param piece piece number
     * @return the created GamePiece
     */
    public static GamePiece createPiece(int piece) {
        switch (piece) {

            //Dot
            case 0 -> {
                int[][] blocks = {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}};
                return new GamePiece("Dot", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //Line
            case 1 -> {
                int[][] blocks = {{0, 0, 0}, {1, 1, 1}, {0, 0, 0}};
                return new GamePiece("Line", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //C
            case 3 -> {
                int[][] blocks = {{0, 0, 0}, {1, 1, 1}, {1, 0, 1}};
                return new GamePiece("C", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //Plus
            case 2 -> {
                int[][] blocks = {{0, 1, 0}, {1, 1, 1}, {0, 1, 0}};
                return new GamePiece("Plus", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }



            //Square
            case 4 -> {
                int[][] blocks = {{1, 1, 0}, {1, 1, 0}, {0, 0, 0}};
                return new GamePiece("Square", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //L
            case 5 -> {
                int[][] blocks = {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}};
                return new GamePiece("L", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //J
            case 6 -> {
                int[][] blocks = {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}};
                return new GamePiece("J", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //S
            case 7 -> {
                int[][] blocks = {{0, 0, 0}, {0, 1, 1}, {1, 1, 0}};
                return new GamePiece("S", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //Z
            case 8 -> {
                int[][] blocks = {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}};
                return new GamePiece("Z", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //T
            case 9 -> {
                int[][] blocks = {{1, 0, 0}, {1, 1, 0}, {1, 0, 0}};
                return new GamePiece("T", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }
            //X
            case 10 -> {
                int[][] blocks = {{1, 0, 1}, {0, 1, 0}, {1, 0, 1}};
                return new GamePiece("X", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //Corner
            case 11 -> {
                int[][] blocks = {{0, 0, 0}, {1, 1, 0}, {1, 0, 0}};
                return new GamePiece("Corner", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //Inverse Corner
            case 12 -> {
                int[][] blocks = {{1, 0, 0}, {1, 1, 0}, {0, 0, 0}};
                return new GamePiece("Inverse Corner", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //Diagonal
            case 13 -> {
                int[][] blocks = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
                return new GamePiece("Diagonal", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

            //Double
            case 14 -> {
                int[][] blocks = {{0, 1, 0}, {0, 1, 0}, {0, 0, 0}};
                return new GamePiece("Double", blocks, random.nextInt(upperBound-lowerBound) + lowerBound);
            }

        }

        //Not a valid piece number
        throw new IndexOutOfBoundsException("No such piece: " + piece);
    }

    /**
     * Create a new GamePiece of the specified piece number and rotation
     * @param piece piece number
     * @param rotation number of times to rotate
     * @return the created GamePiece
     */
    public static GamePiece createPiece(int piece, int rotation) {
        var newPiece = createPiece(piece);

        newPiece.rotate(rotation);
        return newPiece;
    }

    /**
     * Create a new GamePiece with the given name, block makeup and value. Should not be called directly, only via the
     * factory.
     * @param name name of the piece
     * @param blocks block makeup of the piece
     * @param value the value of this piece
     */
    private GamePiece(String name, int[][] blocks, int value) {
        this.name = name;
        this.blocks = blocks;
        this.value = value;

        //Use the shape of the block to create a grid with either 0 (empty) or the value of this shape for each block.
        for(int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
                if(blocks[x][y] == 0) continue;
                blocks[x][y] = value;
            }
        }
    }

    /**
     * Get the value of this piece
     * @return piece value
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the block makeup of this piece
     * @return 2D grid of the blocks representing the piece shape
     */
    public int[][] getBlocks() {
        return blocks;
    }

    /**
     * Rotate this piece the given number of rotations
     * @param rotations number of rotations
     */
    public void rotate(int rotations) {
        for(int rotated = 0; rotated < rotations; rotated ++) {
            rotate();
        }
    }

    /**
     * Rotate this piece exactly once by rotating it's 3x3 grid
     */
    public void rotate() {
        int[][] rotated = new int[blocks.length][blocks[0].length];
        rotated[2][0] = blocks[0][0];
        rotated[1][0] = blocks[0][1];
        rotated[0][0] = blocks[0][2];

        rotated[2][1] = blocks[1][0];
        rotated[1][1] = blocks[1][1];
        rotated[0][1] = blocks[1][2];

        rotated[2][2] = blocks[2][0];
        rotated[1][2] = blocks[2][1];
        rotated[0][2] = blocks[2][2];

        blocks = rotated;
    }


    /**
     * Return the string representation of this piece
     * @return the name of this piece
     */
    public String toString() {
        return this.name;
    }
}
