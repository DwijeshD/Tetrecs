package uk.ac.soton.comp1206.component;;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.beans.property.SimpleListProperty;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Custom component to display and animate the scores
 * dynamically in a VBox by turning each score into a new component
 */
public class ScoresList extends VBox {

    /**
     * Simple List Property that binds to the simple list property in the ScoresScene
     */
    private SimpleListProperty<Pair<String, Integer>> scores;
    /**
     * Logger to keep track of events
     */
    private static final Logger logger = LogManager.getLogger(uk.ac.soton.comp1206.component.ScoresList.class);

    /**
     * Constructor instantiates the List Property with a new Observable List.
     */
    public ScoresList() {
        scores = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    /**
     * Exposes the SimpleListProperty to allow access to it and observe it
     * @return the scores property
     */
    public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
        return scores;
    }

    /**
     * Dynamic reveal of the scores. For each Pair in the SimpleListPropert, dynamically create
     * a component and animate the component
     */
    public void reveal() {
        logger.info("Revealing scores");
        //Create a gridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER_LEFT);

        //Sequential Transition
        SequentialTransition seqTransition = new SequentialTransition();

        for (int i = 0; i < Math.min(scores.size(), 10); i++) {
            Pair<String, Integer> score = scores.get(i);
            //Create name for top 10 in the scores and set styles
            Text name = new Text(score.getKey() + ":");
            name.getStyleClass().add("scorelist");
            Text points = new Text(score.getValue().toString());
            points.getStyleClass().add("scorelist");

            //Add the scores to the gridpane
            gridPane.add(name, 0, i);
            //Add the point sto the gridpane
            gridPane.add(points, 1, i);
            //style the name and points
            name.setOpacity(0);
            points.setOpacity(0);
            //New FadeTransition fades the scores in one by one
            FadeTransition ft = new FadeTransition(Duration.seconds(0.3), name);
            ft.setToValue(1);
            seqTransition.getChildren().add(ft);

            ft = new FadeTransition(Duration.seconds(0.3), points);
            ft.setToValue(1);
            seqTransition.getChildren().add(ft);
        }
        //Add this component to the gridPane
        this.getChildren().add(gridPane);
        //Play animation
        seqTransition.play();
    }

}

