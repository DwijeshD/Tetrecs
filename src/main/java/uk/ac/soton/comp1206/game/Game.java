package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {
    /**
     * Logger for keeping track of events
     */
    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    public final Grid grid;
    /**
     * A random object for random piece selection
     */
    protected Random random;
    /**
     * The current piece being checked
     */
    protected GamePiece currentPiece;
    /**
     * The current score value of the game.
     */
    protected IntegerProperty scoreProperty = new SimpleIntegerProperty(0);

    /**
     * The current level value of the game.
     */
    protected IntegerProperty levelProperty = new SimpleIntegerProperty(0);

    /**
     * The current number of lives the player has.
     */
    protected IntegerProperty livesProperty = new SimpleIntegerProperty(3);

    /**
     * The current multiplier value for lines cleared.
     */
    protected IntegerProperty multiplierProperty = new SimpleIntegerProperty(1);
    /**
     * A hashset containing all the blocks that need to be cleared
     */
    protected HashSet<GameBlockCoordinate> blocksToBeCleared;
    /**
     * The number of lines to be cleared
     */
    protected int linesToBeCleared;

    /**
     * A listener for receiving notifications when the next game piece is generated.
     */
    protected NextPieceListener nextPieceListener;

    /**
     * A listener for receiving notifications when a line is cleared.
     */
    protected LineClearedListener lineClearedListener;

    /**
     * A listener for receiving notifications when the game loop advances.
     */
    protected GameLoopListener gameLoopListener;

    /**
     * A listener for receiving notifications when the game is over.
     */
    protected GameOverListener gameOverListener;
    /**
     * The game piece currently being previewed as the next piece to play.
     */
    protected GamePiece followingPiece;

    /**
     * The coordinates of the currently selected game block.
     */
    protected Pair<Integer, Integer> currentSelectedBlock = new Pair<>(0, 0);

    /**
     * The executor service used to run the game loop.
     */
    protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    /**
     * Temporary place-holder piece
     */
    protected GamePiece tempPiece;

    /**
     * The scheduled task for running the game loop.
     */
    protected ScheduledFuture<?> loop;


    /**
     * Gets the current x-coordinate of the selected game block.
     *
     * @return The current x-coordinate of the selected game block.
     */
    public Integer getCurrentX() {
        return currentSelectedBlock.getKey();
    }
    /**
     * Gets the current y-coordinate of the selected game block.
     *
     * @return The current y-coordinate of the selected game block.
     */
    public Integer getCurrentY() {
        return currentSelectedBlock.getValue();
    }

    /**
     * Sets the current x-coordinate of the selected game block.
     *
     * @param X The new x-coordinate of the selected game block.
     */
    public void setCurrentX(Integer X) {
        this.currentSelectedBlock = new Pair<>(X, getCurrentY());
    }
    /**
     * Sets the current y-coordinate of the selected game block.
     *
     * @param Y The new y-coordinate of the selected game block.
     */
    public void setCurrentY(Integer Y) {
        this.currentSelectedBlock = new Pair<>(getCurrentX(), Y);
    }


    /**
     * Gets the current score of the game
     * @return the current score
     */
    public int getScore() {
        return scoreProperty.get();
    }

    /**
     * Exposes the IntegerProperty for score so that it can be accessed
     * @return the IntegerProperty for score
     */
    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }

    /**
     * Sets the current score to the specified value.
     * @param  score the new score to be set.
     */
    public void setScore(int score) {
        this.scoreProperty.set(score);
    }

    /**
     * Retrieves the current level.
     * @return The current level.
     */
    public int getLevel() {
        return levelProperty.get();
    }

    /**
     * Retrieves the property that represents the current level.
     * @return the IntegerProperty object that represents the current level.
     */
    public IntegerProperty levelProperty() {
        return levelProperty;
    }

    /**
     * Sets the current level to the specified value.
     * @param level The new level to be set.
     */
    public void setLevel(int level) {
        this.levelProperty.set(level);
    }

    /**
     * Retrieves the current number of lives.
     * @return The current number of lives.
     */
    public int getLives() {
        return livesProperty.get();
    }

    /**
     * Retrieves the property that represents the current number of lives.
     * @return The IntegerProperty object that represents the current number of lives.
     */
    public IntegerProperty livesProperty() {
        return livesProperty;
    }

    /**
     * Sets the current number of lives to the specified value.
     * @param lives the new number of lives to be set.
     */
    public void setLives(int lives) {
        this.livesProperty.set(lives);
    }

    /**
     * Retrieves the current multiplier value.
     * @return the current multiplier value.
     */
    public int getMultiplier() {
        return multiplierProperty.get();
    }

    /**
     * Retrieves the property that represents the current multiplier value.
     * @return the IntegerProperty object that represents the current multiplier value.
     */
    public IntegerProperty multiplierProperty() {
        return multiplierProperty;
    }

    /**
     * Sets the current multiplier value to the specified value.
     * @param multiplier the new multiplier value to be set.
     */

    public void setMultiplier(int multiplier) {
        this.multiplierProperty.set(multiplier);
    }

    /**
     * Gets the current GamePiece being played
     * @return the current (next) GamePiece to be played
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }
    /**
     * Sets the listener to be notified when the next piece is spawned.
     * @param nextPieceListener the listener to be set
     */
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }
    /**
     * Sets the listener to be notified when a line is cleared
     * @param lineClearedListener the listener to be set
     */

    public void setLineClearedListener(LineClearedListener lineClearedListener) {
        this.lineClearedListener = lineClearedListener;
    }
    /**
     * Sets the listener to be notified when the game loop resets
     * @param gameLoopListener the listener to be set
     */

    public void setGameLoopListener(GameLoopListener gameLoopListener) {
        this.gameLoopListener = gameLoopListener;
    }
    /**
     * Sets the listener to be notified when lives reaches 0
     * @param gameOverListener the listener to be set
     */
    public void setGameOverListener(GameOverListener gameOverListener) {
        this.gameOverListener = gameOverListener;
    }



    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */


    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        startTimer();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    private void initialiseGame() {
        logger.info("Initialising game");
        //Create the initial piece
        currentPiece = spawnPiece();
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
        tempPiece = currentPiece;
        Multimedia.playFileOnce("/music/game_start.wav", "game.wav");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        if(grid.canPlayPiece(currentPiece, x, y)) {
            Multimedia.playAudio("place.wav");
            grid.playPiece(currentPiece, x, y);
            nextPiece();
            afterPiece();
            level();
        }
        else {
            Multimedia.playAudio("fail.wav");
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Get the piece after the next piece (following piece)
     * @return the following piece
     */
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }

    /**
     * Creates a random piece
     * @return The next piece to be played
     */
    protected GamePiece spawnPiece() {

        //Creates a random piece from available pieces using RNG
        int pieceInt = new Random().nextInt(GamePiece.PIECES);
        int rotationInt = new Random().nextInt(3);
        GamePiece newPiece = GamePiece.createPiece(pieceInt, rotationInt);
        logger.info("Picking random piece {}", newPiece);
        return newPiece;
    }

    /**
     * his method generates the next game piece to be used in the game and returns it.
     */
    protected void nextPiece() {
        //Sets the current piece to the following piece
        currentPiece = followingPiece;
        //Following piece becomes a new piece
        followingPiece = spawnPiece();
        //Set the nextPieceListener for the new set of pieces
        nextPieceListener.nextPiece(currentPiece, followingPiece);
        //Restart timer
        resetTimer();
        logger.info("The next piece is: {} and the following piece is {}", currentPiece, followingPiece);
    }

    /**
     * Check to see if any rows/columns need clearing and clears them
     */
    protected void afterPiece() {

        //Hashset containing all the x and y coordinates of rows and columns that need to be cleared
        blocksToBeCleared = new HashSet<>();

        //Boolean to see if any lines have been cleared
        Boolean resetMultiplier = true;

        //Counts how many lines to be cleared so score and multiplier can be calculated
        linesToBeCleared = 0;

        //Check each column for a line
        for(var x = 0; x < grid.getRows(); x++) {
            var columnLineCounter = 0;
            for(var y = 0; y < grid.getCols(); y++) {
                if(grid.get(x,y) == 0) break;
                columnLineCounter++;
                if(columnLineCounter == 5) {
                    Multimedia.playAudio("clear.wav");
                    resetMultiplier = false;
                    //Add one to the line to be cleared if 5 in a row
                    linesToBeCleared++;
                    logger.info("Clearing vertical line at x = " + (x + 1));

                    //If there is a complete column, the x coordinate of the row gets added to the hashset
                    for(var yLine = 0; yLine < grid.getCols(); yLine++) {
                        GameBlockCoordinate coordinate = new GameBlockCoordinate(x,yLine);
                        blocksToBeCleared.add(coordinate);
                        //Add blocks to hashset
                    }
                }
            }
        }

        //Check each row for a line
        for(var y = 0; y < grid.getCols(); y++) {
            var rowLineCounter = 0;
            for(var x = 0; x < grid.getRows(); x++) {
                if(grid.get(x,y) == 0) break;
                rowLineCounter++;
                if(rowLineCounter == 5) {
                    Multimedia.playAudio("clear.wav");
                    resetMultiplier = false;
                    linesToBeCleared++;
                    logger.info("Clearing horizontal line at y = " + (y + 1));

                    //If there is a complete row, the y coordinate of the row gets added to the hashset
                    for (var xLine = 0; xLine < grid.getCols(); xLine++) {
                        GameBlockCoordinate coordinate = new GameBlockCoordinate(xLine, y);
                        blocksToBeCleared.add(coordinate);
                    }
                }
            }
        }
        score(linesToBeCleared, blocksToBeCleared.size());
        //Calculate new score
        multiplier(resetMultiplier);
        //See if multiplier needs to reset
        lineClearedListener.lineCleared(blocksToBeCleared);
        //Handle the line-cleared event
        //Removes the lines in the hashset
        for(GameBlockCoordinate coordinate : blocksToBeCleared) {
            grid.set(coordinate.getX(), coordinate.getY(), 0);
        }
    }

    /**
     * Updates the score depending on how many blocks and lines were cleared
     * @param lines The number of lines cleared by the last piece
     * @param blocks The number of blocks cleared by the last peice
     */
    protected void score(int lines, int blocks) {
        scoreProperty.set(scoreProperty.get() + (lines * blocks * 10 * multiplierProperty.get()));
    }

    /**
     * Calculates what the multiplier is
     * @param resetMultiplier local variable in afterPiece that informs of whether a line had been cleared previously
     */
    protected void multiplier(Boolean resetMultiplier) {
        //If there are no lines cleared, reset the multiplier
        if(resetMultiplier) {
            this.multiplierProperty.set(1);
            logger.info("Resetting multiplier to 1");
        }
        else {
            //Otherwise add one to the multiplier
            multiplierProperty.set(multiplierProperty.get() + 1);
            logger.info("New multiplier is {}", multiplierProperty.get());
        }
    }

    /**
     * Sets number of levels according to the score
     */
    protected void level() {
        //Play sound for each new level
        int newLevel = (int) Math.floor(scoreProperty.get() / 1000.0);
        if(newLevel != getLevel()) {
            Multimedia.playAudio("level.wav");
        }
        levelProperty.set(newLevel);
    }

    /**
     * Rotates the currentPiece
     * @return the rotated currentPiece
     */
    public GamePiece rotatePiece() {
        //Rotates the piece
        this.currentPiece.rotate();
        return currentPiece;
    }

    /**
     * Swaps the current piece with the followingPiece
     */
    public void swapCurrentPiece() {
        logger.info("Swapping {} for {}", currentPiece, followingPiece);
        //Set swapPiece to currentPiece
        GamePiece swapPiece = currentPiece;
        //Set currentPiece to the followingPiece
        currentPiece = followingPiece;
        //Set followingPiece to currentPiece
        followingPiece = swapPiece;
        //Set tempPiece to currentPiee
        tempPiece = currentPiece;
        Multimedia.playAudio("transition.wav");
    }

    /**
     * Gets the amount of time left
     * @return the lowest value bout of the delay and 250 ms
     */
    protected int getTimerDelay() {
        //Calculate the delay
        int delay = 12000 - (getLevel() * 750);
        return Math.max(delay, 250);
    }

    /**
     * Starts the executor to carry out gameLoop at intervals of time
     */
    protected void startTimer() {
        logger.info("Starting Timer with {} seconds of time ", getTimerDelay()/1000);
        //Starts the loop so that the gameloop can run
        loop = executor.scheduleAtFixedRate(this::gameLoop, getTimerDelay(), getTimerDelay(), TimeUnit.MILLISECONDS);
        gameLoopListener.gameLoop(getTimerDelay());
    }

    /**
     * Resets the timer
     */
    protected void resetTimer() {
        logger.info("Resetting timer with {} seconds of time", getTimerDelay()/1000);
        loop.cancel(false);
        startTimer();
    }

    /**
     * Stops the timer from running
     */
    public void stopTimer() {
        logger.info("Shutting down timer");
        executor.shutdownNow();
        Multimedia.stopMusic();
    }

    /**
     * The gameLoop is the logic behind what happens if the player fails to place a piece
     * on time. For example, a reduction in lives
     */
    protected void gameLoop() {
        // If the piece is still the same after the Timer runs out
        logger.info("Piece has not changed, deduction 1 life");
        setLives(getLives() - 1);
        setMultiplier(1);
        Multimedia.playAudio("lifelose.wav");
        gameOverListener.gameOver(getLives());
        nextPiece();
        resetTimer();
    }
}
