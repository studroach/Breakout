package bounce;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

class Paddle extends Entity {
	
	private Vector velocity;
	private int countdown;

	public Paddle(final float x, final float y) {
		super(x, y);
		addImageWithBoundingBox(ResourceManager
				.getImage(BounceGame.PADDLE_RSC));
		velocity = new Vector(0f, 0f);
		countdown = 0;
	}

	public void setVelocity(final Vector v) {
		velocity = v;
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
