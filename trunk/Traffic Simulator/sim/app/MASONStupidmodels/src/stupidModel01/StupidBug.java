package sim.app.MASONStupidmodels.src.stupidModel01;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;

public class StupidBug implements Steppable {
	private static final int SPACE_SIZE = 100;
	private StupidModel theModel;
	private ObjectGrid2D mySpace;
	private int myX, myY;
	
	public StupidBug(StupidModel m) {
		theModel = m;
		mySpace = m.getBugSpace();
		placeRandomly();
	}
	
	public void placeRandomly() {
		// randomly place bug into space
		do {
			myX = theModel.random.nextInt(SPACE_SIZE);
			myY = theModel.random.nextInt(SPACE_SIZE);
		}
		while (mySpace.field[myX][myY] != null);
		mySpace.field[myX][myY] = this;
	}
	
	public void step(SimState state) {
		move();
	}
	
	public void move() {
		int newX, newY;
		do {
		  int dx = theModel.random.nextInt(9) - 4;  // [-4, 4]
		  int dy = theModel.random.nextInt(9) - 4;
		  newX = mySpace.stx(myX + dx);
		  newY = mySpace.sty(myY + dy);
		}
		while (mySpace.field[newX][newY] != null);
	    mySpace.field[myX][myY] = null;
	    myX = newX;
	    myY = newY;
	    mySpace.field[myX][myY] = this;
	}
}
