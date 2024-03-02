package uk.ac.soton.comp1206.scene;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import java.util.Objects;

/**
 * The instruction scene is a visual demonstration of how the game should be
 * played, what the controls are, what pieces are available, and the general rules
 * of the game.
 */

public class InstructionScene extends BaseScene{

    /**
     * Logger to keep track of events
     */
    private static final Logger logger = LogManager.getLogger(InstructionScene.class);
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instruction Scene");
    }

    /**
     * Initialise the InstructionScene
     */
    @Override
    public void initialise() {
        //Start playing background music
        Multimedia.playBackgroundMusic("menu.mp3");
        //Set on key pressed
        scene.setOnKeyPressed(event -> {
            //If the escape key is pressed, exit to the menu, stop music and cleanup
            if (event.getCode() == KeyCode.ESCAPE) {
                gameWindow.cleanup();
                Multimedia.stopMusic();
                gameWindow.loadScene(new MenuScene(gameWindow));
            }
        });

    }

    /**
     * Build the Instruction scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        var mainPane = new BorderPane();

        //Set up the main layout of borderpane so that it fits the window
        mainPane.setMaxWidth(gameWindow.getWidth());
        mainPane.setMaxHeight(gameWindow.getHeight());
        mainPane.getStyleClass().add("menu-background");
        root.getChildren().add(mainPane);

        //Load and set the instruction image to fit the window
        Image instructionImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Instructions.png")));
        ImageView instructionImageView = new ImageView(instructionImage);            //Set it to fit in the gameWindow
        instructionImageView.setFitWidth((gameWindow.getWidth()/1.5));
        instructionImageView.setPreserveRatio(true);

        //Align the image in the top centre
        mainPane.setAlignment(instructionImageView, Pos.TOP_CENTER);
        mainPane.setCenter(instructionImageView);

        //Create vbox to hold the top writing section of the instructions scene
        VBox topPanel = new VBox();
        topPanel.setAlignment(Pos.CENTER);

        //Create the instruction title
        Text instructionTitle = new Text("Instructions");
        instructionTitle.getStyleClass().add("instructionsTitle");

        //Create and adjust the instructions text so that it fits the window and is centres
        Text instructionText = new Text("TetrECS is a fast-paced gravity-free block placement game, where you must survive by clearing rows through careful placement of the upcoming blocks before the time runs out. Lose all 3 lives and you're destroyed!");
        instructionText.setTextAlignment(TextAlignment.CENTER);
        instructionText.getStyleClass().add("instructions");
        instructionText.setWrappingWidth(gameWindow.getWidth());

        //Add both to the topPanel VBox, so they are vertically arranged
        topPanel.getChildren().add(instructionTitle);
        topPanel.getChildren().add(instructionText);

        //Add the VBox to the top of the borderpane
        mainPane.setTop(topPanel);

        //Create gridPane to hold all the possible pieces
        GridPane piecesGridPane = new GridPane();
        piecesGridPane.setAlignment(Pos.CENTER);

        //Sets gaps between the the grids
        piecesGridPane.setHgap(10);
        piecesGridPane.setVgap(10);

        //Loop through all the game pieces and create a pieceBoard for each one, then add the pieceBoard to the gridPane
        var j = 0;
        var k = 0;
        for(var i = 0; i < GamePiece.PIECES; i++) {
            Grid grid = new Grid(3,3);
            GamePiece gamePiece = GamePiece.createPiece(i);
            PieceBoard pieceBoard = new PieceBoard(grid, 50, 50);
            pieceBoard.displayPiece(gamePiece);
            piecesGridPane.add(pieceBoard, k, j);
            k++;
            if (k == 5) {
                k = 0;
                j++;
            }
        }
        //Create a new Text for Game Piecess and style it
        Text gamePiecesText = new Text("Game Pieces");
        gamePiecesText.getStyleClass().add("instructionsTitle");

        //Place the text and the grid pane in a vbox in the centre of the screen
        VBox bottomPanel = new VBox();
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.getChildren().addAll(gamePiecesText, piecesGridPane);
        mainPane.setBottom(bottomPanel);
    }
}
