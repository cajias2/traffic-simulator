package stupidModel16;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;

public class StupidPredator implements Steppable {
	private int myX, myY;
	
	public StupidPredator(StupidModel theModel) {
		placeRandomly(theModel);
	}
	
	public void placeRandomly(StupidModel theModel) {
		ObjectGrid2D mySpace = theModel.getPredatorSpace();
		int xSpaceSize = theModel.getXSpaceSize();
		int ySpaceSize = theModel.getYSpaceSize();
		// randomly place bug into space
		do {
			myX = theModel.random.nextInt(xSpaceSize);
			myY = theModel.random.nextInt(ySpaceSize);
		}
		while (mySpace.field[myX][myY] != null);
		mySpace.field[myX][myY] = this;
	}
		
	public void step(SimState state) {
		hunt(state);
	}
	
	public void hunt(SimState state) {
		StupidModel theModel = (StupidModel)state;
		ObjectGrid2D mySpace = theModel.getPredatorSpace();
		ObjectGrid2D bugSpace = theModel.getBugSpace();

		// find bugs within 1 cell of the predator (including the 
		// predator's current location)
		Bag neighbors = bugSpace.getNeighborsMaxDistance(myX,myY,1,false,null,null,null);
        // get rid of all the null neighbors
		ArrayList closeBugs = new ArrayList();
		for (int i=0; i<neighbors.objs.length; i++)
			if (neighbors.objs[i] != null)
				closeBugs.add(neighbors.objs[i]);
        // if there are no bugs, then move randomly
        if (closeBugs.size() == 0) randomMove(theModel);
        else {
        	// if there are bugs, then
			// randomly choose an index into closeBugs --
			// the predator will move to that bug's position
			// and eat it
			int foodIndex = closeBugs.size() == 1 ? 0 : theModel.random.nextInt(closeBugs.size()-1);
			StupidBug food = (StupidBug) closeBugs.get(foodIndex);
			// move 
			int newX = food.getX();
			int newY = food.getY();
			// if there's already a predator where the chosen bug is, then
			// this predator is out of luck
			if (mySpace.field[newX][newY] != this) 
				return; 
			mySpace.field[myX][myY] = null;
			myX = newX;
			myY = newY;
			mySpace.field[myX][myY] = this;
			// eat
			bugSpace.field[myX][myY] = null;
			theModel.getBugs().remove(food);
		  }
	}
	
	// not called from schedule (called above)
	private void randomMove(StupidModel theModel) {
		ObjectGrid2D mySpace = theModel.getPredatorSpace();
		// randomly choose a new position, 1 cell away
		// from the old one
		int newX, newY;
		do {
			newX = myX + theModel.random.nextInt(3) - 1; // [-1, 1]
			newY = myY + theModel.random.nextInt(3) - 1; // [-1, 1]
 	        if (newX < 0) newX++;
 	        else if (newX == mySpace.getWidth()) newX--;
 	        if (newY < 0) newY++;
 	        else if (newY == mySpace.getHeight()) newY--;
		} while (myX == newX && myY == newY);
		
		// can't move to where there's already another predator
		if (mySpace.field[newX][newY] == null) {
			// move
		    mySpace.field[myX][myY] = null;
		    myX = newX;
		    myY = newY;
		    mySpace.field[myX][myY] = this;
		}
	}
}

