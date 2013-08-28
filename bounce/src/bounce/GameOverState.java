package bounce;

import java.util.Iterator;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;


class GameOverState extends BasicGameState {
	

	int timer;
	int lastKnownBounces;
	
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
		BounceGame bg = (BounceGame)game;
		g.drawString("Bounces: " + lastKnownBounces, 10, 30);
		for (Bang b : bg.explosions)
			b.render(g);
		g.drawImage(ResourceManager.getImage("resource/GameOver.png"), 225,
				270);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		
		timer -= delta;
		if (timer <= 0)
			game.enterState(BounceGame.STARTUPSTATE, new EmptyTransition(), new HorizontalSplitTransition() );
		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = ((BounceGame)game).explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

	}

	@Override
	public int getID() {
		return BounceGame.GAMEOVERSTATE;
	}
	
}