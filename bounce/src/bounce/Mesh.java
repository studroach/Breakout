package bounce;

import org.newdawn.slick.Graphics;

import jig.ResourceManager;

//The mesh keeps track of all the bricks' data
public class Mesh {
	int height, width, xCount, yCount, xOffset, yOffset;
	int[][] index;
			
	public Mesh() {
		height = 342;
		width = 654;
		xCount = 10;
		yCount = 8;
		xOffset = 223;
		yOffset = 120;
	}
	
	public void drawBricks(Graphics g) {
		for(int i = 0; i < xCount; i++) {
			for(int j = 0; j < yCount; j++) {
				g.drawImage(ResourceManager.getImage(BounceGame.BRICK0_RSC), (float)(xOffset + (i*66)), (float)(yOffset + (j*46)));
			}
		}
	}
}
