package uk.ac.soton.comp1206.event;


/**
 * The GameLoop Listener is used for listening to the game loop resets
 */

public interface GameLoopListener {
    /**
     * Handles the gameLoop resetting by grabbing the timerDelay after each reset
     * @param timerDelay the time delay for each reset
     */
    void gameLoop(int timerDelay);
}
