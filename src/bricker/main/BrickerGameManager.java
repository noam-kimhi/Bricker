package bricker.main;

import bricker.brick_strategies.*;
import bricker.gameobjects.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static danogl.collisions.Layer.*;

/**
 * Bricker game main class through which the game is running.
 * written by: Noam K
 */

public class BrickerGameManager extends GameManager {

    // private constants for the game creation:
    private static final String GAME_TITLE = "Bricker";
    private static final int VALID_ARG_COUNT = 2;
    private static final int FIRST_ARG = 0;
    private static final int SECOND_ARG = 1;

    // Border creation constants
    private static final String BORDER_TAG = "Border";

    // Ball constants
    private static final float BALL_POSITION_MULT_FROM_WINDOW_DIMENSIONS = 0.5f;
    private static final int MAX_TURBO_BALL_HITS = 6;

    // Paddle Constants
    private static final int PADDLE_POSITION_OFFSET = 30;

    // Bricks creation constants
    private static final int DEFAULT_BRICKS_NUMBER_PER_ROW = 8;
    private static final int DEFAULT_NUMBER_OF_BRICK_ROWS = 7;
    private static final int BRICK_HEIGHT = 15;
    private static final int BRICKS_PADDING_SIZE = 15;
    private static final String BRICK_TAG = "Brick";
    private static final int R_VAL_FOR_BROWN = 78;
    private static final int G_VAL_FOR_BROWN = 55;
    private static final int B_VAL_FOR_BROWN = 8;

    // Numeric life constants
    private static final int NUMERIC_Y_POSITION_OFFSET = 21;
    private static final int NUMERIC_LIVES_SIZE = 15;
    private static final int NUMERIC_LIVES_X_POSITION = 10;
    private static final int ONE_LIFE_LEFT = 1;
    private static final int ZERO_LIFE_LEFT = 0;
    private static final int TWO_LIVES_LEFT = 2;

    // Graphical life constants
    private static final int HEART_Y_POSITION_OFFSET = 22;
    private static final int HEART_PADDING = 7;
    private static final int MAX_HEARTS_AMOUNT = 4;

    // Default number of lives
    private static final int DEFAULT_LIVES_AMOUNT = 3;

    // User messages constants
    private static final String WIN_PROMPT = "You win!";
    private static final String LOSE_PROMPT = "You lose!";
    private static final String ASK_IF_PLAY_AGAIN = " Play again?";

    // Images paths
    private static final String BACKGROUND_IMAGE_PATH = "assets/DARK_BG2_small.jpeg";
    private static final String BALL_IMAGE_PATH = "assets/ball.png";
    private static final String BRICK_IMAGE_PATH = "assets/brick.png";

    // frame rate
    private static final int TARGET_FRAMERATE = 60;

    // private fields for the game creation
    private final int numberOfBricksPerRow;
    private final int numberOfBrickRows;
    private Vector2 windowDimension;
    private WindowController windowController;
    private UserInputListener inputListener;
    private final Counter bricksCount;
    private SoundReader soundReader;
    private ImageReader imageReader;

    // ball behavior fields
    private Paddle originalPaddle;
    private Ball ball;
    private int turboBallHits = 0;

    // lives handling fields
    private int currLivesAmount = 0;
    private final List<Heart> heartList = new ArrayList<>();
    private TextRenderable numericLivesAmount;
    private int nextHeartXPosition = NUMERIC_LIVES_SIZE + HEART_PADDING;


    /**
     * Constructor for Bricker game with cmd arguments
     *
     * @param windowTitle          the title for the game window
     * @param windowDimensions     the dimensions of the game window
     * @param numberOfBricksPerRow the number of bricks per single row
     * @param numberOfBrickRows    the number of brick rows
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions,
                              int numberOfBricksPerRow, int numberOfBrickRows) {
        super(windowTitle, windowDimensions);
        this.numberOfBricksPerRow = numberOfBricksPerRow;
        this.numberOfBrickRows = numberOfBrickRows;
        this.bricksCount = new Counter(0); // initialize bricks count to 0
    }

    /**
     * Constructor for Bricker game with no cmd args, setting default values
     *
     * @param windowTitle      the title for the game window
     * @param windowDimensions the dimensions of the game window
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.numberOfBricksPerRow = DEFAULT_BRICKS_NUMBER_PER_ROW;
        this.numberOfBrickRows = DEFAULT_NUMBER_OF_BRICK_ROWS;
        this.bricksCount = new Counter(0); // initialize bricks count to 0

    }

    /**
     * Used to initialize the game, setting up the different objects and their
     * parameters for the game.
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self-explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        this.windowController = windowController;
        this.inputListener = inputListener;
        this.soundReader = soundReader;
        this.imageReader = imageReader;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        windowDimension = windowController.getWindowDimensions(); // get window dimensions
        createGameObjects();
        this.turboBallHits = 0;
        this.currLivesAmount = 0;
        // add hearts to the screen (numeric and graphic)
        handleLivesSetting();
    }

    /**
     * Creates all the game objects needed for initialization
     */
    private void createGameObjects() {
        createBackground(); // create background
        createBorders(); // add borders
        createBall(); // create ball
        createPaddle(); // create paddles
        createBricks(); // create brick
    }

    /**
     * Handles the lives setting at the start of a new game, setting up numeric and graphic life count.
     */
    private void handleLivesSetting() {
        numericLivesAmount = new TextRenderable(Integer.toString(0));
        nextHeartXPosition = NUMERIC_LIVES_X_POSITION +
                NUMERIC_LIVES_SIZE + HEART_PADDING;
        resetHeartListToDefault();
        GameObject livesCount = new GameObject(new Vector2(NUMERIC_LIVES_X_POSITION,
                windowDimension.y() - NUMERIC_Y_POSITION_OFFSET),
                new Vector2(NUMERIC_LIVES_SIZE, NUMERIC_LIVES_SIZE), numericLivesAmount);
        this.gameObjects().addGameObject(livesCount, UI); // Add numeric lives count to UI layer
    }

    /**
     * Updates the board, with each refresh checking if the game has ended
     *
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        windowController.setTargetFramerate(TARGET_FRAMERATE);
        super.update(deltaTime);
        checkForGameEnd(); // check if the game ended with every update
        checkForOutOfScreenObject(); // check if any object fell out of the screen;
        checkBallState(); // handles special ball conditions
        checkFallingHearts(); // handles collecting falling hearts
    }

    /**
     * Check if a heart was collected by the main paddle
     */
    private void checkFallingHearts() {
        for (GameObject gameObject : this.gameObjects().objectsInLayer(DEFAULT)) {
            if (gameObject.getTag().equals(Constants.FALLING_HEART_TAG)) {
                float heartXPosition = gameObject.getCenter().x();
                float heartYPosition = gameObject.getCenter().y();

                // Check if the X and Y coordinates of the heart overlap with the paddle
                boolean xOverlap = heartXPosition >= this.originalPaddle.getTopLeftCorner().x() &&
                        heartXPosition <= this.originalPaddle.getTopLeftCorner().x() + Constants.PADDLE_WIDTH;

                boolean yOverlap = heartYPosition + Constants.HEART_SIZE >=
                        this.originalPaddle.getTopLeftCorner().y() &&
                        heartYPosition <= this.originalPaddle.getTopLeftCorner().y()
                                + Constants.PADDLE_HEIGHT;

                if (xOverlap && yOverlap) {
                    // Heart and paddle overlap; add a life and remove the heart
                    addSingleLife();
                    gameObjects().removeGameObject(gameObject);
                    break; // Exit loop after handling collision
                }
            }
        }
    }

    /**
     * This method is called in update method to check the state of the ball, if needed to activate
     * turbo mode and for how long
     */
    private void checkBallState() {
        if (this.ball.getTag().equals(Constants.TURBO_BALL_TAG) && turboBallHits == 0) {
            this.turboBallHits = (this.ball).getCollisionCounter() + MAX_TURBO_BALL_HITS;
            return;
        }
        if (turboBallHits == (this.ball).getCollisionCounter() &&
                (this.ball).getCollisionCounter() != 0) {
            turboBallHits = 0; //reset turbo ball hits counter
            this.ball.setTag(Constants.MAIN_BALL_TAG);
            // we need to get its original sign of velocity and divide by the multiplier we added
            Vector2 turboVelocity = this.ball.getVelocity();
            this.ball.setVelocity(new Vector2(turboVelocity.x() / Constants.SPEED_MULTIPLIER,
                    turboVelocity.y() / Constants.SPEED_MULTIPLIER));
            Renderable ballImage = imageReader.readImage(BALL_IMAGE_PATH, true); // load photo
            this.ball.renderer().setRenderable(ballImage);
        }
    }

    /**
     * Checks for objects that are out of the window and removes them from game list.
     */
    private void checkForOutOfScreenObject() {
        for (GameObject gameObject : this.gameObjects().objectsInLayer(DEFAULT)) {
            if (gameObject.getCenter().y() > windowDimension.y()) {
                this.gameObjects().removeGameObject(gameObject);
            }
        }
    }

    /**
     * Check if no lives are left or if no bricks are left.
     * Asks the user if he wants to play another game in case game has ended.
     */
    private void checkForGameEnd() {
        double ballHeight = ball.getCenter().y();
        String prompt = "";
        // Check if there's a win state
        if (bricksCount.value() == 0 || inputListener.isKeyPressed(KeyEvent.VK_W)) {
            // no bricks left OR user pressed W, we won
            prompt = WIN_PROMPT;
        }
        // Check if there's a loss state
        if (ballHeight > windowDimension.y()) {
            // remove single heart
            boolean moreLives = removeSingleLife();
            updateNumericLivesCount();
            setBallToCenter();
            // we lost - no more lives left
            if (!moreLives) {
                prompt = LOSE_PROMPT;
            }
        }
        // check if a game ending message was received
        checkIfPlayAgain(prompt);
    }

    /**
     * This method checks if windowController should play another game based on
     * if it received a prompt indicating at a win or a loss
     *
     * @param prompt win/loss prompt will ask user for play a game, empty string will not
     */
    private void checkIfPlayAgain(String prompt) {
        if (!prompt.isEmpty()) {
            prompt += ASK_IF_PLAY_AGAIN;
            if (windowController.openYesNoDialog(prompt)) {
                // play again
                windowController.resetGame();
            } else {
                // the user pressed "No"
                windowController.closeWindow();
            }
        }
    }

    /**
     * Creates background for the game in the background layer that follow camera movement.
     */
    private void createBackground() {
        Renderable backgroundImage = imageReader.readImage(BACKGROUND_IMAGE_PATH, false);
        GameObject background = new GameObject(Vector2.LEFT, this.windowDimension, backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.gameObjects().addGameObject(background, BACKGROUND);
    }

    /**
     * Creating the ball for the game from a given image, controlling its start position and velocity.
     */
    private void createBall() {
        // create ball and set its parameters
        Renderable ballImage = imageReader.readImage(BALL_IMAGE_PATH, true); // load photo
        Sound collisionSound = soundReader.readSound(Constants.BALL_COLLISION_SOUND_PATH);
        Ball ball = new Ball(Vector2.ZERO,
                new Vector2(Constants.BALL_DIMENSIONS, Constants.BALL_DIMENSIONS),
                ballImage, collisionSound); // create object
        // randomly change ball direction
        this.ball = ball;
        setBallToCenter();
        setBallSpeed();
        ball.setTag(Constants.MAIN_BALL_TAG);
        this.gameObjects().addGameObject(ball); // add the ball to game objects
    }

    /**
     * Sets the ball to the center of the screen and then gives it a random start direction.
     */
    private void setBallSpeed() {
        float ballVelX = Constants.BALL_SPEED;
        float ballVelY = Constants.BALL_SPEED;
        Random rand = new Random(); // decide random direction for movement in some diagonals
        if (rand.nextBoolean())
            ballVelX *= -1;
        if (rand.nextBoolean())
            ballVelY *= -1;
        ball.setVelocity(new Vector2(ballVelX, ballVelY)); // ball's speed
    }

    /**
     * Sets the ball to the center of the screen.
     */
    private void setBallToCenter() {
        ball.setCenter(
                windowDimension.mult(
                        BALL_POSITION_MULT_FROM_WINDOW_DIMENSIONS)); // ball start position
    }

    /**
     * Create a paddle in the game.
     */
    private void createPaddle() {
        float windowWidth = windowDimension.x();
        float windowHeight = windowDimension.y();

        // create paddle and set its parameters
        Renderable paddleImage = imageReader.readImage(
                Constants.PADDLE_IMAGE_PATH, true); // load photo

        Paddle paddle = new Paddle(Vector2.ZERO,
                new Vector2(Constants.PADDLE_WIDTH, Constants.PADDLE_HEIGHT), paddleImage,
                inputListener); // create object
        paddle.setTag(Constants.ORIGINAL_PADDLE_TAG);
        this.originalPaddle = paddle;
        paddle.setCenter(
                new Vector2(windowWidth / 2,
                        (int) windowHeight - PADDLE_POSITION_OFFSET)); // paddle start position
        gameObjects().addGameObject(paddle); // add the paddle to game objects
    }

    /**
     * This method creates left right and top border for the game.
     */
    private void createBorders() {
        float windowWidth = windowDimension.x();
        float windowHeight = windowDimension.y();

        // create rectangle for the borders
        Color brown = new Color(R_VAL_FOR_BROWN, G_VAL_FOR_BROWN, B_VAL_FOR_BROWN);
        Renderable brownRectangle = new RectangleRenderable(brown);

        // create left, right and top borders
        GameObject leftBorder = new GameObject(
                Vector2.LEFT, new Vector2(Constants.BORDER_LENGTH, windowHeight), brownRectangle);
        GameObject rightBorder = new GameObject(
                new Vector2(windowWidth - Constants.BORDER_LENGTH + 1, 0), // +1 to fix offset
                new Vector2(Constants.BORDER_LENGTH, windowHeight),
                brownRectangle);
        GameObject topBorder = new GameObject(
                new Vector2(Vector2.LEFT),
                new Vector2(windowWidth, Constants.BORDER_LENGTH),
                brownRectangle);
        // add borders to gameObjects
        this.gameObjects().addGameObject(leftBorder, STATIC_OBJECTS);
        this.gameObjects().addGameObject(rightBorder, STATIC_OBJECTS);
        this.gameObjects().addGameObject(topBorder, STATIC_OBJECTS);
        // set their tags
        leftBorder.setTag(BORDER_TAG);
        rightBorder.setTag(BORDER_TAG);
        topBorder.setTag(BORDER_TAG);
    }

    /**
     * Puts the bricks on the game screen. assigns each brick a collisionStrategy according to the factory.
     */
    private void createBricks() {
        this.bricksCount.reset(); // reset brickCount before adding new bricks

        // calculate brick width and height
        float brickWidth = calculateBrickWidth();
        float brickHeightPosition = Constants.BORDER_LENGTH + BRICKS_PADDING_SIZE;

        // create bricks and add them to the game
        Renderable brickImage = imageReader.readImage(BRICK_IMAGE_PATH, true); // load photo
        CollisionStrategyFactory strategyFactory = new CollisionStrategyFactory(gameObjects(), imageReader,
                soundReader, inputListener, ball, originalPaddle, bricksCount);

        createBrickRows(brickWidth, brickHeightPosition, strategyFactory, brickImage);
    }

    /**
     * Handles the iteration that creates bricks and adds them to the game
     *
     * @param brickWidth          the width of each brick
     * @param brickHeightPosition the height of each bricks
     * @param strategyFactory     factory that creates a collision strategy for each brick
     * @param brickImage          image of the bricks
     */
    private void createBrickRows(float brickWidth, float brickHeightPosition,
                                 CollisionStrategyFactory strategyFactory, Renderable brickImage) {
        for (int row = 0; row < this.numberOfBrickRows; row++) {
            int brickLeftCornerPosition = Constants.BORDER_LENGTH + BRICKS_PADDING_SIZE;

            for (int col = 0; col < this.numberOfBricksPerRow; col++) {
                // create a random strategy
                CollisionStrategy collisionStrategy = strategyFactory.randomlyCreateCollisionStrategy();
                // create brick
                GameObject brick = new Brick(new Vector2(brickLeftCornerPosition, brickHeightPosition),
                        new Vector2(brickWidth, BRICK_HEIGHT),
                        brickImage, collisionStrategy);
                // add the ball to game objects
                this.gameObjects().addGameObject(brick, STATIC_OBJECTS);
                //update position
                brickLeftCornerPosition +=
                        (int) (brickWidth + BRICKS_PADDING_SIZE);
                brick.setTag(BRICK_TAG); // set tag
                bricksCount.increment(); // increase brickCount by 1 for each brick created
            }
            // update height of next row
            brickHeightPosition += (BRICK_HEIGHT + (float) BRICKS_PADDING_SIZE / 2);
        }
    }

    /**
     * Calculates the width of a brick according to number of bricks per row and the
     * width of the window
     *
     * @return the width of the brick
     */
    private float calculateBrickWidth() {
        float windowWidth = windowDimension.x();
        float windowWidthWithoutBorders = windowWidth -
                (2 * Constants.BORDER_LENGTH) - (2 * BRICKS_PADDING_SIZE);
        return (windowWidthWithoutBorders / this.numberOfBricksPerRow) -
                BRICKS_PADDING_SIZE +
                (float) BRICKS_PADDING_SIZE / this.numberOfBricksPerRow;
    }

    /**
     * Resets the live count to 0 and adds the default lives amount
     */
    private void resetHeartListToDefault() {
        // clear any leftover hearts from the game and from heartList
        int heartsLeft = currLivesAmount;
        for (int i = 0; i < heartsLeft; i++) {
            removeSingleLife(); // Remove all hearts from the screen
        }
        for (int i = 0; i < DEFAULT_LIVES_AMOUNT; i++) {
            addSingleLife(); // add defaultLivesAmount of hearts
        }
    }

    /**
     * Updates the numeric representation of the current lives amount
     */
    private void updateNumericLivesCount() {
        // update the numeric lives count to the current lives amount
        numericLivesAmount.setString(Integer.toString(currLivesAmount));
        // color the number according to the number of lives
        if (currLivesAmount == ONE_LIFE_LEFT || currLivesAmount == ZERO_LIFE_LEFT) {
            numericLivesAmount.setColor(Color.red);
        } else if (currLivesAmount == TWO_LIVES_LEFT) {
            numericLivesAmount.setColor(Color.yellow);
        } else {
            numericLivesAmount.setColor(Color.green);
        }
    }

    /**
     * Increase lives amount by 1 in case the max amount was not reached yet.
     */
    private void addSingleLife() {
        if (currLivesAmount == MAX_HEARTS_AMOUNT) {
            return;
        }
        // render heart image
        Renderable heartImage = imageReader.readImage(Constants.HEART_IMAGE_PATH, true); // load photo
        Heart heart = new Heart(new Vector2(nextHeartXPosition,
                windowDimension.y() - HEART_Y_POSITION_OFFSET),
                new Vector2(Constants.HEART_SIZE, Constants.HEART_SIZE), heartImage);
        heartList.add(heart);
        this.gameObjects().addGameObject(heart, UI); // add heart to the gameObject UI layer
        currLivesAmount++;
        // update position of next heart
        this.nextHeartXPosition += HEART_PADDING + Constants.HEART_SIZE;
        updateNumericLivesCount();
    }

    /**
     * Decrease lives amount by 1
     *
     * @return false in case it was the last heart removed, true otherwise
     */
    private boolean removeSingleLife() {
        currLivesAmount--;
        if (currLivesAmount == 0) {
            return false;
        }
        this.gameObjects().removeGameObject(heartList.get(heartList.size() - 1), UI); // remove it from the game
        heartList.remove(heartList.size() - 1); // pop the heart out of the list
        updateNumericLivesCount();
        nextHeartXPosition -= (Constants.HEART_SIZE + HEART_PADDING); // update the location of the next heart
        return true;
    }

    /**
     * The main method that runs the game
     *
     * @param args optional: args[0] = bricks per row
     *             args[1] = number of rows
     */
    public static void main(String[] args) {
        // parameters given were number of bricks per row, number of rows
        if (args.length == VALID_ARG_COUNT) {
            int numOfBricksPerRow = Integer.parseInt(args[FIRST_ARG]);
            int numOfRows = Integer.parseInt(args[SECOND_ARG]);
            new BrickerGameManager(GAME_TITLE, new Vector2(Constants.WINDOW_WIDTH,
                    Constants.WINDOW_HEIGHT),
                    numOfBricksPerRow, numOfRows).run();

        }
        // no parameters given, set number of rows and bricks per row to default values
        else {
            new BrickerGameManager(GAME_TITLE,
                    new Vector2(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT)).run();
        }
    }
}
