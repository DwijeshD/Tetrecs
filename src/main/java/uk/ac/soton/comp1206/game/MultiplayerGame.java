package uk.ac.soton.comp1206.game;
import javafx.application.Platform;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.*;

/**
 * Multiplayer
 */
public class MultiplayerGame extends Game{
    /**
     * Logger to keep track of events
     */
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
    /**
     * Communicator field variable to receive messages
     */
    private final Communicator communicator;
    /**
     * Linked list containing all the game pieces
     */
    private LinkedList<GamePiece> queue;
    /**
     * Arraylist of pairs of string, intgers
     */
    private ArrayList<Pair<String, Integer>> onlineScores;

    /**
     * Getter method to get teh arraylist
     * @return the arraylist
     */

    public ArrayList<Pair<String, Integer>> getOnlineScores() {
        return onlineScores;
    }

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols         number of columns
     * @param rows         number of rows
     * @param communicator the communicator being used
     */
    public MultiplayerGame(int cols, int rows, Communicator communicator) {
        super(cols, rows);
        this.communicator = communicator;
        this.queue = new LinkedList<>();
        communicator.addListener((communication -> Platform.runLater(() -> handleIncomingMessages(communication))));

    }


    /**
     * Starts the game
     */
    public void start() {
        logger.info("Starting game");
        initialise();
        startTimer();
    }

    /**
     * Initialise the game
     */
    public void initialise() {

        communicator.send("PIECE");
        communicator.send("PIECE");
        onlineScores = new ArrayList<>();
        nextOnlinePiece();
    }

    /**
     * Handles the incoming messages
     * @param msg the message to be handled
     */
    private void handleIncomingMessages(String msg) {
        logger.info("Message: {} has been received", msg);
        String[] components = msg.trim().split(":");
        String command = components[0];
        if (command.equals("PIECE") && components.length > 1) {
            createPiece(Integer.parseInt(components[1]));
        } else if (command.equals("SCORES") && components.length > 1) {
            getMultiplayerScores(components[1]);
        }
    }

    /**
     * Sends score to communicator
     */
    private void sendScore() {
        logger.info("Sending current score to server");
        communicator.send("SCORE " + scoreProperty().getValue());
    }

    /**
     * Gets the multiplayer scores
     * @param message message about the scores
     */
    private void getMultiplayerScores(String message) {
        logger.info("Recieved online scores {}", message);
        onlineScores.clear();
        String[] seperateScores = message.split("/n");

        for(int i = 0; i < seperateScores.length; ++i) {
            String nameAndPoints = seperateScores[i];
            String[] components = nameAndPoints.split(":");
            logger.info("Received score: {} = {}", components[0], Integer.parseInt(components[1]));
            onlineScores.add(new Pair(components[0], Integer.parseInt(components[1])));
        }

        sortScores(onlineScores);
    }

    /**
     * Sends state of the board to the server
     */
    public void sendBoard() {
        String board = "";

        for(int x = 0; x < this.cols; ++x) {
            for(int y = 0; y < this.rows; ++y) {
                int blockValue = this.grid.get(x, y);
                board += blockValue + " " + x + "," + y;
            }
        }
        communicator.send("BOARD " + board);
    }

    /**
     * Plays a piece
     * @return
     */
    private GamePiece playPiece() {
        GamePiece gamePiece = queue.getFirst();
        this.communicator.send("PIECE");
        queue.removeFirst();
        return gamePiece;
    }

    /**
     * Sets the current piece and following piece and removes first 2 pieces
     */
    private void nextOnlinePiece() {
        //Sets the current piece to the following piece
        currentPiece = queue.get(1);
        //Following piece becomes a new piece
        followingPiece = queue.get(2);
        queue.removeFirst();
        queue.remove(1);
        //Set the nextPieceListener for the new set of pieces
        nextPieceListener.nextPiece(currentPiece, followingPiece);
        //Restart timer
        resetTimer();
        logger.info("The next piece is: {} and the following piece is {}", currentPiece, followingPiece);
    }

    /**
     * Creates a piece according to int recieved by communicator
     * @param piece the piece to be played
     */
    private void createPiece(int piece) {

        GamePiece piece1 = GamePiece.createPiece(piece);
        logger.info("Creating piece " + piece1.getValue());
        queue.add(piece1);
        currentPiece = queue.getFirst();
    }

    /**
     * Updates the grid when a block gets clicked, and handles the events
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        if(grid.canPlayPiece(currentPiece, x, y)) {
            Multimedia.playAudio("place.wav");
            grid.playPiece(currentPiece, x, y);
            nextOnlinePiece();
            afterPiece();
            setScore(scoreProperty.get());
            sendBoard();
        }
        else {
            Multimedia.playAudio("fail.wav");
        }
    }

    /**
     * Sorts the scores of an arraylist
     * @param arrayList
     */
    private void sortScores(ArrayList arrayList) {
        Comparator<Pair<String, Integer>> comparator = Comparator.comparing(Pair::getValue);
        comparator = comparator.reversed();
        Collections.sort(arrayList, comparator);
    }

}
