package bounce;

import java.util.Iterator;
import java.util.Random;

import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 * 
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	int bounces;
	int lives;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		bounces = 0;
		lives = 3;
		container.setSoundOn(true);
		BounceGame bg = (BounceGame)game;
		bg.mesh.drawBricks(bg.level);
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		for(int i = 0; i < bg.mesh.xCount; i++) {
			for(int j = 0; j < bg.mesh.yCount; j++) {
				bg.mesh.index[i][j].render(g);
			}
		}
		bg.ball.render(g);
		bg.paddle.render(g);
		g.drawString("Bounces: " + bounces, 10, 30);
		g.drawString("Lives: " + lives, 10, 50);
		for (Bang b : bg.explosions)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;
		
		//testing level navigation
		if (input.isKeyDown(Input.KEY_1)) {
			bg.level = 1;
			bg.enterState(BounceGame.PLAYINGSTATE);
		}else if (input.isKeyDown(Input.KEY_2)) {
			bg.level = 2;
			bg.enterState(BounceGame.PLAYINGSTATE);
		}else if (input.isKeyDown(Input.KEY_3)) {
			bg.level = 3;
			bg.enterState(BounceGame.PLAYINGSTATE);
		}
		//paddle controls
		if (input.isKeyDown(Input.KEY_A) && input.isKeyDown(Input.KEY_D)) {
			bg.paddle.setVelocity(new Vector(0, 0));
		}else if (input.isKeyDown(Input.KEY_D)) {
			if(bg.paddle.getCoarseGrainedMaxX() > (bg.ScreenWidth - 10)) {
				bg.paddle.setVelocity(new Vector(0, 0));
			}else {
				bg.paddle.setVelocity(new Vector(1, 0));
			}
		}else if (input.isKeyDown(Input.KEY_A)) {
			if(bg.paddle.getCoarseGrainedMinX() < 10) {
				bg.paddle.setVelocity(new Vector(0, 0));
			}else {
				bg.paddle.setVelocity(new Vector(-1, 0));
			}
		}else {
			bg.paddle.setVelocity(new Vector(0, 0));
		}
		
		// bounce the ball...
		boolean bounced = false;
		int trackPaddle = bg.paddle.tracker(bg.ball);
		int brickBounce = bg.mesh.checkCollisions(bg.ball);
		if (bg.ball.getCoarseGrainedMaxX() > bg.ScreenWidth
				|| bg.ball.getCoarseGrainedMinX() < 0) {
			bg.ball.bounce(90);
			bounced = true;
		} else if (bg.ball.getCoarseGrainedMinY() < 0) {
			bg.ball.bounce(0);
			bounced = true;
		} else if (trackPaddle == 1) {
			bg.ball.bounce(0);
			bounced = true;
		} else if (trackPaddle == 2) {
			bg.ball.bounce(90);
			bounced = true;
		} else if (brickBounce == 1) {
			bg.ball.bounce(90);
			bounced = true;
		} else if (brickBounce == 2) {
			bg.ball.bounce(0);
			bounced = true;
		}
		
		if (bounced) {
			bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
			bounces++;
			bg.ball.update(delta);
		}
		if (bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight) {
			lives--;
			//respawn the ball if it hits the bottom
			bg.ball.setPosition(bg.ScreenWidth / 2, (bg.ScreenHeight / 6) * 5);
			//gives the ball a new random velocity
			Random rand = new Random();
			bg.ball.setVelocity(new Vector( (float)(rand.nextFloat() -.5) , -.3f ));
		}
		bg.ball.update(delta);
		bg.paddle.update(delta);

		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}
		
		if (bg.mesh.brickCount == 0) {
			bg.level++;
			bg.enterState(BounceGame.PLAYINGSTATE);
		}
		
		if (lives <= 0) {
			((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(bounces);
			game.enterState(BounceGame.GAMEOVERSTATE);
		}
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}