package stupidModel03;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.ObjectGrid2D;

public class HabitatCell implements Steppable {
	private double foodAvailable;
	private double maxFoodProductionRate = .01;
	
	private StupidModel theModel;
	private ObjectGrid2D mySpace;
	
	public HabitatCell(StupidModel m, int x, int y) {
		theModel = m;
		mySpace = m.getHabitatSpace();
		foodAvailable = 0.0;
		mySpace.field[x][y] = this;
	}

	public void step(SimState state) {
		grow();
	}

	public void grow() {
		foodAvailable += theModel.random.nextDouble() * maxFoodProductionRate;
	}

	public double getFoodAvailable() {
		return foodAvailable;
	}

	public void setFoodAvailable(double foodAvailable) {
		this.foodAvailable = foodAvailable;
	}
	
}
