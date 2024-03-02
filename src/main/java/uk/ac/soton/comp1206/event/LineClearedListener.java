package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.HashSet;

/**
 * The LineClearedListener handles what happens when a line is cleared. It passes a
 * hashmap of the blocks to be cleared in a line
 */
public interface LineClearedListener {
    /**
     * Handles the event when a line needs to be cleared
     * @param blocksToBeCleared the blocks to be cleared
     */
    void lineCleared(HashSet<GameBlockCoordinate> blocksToBeCleared);
}
