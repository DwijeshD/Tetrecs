package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class MultiplayerScene extends ChallengeScene {

    /**
     * The ChallengeScene class represents the scene for the game challenge mode.
     */
    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
//    private Communicator communicator;
//    private BorderPane mainPane;
//    private MultiplayerGame game;
//    private VBox players;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
//        this.communicator = gameWindow.getCommunicator();
    }
//
//
//    /**
//     * Build the Challenge window
//     */
//    @Override
//    public void build() {
//        logger.info("Building " + this.getClass().getName());
//        //Setup the game
//        setupGame();
//        //Root is gamePane
//        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
//
//        //New stack pane to hold everything
//        var challengePane = new StackPane();
//
//        //Style the challengePane
//        challengePane.setMaxWidth(gameWindow.getWidth());
//        challengePane.setMaxHeight(gameWindow.getHeight());
//        challengePane.getStyleClass().add("menu-background");
//        root.getChildren().add(challengePane);
//
//        //Create a new borderpane to hold the different components
//        mainPane = new BorderPane();
//        challengePane.getChildren().add(mainPane);
//
//        //create a new board and size it to fit
//        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
//        //Allow tracking on the board
//        board.setMouseTrack();
//
//        //Create PieceBoard and followingBoard for the scene
//        pieceBoard = new PieceBoard(new Grid(3,3), gameWindow.getWidth()/5,gameWindow.getWidth()/5);
//        followingPieceBoard = new PieceBoard(new Grid(3,3), gameWindow.getWidth()/7,gameWindow.getWidth()/7);
//
//        //Set the gameBoard as the centre
//        mainPane.setCenter(board);
//
//        //Add score text and style
//        Text scoreText = new Text("Score");
//        scoreText.getStyleClass().add("heading");
//
//        //Add multiplier text and style
//        Text multiplierText = new Text("Multiplier");
//        multiplierText.getStyleClass().add("heading");
//
//        //Add lives text and style
//        Text livesText = new Text("Lives");
//        livesText.getStyleClass().add("heading");
//
//        //Add highscores text and style
//        Text highScoreText = new Text("HighScore: ");
//        highScoreText.getStyleClass().add("heading");
//
//        //Add level text and style
//        Text levelText = new Text("level");
//        levelText.getStyleClass().add("heading");
//
//        //Add challengemode text and style
//        Text challengeModeText = new Text("Multiplayer Mode");
//        challengeModeText.getStyleClass().add("title");
//
//        //Add scores int text and style
//        Text score = new Text();
//        score.textProperty().bind(game.scoreProperty().asString("%d"));
//        score.getStyleClass().add("score");
//
//        //Add Multiplier int text and style
//        Text multiplier = new Text();
//        multiplier.textProperty().bind(game.multiplierProperty().asString("%dx"));
//        multiplier.getStyleClass().add("score");
//
//
//        //Add the level int as a text
//        Text level = new Text();
//        level.textProperty().bind(game.levelProperty().asString("%d"));
//        level.setTextAlignment(TextAlignment.CENTER);
//        level.getStyleClass().add("level");
//
//        //Add the lives as a text
//        Text lives = new Text();
//        lives.textProperty().bind(game.livesProperty().asString("%d"));
//        lives.getStyleClass().add("lives");
//
//
//        //Set the leftSidePanel to contain its corresponding components
//        var leftSidePanel = new VBox();
//        leftSidePanel.getChildren().addAll(scoreText, score, multiplierText, multiplier);
//        leftSidePanel.setAlignment(Pos.TOP_CENTER);
//
////        players = new VBox();
////        for(Pair pair: game.getOnlineScores() ) {
////            Text name = new Text((String) pair.getKey());
////            Text playerScore = new Text((String) pair.getValue());
////            players.getChildren().addAll(name, playerScore);
////        }
//        //Set the rightSidePanel to contain its corresponding components
//        var rightSidePanel = new VBox();
//        rightSidePanel.setSpacing(10);
//        rightSidePanel.getChildren().addAll(livesText,lives, players, levelText, level,  pieceBoard, followingPieceBoard);
//        rightSidePanel.setAlignment(Pos.TOP_CENTER);
//
//
//
//        //Add the panels to the borderpane
//        mainPane.setLeft(leftSidePanel);
//        mainPane.setRight(rightSidePanel);
//        mainPane.setTop(challengeModeText);
//        mainPane.setAlignment(challengeModeText, Pos.CENTER);
//
//
//        //Handle block on gameboard grid being clicked
//        board.setOnBlockClick(this::blockClicked);
//
//        //Handle right-clicks on the GameBoard
//        board.setOnRightClick(this::rightClicked);
//
//        //Handle the game loop
//        gameLoopListener = timerDelay -> handleTimeBar(timerDelay, mainPane);
//    }
//
//
//    /**
//     * Setup the game object and model
//     */
//    public void setupGame() {
//        logger.info("Starting a new challenge");
//        NextPieceListener nextPieceListener = (nextPiece, followingPiece) -> handleNextPieceListener(nextPiece, followingPiece);
//        GameOverListener gameOverListener = lives -> handleGameOverListener(lives);
//        //Start new game
//        game = new MultiplayerGame(5, 5, communicator);
//        game.setNextPieceListener(nextPieceListener);
//        game.setGameOverListener(gameOverListener);
//    }
//
//
//
//
//    /**
//     Handles key press events to control the game.
//     If the escape key is pressed, the game is stopped and the menu scene is loaded.
//     If the R or space key is pressed, the current and following pieces are swapped, and the piece board and following piece board are updated.
//     If the Q, Z, or left brace key is pressed, the current piece is rotated three times, and the piece board is updated.
//     If the E, C, or right brace key is pressed, the current piece is rotated once, and the piece board is updated.
//     If the enter or X key is pressed, the block clicked is checked to determine if it can play the current piece, and the appropriate audio is played.
//     If the W or up arrow key is pressed, the current Y value is decremented, and the block hovered over is updated.
//     If the A or left arrow key is pressed, the current X value is decremented, and the block hovered over is updated.
//     If the S or down arrow key is pressed, the current Y value is incremented, and the block hovered over is updated.
//     If the D or right arrow key is pressed, the current X value is incremented, and the block hovered over is updated.
//     @param event the key event to be handled
//     */
//    @Override
//    protected void handleKeyPressed(KeyEvent event) {
//        super.handleKeyPressed(event);
//        if (event.getCode() == KeyCode.T) {
//            TextField enterMessage = new TextField();
//            enterMessage.setPrefWidth(gameWindow.getWidth());
//            enterMessage.setOnKeyPressed(event1 -> {
//                if (event1.getCode() == KeyCode.ENTER) {
//                    communicator.send("MSG " + enterMessage.getText());
//                }
//            });
//            mainPane.setBottom(enterMessage);
//        }
//
//    }
//
//
//
//
//
//    /**
//     * Handles what happens when the number of lives reaches -1 (game over)
//     * @param lives the number of lives currently left
//     */
//    protected void handleGameOverListener(int lives) {
//        if(lives == -1) {
//            //If lives is less than 0, startScoresScene
//            Platform.runLater(this::startScoresScene);
//        }
//    }
//
//    /**
//     * Starts the ScoresScene at the end of the game
//     */
//    protected void startScoresScene() {
//        //Stop the timer
//        game.stopTimer();
//        Multimedia.stopMusic();
//        this.gameWindow.cleanup();
//        //Start the scoresScene
//        this.gameWindow.startScoresScene(game);
//    }
}
