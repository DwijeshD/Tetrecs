package uk.ac.soton.comp1206.scene;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Scores scene dynamically reveals the online high scores and the local high scores, as well as
 * asking the user for their name if they happen to beat any of the high scores (top 10 from each list)
 */
public class ScoresScene extends BaseScene{


    /**
     * The logger to keep track of events
     */
    private static final Logger logger = LogManager.getLogger(uk.ac.soton.comp1206.scene.ScoresScene.class);
    /**
     * The array list to hold the local scores
     */
    private ArrayList<Pair<String, Integer>> localScoresArrayList;
    /**
     * The array list to hold the remote scores
     */
    private ArrayList<Pair<String, Integer>> remoteScoresArrayList;
    /**
     * The wrapper that allows the local scores arraylist to be bound to
     */
    private SimpleListProperty<Pair<String, Integer>> localScoresArrayListProperty;
    /**
     * The wrapper that allows the remote scores arraylist to be bound to
     */
    private SimpleListProperty<Pair<String, Integer>> remoteScoresArrayListProperty;
    /**
     * The communicator field variable for the class
     */
    private Communicator communicator;
    /**
     * The game field variable for the current scores
     */
    private Game game;
    /**
     * the localScoreBox VBox field variable for containing local scores grid
     */
    private VBox localScoreBox;
    /**
     * the remoteScoreBox VBox field variable for containing remote scores grid
     */
    private VBox remoteScoreBox;
    /**
     * The primary VBox for containing everything
     */
    private VBox primaryVbox;
    /**
     * The borderpane for the scene
     */
    private BorderPane mainPane;
    /**
     * Text field variable for the high score text
     */

    private Text hiscoreText;
    /**
     * Text field for entering users name
     */

    private TextField nameField;
    /**
     * Button for moving onto the scores
     */
    private Button button;
    /**
     * int field variable to ensure the user cannot click multiple times
     */
    private int numButtonClicks;
    /**
     * ScoresList custom component for the local scores
     */
    private ScoresList localScoresList;
    /**
     * ScoresList custom component for the remote scores
     */
    private ScoresList remoteScoresList;
    /**
     * String for the users name after entering it into the Text Field
     */
    private String nameText;
    /**
     * Boolean variable to keep track of if a local high-score has been beaten
     */
    private Boolean localScoreHasBeenBeaten = false;
    /**
     * Boolean variable to keep track of if a remote high-score has been beaten
     */
    private Boolean remoteScoreHasBeenBeaten = false;
    /**
     * HBox to contain text-field and button for entering users name
     */
    private HBox enterScoreHBox;
    /**
     * Text to display if the user has been a high score
     */
    private Text beatenScoreText;
    /**
     * VBox for the input name screen
     */
    private VBox inputNameVBox;
    /**
     * boolean field variable to keep track of whether the scores have been shown
     */
    private boolean scoresHaveBeenShown = false;
    /**
     * File root for the local scores
     */

    private static final String fileName = "src/main/resources/scores/scores.txt";


    /**
     * Create a new score scene
     * @param gameWindow the Game Window this will be displayed in
     * @param game the game being played
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        this.communicator = gameWindow.getCommunicator();
        Multimedia.playFileOnce("/music/end.wav", "menu.mp3");
        logger.info("Creating Scores Scene");
        localScoresArrayList = new ArrayList<>();
        remoteScoresArrayList = new ArrayList<>();
        localScoresArrayListProperty = new SimpleListProperty<>();
        remoteScoresArrayListProperty = new SimpleListProperty<>();
        localScoresList = new ScoresList();
        remoteScoresList = new ScoresList();
        numButtonClicks = 0;
    }

    /**
     * Initialise teh ScoresScene
     */
    @Override
    public void initialise() {
        logger.info("Initialising Scores Scene");

        scene.setOnKeyPressed(this::handleKeyPressed);
        checkIfFileExists();
        loadScores();
        loadOnlineScores("HISCORES");
        checkLocalScoreHasBeenBeaten();
        checkRemoteScoreHasBeenBeaten();
        sortScores(localScoresArrayList);
        sortScores(remoteScoresArrayList);
        handleIfScoreBeaten();
    }

    /**
     * A checker to see if the files exist, if it doesn't, then generate random scores
     */
    private void checkIfFileExists() {
        File file = new File(fileName);
        boolean fileExists = file.exists();
        if (!fileExists) {
            logger.info("Could not find the scores file");
            // Write default list of scores to file
            localScoresArrayList.add(new Pair<>("Oli", 10000));
            localScoresArrayList.add(new Pair<>("Oli", 9000));
            localScoresArrayList.add(new Pair<>("Oli", 8000));
            localScoresArrayList.add(new Pair<>("Oli", 7000));
            localScoresArrayList.add(new Pair<>("Oli", 6000));
            localScoresArrayList.add(new Pair<>("Oli", 5000));
            localScoresArrayList.add(new Pair<>("Oli", 4000));
            localScoresArrayList.add(new Pair<>("Oli", 3000));
            localScoresArrayList.add(new Pair<>("Oli", 2000));
            localScoresArrayList.add(new Pair<>("Oli", 1000));
        }
        checkLocalScoreHasBeenBeaten();
    }

    /**
     * Load scores from the file path and add them to the localScoresArrayList
     */
    private void loadScores() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null && !line.isEmpty()) {
                String[] parts = line.split(":");
                String name = parts[0].trim();
                int score = Integer.parseInt(parts[1].trim());
                localScoresArrayList.add(new Pair<>(name, score));
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);

        }
    }

    /**
     * Retrieve the online scores using communicator, and store the scores in remoteScoresArrayList
     * @param s
     */
    private void loadOnlineScores(String s) {
        communicator.send(s);
        logger.info("Receiving online scores");
        communicator.addListener(communication -> {
            String[] lines = communication.split("\n");
            for (String line : lines) {
                if(line.contains("HISCORES ")) {
                    line = line.replace("HISCORES ", "");
                }
                String[] parts = line.split(":", 2);
                var name = parts[0];
                var score = Integer.parseInt(parts[1]);
                var pair = new Pair<>(name, score);
                remoteScoresArrayList.add(pair);
            }
            checkRemoteScoreHasBeenBeaten();
        });
    }

    /**
     * Writes scores to the local scores file
     */
    private void writeScores() {
        logger.info("Writing scores to {}", fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            File file = new File(fileName);
            boolean fileExists = file.exists();
            if (!fileExists) {
                logger.info("Could not find the scores file");
                // Write default list of scores to file
                localScoresArrayList.add(new Pair<>("Player 1", 10000));
                localScoresArrayList.add(new Pair<>("Player 2", 9000));
                localScoresArrayList.add(new Pair<>("Player 3", 8000));
            }

            // Clear the file before writing scores
            new FileWriter(fileName, false).close();

            // Sort and write the scores to the file
            sortScores(localScoresArrayList);
            for(Pair pair : localScoresArrayList) {
                writer.write(pair.getKey() + ": " + pair.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing scores to file: " + e.getMessage());
        }
    }

    /**
     * Sends the server the score
     * @param nameText the name of the player
     */
    private void writeOnlineScore(String nameText) {
        communicator.send("HISCORE <" + nameText + ">:<" + game.getScore() + ">");
    }



    /**
     * Build the ScoreScene layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(this.gameWindow.getWidth(), this.gameWindow.getHeight());
        mainPane = new BorderPane();
        mainPane.getStyleClass().add("menu-background");
        root.getChildren().add(mainPane);

        //Create the nameField so that the user can enter their name
        nameField = new TextField();
        nameField.getStyleClass().add("space-textfield");
        nameField.setPromptText("Name");
        setTextFieldLimit(nameField, 5);
        button = new Button();
        button.getStyleClass().add("menuItem");
        handleMouse();

        beatenScoreText = new Text();
        beatenScoreText.getStyleClass().add("heading");
        beatenScoreText.setWrappingWidth(gameWindow.getWidth());

        inputNameVBox = new VBox();
        inputNameVBox.setSpacing(20);
        inputNameVBox.setAlignment(Pos.CENTER);

        enterScoreHBox = new HBox();
        enterScoreHBox.setSpacing(10);
        enterScoreHBox.setAlignment(Pos.CENTER);

        localScoreBox = new VBox();
        localScoreBox.setAlignment(Pos.TOP_CENTER);
        localScoreBox.setSpacing(20.0);

        remoteScoreBox = new VBox();
        remoteScoreBox.setAlignment(Pos.TOP_CENTER);
        remoteScoreBox.setSpacing(20.0);

        primaryVbox = new VBox();
        primaryVbox.setAlignment(Pos.TOP_CENTER);

        Text gameOverText = new Text("Game Over");
        gameOverText.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(gameOverText, Priority.ALWAYS);
        gameOverText.getStyleClass().add("bigtitle");
        primaryVbox.getChildren().add(gameOverText);

        hiscoreText = new Text("High Scores");
        hiscoreText.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(hiscoreText, Priority.ALWAYS);
        hiscoreText.getStyleClass().add("title");
        hiscoreText.setFill(Color.YELLOW);
        primaryVbox.getChildren().add(hiscoreText);

        GridPane localScoreGrid = new GridPane();
        localScoreGrid.setAlignment(Pos.CENTER);
        localScoreGrid.setHgap(100.0);
        localScoreBox.getChildren().add(localScoreGrid);

        GridPane remoteScoreGrid = new GridPane();
        remoteScoreGrid.setAlignment(Pos.CENTER);
        remoteScoreGrid.setHgap(100.0);
        remoteScoreBox.getChildren().add(remoteScoreGrid);

        Text localScoresLabel = new Text("Local Scores");
        localScoresLabel.setTextAlignment(TextAlignment.CENTER);
        localScoresLabel.getStyleClass().add("heading");
        GridPane.setHalignment(localScoresLabel, HPos.CENTER);
        localScoreGrid.add(localScoresLabel, 0, 0);

        Text remoteScoresLabel = new Text("Online Scores");
        remoteScoresLabel.setTextAlignment(TextAlignment.CENTER);
        remoteScoresLabel.getStyleClass().add("heading");
        GridPane.setHalignment(remoteScoresLabel, HPos.CENTER);
        remoteScoreGrid.add(remoteScoresLabel, 0, 0);

    }


    /**
     * If local score has been beaten, add a TextField and a button to centre of mainPane and
     * receive the name from the user
     */
    private void handleIfScoreBeaten() {
        checkRemoteScoreHasBeenBeaten();
        checkLocalScoreHasBeenBeaten();
        if(localScoreHasBeenBeaten || remoteScoreHasBeenBeaten) {
            if(localScoreHasBeenBeaten) {
                beatenScoreText.setText("Congratulations, you made it to the local leaderboard!");
                beatenScoreText.setTextAlignment(TextAlignment.CENTER);
                mainPane.setTop(beatenScoreText);
            }
            else if(remoteScoreHasBeenBeaten) {
                beatenScoreText.setText("Congratulations, you made it to the remote leaderboard!");
                mainPane.setTop(beatenScoreText);
            }
            if(numButtonClicks == 1) return;
            button.setText("Enter name");
            enterScoreHBox.getChildren().addAll(nameField, button);
            inputNameVBox.getChildren().addAll(beatenScoreText, enterScoreHBox);
            mainPane.setCenter(inputNameVBox);
            button.setOnAction(event -> showScores());
        }
        else {
            button.setText("Reveal scores");
            enterScoreHBox.getChildren().add(button);
            mainPane.setCenter(enterScoreHBox);
            button.setOnAction(event -> showScores());
            logger.info("Player did not make it onto the leaderboard");
        }
    }

    /**
     * Displays the scores by adding components to the current screen
     */
    private void showScores() {

        checkRemoteScoreHasBeenBeaten();
        checkLocalScoreHasBeenBeaten();

        if(localScoreHasBeenBeaten) {
            if (this.nameField.getText().isEmpty()) {
                nameText = "Guest";
            } else {
                nameText = nameField.getText();
            }
            localScoresArrayList.add(new Pair<>(nameText, game.getScore()));
            logger.info("Added new local score: {}: {}", nameText, game.getScore());
        }
        if(remoteScoreHasBeenBeaten) {
            logger.info("Online Score has been beaten!");
            if (this.nameField.getText().isEmpty()) {
                nameText = "Guest";
            } else {

                nameText = nameField.getText();
            }
            remoteScoresArrayList.add(new Pair<>(nameText, game.getScore()));
            logger.info("Added new remote score: {}: {}", nameText, game.getScore());
        }

        writeOnlineScore(nameText);
        writeScores();

        sortScores(localScoresArrayList);
        sortScores(remoteScoresArrayList);

        localScoresArrayListProperty.set(FXCollections.observableArrayList(localScoresArrayList));
        remoteScoresArrayListProperty.set(FXCollections.observableArrayList(remoteScoresArrayList));

        localScoresList.scoresProperty().bind(localScoresArrayListProperty);
        remoteScoresList.scoresProperty().bind(remoteScoresArrayListProperty);

        //Once button has been clicked, mainPane updates with the ScoreBox containing scoresList
        HBox mainHBox = new HBox();
        mainHBox.getChildren().addAll(localScoreBox, remoteScoreBox);
        mainHBox.setSpacing(100);
        mainHBox.setAlignment(Pos.CENTER);

        primaryVbox.getChildren().add(mainHBox);
        primaryVbox.setSpacing(20);
        mainPane.setCenter(primaryVbox);

        localScoreBox.getChildren().add(localScoresList);
        remoteScoreBox.getChildren().add(remoteScoresList);

        localScoresList.setAlignment(Pos.CENTER);
        remoteScoresList.setAlignment(Pos.CENTER);


        //Do dynamic reveal of scoresList
        localScoresList.reveal();
        remoteScoresList.reveal();
        scoresHaveBeenShown = true;
        numButtonClicks++;
    }

    /**
     * Handles what happens when a key is pressed
     * @param event the key pressed
     */
    private void handleKeyPressed(KeyEvent event) {
        if(scoresHaveBeenShown) {
            gameWindow.cleanup();
            Multimedia.stopMusic();
            gameWindow.loadScene(new MenuScene(gameWindow));
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            gameWindow.cleanup();
            Multimedia.stopMusic();
            gameWindow.loadScene(new MenuScene(gameWindow));
        }
        else if(event.getCode() == KeyCode.ENTER) {
            showScores();
        }
    }

    /**
     * Handles when the mouse hovers over the button
     */
    private void handleMouse() {
        button.setOnMouseEntered(event -> {
            button.getStyleClass().clear();
            button.getStyleClass().add("menuItemSelected");
        });

        button.setOnMouseExited(event -> {
            button.getStyleClass().clear();
            button.getStyleClass().add("menuItem");
        });
    }

    /**
     * Sets the text field limit for the input name text field to prevent long name
     * @param textField the text field being limited
     * @param limit the limit of characters allowed
     */
    private void setTextFieldLimit(TextField textField, int limit) {
        int maxLength = limit; // maximum number of characters allowed
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > maxLength) {
                return null; // reject the change
            }
            return change; // accept the change
        });
        textField.setTextFormatter(textFormatter);
    }

    /**
     * Checker to see if the local score has been beaten
     */
    private void checkLocalScoreHasBeenBeaten() {
        for(var i = 0; i < Math.min(localScoresArrayList.size(), 10); i++) {
            if(localScoresArrayList.get(i).getValue() < game.getScore() && !localScoreHasBeenBeaten || localScoresArrayList.size() <= 10 && !localScoreHasBeenBeaten) {
                localScoreHasBeenBeaten = true;
                logger.info("New Local Highscore!");
            }
        }
    }

    /**
     * Checker to see if online scores has been beaten
     */
    private void checkRemoteScoreHasBeenBeaten() {
        for(var i = 0; i < Math.min(remoteScoresArrayList.size(), 10); i++) {
            if(remoteScoresArrayList.get(i).getValue() < game.getScore() && !remoteScoreHasBeenBeaten || remoteScoresArrayList.size() <= 10 && !remoteScoreHasBeenBeaten) {
                remoteScoreHasBeenBeaten = true;
                logger.info("New Remote Highscore!");
            }
        }
    }

    /**
     * Method for sorting scores in an arrayList
     * @param arrayList
     */
    private void sortScores(ArrayList arrayList) {
        Comparator<Pair<String, Integer>> comparator = Comparator.comparing(Pair::getValue);
        comparator = comparator.reversed();
        Collections.sort(arrayList, comparator);
    }
}
