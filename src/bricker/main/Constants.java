package bricker.main;

/**
 * A class containing public constants to use for the Bricker game.
 */
public class Constants {
    // Window settings
    /** Width of the game window */
    public static final int WINDOW_WIDTH = 700;
    /** Height of the game window */
    public static final int WINDOW_HEIGHT = 500;

    // Borders
    /** Width of the game borders */
    public static final int BORDER_LENGTH = 3;

    // Ball
    /** Dimensions of the game ball */
    public static final int BALL_DIMENSIONS = 20;
    /** Speed of the game ball */
    public static final int BALL_SPEED = 200;
    /** Multiplier for the ball speed */
    public static final float SPEED_MULTIPLIER = 1.4F;
    /** Tag of the game ball in its normal state */
    public static final String MAIN_BALL_TAG = "mainBall";
    /** Tag of the game ball in its turbo state */
    public static final String TURBO_BALL_TAG = "turboBall";

    // Paddle constants
    /** Width of the game paddle */
    public static final int PADDLE_WIDTH = 100;
    /** Height of the game paddle */
    public static final int PADDLE_HEIGHT = 15;
    /** Tag of the game main paddle */
    public static final String ORIGINAL_PADDLE_TAG = "originalPaddle";
    /** Tag of the game mock paddle */
    public static final String MOCK_PADDLE_TAG = "mockPaddle";

    // Falling Heart constants
    /** Size of hearts in the game */
    public static final int HEART_SIZE = 17;
    /** Tag of the collectable falling hearts in the game */
    public static final String FALLING_HEART_TAG = "fallingHeart";

    // Images and sounds
    /** Path to the sound a ball makes upon collision */
    public static final String BALL_COLLISION_SOUND_PATH = "assets/blop.wav";
    /** Path to the image of a paddle */
    public static final String PADDLE_IMAGE_PATH = "assets/paddle.png";
    /** Path to the image of a heart */
    public static final String HEART_IMAGE_PATH = "assets/heart.png";


}
