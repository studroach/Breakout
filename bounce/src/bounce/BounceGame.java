package bounce;

import java.util.ArrayList;
import java.util.Iterator;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;

/**
 * A Simple Game of Bounce.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 * 
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 * 
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 * 
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 * 
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 * 
 * 
 * @author wallaces
 * 
 */
public class BounceGame extends StateBasedGame {

	private final int ScreenWidth;
	private final int ScreenHeight;

	private Ball ball;
	private int bounces;
	private ArrayList<Bang> explosions;

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
		explosions = new ArrayList<Bang>(10);
		
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingState());
		
		enterState(0);
	}


	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new BounceGame("Bounce!", 800, 600));
			app.setDisplayMode(800, 600, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

	/**
	 * The Ball class is an Entity that has a velocity (since it's moving). When
	 * the Ball bounces off a surface, it temporarily displays a image with
	 * cracks for a nice visual effect.
	 * 
	 */
	class Ball extends Entity {

		private Vector velocity;
		private int countdown;

		public Ball(final float x, final float y, final float vx, final float vy) {
			super(x, y);
			addImageWithBoundingBox(ResourceManager
					.getImage("resource/ball.png"));
			velocity = new Vector(vx, vy);
			countdown = 0;
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
			ball.removeImage(ResourceManager.getImage("resource/ball.png"));
			ball.addImageWithBoundingBox(ResourceManager
					.getImage("resource/brokenball.png"));
			countdown = 500;
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
			if (countdown > 0) {
				countdown -= delta;
				if (countdown <= 0) {
					ball.addImageWithBoundingBox(ResourceManager
							.getImage("resource/ball.png"));
					ball.removeImage(ResourceManager
							.getImage("resource/brokenball.png"));
				}
			}
		}
	}

	/**
	 * A class representing a transient explosion. The game should monitor
	 * explosions to determine when they are no longer active and remove/hide
	 * them at that point.
	 */
	class Bang extends Entity {
		private Animation explosion;

		public Bang(final float x, final float y) {
			super(x, y);
			explosion = new Animation(ResourceManager.getSpriteSheet(
					"resource/explosion.png", 64, 64), 0, 0, 22, 0, true, 50,
					true);
			addAnimation(explosion);
			explosion.setLooping(false);
			ResourceManager.getSound("resource/explosion.wav").play();
		}

		public boolean isActive() {
			return !explosion.isStopped();
		}
	}
	
	class StartUpState extends BasicGameState {

		@Override
		public void init(GameContainer container, StateBasedGame game)
				throws SlickException {
			ball = new Ball(ScreenWidth / 2, ScreenHeight / 2, .1f, .2f);

			// the sound resource takes a particularly long time to load,
			// we preload it here to (1) reduce latency when we first play it
			// and (2) because loading it will load the audio libraries and
			// unless that is done now, we can't *disable* sound as we
			// attempt to do in the startUp() method.
			ResourceManager.loadSound("resource/explosion.wav");	

			System.out.println("StartUpState init!");
		}
		@Override
		public void enter(GameContainer container, StateBasedGame game) {
			container.setSoundOn(false);
			System.out.println("StartUpState enter!");
		}


		@Override
		public void render(GameContainer container, StateBasedGame game,
				Graphics g) throws SlickException {
			ball.render(g);
			g.drawString("Bounces: ?", 10, 30);
			for (Bang b : explosions)
				b.render(g);
			g.drawImage(ResourceManager.getImage("resource/PressSpace.png"),
					225, 270);		
		}

		@Override
		public void update(GameContainer container, StateBasedGame game,
				int delta) throws SlickException {
			Input input = container.getInput();
			if (input.isKeyDown(Input.KEY_SPACE))
				enterState(1);	
			
			// bounce the ball...
			boolean bounced = false;
			if (ball.getCoarseGrainedMaxX() > ScreenWidth
					|| ball.getCoarseGrainedMinX() < 0) {
				ball.bounce(90);
				bounced = true;
			} else if (ball.getCoarseGrainedMaxY() > ScreenHeight
					|| ball.getCoarseGrainedMinY() < 0) {
				ball.bounce(0);
				bounced = true;
			}
			if (bounced) {
				explosions.add(new Bang(ball.getX(), ball.getY()));
				bounces++;
			}
			ball.update(delta);

			// check if there are any finished explosions, if so remove them
			for (Iterator<Bang> i = explosions.iterator(); i.hasNext();) {
				if (!i.next().isActive()) {
					i.remove();
				}
			}

		}

		@Override
		public int getID() {
			return 0;
		}
		
	}

	class PlayingState extends BasicGameState {

		@Override
		public void init(GameContainer container, StateBasedGame game)
				throws SlickException {
		}

		@Override
		public void enter(GameContainer container, StateBasedGame game) {
			bounces = 0;
			container.setSoundOn(true);
		}
		@Override
		public void render(GameContainer container, StateBasedGame game,
				Graphics g) throws SlickException {
			ball.render(g);
			g.drawString("Bounces: " + bounces, 10, 30);
			for (Bang b : explosions)
				b.render(g);
		}

		@Override
		public void update(GameContainer container, StateBasedGame game,
				int delta) throws SlickException {

			Input input = container.getInput();

			if (input.isKeyDown(Input.KEY_W)) {
				ball.setVelocity(ball.getVelocity().add(new Vector(0f, -.001f)));
			}
			if (input.isKeyDown(Input.KEY_S)) {
				ball.setVelocity(ball.getVelocity().add(new Vector(0f, +.001f)));
			}
			if (input.isKeyDown(Input.KEY_A)) {
				ball.setVelocity(ball.getVelocity().add(new Vector(-.001f, 0)));
			}
			if (input.isKeyDown(Input.KEY_D)) {
				ball.setVelocity(ball.getVelocity().add(new Vector(+.001f, 0f)));
			}
			// bounce the ball...
			boolean bounced = false;
			if (ball.getCoarseGrainedMaxX() > ScreenWidth
					|| ball.getCoarseGrainedMinX() < 0) {
				ball.bounce(90);
				bounced = true;
			} else if (ball.getCoarseGrainedMaxY() > ScreenHeight
					|| ball.getCoarseGrainedMinY() < 0) {
				ball.bounce(0);
				bounced = true;
			}
			if (bounced) {
				explosions.add(new Bang(ball.getX(), ball.getY()));
				bounces++;
			}
			ball.update(delta);

			// check if there are any finished explosions, if so remove them
			for (Iterator<Bang> i = explosions.iterator(); i.hasNext();) {
				if (!i.next().isActive()) {
					i.remove();
				}
			}

			if (bounces >= 10) {
				game.enterState(2);
			}
		}

		@Override
		public int getID() {
			return 1;
		}
		
	}
	
	class GameOverState extends BasicGameState {
		int timer;
		
		@Override
		public void init(GameContainer container, StateBasedGame game)
				throws SlickException {
		}
		@Override
		public void enter(GameContainer container, StateBasedGame game) {
			timer = 4000;
		}

		@Override
		public void render(GameContainer container, StateBasedGame game,
				Graphics g) throws SlickException {
			g.drawString("Bounces: " + bounces, 10, 30);
			for (Bang b : explosions)
				b.render(g);
			g.drawImage(ResourceManager.getImage("resource/GameOver.png"), 225,
					270);

		}

		@Override
		public void update(GameContainer container, StateBasedGame game,
				int delta) throws SlickException {
			timer -= delta;
			if (timer <= 0)
				game.enterState(0, new EmptyTransition(), new HorizontalSplitTransition() );
			// check if there are any finished explosions, if so remove them
			for (Iterator<Bang> i = explosions.iterator(); i.hasNext();) {
				if (!i.next().isActive()) {
					i.remove();
				}
			}

		}

		@Override
		public int getID() {
			return 2;
		}
		
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		// TODO Auto-generated method stub
		
	}
}
