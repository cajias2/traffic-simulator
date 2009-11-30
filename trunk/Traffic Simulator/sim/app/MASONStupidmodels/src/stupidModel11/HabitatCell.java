package stupidModel11;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;

public class HabitatCell implements Steppable {
	private double foodAvailable;
	private int myX, myY;
	
	private StupidModel theModel;
	private ObjectGrid2D mySpace;
	
	public HabitatCell(StupidModel m, int x, int y) {
		theModel = m;
		mySpace = m.getHabitatSpace();
		foodAvailable = 0.0;
		mySpace.field[x][y] = this;
		myX = x;
		myY = y;
	}

	public void step(SimState state) {
		grow(state);
	}

	public void grow(SimState state) {
		StupidModel theModel = (StupidModel) state;
		foodAvailable += theModel.random.nextDouble() * 
		                 theModel.getCellMaxFoodProduction();
	}

	public double getFoodAvailable() {
		return foodAvailable;
	}

	public void setFoodAvailable(double foodAvailable) {
		this.foodAvailable = foodAvailable;
	}

	public int getX() {
		return myX;
	}

	public int getY() {
		return myY;
	}
}
