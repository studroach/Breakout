package bounce;

//The mesh keeps track of all the bricks' data
public class Mesh {
	int height, width, xCount, yCount, xOffset, yOffset;
	Brick[][] index;
	int brickCount;
	int[] ballLoc;//this was a funny coincidence
			
	public Mesh() {
		height = 362;
		width = 654;
		xCount = 10;
		yCount = 8;
		xOffset = 223;
		yOffset = 120;
		index = new Brick[xCount][yCount];
		ballLoc = new int[4];
	}
	
	public void drawBricks(int level) {
		for(int i = 0; i < xCount; i++) {
			for(int j = 0; j < yCount; j++) {
				index[i][j] = new Brick(xOffset + (i*66), yOffset + (j*46), level);
			}
		}
		brickCount = 80 * level;
	}
	
	//              2
	//              v
	//          +--------+
	//          |        |
	//    0 --> |        | <-- 1
	//          |        |
	//          +--------+
	//              ^
	//              3
	//get rows and columns ball is in
	public void trackBall(Ball ball) {
		float ball0 = ball.getCoarseGrainedMinX();
		float ball1 = ball.getCoarseGrainedMaxX();
		float ball2 = ball.getCoarseGrainedMinY();
		float ball3 = ball.getCoarseGrainedMaxY();
		//binary search
		//check 0
		if(ball0 > 547) {
			if(ball0 > 679) {
				if(ball0 > 745) {
					if(ball0 > 811) {
						if(ball0 > 877) {
							ballLoc[0] = 0;
						}else {
							ballLoc[0] = 10;
						}
					}else {
						ballLoc[0] = 9;
					}
				}else {
					ballLoc[0] = 8;
				}
			}else {
				if(ball0 > 613) {
					ballLoc[0] = 7;
				}else {
					ballLoc[0] = 6;
				}
			}
		}else {
			if(ball0 > 349) {
				if(ball0 > 415) {
					if(ball0 > 481) {
						ballLoc[0] = 5;
					}else {
						ballLoc[0] = 4;
					}
				}else {
					ballLoc[0] = 3;
				}
			}else {
				if(ball0 > 223) {
					if(ball0 > 283) {
						ballLoc[0] = 2;
					}else {
						ballLoc[0] = 1;
					}
				}else {
					ballLoc[0] = 0;
				}
			}
		}
		
		//check 1
		if(ball1 > 553) {
			if(ball1 > 751) {
				if(ball1 > 877) {
					ballLoc[1] = 0;
				}else {
					if(ball1 > 817) {
						ballLoc[1] = 10;
					}else {
						ballLoc[1] = 9;
					}
				}
			}else {
				if(ball1 > 685) {
					ballLoc[1] = 8;
				}else {
					if(ball1 > 619) {
						ballLoc[1] = 7;
					}else {
						ballLoc[1] = 6;
					}
				}
			}
		}else {
			if(ball1 > 412) {
				if(ball1 > 487) {
					ballLoc[1] = 5;
				}else {
					ballLoc[1] = 4;
				}
			}else {
				if(ball1 > 355) {
					ballLoc[1] = 3;
				}else {
					if(ball1 > 289) {
						ballLoc[1] = 2;
					}else {
						if(ball1 > 223) {
							ballLoc[1] = 1;
						}else {
							ballLoc[1] = 0;
						}
					}
				}
			}
		}
		
		//check 2
		if(ball2 > 298) {
			if(ball2 > 390) {
				if(ball2 > 436) {
					if(ball2 > 482) {
						ballLoc[2] = 0;
					}else {
						ballLoc[2] = 8;
					}
				}else {
					ballLoc[2] = 7;
				}
			}else {
				if(ball2 > 344) {
					ballLoc[2] = 6;
				}else {
					ballLoc[2] = 5;
				}
			}
		}else {
			if(ball2 > 206) {
				if(ball2 > 252) {
					ballLoc[2] = 4;
				}else {
					ballLoc[2] = 3;
				}
			}else {
				if(ball2 > 160) {
					ballLoc[2] = 2;
				}else {
					if(ball2 > 120) {
						ballLoc[2] = 1;
					}else {
						ballLoc[2] = 0;
					}
				}
			}
		}
		
		//check 3
		if(ball3 > 350) {
			if(ball3 > 442) {
				if(ball3 > 482) {
					ballLoc[3] = 0;
				}else {
					ballLoc[3] = 8;
				}
			}else {
				if(ball3 > 396) {
					ballLoc[3] = 7;
				}else {
					ballLoc[3] = 6;
				}
			}
		}else {
			if(ball3 > 258) {
				if(ball3 > 304) {
					ballLoc[3] = 5;
				}else {
					ballLoc[3] = 4;
				}
			}else {
				if(ball3 > 212) {
					ballLoc[3] = 3;
				}else {
					if(ball3 > 166) {
						ballLoc[3] = 2;
					}else {
						if(ball3 > 120) {
							ballLoc[3] = 1;
						}else {
							ballLoc[3] = 0;
						}
					}
				}
			}
		}
	}
	
	//0 = no collision, 1 = collision from side (horizontal), 2 = collision from vertical
	public int checkCollisions(Ball ball) {
		int[] oldLoc = new int[4];
		for(int i = 0; i < 4; i++) {
			oldLoc[i] = ballLoc[i];
		}
		trackBall(ball);
		//compare locs
		int bounceDir = 0;
		if((ballLoc[0] == 0 && ballLoc[1] == 0) || (ballLoc[2] == 0 && ballLoc[3] == 0)) {
			return 0;
		}else {
			//impact from 0
			if(ballLoc[0] > 0 && ballLoc[0] != oldLoc[0]) {
				if(ballLoc[2] > 0) {
					if(index[ballLoc[0] - 1][ballLoc[2] - 1].hit()) {
						bounceDir = 1;
						brickCount--;
					}
				}
				if(ballLoc[3] > 0) {
					if(index[ballLoc[0] - 1][ballLoc[3] - 1].hit()) {
						bounceDir = 1;
						brickCount--;
					}
				}
			}
			//impact from 1
			if(ballLoc[1] > 0 && ballLoc[1] != oldLoc[1]) {
				if(ballLoc[2] > 0) {
					if(index[ballLoc[1] - 1][ballLoc[2] - 1].hit()) {
						bounceDir = 1;
						brickCount--;
					}
				}
				if(ballLoc[3] > 0) {
					if(index[ballLoc[1] - 1][ballLoc[3] - 1].hit()) {
						bounceDir = 1;
						brickCount--;
					}
				}
			}
			//impact from 2
			if(ballLoc[2] > 0 && ballLoc[2] != oldLoc[2]) {
				if(ballLoc[0] > 0) {
					if(index[ballLoc[0] - 1][ballLoc[2] - 1].hit()) {
						bounceDir = 2;
						brickCount--;
					}
				}
				if(ballLoc[1] > 0) {
					if(index[ballLoc[1] - 1][ballLoc[2] - 1].hit()) {
						bounceDir = 2;
						brickCount--;
					}
				}
			}
			//impact from 3
			if(ballLoc[3] > 0 && ballLoc[3] != oldLoc[3]) {
				if(ballLoc[0] > 0) {
					if(index[ballLoc[0] - 1][ballLoc[3] - 1].hit()) {
						bounceDir = 2;
						brickCount--;
					}
				}
				if(ballLoc[1] > 0) {
					if(index[ballLoc[1] - 1][ballLoc[3] - 1].hit()) {
						bounceDir = 2;
						brickCount--;
					}
				}
			}
		}
		return bounceDir;
	}
}























