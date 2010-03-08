/**
 * 
 */
package sim.app.agents;

import java.util.List;
import java.util.logging.Logger;

import edu.uci.ics.jung.graph.Graph;

import processing.core.PApplet;
import sim.app.graph.Road;
import sim.app.graph.Street;
import sim.app.graph.StreetXing;

/**
 * @author biggie
 */
public class Car extends Vehicle {

	private final double ACCELERATION = 6.25;
	private final double MAX_VELOCITY = 80.0;
	private final int SIZE = 2;

	public Car(List<Road> trayectory_, Graph<StreetXing, Road> city_,
			Logger log_, PApplet parent_) {
		super(trayectory_, city_, log_, parent_);
	}

	public double getMaxVelocity() {
		return MAX_VELOCITY;
	}

	public double getAcceleration() {
		// TODO Auto-generated method stub
		return ACCELERATION;
	}

}
