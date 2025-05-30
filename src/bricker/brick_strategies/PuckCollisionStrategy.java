package bricker.brick_strategies;

import bricker.gameobjects.Ball;
import bricker.main.Constants;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

/**
 * Defines behavior upon a collision with a unit that has this trait.
 * In addition to breaking the brick, will spawn 2 "puck" balls which behave like the main ball,
 * but will not cause the player to lose, health upon falling behind the screen.
 * Written by: Noam K
 */
class PuckCollisionStrategy implements CollisionStrategy {

    private static final float PUCK_BALL_RATIO_FROM_ORIGINAL = 0.75F;
    private static final String PUCK_BALL_TAG = "puckBall";
    private static final String MOCK_BALL_IMAGE_PATH = "assets/mockBall.png";

    // private fields
    private final GameObjectCollection gameObjects;
    private final CollisionStrategy baseStrategy;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final float puckBallSize;
    private final int puckBallSpeed;
    private final Random random;

    /**
     * Constructor for the PuckCollisionStrategy
     *
     * @param gameObjects  list of the objects in the game
     * @param baseStrategy a basic strategy to wrap and add more functionality on top
     * @param imageReader  Used to read images
     * @param soundReader  Used to read sound files
     */
    PuckCollisionStrategy(GameObjectCollection gameObjects,
                                 CollisionStrategy baseStrategy, ImageReader imageReader,
                                 SoundReader soundReader) {
        this.gameObjects = gameObjects;
        this.baseStrategy = baseStrategy;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.puckBallSize = Constants.BALL_DIMENSIONS * PUCK_BALL_RATIO_FROM_ORIGINAL;
        this.puckBallSpeed = Constants.BALL_SPEED;
        this.random = new Random();
    }

    /**
     * Extends the use of onCollision so that it will spawn to puck balls in addition to removing the brick
     *
     * @param object1 first object to collide
     * @param object2 second object to collide
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // perform basic behavior
        baseStrategy.onCollision(object1, object2);
        Vector2 brickLocation = object1.getCenter();
        for (int i = 0; i < 2; i++) {
            createPuckBall(brickLocation);
        }
    }

    /**
     * This method create a Puck ball, sets its center position and defines its speed
     *
     * @param startPosition the position to locate the ball at
     */
    private void createPuckBall(Vector2 startPosition) {
        // create ball and set its parameters
        Renderable puckBallImage = imageReader.readImage(
                MOCK_BALL_IMAGE_PATH, true); // load photo
        Sound collisionSound = soundReader.readSound(Constants.BALL_COLLISION_SOUND_PATH);
        GameObject puckBall = new Ball(Vector2.ZERO,
                new Vector2(puckBallSize, puckBallSize),
                puckBallImage, collisionSound); // create object
        gameObjects.addGameObject(puckBall); // add the ball to game objects
        puckBall.setCenter(startPosition); // set ball position
        // set ball velocity - based on a random number on the upper half of a unit circle
        double angle = random.nextDouble() * Math.PI;
        float velX = (float) Math.cos(angle) * puckBallSpeed;
        float velY = (float) Math.sin(angle) * puckBallSpeed;
        puckBall.setVelocity(new Vector2(velX, velY));
        puckBall.setTag(PUCK_BALL_TAG);
    }
}
