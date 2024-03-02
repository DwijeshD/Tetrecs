package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.Objects;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        Multimedia.playBackgroundMusic("menu.mp3");
        scene.setOnKeyPressed(event -> {
            //Handle escape key pressed, shutdown the app
            if (event.getCode() == KeyCode.ESCAPE) {
                App.getInstance().shutdown();
            }
        });
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        //Create stackPane to hold everything, size the stackPane
        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Title
        try{
            // Load the imag
            Image tetrecsLogoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/TetrECS.png")));
            ImageView tetrecsLogo = new ImageView(tetrecsLogoImage);

            //Set it to fit in the gameWindow
            tetrecsLogo.setFitHeight(gameWindow.getHeight()/5);
            tetrecsLogo.setPreserveRatio(true);

            //Add it to the menuPane
            menuPane.getChildren().add(tetrecsLogo);
            menuPane.setAlignment(tetrecsLogo, Pos.CENTER);

            //Create the rotation of the image
            Rotate rotate = new Rotate(0, gameWindow.getWidth()/4 + gameWindow.getWidth()/10,gameWindow.getHeight()/4);
            tetrecsLogo.getTransforms().add(rotate);

            // Create a Timeline that rotates the image back and forth
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), new KeyValue(rotate.angleProperty(), 0)),
                    new KeyFrame(Duration.seconds(3), new KeyValue(rotate.angleProperty(), 10)),
                    new KeyFrame(Duration.seconds(6), new KeyValue(rotate.angleProperty(), 0)),
                    new KeyFrame(Duration.seconds(9), new KeyValue(rotate.angleProperty(), -10)),
                    new KeyFrame(Duration.seconds(12), new KeyValue(rotate.angleProperty(), 0))
            );
            //Run the timeline infinitely
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }catch(Exception e ){
            System.err.println("Image file not found: " + e.getMessage());
            e.printStackTrace();
        }




        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var startGame = new Button("Single Player");
        startGame.getStyleClass().add("menuItem");
        var multiPlayer = new Button("Multiplayer");
        multiPlayer.getStyleClass().add("menuItem");
        var howToPlay = new Button("How to Play");
        howToPlay.getStyleClass().add("menuItem");
        var exit = new Button("Exit  ");
        exit.getStyleClass().add("menuItem");

        //Create VBox to contain the menu
        var buttonMenu = new VBox();
        buttonMenu.getStyleClass().add("vbox");
        buttonMenu.getChildren().addAll(startGame, multiPlayer, howToPlay, exit);
        buttonMenu.setAlignment(Pos.BOTTOM_CENTER);
        menuPane.getChildren().add(buttonMenu);

        //Bind the startGame button action to the startGame method in the menu
        startGame.setOnAction(event -> {
            startGame(event);
            Multimedia.playAudio("rotate.wav");
            Multimedia.stopMusic();
            gameWindow.cleanup();
        });

        //Bind the howToPlay button action to the instruction scene
        howToPlay.setOnAction(event -> {
            startInstructionScene(event);
            Multimedia.playAudio("rotate.wav");
            Multimedia.stopMusic();
            gameWindow.cleanup();
        });
        //Handles the menu button click
        multiPlayer.setOnAction(event -> {
            startLobbyScene(event);
            Multimedia.playAudio("pling.wav");
            Multimedia.stopMusic();
            gameWindow.cleanup();
        });

        //Bind the Exit button action to the shutdown method of App
        exit.setOnAction(handler -> {
            App.getInstance().shutdown();
        });
        //Handles the hover feature for startGame
        startGame.setOnMouseEntered(event -> {
            startGame.getStyleClass().clear();
            startGame.getStyleClass().add("menuItemSelected");
        });
        startGame.setOnMouseExited(event -> {
            startGame.getStyleClass().clear();
            startGame.getStyleClass().add("menuItem");
        });
        //Handles the hover feature for instructions
        howToPlay.setOnMouseEntered(event -> {
            howToPlay.getStyleClass().clear();
            howToPlay.getStyleClass().add("menuItemSelected");
        });
        howToPlay.setOnMouseExited(event -> {
            howToPlay.getStyleClass().clear();
            howToPlay.getStyleClass().add("menuItem");
        });
        //Handles the hover feature for multiplayer
        multiPlayer.setOnMouseEntered(event -> {
            multiPlayer.getStyleClass().clear();
            multiPlayer.getStyleClass().add("menuItemSelected");
        });
        multiPlayer.setOnMouseExited(event -> {
            multiPlayer.getStyleClass().clear();
            multiPlayer.getStyleClass().add("menuItem");
        });
        //Handles the hover feature for exit
        exit.setOnMouseEntered(event -> {
            exit.getStyleClass().clear();
            exit.getStyleClass().add("menuItemSelected");
        });
        exit.setOnMouseExited(event -> {
            exit.getStyleClass().clear();
            exit.getStyleClass().add("menuItem");
        });
    }



    /**
     * Handle when the Start Game button is pressed
     * @param event The event when the button is pressed
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

    /**
     * Handle when the Instructions button is pressed
     * @param event The event when the button is pressed
     */

    private void startInstructionScene(ActionEvent event) {
        gameWindow.startInstructionScene();
    }
    /**
     * Handle when the Multiplayer button is pressed
     * @param event The event when the button is pressed
     */
    private void startLobbyScene(ActionEvent event) {
        gameWindow.startLobbyScene();
    }

}
