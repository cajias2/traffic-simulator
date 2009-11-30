package stupidModel12;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;

public class StupidBug implements Steppable {
	private static final int SPACE_SIZE = 100;
	private int myX, myY;
	
	private double size;
	
	// this constructor is for offspring
	public StupidBug () {
		size = 0.0;
	}

	// this is the original 1-argument constructor from version 1
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
	
	// used just for moving, since the order of Bugs is supposed
	// to be randomized, and we're using RandomSequence to do this
	public void step(SimState state) {
		move(state);
	}
	
	public void move(SimState state) {
		StupidModel theModel = (StupidModel) state;
		ObjectGrid2D mySpace = theModel.getBugSpace();
		ObjectGrid2D habitatSpace = theModel.getHabitatSpace();
		Bag nearbyCells = habitatSpace.getNeighborsMaxDistance(myX,myY,4,true,null,null,null);
        // find the cell with the most food
        HabitatCell bestCell = (HabitatCell) habitatSpace.field[myX][myY];
        for (int i=0; i<nearbyCells.size(); i++) {
        	HabitatCell nextCell = (HabitatCell) nearbyCells.get(i);
        	if (mySpace.field[nextCell.getX()][nextCell.getY()] == null &&
    	        nextCell.getFoodAvailable() > bestCell.getFoodAvailable()) 
        		bestCell = nextCell;
        }
        if (bestCell != habitatSpace.field[myX][myY]) {
        	mySpace.field[myX][myY] = null;
        	myX = bestCell.getX();  
        	myY = bestCell.getY();
        	mySpace.field[myX][myY] = this;
        }
	}
	
	public void grow(SimState state) {
		StupidModel theModel = (StupidModel) state;
		ObjectGrid2D habitatSpace = theModel.getHabitatSpace();
		HabitatCell myCell = (HabitatCell) habitatSpace.field[myX][myY];
		double foodEaten = Math.min(theModel.getBugMaxDailyFoodConsumption(), myCell.getFoodAvailable()); 
		size += foodEaten;
		myCell.setFoodAvailable(myCell.getFoodAvailable() - foodEaten);
		if (size > theModel.getBugReproductionSize()) 
			reproduce(state);
	}

	public void reproduce(SimState state) {
		StupidModel theModel = (StupidModel) state;
		Bag theBugs = theModel.getBugs();
		ObjectGrid2D bugSpace = theModel.getBugSpace();
		// make 5 offspring
		for (int i=0; i<5; i++) {
			StupidBug offspring = new StupidBug();
			// attempt to place the offspring 5 times
			for (int j=0; j<5; j++) {
				// myX +- 3
				int newX = bugSpace.stx(myX + (theModel.random.nextInt(7) - 3));
				int newY = bugSpace.sty(myY + (theModel.random.nextInt(7) - 3));
            	if (bugSpace.field[newX][newY] == null) {
            		bugSpace.field[newX][newY] = offspring;
            		offspring.myX = newX;
            		offspring.myY = newY;
            		theBugs.add(offspring);
            		break;
            	}
			}
		}
		// die
		bugSpace.field[myX][myY] = null;
		theBugs.remove(this);
	}

	public void survive(SimState state) {
		StupidModel theModel = (StupidModel) state;
		ObjectGrid2D bugSpace = theModel.getBugSpace();
		Bag theBugs = theModel.getBugs();
		double mightSurvive = theModel.random.nextDouble();
		if (mightSurvive > theModel.getBugSurvivalProbability()) {
			// die
			bugSpace.field[myX][myY] = null;
			theBugs.remove(this);
		}
	}
	public double getSize() {
		return size;
	}
}
