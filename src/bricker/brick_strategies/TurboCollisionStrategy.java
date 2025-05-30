package bricker.brick_strategies;


import bricker.main.Constants;
import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Defines behavior upon a collision with a unit that has this trait.
 * In addition to breaking the brick, will cause the ball to increase its speed by a factor, if it was
 * the main ball. Will only activate if not activated already.
 * Will last for 6 collisions with other units.
 * Written by: Noam K
 */
class TurboCollisionStrategy implements CollisionStrategy {

    private static final String RED_BALL_IMAGE_PATH = "assets/redball.png";

    // private fields
    private final CollisionStrategy baseStrategy;
    private final ImageReader imageReader;
    private final GameObject ball;

    /**
     * This strategy makes the main ball become faster for a set amount of collision
     * in addition to removing the brick
     *
     * @param baseStrategy a basic strategy to wrap and add more functionality on top
     * @param imageReader  Used to read images
     * @param ball         the ball to turn turbo
     */
    TurboCollisionStrategy(CollisionStrategy baseStrategy,
                                  ImageReader imageReader, GameObject ball) {
        this.baseStrategy = baseStrategy;
        this.imageReader = imageReader;
        this.ball = ball;
    }

    /**
     * Defines the behavior of the brick upon collision.
     * Turns the main ball faster for a set amount of collisions.
     *
     * @param object1 first object to collide
     * @param object2 second object to collide
     */
    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        baseStrategy.onCollision(object1, object2);
        // only activate if main ball hit object1, not mock ball or turbo ball
        if (object2.getTag().equals(Constants.MAIN_BALL_TAG)) {
            activateTurboBall();
        }
    }

    /**
     * This makes the ball faster for a couple of collisions.
     */
    private void activateTurboBall() {
        this.ball.setTag(Constants.TURBO_BALL_TAG); // set the tag of the ball to turbo
        Vector2 originalVel = this.ball.getVelocity(); // multiply ball velocity
        this.ball.setVelocity(new Vector2(originalVel.x() * Constants.SPEED_MULTIPLIER,
                originalVel.y() * Constants.SPEED_MULTIPLIER));
        Renderable redBall = imageReader.readImage(RED_BALL_IMAGE_PATH, true); // load photo
        this.ball.renderer().setRenderable(redBall);
    }
}
