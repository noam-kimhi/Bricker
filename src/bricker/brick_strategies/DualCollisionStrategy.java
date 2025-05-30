package bricker.brick_strategies;

import danogl.GameObject;

/**
 * Has 2 collision strategies. Both will be activated upon collision
 */
class DualCollisionStrategy implements CollisionStrategy {

    // strategies
    private final CollisionStrategy strategy1;
    private final CollisionStrategy strategy2;

    /**
     * Constructor that receives the strategies.
     *
     * @param strategy1 first strategy
     * @param strategy2 second strategy
     */
    DualCollisionStrategy(CollisionStrategy strategy1, CollisionStrategy strategy2) {
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    /**
     * Upon collision, activate both strategies.
     *
     * @param object1 first object to collide
     * @param object2 second object to collide
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        strategy1.onCollision(object1, object2);
        strategy2.onCollision(object1, object2);
    }
}
