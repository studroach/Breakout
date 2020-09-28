package bounce;

import jig.Entity;
import jig.ResourceManager;

public class Brick extends Entity{
	float maxX;
	float maxY;
	float minX;
	float minY;
	int stage;
	
	public Brick(int x, int y, int level) {
		super(x + 30, y + 20);
		maxX = x + 60;
		maxY = y + 40;
		minX = x; 
		minY = y;
		stage = level;
		addImageWithBoundingBox(ResourceManager.getImage("bounce/resource/Brick" + stage + ".png"));
	}
	
	public boolean hit() {
		if(stage > 0) {
			stage = stage -1;
			removeImage(ResourceManager.getImage("bounce/resource/Brick" + (stage + 1) + ".png"));
			addImageWithBoundingBox(ResourceManager.getImage("bounce/resource/Brick" + stage + ".png"));
			return true;
		}
		return false;
	}
	
}
