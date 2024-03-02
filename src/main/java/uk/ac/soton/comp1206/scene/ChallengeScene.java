package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
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
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    /**
     * The ChallengeScene class represents the scene for the game challenge mode.
     */
    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

    /**
     * The game instance for the current game being played.
     */
    public Game game;

    /**
     * The PieceBoard for displaying the current piece.
     */
    protected PieceBoard pieceBoard;

    /**
     * The PieceBoard for displaying the following piece.
     */
    protected PieceBoard followingPieceBoard;

    /**
     * The GameBoard for the current game being played.
     */
    public GameBoard board;

    /**
     * The GameLoopListener for listening for when the Game gameLoop resets.
     */
    public GameLoopListener gameLoopListener;

    /**
     * Gets the current game instance.
     *
     * @return the current game instance
     */
    public Game getGame() {
        return game;
    }

    /**
     * The Text field variable for the high score.
     */
    protected Text highScore;

    /**
     * A boolean indicating whether the highs-core has shaken after new highs-core.
     */
    protected boolean shake = false;



    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }
    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {

        logger.info("Initialising Challenge");

        //Handle the event of key getting pressed
        scene.setOnKeyPressed(event -> {
            handleKeyPressed(event);
        });

        //Handle what happens when a block is clicked on the pieceBoard
        pieceBoard.setOnBlockClick(e -> {
            handlePieceBoardBlockClicked();
        });
        //Handle what happens when a block is clicked on followingPieceBoard
        followingPieceBoard.setOnBlockClick(e -> {
            handleFollowingPieceBoardBlockClicked();
        });

        //Set the LineClearedListener for game
        game.setLineClearedListener(blocksToBeCleared -> board.fadeOut(blocksToBeCleared));

        //Set the GameLoopListener for game
        game.setGameLoopListener(gameLoopListener);

        game.scoreProperty().addListener((observable, oldValue, newValue) -> {
            handleNewHighScore();
        });

        //Start the game
        game.start();

    }
    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        //Setup the game
        setupGame();
        //Root is gamePane
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        //New stack pane to hold everything
        var challengePane = new StackPane();

        //Style the challengePane
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        //Create a new borderpane to hold the different components
        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        //create a new board and size it to fit
        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        //Allow tracking on the board
        board.setMouseTrack();

        //Create PieceBoard and followingBoard for the scene
        pieceBoard = new PieceBoard(new Grid(3,3), gameWindow.getWidth()/5,gameWindow.getWidth()/5);
        followingPieceBoard = new PieceBoard(new Grid(3,3), gameWindow.getWidth()/7,gameWindow.getWidth()/7);

        //Set the gameBoard as the centre
        mainPane.setCenter(board);

        //Add score text and style
        Text scoreText = new Text("Score");
        scoreText.getStyleClass().add("heading");

        //Add multiplier text and style
        Text multiplierText = new Text("Multiplier");
        multiplierText.getStyleClass().add("heading");

        //Add lives text and style
        Text livesText = new Text("Lives");
        livesText.getStyleClass().add("heading");

        //Add highscores text and style
        Text highScoreText = new Text("HighScore: ");
        highScoreText.getStyleClass().add("heading");

        //Add level text and style
        Text levelText = new Text("level");
        levelText.getStyleClass().add("heading");

        //Add challengemode text and style
        Text challengeModeText = new Text("Challenge Mode");
        challengeModeText.getStyleClass().add("title");

        //Add scores int text and style
        Text score = new Text();
        score.textProperty().bind(game.scoreProperty().asString("%d"));
        score.getStyleClass().add("score");

        //Add Multiplier int text and style
        Text multiplier = new Text();
        multiplier.textProperty().bind(game.multiplierProperty().asString("%dx"));
        multiplier.getStyleClass().add("score");

        //Add the highest score as a text
        highScore = new Text("" + getHighScore());
        highScore.getStyleClass().add("hiscore");

        //Add the level int as a text
        Text level = new Text();
        level.textProperty().bind(game.levelProperty().asString("%d"));
        level.setTextAlignment(TextAlignment.CENTER);
        level.getStyleClass().add("level");

        //Add the lives as a text
        Text lives = new Text();
        lives.textProperty().bind(game.livesProperty().asString("%d"));
        lives.getStyleClass().add("lives");

        //Set the leftSidePanel to contain its corresponding components
        var leftSidePanel = new VBox();
        leftSidePanel.getChildren().addAll(scoreText, score, multiplierText, multiplier);
        leftSidePanel.setAlignment(Pos.TOP_CENTER);

        //Set the rightSidePanel to contain its corresponding components
        var rightSidePanel = new VBox();
        rightSidePanel.setSpacing(10);
        rightSidePanel.getChildren().addAll(livesText,lives, highScoreText, highScore, levelText, level,  pieceBoard, followingPieceBoard);
        rightSidePanel.setAlignment(Pos.TOP_CENTER);

        //Add the panels to the borderpane
        mainPane.setLeft(leftSidePanel);
        mainPane.setRight(rightSidePanel);
        mainPane.setTop(challengeModeText);
        mainPane.setAlignment(challengeModeText, Pos.CENTER);


        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        //Handle right-clicks on the GameBoard
        board.setOnRightClick(this::rightClicked);

        //Handle the game loop
        gameLoopListener = timerDelay -> handleTimeBar(timerDelay, mainPane);
    }


    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    protected void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }
    /**
     * Handle when the GameBoard is clicked on
     * @param board the GameBoard that is being clicked on
     */
    protected void rightClicked(GameBoard board) {
        logger.info("Rotating piece: {}", game.getCurrentPiece());
        Multimedia.playAudio("rotate.wav");
        //Reset the piece-board with the rotated piece
        pieceBoard.resetGrid();
        pieceBoard.displayPiece(game.rotatePiece());
    }


    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");
        NextPieceListener nextPieceListener = (nextPiece, followingPiece) -> handleNextPieceListener(nextPiece, followingPiece);
        GameOverListener gameOverListener = lives -> handleGameOverListener(lives);
        //Start new game
        game = new Game(5, 5);

        game.setNextPieceListener(nextPieceListener);
        game.setGameOverListener(gameOverListener);
    }



    /**
     * Handles the creation and animation of the timer bar for the challengeScene
     *
     * @param timerDelay the time in milliseconds for the timer bar to go down
     * @param mainPane the BorderPane where the timer bar will be displayed
     */
    protected void handleTimeBar(int timerDelay, BorderPane mainPane) {
        // Create the timer bar rectangle
        logger.info("Starting timerBar timer with {} seconds of time", timerDelay/1000);
        Rectangle timerBar = new Rectangle();
        timerBar.setWidth(gameWindow.getWidth());
        timerBar.setHeight(40);
        timerBar.setFill(Color.GREEN);
        mainPane.setBottom(timerBar);

        // Create the animation that will update the timer bar
        Timeline timeline = new Timeline(
                new KeyFrame(new Duration((double)timerDelay * 0.5), new KeyValue(timerBar.fillProperty(), Color.YELLOW)),
                new KeyFrame(new Duration((double)timerDelay * 0.75), new KeyValue(timerBar.fillProperty(), Color.RED)),
                new KeyFrame(new Duration((double)timerDelay), new KeyValue(timerBar.widthProperty(), 0)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     Handles key press events to control the game.
     If the escape key is pressed, the game is stopped and the menu scene is loaded.
     If the R or space key is pressed, the current and following pieces are swapped, and the piece board and following piece board are updated.
     If the Q, Z, or left brace key is pressed, the current piece is rotated three times, and the piece board is updated.
     If the E, C, or right brace key is pressed, the current piece is rotated once, and the piece board is updated.
     If the enter or X key is pressed, the block clicked is checked to determine if it can play the current piece, and the appropriate audio is played.
     If the W or up arrow key is pressed, the current Y value is decremented, and the block hovered over is updated.
     If the A or left arrow key is pressed, the current X value is decremented, and the block hovered over is updated.
     If the S or down arrow key is pressed, the current Y value is incremented, and the block hovered over is updated.
     If the D or right arrow key is pressed, the current X value is incremented, and the block hovered over is updated.
     @param event the key event to be handled
     */
    protected void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            //If escape key pressed, go the menu
            gameWindow.cleanup();
            gameWindow.loadScene(new MenuScene(gameWindow));
            Multimedia.stopMusic();
            game.stopTimer();
        }
        else if(event.getCode() == KeyCode.R || event.getCode() == KeyCode.SPACE) {
            //If R or space is pressed, rotate the board
            game.swapCurrentPiece();
            pieceBoard.resetGrid();
            pieceBoard.displayPiece(game.getCurrentPiece());
            followingPieceBoard.resetGrid();
            followingPieceBoard.displayPiece(game.getFollowingPiece());
            Multimedia.playAudio("transition.wav");

        }
        else if(event.getCode() == KeyCode.Q || event.getCode() == KeyCode.Z || event.getCode() == KeyCode.BRACELEFT) {
            //If Q, Z or BRACELEFT is pressed, rotate the piece left
            game.rotatePiece();
            game.rotatePiece();
            game.rotatePiece();
            pieceBoard.resetGrid();
            pieceBoard.displayPiece(game.getCurrentPiece());
            Multimedia.playAudio("rotate.wav");
        }
        else if(event.getCode() == KeyCode.E || event.getCode() == KeyCode.C || event.getCode() == KeyCode.BRACERIGHT) {
            //If E, c or BRACERIGHT is pressed, rotate the piece right
            game.rotatePiece();
            pieceBoard.resetGrid();
            pieceBoard.displayPiece(game.getCurrentPiece());
            Multimedia.playAudio("rotate.wav");
        }
        //If enter is pressed, play the piece
        else if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.X) {
            //If piece cant be played, play noise
            if(!(game.getGrid().canPlayPiece(game.getCurrentPiece(), game.getCurrentX(), game.getCurrentY()))) {
                Multimedia.playAudio("fail.wav");
            }
            else {
                //Play the piece
                game.blockClicked(board.getBlock(game.getCurrentX(), game.getCurrentY()));
            }

        }
        else if(event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
            if(game.getCurrentY() == 0) return;
            game.setCurrentY(game.getCurrentY() - 1);
            board.getBlock(game.getCurrentX(), game.getCurrentY()).drawHover(board.getBlock(game.getCurrentX(), game.getCurrentY()).getColor());
            board.getBlock(game.getCurrentX(), game.getCurrentY() + 1).removeHover();

        }
        else if(event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
            if(game.getCurrentX() == 0) return;
            game.setCurrentX(game.getCurrentX() - 1);
            board.getBlock(game.getCurrentX(), game.getCurrentY()).drawHover(board.getBlock(game.getCurrentX(), game.getCurrentY()).getColor());
            board.getBlock(game.getCurrentX() + 1, game.getCurrentY()).removeHover();
        }
        else if(event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN) {
            if(game.getCurrentY() == 4) return;
            game.setCurrentY(game.getCurrentY() + 1);
            board.getBlock(game.getCurrentX(), game.getCurrentY()).drawHover(board.getBlock(game.getCurrentX(), game.getCurrentY()).getColor());
            board.getBlock(game.getCurrentX(), game.getCurrentY() - 1).removeHover();
        }
        else if(event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
            if(game.getCurrentX() == 4) return;
            game.setCurrentX(game.getCurrentX() + 1);
            board.getBlock(game.getCurrentX(), game.getCurrentY()).drawHover(board.getBlock(game.getCurrentX(), game.getCurrentY()).getColor());
            board.getBlock(game.getCurrentX() - 1, game.getCurrentY()).removeHover();
        }
    }

    /**
     * Handles when the piece-board gets clicked on to rotate current piece
     */
    protected void handlePieceBoardBlockClicked() {
        //Rotate the piece and reset the grid
        pieceBoard.resetGrid();
        pieceBoard.displayPiece(game.rotatePiece());
        Multimedia.playAudio("rotate.wav");
    }

    /**
     * Handles what happens when the following PieceBoard is clicked on and pieces swap
     */
    protected void handleFollowingPieceBoardBlockClicked() {
        //Swaps the following piece and resets both grids
        game.swapCurrentPiece();
        pieceBoard.resetGrid();
        //For both boards, display the corresponding next piece
        pieceBoard.displayPiece(game.getCurrentPiece());
        followingPieceBoard.resetGrid();
        followingPieceBoard.displayPiece(game.getFollowingPiece());
    }

    /**
     * Handles what happens when nextPiece method in Game is called
     * @param nextPiece the next piece to be played
     * @param followingPiece the following piece after the next piece to be played
     */
    protected void handleNextPieceListener(GamePiece nextPiece, GamePiece followingPiece) {
        //Reset both grids
        pieceBoard.resetGrid();
        //Display the next piece and following piece
        pieceBoard.displayPiece(nextPiece);
        followingPieceBoard.resetGrid();
        followingPieceBoard.displayPiece(followingPiece);
    }

    /**
     * Handles what happens when the number of lives reaches -1 (game over)
     * @param lives the number of lives currently left
     */
    protected void handleGameOverListener(int lives) {
        if(lives == -1) {
            //If lives is less than 0, startScoresScene
            Platform.runLater(this::startScoresScene);
        }
    }

    /**
     * Starts the ScoresScene at the end of the game
     */
    protected void startScoresScene() {
        //Stop the timer
        game.stopTimer();
        Multimedia.stopMusic();
        this.gameWindow.cleanup();
        //Start the scoresScene
        this.gameWindow.startScoresScene(game);
    }

    /**
     * Gets the highest score to beat from a text file containing highs-cores
     * @return highScore the highest score to beat
     */
    protected int getHighScore() {
        int highScore = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/scores/scores.txt"))) {
            String line = reader.readLine(); // read only the first line
            int index = line.indexOf(":"); // find the index of the colon
            String scoreStr = line.substring(index + 1).trim(); // extract the score string
            highScore = Integer.parseInt(scoreStr); // convert the score string to an integer
            // use the score variable as needed
        } catch (IOException e) {
            System.err.println("Error reading scores from file: " + e.getMessage());
        }
        return highScore;
    }

    /**
     * Handles what happens when the user beats the highs-core
     * Plays an animation
     */
    protected void handleNewHighScore() {
        int newHighScore = game.scoreProperty().get();
        if (newHighScore > getHighScore() && !shake) {
            highScore.textProperty().unbind();
            highScore.textProperty().bind(game.scoreProperty().asString("%d"));

            //Create a timeline
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(highScore.scaleXProperty(), 1.0),
                            new KeyValue(highScore.scaleYProperty(), 1.0),
                            new KeyValue(highScore.fillProperty(), highScore.getFill())),
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(highScore.scaleXProperty(), 2),
                            new KeyValue(highScore.scaleYProperty(), 2),
                            new KeyValue(highScore.fillProperty(), Color.GREEN)),
                    new KeyFrame(Duration.seconds(2),
                            new KeyValue(highScore.scaleXProperty(), 1.0),
                            new KeyValue(highScore.scaleYProperty(), 1.0))
            );

            // Create a RotateTransition animation
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.2), highScore);
            rotateTransition.setByAngle(20);
            rotateTransition.setAutoReverse(true);
            rotateTransition.setCycleCount(6);

            // Play the animations together
            ParallelTransition parallelTransition = new ParallelTransition(highScore, timeline, rotateTransition);
            parallelTransition.play();
            shake = true;
        }
    }
}
