package bricker.brick_strategies;

import danogl.GameObject;

/**
 * This interface handles collision strategies of 2 objects in the game.
 * Written by: Noam K
 */
public interface CollisionStrategy {

    /**
     * handles the behavior of 2 objects upon collision
     * @param object1 first object to collide
     * @param object2 second object to collide
     */
    void onCollision (GameObject object1, GameObject object2);
}
