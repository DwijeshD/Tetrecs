package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Grid;

import java.util.HashSet;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {
    /**
     * The logger to keep track of events
     */

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;

    /**
     * The listener to call when user right-clicks on a board
     */
    private RightClickedListener rightClickedListener;

    /**
     * Boolean variable to ensure only the GameBoard grid has the mouse functionality, and the pieceBoard doesn't
     */
    Boolean mouseTrack = false;
    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}",cols,rows);
        //Set the width and heigh
        setMaxWidth(width);
        setMaxHeight(height);
        //set gridlines
        setGridLinesVisible(true);
        //Set listener
        setOnRightClick(rightClickedListener);
        blocks = new GameBlock[cols][rows];
        //Create array of blocks
        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x,y);
                //Create blocks for each coordinate
            }
        }

        this.setOnMouseClicked((e) -> {
            //If the mouse is right-clicked, then do righClicked()
            if(e.getButton() != MouseButton.SECONDARY) return;
            rightClicked();
        });

    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * @param x the x coordinate of the block
     * @param y the y coordinate of the block
     * @return the block at these coordinates
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);
        //Add to the GridPane
        add(block,x,y);

        //Add mouse enter handler to create hover effect
        block.setOnMouseEntered(event -> {
            if(block.getValue() != 0) return;
            block.drawHover(block.getColor());
        });

        //Add mouse exit handler to return block back to normal
        block.setOnMouseExited(event -> {
            if(block.getValue() != 0) return;
            block.paintEmpty();
        });

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) ->  {
            if(e.getButton() != MouseButton.PRIMARY) return;
            blockClicked(e, block);
        });
        
        //Add to our block directory
        blocks[x][y] = block;
        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));



        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener to be set
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);

        if(blockClickedListener != null) {
            //If there is no listener, set the listener to the block
            blockClickedListener.blockClicked(block);
        }
    }

    /**
     * Set the listener for right-clicks
     * @param rightClickedListener to be set
     */
    public void setOnRightClick(RightClickedListener rightClickedListener) {
        //Set the right-clicked listener
        this.rightClickedListener = rightClickedListener;
    }

    /**
     * Handles the right click event by rotating a piece using the listener
     */
    private void rightClicked() {
        logger.info("Rotating piece: {}");
        if(this.rightClickedListener != null ) {
            //If there is no listener, set the listener to the block

            rightClickedListener.rightClick(this);
        }
    }

    /**
     * Sets whether the mouse should be tracked for block hover effect
     */
    public void setMouseTrack() {
        //Sets the mouse tracking
        mouseTrack = true;
    }

    /**
     * Animates the fadeout
     * @param coordinates for the animation to occur
     */
    public void fadeOut(HashSet<GameBlockCoordinate> coordinates) {
        for (GameBlockCoordinate coordinate : coordinates) {
            //For each block in the hashset, fade the block
            getBlock(coordinate.getX(), coordinate.getY()).fadeOut();
        }
    }
}
