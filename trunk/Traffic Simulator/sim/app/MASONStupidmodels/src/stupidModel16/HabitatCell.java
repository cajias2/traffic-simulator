package stupidModel16;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;

public class HabitatCell implements Steppable {
	private double foodAvailable;
	private double foodProductionRate;
	
	private int myX, myY;
	
	private StupidModel theModel;
	private ObjectGrid2D mySpace;
	
	public HabitatCell(StupidModel m, int x, int y, double prodRate) {
		theModel = m;
		mySpace = m.getHabitatSpace();
		foodAvailable = 0.0;
		foodProductionRate = prodRate;
		mySpace.field[x][y] = this;
		myX = x;
		myY = y;
	}

	public void step(SimState state) {
		grow(state);
	}

	public void grow(SimState state) {
		StupidModel theModel = (StupidModel) state;
		foodAvailable += foodProductionRate;
	}

	public double getFoodAvailable() {
		return foodAvailable;
	}

	public int getX() {
		return myX;
	}

	public int getY() {
		return myY;
	}

	public void setFoodAvailable(double foodAvailable) {
		this.foodAvailable = foodAvailable;
	}
	
}
