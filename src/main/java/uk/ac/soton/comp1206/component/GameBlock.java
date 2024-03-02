package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {
    /**
     * Logger to keep track of events occurring
     */

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };
    /**
     * Field variables for the width
     */
    private final double width;
    /**
     * Field variables for the height
     */
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);



    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        /**
         *
         */
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    public void paintEmpty() {
        var gc = getGraphicsContext2D();

        // Clear
        gc.clearRect(0, 0, width, height);

        // Fill
        gc.setFill(Color.rgb(0, 0, 0, 0.6)); // Partially transparent black color
        gc.fillRect(0, 0, width, height);

        // Border
        gc.setStroke(Color.GREY);
        gc.strokeRect(0, 0, width, height);

    }

    /**
     * Paint this canvas with the given colour
     *
     * @param colour the colour to paint
     */
    public void paintColor(Color colour) {
            var gc = getGraphicsContext2D();

            // Clear canvas
            gc.clearRect(0, 0, getWidth(), getHeight());

            // Draw first triangle
            gc.setFill(colour);
            gc.fillPolygon(new double[]{0, getWidth(), 0}, new double[]{0, 0, getHeight()}, 3);

            // Draw second triangle with washed-out color
            Color washedOutColor = createWashedOutColor(colour);
            gc.setFill(washedOutColor);
            gc.fillPolygon(new double[]{getWidth(), getWidth(), 0}, new double[]{0, getHeight(), getHeight()}, 3);
        }

    /**
     * Creates a washed down version of the previous colour
     * @param paint takes a paint parameter and lowers the rgb values
     * @return A washed down version of the paint colour
     */
    private Color createWashedOutColor(Paint paint) {
            if (paint instanceof Color) {
                //If block is painted
                Color color = (Color) paint;
                //Create washed out red
                double red = color.getRed() * 0.8;
                //Create washed out green
                double green = color.getGreen() * 0.8;
                //Create washed out blue
                double blue = color.getBlue() * 0.8;
                double opacity = color.getOpacity();
                return new Color(red, green, blue, opacity);
            } else {
                return Color.WHITE; // fallback to white if paint is not a color
            }
        }

    /**
     * Draw a circle on the center of a block
     */

    public void drawCircle() {
        var gc = getGraphicsContext2D();

        //Calculate the radius of the circle
        double radius = Math.min(width, height) * 0.3;

        //Calculate the center of the rectangle
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        //Draw the grey circle
        gc.setFill(Color.rgb(255, 255, 255, 0.6));
        gc.fillOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
    }

    /**
     * Draws the hover on a block
     * @param colour sets colour of the block underneath the hover
     */
    public void drawHover(Color colour) {
        var gc = getGraphicsContext2D();

        // Fill
        gc.setFill(Color.rgb(255, 255, 255, 0.3)); // Partially transparent black color
        gc.fillRect(0, 0, width, height);

        // Border
        gc.setStroke(Color.GREY);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Removes the hover effect on the block
     */
    public void removeHover() {
        var gc = getGraphicsContext2D();

        // Clear
        gc.clearRect(0, 0, width, height);
        paint();
    }

    /**
     * Creates a fadeout animation when the block is removed from the board
     */
    public void fadeOut() {
        var gc = getGraphicsContext2D();
        AnimationTimer timer = new AnimationTimer() {

            private long startTime = -1;
            private final long duration = 1000_000_000; // 1 second in nanoseconds
            private final double initialOpacity = 1.0;
            private final double maxGreen = 1.0;
            private double greenLevel = 0.0;

            @Override
            public void handle(long now) {
                if(getValue() != 0) return;
                if (startTime < 0) {
                    startTime = now;
                }
                double elapsedSeconds = (now - startTime) / 1_000_000_000.0;
                double opacity = initialOpacity * (1.0 - elapsedSeconds / (duration / 1_000_000_000.0));
                if (opacity <= 0.0) {
                    stop();
                    paintEmpty();
                } else {
                    paintEmpty();
                    greenLevel = Math.min(maxGreen, elapsedSeconds / (duration / 1_000_000_000.0));
                    Color fill = getColor();
                    gc.setFill(new Color(fill.getRed(), greenLevel, fill.getBlue(), opacity));
                    gc.fillRect(getX(), getY(), getWidth(), getHeight());
                }
            }
        };
        timer.start();
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Gets the colour of the block
     * @return the int value of the colour of the block
     */
    public Color getColor() {
        return COLOURS[getValue()];
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * Creates a string representation of the block
     * @return the string representation of the block
     */
    @Override
    public String toString() {
        return "GameBlock{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + value.toString() +
                '}';
    }
}
