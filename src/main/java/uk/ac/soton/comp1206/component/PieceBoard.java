package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
/**
 * A PieceBoard is a visual component to represent the next piece.
 * It extends a GridPane to hold a grid of GameBlocks. It updates with
 * the next piece to be played when the game logic updates and the
 * piece is played
 *
 */
public class PieceBoard extends GameBoard{
    /**
     * Logger to keep track of events occurring
     */
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);

    /**
     * Create a new PieceBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public PieceBoard(Grid grid, double width, double height) {
        super(grid, width, height);
    }

    /**
     * Displays the next piece to be played
     * @param gamePiece the next piece to be played
     */
    public void displayPiece(GamePiece gamePiece) {
        logger.info("Displaying a new piece: {}", gamePiece);
        //Gets the blocks for the piece
        int[][] blocks = gamePiece.getBlocks();
        for(var blockX = 0; blockX < blocks.length; blockX++)
            for(var blockY = 0; blockY < blocks.length; blockY++) {
                if(blocks[blockX][blockY] != 0) {
                    //If the value of the block isnt 0, set the grid to this block
                    grid.set(blockX, blockY, gamePiece.getValue());
                }
            }
        this.getBlock(1,1).drawCircle();
        //Draw centre circle
    }

    /**
     * Resets the grid so that the visual pieces don't stack on top of each other
     */
    public void resetGrid() {
        for(var rows = 0; rows < grid.getRows(); rows++) {
            for (var columns = 0; columns < grid.getCols(); columns++) {
                //For each coordinate in the PieceBoard, reset the block
                grid.set(rows, columns, 0);
            }
        }
    }
}
