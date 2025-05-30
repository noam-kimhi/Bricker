package bricker.brick_strategies;

import bricker.gameobjects.MockPaddle;
import bricker.main.Constants;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import static danogl.collisions.Layer.DEFAULT;

/**
 * Defines behavior upon a collision with a unit that has this trait.
 * In addition to breaking the brick, will spawn a temporary paddle that moves with the main paddle at the
 * middle of the screen. Will disappear after a set amount of collisions with a ball.
 * Written by: Noam K
 */
class MockPaddleCollisionStrategy implements CollisionStrategy {

    // private fields
    private final GameObjectCollection gameObjects;
    private final CollisionStrategy baseStrategy;
    private final ImageReader imageReader;
    private final UserInputListener inputListener;
    private final int originalPaddleWidth;
    private final int originalPaddleHeight;
    private final Vector2 windowDimensions;


    /**
     * Constructor to create a strategy that will spawn a mock paddle
     *
     * @param gameObjects   a list of objects in the game
     * @param baseStrategy  a base strategy to wrap
     * @param imageReader   Used to read images
     * @param inputListener Used to receive input from the user
     */
    MockPaddleCollisionStrategy(GameObjectCollection gameObjects,
                                       CollisionStrategy baseStrategy, ImageReader imageReader,
                                       UserInputListener inputListener) {
        this.gameObjects = gameObjects;
        this.baseStrategy = baseStrategy;
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        this.originalPaddleWidth = Constants.PADDLE_WIDTH;
        this.originalPaddleHeight = Constants.PADDLE_HEIGHT;
        this.windowDimensions = new Vector2(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }

    /**
     * Upon collision, create a mock paddle that will last for a couple of hits, following
     * user's input
     * @param object1 first object to collide
     * @param object2 second object to collide
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        this.baseStrategy.onCollision(object1, object2); // perform basic collision behavior
        // if there's no other mock paddle, create one
        if (!isThereAnotherMockPaddle()) {
            createMockPaddle();
        }
    }

    /**
     * handles the creation of the mock paddle
     */
    private void createMockPaddle() {
        Renderable paddleImage = imageReader.readImage(Constants.PADDLE_IMAGE_PATH, true); // load photo
        GameObject mockPaddle = new MockPaddle(
                Vector2.ZERO,
                new Vector2(originalPaddleWidth, originalPaddleHeight),
                paddleImage,
                inputListener); // create object
        mockPaddle.setCenter(
                new Vector2(windowDimensions.x() / 2,
                        windowDimensions.y() / 2)); // mockPaddle start position
        mockPaddle.setTag(Constants.MOCK_PADDLE_TAG); // set tag of mockPaddle
        gameObjects.addGameObject(mockPaddle); // add the mockPaddle to game objects
    }

    /**
     * Looks for another mock paddle in the game
     *
     * @return true if there's another, false otherwise
     */
    private boolean isThereAnotherMockPaddle() {
        for (GameObject gameObject : gameObjects.objectsInLayer(DEFAULT)) {
            if ((gameObject.getTag().equals(Constants.MOCK_PADDLE_TAG))) {
                return true;
            }
        }
        return false;
    }
}
