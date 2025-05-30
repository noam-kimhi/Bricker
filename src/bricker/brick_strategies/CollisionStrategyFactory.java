package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.util.Counter;

import java.util.Random;

/**
 * The factory creates a collision strategy on a random basis
 */
public class CollisionStrategyFactory {
    // percentages stats
    private static final int RANDOM_BOUND = 100;
    private static final int BOUND_WITH_DUAL_COLLISION = 50;
    private static final int BOUND_WITHOUT_DUAL_COLLISION = 40;
    private static final int TEN_PERCENT_CHANCE = 10;
    private static final int TWENTY_PERCENT_CHANCE = 20;
    private static final int THIRTY_PERCENT_CHANCE = 30;
    private static final int FORTY_PERCENT_CHANCE = 40;
    private static final int FIFTY_PERCENT_CHANCE = 50;

    // private fields
    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final UserInputListener inputListener;
    private final GameObject ball;
    private final GameObject originalPaddle;
    private final Random random = new Random();
    private final CollisionStrategy basicStrategy;

    /**
     * Will receive all the needed parameters for creating any kind of collision strategy
     *
     * @param gameObjects    the list of objects in the game
     * @param imageReader    Used to read images
     * @param soundReader    Used to read sounds
     * @param inputListener  Used to receive user input
     * @param ball           The ball in the game
     * @param originalPaddle The paddle in the game
     * @param bricksCounter  the updating bricks counter, allowing to change their number by reference
     */
    public CollisionStrategyFactory(GameObjectCollection gameObjects,
                                    ImageReader imageReader,
                                    SoundReader soundReader,
                                    UserInputListener inputListener,
                                    GameObject ball,
                                    GameObject originalPaddle,
                                    Counter bricksCounter) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.ball = ball;
        this.originalPaddle = originalPaddle;
        this.basicStrategy = new BasicCollisionStrategy(this.gameObjects, bricksCounter);
    }

    /**
     * Randomly creates a collision strategy based on these probabilities:
     * 50% for BasicCollisionStrategy - Only delete the brick
     * Otherwise, on top of the basic behavior the brick can receive:
     * 10% for PuckCollisionStrategy - creates 2 puck balls at the location of the destroyed brick.
     * 10% for MockPaddleCollisionStrategy - create a temporary paddle that follows the user's movement.
     * 10% for TurboCollisionStrategy - temporarily change the ball to be faster
     * 10% for HeartCollisionStrategy - a heart will fall out of the brick, catching it grants 1 HP
     * 10% for DualCollisionStrategy - randomly select 2 of the 5 special behaviors (can only nest once)
     *
     * @return the randomly created CollisionStrategy
     */
    public CollisionStrategy randomlyCreateCollisionStrategy() {
        int chance = random.nextInt(RANDOM_BOUND);
        // 50% to return basic behavior
        if (chance < FIFTY_PERCENT_CHANCE) {
            return basicStrategy;
        }
        // 50% to create special behavior
        else {
            // check if the special behavior will be dual in a 20% chance
            if (shouldSpecialStrategyBeDual()) {
                return createDualStrategy();
            } else {
                // return a non-dual behavior, each with a 25% chance
                return randomlyCreateNonDualSpecialStrategy();
            }
        }
    }

    /**
     * Decides if the special strategy will become dual with a 20% chance.
     *
     * @return true if the strategy should be dual, false otherwise
     */
    private boolean shouldSpecialStrategyBeDual() {
        int chance = random.nextInt(BOUND_WITH_DUAL_COLLISION);
        return chance > FORTY_PERCENT_CHANCE;
    }


    /**
     * Will create a dual strategy.
     * can contain up to 3 special strategies in case one strategy is dual again.
     *
     * @return the created dual strategy
     */
    private DualCollisionStrategy createDualStrategy() {
        // Randomly create two collision strategies, first is non-dual
        CollisionStrategy strategy1 = randomlyCreateNonDualSpecialStrategy();

        // decide if the second one should be dual strategy or not.
        // this is called twice with an "or" operator to represent each of the two special behaviors having
        // a chance to roll a dual behavior.
        if (shouldSpecialStrategyBeDual() || shouldSpecialStrategyBeDual()) {
            CollisionStrategy subStrategy1 = randomlyCreateNonDualSpecialStrategy();
            CollisionStrategy subStrategy2 = randomlyCreateNonDualSpecialStrategy();
            DualCollisionStrategy strategy2 = new DualCollisionStrategy(subStrategy1, subStrategy2);
            return new DualCollisionStrategy(strategy1, strategy2);
        } else {
            CollisionStrategy strategy2 = randomlyCreateNonDualSpecialStrategy();
            return new DualCollisionStrategy(strategy1, strategy2);
        }
    }

    /**
     * Randomly create one of the 4 special strategies (can not create dual strategy)
     *
     * @return a non-dual special strategy
     */
    private CollisionStrategy randomlyCreateNonDualSpecialStrategy() {
        int chance = random.nextInt(BOUND_WITHOUT_DUAL_COLLISION);

        if (chance < TEN_PERCENT_CHANCE) { // chance between 0 and 10, return MockPaddleCollisionStrategy
            return new PuckCollisionStrategy(gameObjects, basicStrategy, imageReader, soundReader);
        } else if (chance < TWENTY_PERCENT_CHANCE) { // chance between 10 and 20, return MockPaddleCollisionStrategy
            return new MockPaddleCollisionStrategy(gameObjects, basicStrategy, imageReader, inputListener);
        } else if (chance < THIRTY_PERCENT_CHANCE) { // chance between 20 and 30, return TurboCollisionStrategy
            return new TurboCollisionStrategy(basicStrategy, imageReader, ball);
        } else { // chance between 30 and 40, return HeartCollisionStrategy
            return new HeartCollisionStrategy(gameObjects, basicStrategy, imageReader, originalPaddle);
        }
    }

}

