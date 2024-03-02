package uk.ac.soton.comp1206.scene;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;
import java.util.ArrayList;

/**
 * Lobby scene presents the current active channels as well as a chatbox to communicate with
 * other people in the channel. There is also the option to start a game.
 */
public class LobbyScene extends BaseScene {

    /**
     * logger for keeping track of the events
     */
    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    /**
     * The communicator field variable for sending adn receiving messages from server
     */
    private final Communicator communicator;
    /**
     * An infinite timeline for getting the list of active channels
     */
    private Timeline timeline;
    /**
     * Array list containing the channels
     */
    private final ArrayList<String> channelList;
    /**
     * The VBox containing channel names, as well as other components
     */
    private VBox currentGamesVBox;
    /**
     * The main borderpane
     */
    private BorderPane mainPane;
    /**
     * An arraylist for keeping track of what channels have been added so there aren't any duplicates
     */
    private final ArrayList<String> channelListAdded;
    /**
     * VBox to contain the channel names themselves
     */
    private final VBox channelNames;
    /**
     * Boolean checker to see fi your in a channel, to prevent you from doing certain things
     */
    private Boolean inAChannel = false;
    /**
     * Second borderpane for the chat box
     */
    private BorderPane secondPane;
    /**
     * Arraylist containing the users in a game
     */
    private final ArrayList<String> usersInGame;
    /**
     * An arraylist containing messages that have been sent and received to prevent duplicates
     */
    private ArrayList<String> messagesSentAndReceived;
    /**
     * VBox containing the messages
     */
    private VBox messages;
    /**
     * String containing the current channel name
     */
    private String currentChannel;
    /**
     * Boolean variable to check if the text field is open or not
     */
    private Boolean textFieldOpen = false;
    /**
     * Host variable to see if the user is the host or not
     */
    private Boolean host = false;
    /**
     * Button for starting a game
     */
    private Button startGameButton;



    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
        this.channelList = new ArrayList<>();
        this.channelListAdded = new ArrayList<>();
        this.channelNames = new VBox();
        this.usersInGame = new ArrayList<>();
        this.messagesSentAndReceived = new ArrayList<>();
    }

    /**
     * Initialise the LobbyScene
     */
    @Override
    public void initialise() {
        Multimedia.playBackgroundMusic("menu.mp3");
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                communicator.send("QUIT");
                gameWindow.cleanup();
                timeline.stop();
                inAChannel = false;
                gameWindow.loadScene(new MenuScene(gameWindow));
                Multimedia.stopMusic();
            }
        });
        startTimer();
    }

    /**
     * Start the timer to receive the list of channel names and update the list of channels at
     * regular intervals of time
     */
    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> {
            logger.info("Refreshing channel list");
            communicator.send("LIST");
            communicator.addListener(communication -> Platform.runLater(() -> handleIncomingMessages(communication)));
        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }



    /**
     * Restarts the timer which gets a fresh list of channel names
     */
    private void refresh() {
        timeline.stop();
        timeline.play();
    }

    /**
     * Builds the main Lobby scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        Text multiplayerText = new Text("Multiplayer");
        BorderPane.setAlignment(multiplayerText, Pos.CENTER);
        multiplayerText.setTextAlignment(TextAlignment.CENTER);
        multiplayerText.getStyleClass().add("title");
        mainPane.setTop(multiplayerText);

        currentGamesVBox = new VBox();
        currentGamesVBox.setSpacing(20);


        mainPane.setLeft(currentGamesVBox);

        Label currentGamesText = new Label("Current Games");
        currentGamesText.setTextAlignment(TextAlignment.CENTER);
        currentGamesText.getStyleClass().add("menuItem");
        currentGamesVBox.getChildren().add(currentGamesText);
        currentGamesVBox.setPadding(new Insets(10, 10, 10 ,10));

        var hostNewGameButton = new Button("Host New Game");
        hostNewGameButton.getStyleClass().add("onlineMenuItem");
        hostNewGameButton.setAlignment(Pos.CENTER);
        currentGamesVBox.getChildren().add(hostNewGameButton);

        hostNewGameButton.setOnMouseEntered(event -> {
            hostNewGameButton.getStyleClass().clear();
            hostNewGameButton.getStyleClass().add("onlineMenuItemSelected");
        });
        hostNewGameButton.setOnMouseExited(event -> {
            hostNewGameButton.getStyleClass().clear();
            hostNewGameButton.getStyleClass().add("onlineMenuItem");
        });

        hostNewGameButton.setOnAction(event -> {
            if(textFieldOpen) return;
            if(inAChannel) return;
            createChannel();
        });
    }

    /**
     * Handle the message received from the communicator
     * @param message sent by the communicator
     */
    private void handleIncomingMessages(String message) {

        channelList.clear();
        channelNames.getChildren().clear();
        channelListAdded.clear();
        usersInGame.clear();

        String message2 = message.trim();
        String[] split_data = message2.split(" ", 2);
        String command = split_data[0];

        if (command.equals("CHANNELS") || command.equals("USERS") || command.equals("MSG") || command.equals("START") || command.equals("ERROR")) {
            if(command.equals("START")) {
                logger.info("Starting Game");
                this.gameWindow.cleanup();
                Multimedia.stopMusic();
                timeline.stop();
                this.gameWindow.startMultiplayer();
            }
            if(split_data.length == 1) return;
            switch (command) {
                case "CHANNELS" -> {
                    String[] dataArray = split_data[1].split("\\n");
                    for (String data : dataArray) {
                        if (channelList.contains(data)) return;
                        channelList.add(data);
                    }
                    addChannels();
//                    checkChannelExists();
                }
                case "USERS" -> {
                    String[] dataArray = split_data[1].split("\\n");
                    for (String data : dataArray) {
                        if (usersInGame.contains(data)) return;
                        usersInGame.add(data);
                    }
                    getPlayers();
                }
                case "MSG" -> {
                    if (messagesSentAndReceived.contains(split_data[1])) return;
                    messagesSentAndReceived.add(split_data[1]);
                    receiveMessage(split_data[1]);
                }
                case "ERROR" -> {
                    mainPane.setRight(null);
                    inAChannel = false;
                    Alert alert = new Alert(Alert.AlertType.ERROR, split_data[1]);
                    alert.show();
                }

            }
        }
        else if(command.equals("PARTED")) {
            mainPane.setRight(null);
            inAChannel = false;
            refresh();
        }
    }

    /**
     * Handles the creation a new server by the user
     */
    private void createChannel() {
        HBox enterChannelName = new HBox();
        TextField enterChannel = new TextField();
        textFieldOpen = true;
        enterChannelName.getChildren().addAll(enterChannel);
        currentGamesVBox.getChildren().add(enterChannelName);
        enterChannelName.setPrefWidth(currentGamesVBox.getWidth());
        scene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER && !inAChannel) {
                host = true;
                textFieldOpen = false;
                communicator.send("CREATE " + enterChannel.getText());
                enterChannelName.getChildren().clear();
                inAChannel = true;
                buildTextBox();
                currentChannel = enterChannel.getText();
                refresh();
                logger.info("Channel created: {}, you are the host: {}", enterChannel.getText(), host);
            }
            else if(event.getCode() == KeyCode.ESCAPE) {
                enterChannelName.getChildren().clear();
                inAChannel = false;
            }
        });
    }

    /**
     * Creates a button for each channel received and adds it to the channelNames VBox
     */
    private void addChannels() {
        currentGamesVBox.getChildren().remove(channelNames);
        for (String channelName : channelList) {
            if(channelListAdded.contains(channelName)) return;
            Button button = new Button("" + channelName);
            button.getStyleClass().add("onlineMenuItem");
            button.setOnAction(event -> {
                messagesSentAndReceived = new ArrayList<>();
                messagesSentAndReceived.clear();
                communicator.send("JOIN " + channelName);
                buildTextBox();
                getPlayers();
                refresh();
                currentChannel = channelName;

                logger.info("You are currently in {} channel", channelName);
            });
            //Add hover feature
            button.setOnMouseEntered(event -> {
                button.getStyleClass().clear();
                button.getStyleClass().add("onlineMenuItemSelected");
            });
            button.setOnMouseExited(event -> {
                button.getStyleClass().clear();
                button.getStyleClass().add("onlineMenuItem");
            });

            channelNames.getChildren().add(button);
            channelListAdded.add(channelName);
        }
        currentGamesVBox.getChildren().add(channelNames);
    }


    /**
     * Build the textbook for entering messages and presenting messages
     */
    private void buildTextBox() {
        //Create a BorderPane to hold everything
        secondPane = new BorderPane();
        secondPane.setPrefWidth((float) gameWindow.getWidth()/2);
        secondPane.setPrefHeight((float) gameWindow.getHeight()/2);

        secondPane.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE) {
                communicator.send("PART");
                mainPane.setRight(null);
            }
        });

        //Create a VBox to hold the messages
        messages = new VBox();
        messages.setVisible(true);

        Text intro = new Text("Welcome to the lobby\nType /nick NewName to change your name\n\n");
        intro.getStyleClass().add("channelItem");

        messages.getChildren().add(intro);

        //Create a scroll pane to allow scrolling messages
        ScrollPane scroller = new ScrollPane();
        scroller.getStyleClass().add("message-pane");
        scroller.setOpacity(0.5);
        //Fit the scroll pane to the width, so we only scroll vertically
        scroller.setFitToWidth(true);
        scroller.setContent(messages);

        secondPane.setCenter(scroller);

        //Create a Horizontal Box to hold the input text field and buttons
        HBox inputBox = new HBox();

        //Create the text field for entering messages
        TextField inputField = new TextField();
        inputField.setPromptText("Send a message");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                checkMessage(inputField.getText());
                inputField.clear();
                inputField.requestFocus();
            }
            else if(event.getCode() == KeyCode.ESCAPE) {
                communicator.send("PART");
                mainPane.setRight(null);
            }
        });


        //Add the send button
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> {
            logger.info("Successfully sent message");
            checkMessage(inputField.getText());
            inputField.clear();
            inputField.requestFocus();
        });

        //Add the quit button
        Button quitButton = new Button("Leave game");
        quitButton.setOnAction(event -> {
            logger.info("You have left the channel");
            communicator.send("PART");
            usersInGame.clear();
            host = false;
            inAChannel = false;
            mainPane.setRight(null);
            inputBox.getChildren().remove(startGameButton);
            refresh();
        });

        //Add start button
        startGameButton = new Button("Start");
        startGameButton.setOnAction(event -> {
            logger.info("Starting game");
            communicator.send("START");
            gameWindow.cleanup();
            this.gameWindow.startMultiplayer();
            timeline.stop();
            Multimedia.stopMusic();
        });


        //Add the text field, send button, and quit button to the input box
        inputBox.getChildren().addAll(inputField, sendButton, quitButton);

        if(host) {
            inputBox.getChildren().add(startGameButton);
        }

        //Add the input box to the bottom of the BorderPane
        secondPane.setBottom(inputBox);
        mainPane.setRight(secondPane);
    }

    /**
     * Gets the players in the current channel
     */
    private void getPlayers() {
        FlowPane usersFlowPane = new FlowPane();
        for(String user : usersInGame) {
            Text name = new Text(user + " ");
            name.getStyleClass().add("hiscore");
            usersFlowPane.getChildren().add(name);
        }
        secondPane.setTop(usersFlowPane);
    }

    /**
     * Handles the message being received
     * @param msg the message being received
     */
    private void receiveMessage(String msg) {
        logger.info("Message: {} has been received", msg);
        String[] components = msg.split(":");
        if(components.length < 2) return;
        Text messageText = new Text(components[0] + ": " + components[1]);
        messages.getChildren().add(messageText);
        refresh();
    }

    /**
     * Method to check if the channel still exists or not, and if it doesn't then close the chatbox
     */
    private void checkChannelExists() {
        if(!channelList.contains(currentChannel)) {
            mainPane.setRight(null);
            logger.info("Channel no longer exists!");
        }
    }
    /**
     * Check the message for change nickname commands
     */
    private void checkMessage(String message) {
        if(message.startsWith("/")) {
            String[] parts = message.split(" ", 2);
            if (message.contains("/nick")) {
                communicator.send("NICK " + parts[1]);
                logger.info("Name changed to {}", parts[1]);
            }
        }
        else{
            communicator.send("MSG " + message);
            logger.info("Message {} sent", message);
        }
    }
}
