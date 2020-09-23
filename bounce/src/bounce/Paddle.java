package bounce;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

class Paddle extends Entity {
	
	private Vector velocity;
	private int countdown;
	//for tracking if the ball impacts from vertical or horizontal
	private boolean vTracker, hTracker;

	public Paddle(final float x, final float y) {
		super(x, y);
		addImageWithBoundingBox(ResourceManager
				.getImage(BounceGame.PADDLE_RSC));
		velocity = new Vector(0, 0);
		countdown = 0;
		vTracker = false;
		hTracker = false;
	}

	public void setVelocity(final Vector v) {
		velocity = v;
	}
	
	public int tracker(Ball ball) {
		boolean oldV = vTracker, oldH = hTracker;
		checkV(ball);
		checkH(ball);
		if(vTracker && oldV && hTracker) {
			return(1);
		}else if(hTracker && oldH && vTracker) {
			return(2);
		}
		return 0;
	}
	
	private void checkV(Ball ball) {
		if(ball.getCoarseGrainedMaxX() > getCoarseGrainedMinX() && ball.getCoarseGrainedMinX() < getCoarseGrainedMaxX()) {
			vTracker = true;
		}else {
			vTracker = false;
		}
	}
	
	private void checkH(Ball ball) {
		if(ball.getCoarseGrainedMaxY() > getCoarseGrainedMinY() && ball.getCoarseGrainedMinY() < getCoarseGrainedMaxY()) {
			hTracker = true;
		}else {
			hTracker = false;
		}
	}
	
	public void update(final int delta) {
		translate(velocity.scale(delta));
		if (countdown > 0) {
			countdown -= delta;
			if (countdown <= 0) {
				addImageWithBoundingBox(ResourceManager
						.getImage(BounceGame.PADDLE_RSC));
			}
		}
	}
}
