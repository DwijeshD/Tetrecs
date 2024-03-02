package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBoard;

/**
 * Right click listener is used to handle what happens when a user right-clicks on the gameBoard.
 * The listener passes the board that is being clicked on
 */
public interface RightClickedListener {
    /**
     * Handles when the board is right-clicked on
     * @param board the board being right clicked on
     */
    void rightClick(GameBoard board);
}
