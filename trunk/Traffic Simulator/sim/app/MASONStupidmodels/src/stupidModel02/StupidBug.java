package sim.app.MASONStupidmodels.src.stupidModel02;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;

// in v1, StupidBug implemented Steppable, because
// the bugs were directly placed on the schedule.
// now, because we want all the bugs to move and then
// all of them to grow, can't place the bugs themselves
// on the schedule (because all the schedule can do
// is call step).  But we still have to implement
// Steppable, because a Sequence requires an array
// of Steppable objects
public class StupidBug implements Steppable {
	private static final int SPACE_SIZE = 100;
	private int myX, myY;
	
	private double size;
	
	public StupidBug(StupidModel theModel) {
		placeRandomly(theModel);
		size = 1.0;
	}
	
	public void placeRandomly(StupidModel theModel) {
		ObjectGrid2D mySpace = theModel.getBugSpace();
		// randomly place bug into space
		do {
			myX = theModel.random.nextInt(SPACE_SIZE);
			myY = theModel.random.nextInt(SPACE_SIZE);
		}
		while (mySpace.field[myX][myY] != null);
		mySpace.field[myX][myY] = this;
	}
	
	/* not used, because we want all the bugs to move,
	 * and then all the bugs to grow. */
	public void step(SimState state) {
		// this would make a bug move and grow, then the
		// next bug move and grow...  not what we want
/*		move();
		grow();  */
	}
	
	public void move(SimState state) {
		StupidModel theModel = (StupidModel) state;
		ObjectGrid2D mySpace = theModel.getBugSpace();
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
	
	public void grow(SimState state) {
		size += 1.0;
	}

	public double getSize() {
		return size;
	}
}
