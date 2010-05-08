/**
 * 
 */
package sim.app.agents.display.vehicle;

import java.util.List;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.geo.Road;
import sim.app.geo.StreetXing;
import sim.app.geo.distance.Distance;
import sim.app.geo.distance.Kilometers;
import sim.app.geo.distance.Meters;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 */
public class Car extends Vehicle {

    private final Distance ACCELERATION = new Kilometers(6.25);
    private final Distance MAX_VELOCITY = new Kilometers(100.0);
    private final Distance SIZE = new Meters(4.0);

    public Car(List<Road> trayectory_, Graph<StreetXing, Road> city_, Logger log_, PApplet parent_) {
	super(trayectory_, city_, log_, parent_);
    }

    /**
     * Given in K. Assumed to be K/s
     */
    @Override
    public Distance getMaxVelocity() {
	return MAX_VELOCITY;
    }

    /**
     * Given in K. Assumed to be K/s^2
     */
    @Override
    public Distance getAcceleration() {
	return ACCELERATION;
    }

    /**
     * Given in m.
     */
    @Override
    public Distance getSize() {
	return SIZE;
    }

    @Override
    public void display() {
	super.display();
    }

}
