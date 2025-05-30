package bricker.gameobjects;

import bricker.main.Constants;
import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * This class extends paddle, and creates a paddle in the center of the screen that disappears after
 * a few collisions.
 */
public class MockPaddle extends Paddle {

    private static final int MOCK_PADDLE_MAX_COLLISIONS_NUMBER = 4;

    private int collisionCounter = 0;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner   Position of the object, in window coordinates (pixels).
     *                        Note that (0,0) is the top-left corner of the window.
     * @param dimensions      Width and height in window coordinates.
     * @param renderable      The renderable representing the object. Can be null, in which case
     *                        the GameObject will not be rendered.
     * @param inputListener   An interface for reading user input in the current frame
     */
    public MockPaddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                      UserInputListener inputListener) {
        super(topLeftCorner, dimensions, renderable, inputListener);
    }

    /**
     * defines the behavior of the paddle upon exising from a collision. after a certain amount of hits
     * the paddle will disappear.
     *
     * @param other The former collision partner.
     */
    @Override
    public void onCollisionExit(GameObject other) {
        super.onCollisionExit(other);
        this.collisionCounter++;
        if (collisionCounter == MOCK_PADDLE_MAX_COLLISIONS_NUMBER &&
                this.getTag().equals(Constants.MOCK_PADDLE_TAG)) {
            this.setCenter(new Vector2(0, Constants.WINDOW_HEIGHT * 2));
        }
    }
}
