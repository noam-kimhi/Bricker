package bricker.gameobjects;

import bricker.main.Constants;
import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * The paddle in a game. Controlled by user through VK_Left and VK_Right
 * cannot exceed the limits of the screen
 * Written by: Noam K
 */
public class Paddle extends GameObject {

    private static final float PADDLE_MOVEMENT_SPEED = 350;

    private final UserInputListener inputListener;

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
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
    }

    /**
     * Defines the movement of the paddle through user input with every update.
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDir = Vector2.ZERO; // set starting movement speed direction to 0

        // read user input and move accordingly left or right
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add((Vector2.LEFT));
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = movementDir.add((Vector2.RIGHT));
        }
        setVelocity(movementDir.mult(PADDLE_MOVEMENT_SPEED));

        checkBoundaries(); // check for leaving boundaries
    }

    /**
     * Checking if the paddle is leaving the boundaries, if so, repositions it inside the screen.
     */
    private void checkBoundaries(){
        // check if leaving left boundaries
        // if it does set its position right after the left border
        if (this.getTopLeftCorner().x() < Constants.BORDER_LENGTH + 1) {
            this.setTopLeftCorner(new Vector2(Constants.BORDER_LENGTH + 1, super.getTopLeftCorner().y()));
        }
        // check if leaving right boundaries which are the top left corner of the paddle + its width
        // in case it does, set its position right after the right border + its width
        if (this.getTopLeftCorner().x() + this.getDimensions().x() > Constants.WINDOW_WIDTH -
                Constants.BORDER_LENGTH + 1) {
            this.setTopLeftCorner(new Vector2(Constants.WINDOW_WIDTH - this.getDimensions().x() -
                    Constants.BORDER_LENGTH,
                    super.getTopLeftCorner().y()));
        }
    }
}
