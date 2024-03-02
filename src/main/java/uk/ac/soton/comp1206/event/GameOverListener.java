package uk.ac.soton.comp1206.event;
/**
 * The Game Over listener is used to handle the event when lives go below 0. It
 * passes the number of lives remaining
 */
public interface GameOverListener {
    /**
     * Handles what happens when the number of lives goes below 0
     * @param lives the number of lives remaining
     */
    void gameOver(int lives);
}
