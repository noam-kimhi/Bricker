package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * This method implements the basic collision strategy
 * Written by: Noam K
 */
class BasicCollisionStrategy implements CollisionStrategy{

    // private fields
    private final GameObjectCollection gameObjects;
    private final Counter bricksCounter;

    /**
     * Creates a BasicCollisionStrategy
     * @param gameObjects the list of objects in the game
     * @param bricksCounter the updating bricks counter, allowing to change their number by reference
     */
    BasicCollisionStrategy(GameObjectCollection gameObjects, Counter bricksCounter){
        this.gameObjects = gameObjects;
        this.bricksCounter = bricksCounter;
    }

    /**
     * Defines basic behavior upon collision
     * @param object1 first object to collide
     * @param object2 second object to collide
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        // remove object1 from the game, and decrement bricksCounter by 1 to ensure
        // only 1 decrement was made in case the brick was hit by 2 units at the same time.
        if(this.gameObjects.removeGameObject(object1, Layer.STATIC_OBJECTS)){
            bricksCounter.decrement();
        }
    }
}
