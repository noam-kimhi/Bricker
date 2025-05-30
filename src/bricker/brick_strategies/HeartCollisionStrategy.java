package bricker.brick_strategies;

import bricker.gameobjects.Heart;
import bricker.main.Constants;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Defines behavior upon a collision with a unit that has this trait.
 * In addition to breaking the brick, will cause a heart to fall for the user to pick up.
 * Written by: Noam K
 */
class HeartCollisionStrategy implements CollisionStrategy {

    private static final int HEART_FALLING_SPEED = 100;

    // private fields
    private final GameObjectCollection gameObjects;
    private final CollisionStrategy baseStrategy;
    private final ImageReader imageReader;
    private final GameObject originalPaddle;

    /**
     * Creates this type of collisionStrategy
     *
     * @param gameObjects    a list of objects in the game
     * @param baseStrategy   a base strategy to wrap
     * @param imageReader    Used to read images
     * @param originalPaddle the paddle in the game
     */
    HeartCollisionStrategy(GameObjectCollection gameObjects,
                                  CollisionStrategy baseStrategy,
                                  ImageReader imageReader,
                                  GameObject originalPaddle) {
        this.gameObjects = gameObjects;
        this.baseStrategy = baseStrategy;
        this.imageReader = imageReader;
        this.originalPaddle = originalPaddle;
    }

    /**
     * Defines the behavior upon collision
     *
     * @param object1 first object to collide
     * @param object2 second object to collide
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        baseStrategy.onCollision(object1, object2); // apply basic collision behavior
        Vector2 brickLocation = object1.getCenter();
        createHeart(brickLocation);
    }

    /**
     * Creates a single heart that will fall from the given position downwards
     *
     * @param startPosition starting position to fall from
     */
    private void createHeart(Vector2 startPosition) {
        Renderable heartImage = imageReader.readImage(Constants.HEART_IMAGE_PATH, true); // load photo
        GameObject heart = new Heart(Vector2.ZERO,
                new Vector2(Constants.HEART_SIZE, Constants.HEART_SIZE), heartImage);
        gameObjects.addGameObject(heart);
        heart.setCenter(startPosition);
        heart.setTag(Constants.FALLING_HEART_TAG);
        heart.setVelocity(new Vector2(0, HEART_FALLING_SPEED));
        heart.shouldCollideWith(originalPaddle);
    }
}
