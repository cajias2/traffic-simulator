package stupidModel06;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;

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
	
	// not used, since I subclassed Sequence in the StupidModel
	public void step(SimState state) {
		move(state);
		grow(state);  // not what we want -- we want all the
		         // bugs to move, then all of them to grow
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
		StupidModel theModel = (StupidModel) state;
		ObjectGrid2D habitatSpace = theModel.getHabitatSpace();
		size += Math.min(theModel.getBugMaxDailyFoodConsumption(), 
				((HabitatCell)habitatSpace.field[myX][myY]).getFoodAvailable());
	}

	public double getSize() {
		return size;
	}
}
