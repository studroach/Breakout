package bounce;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * A Simple Game of Bounce.
 * 
 * Our game now displays a moving rectangular "ball" that bounces off the sides
 * of the game container.
 * 
 * 
 * @author wallaces
 * 
 */
public class BounceGame extends BasicGame {
	private final int ScreenWidth;
	private final int ScreenHeight;

	Ball ball;

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public BounceGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
	}

	/**
	 * Initialize the game after the container has been set up. This is one-time
	 * initialization, and a good place to do things like load sounds and
	 * graphics...
	 * 
	 */
	@Override
	public void init(GameContainer container) throws SlickException {
		ball = new Ball(ScreenWidth / 2, ScreenHeight / 2, .1f, .2f);
	}

	/**
	 * Render the game state. For now, just draw the ball.
	 */
	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		ball.render(g);
	}

	/**
	 * Update the game state. For now, we're just bouncing the ball.
	 */
	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		// bounce the ball...
		if (ball.getCoarseGrainedMaxX() > ScreenWidth
				|| ball.getCoarseGrainedMinX() < 0) {
			ball.bounce(90);
		} else if (ball.getCoarseGrainedMaxY() > ScreenHeight
				|| ball.getCoarseGrainedMinY() < 0) {
			ball.bounce(0);
		}
		ball.update(delta);
	}

	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new BounceGame("Bounce!", 800, 600));
			app.setDisplayMode(800, 600, false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

	/**
	 * The Ball class is an Entity that has a velocity (since it's moving).
	 * 
	 */
	class Ball extends Entity {

		private Vector velocity;

		public Ball(final float x, final float y, final float vx, final float vy) {
			super(x, y);
			addImageWithBoundingBox(ResourceManager
					.getImage("resource/ball.png"));
			velocity = new Vector(vx, vy);
		}

		public void setVelocity(final Vector v) {
			velocity = v;
		}

		public Vector getVelocity() {
			return velocity;
		}

		/**
		 * Bounce the ball off a surface. This simple implementation, combined
		 * with the test used when calling this method can cause "issues" in
		 * some situations. Can you see where/when? If so, it should be easy to
		 * fix!
		 * 
		 * @param surfaceTangent
		 */
		public void bounce(float surfaceTangent) {
			velocity = velocity.bounce(surfaceTangent);
		}

		/**
		 * Update the Ball based on how much time has passed...
		 * 
		 * @param delta
		 *            the number of milliseconds since the last update
		 */
		public void update(final int delta) {
			translate(velocity.scale(delta));
		}
	}

}
