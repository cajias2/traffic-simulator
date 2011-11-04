/**
 * 
 */
package sim.agents.traffic.vhcl;

import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import processing.core.PApplet;
import sim.app.geo.distance.Distance;
import sim.app.geo.distance.Kilometers;
import sim.app.geo.distance.Meters;
import sim.graph.traffic.Road;
import sim.graph.traffic.StreetXing;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 */
public class Car extends Vehicle {

    private final Distance ACCELERATION = new Kilometers(6.25);
    private final Distance MAX_VELOCITY = new Kilometers(100.0);
    private final Distance SIZE = new Meters(4.0);

    public Car(List<Road> trayectory_, Graph<StreetXing, Road> city_, Logger log_, PApplet parent_) {
	super(null, trayectory_, city_, log_, parent_);
    }

    public Car(List<Road> route_, Graph<StreetXing, Road> city_, Document doc_, Logger log_) {
	super(route_, city_, doc_, log_);
    }

    /**
     * Given in K. Assumed to be K/h
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
    public Distance getLegth() {
	return SIZE;
    }

}
