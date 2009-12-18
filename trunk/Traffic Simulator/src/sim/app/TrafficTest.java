package sim.app;

import java.util.ArrayList;
import java.util.List;

import sim.app.graph.CitySimState;
import sim.app.graph.Street;
import sim.app.graph.StreetXing;
import sim.util.Double2D;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class TrafficTest extends CitySimState {

	public double updateInterval = 2;
	private static final int CITY_SIZE = 5; // Number of intersections in the
	// city.
	public static final double XMIN = 0;
	public static final double XMAX = 800;
	public static final double YMIN = 0;
	public static final double YMAX = 600;

	public static final double DIAMETER = 8;

	// public Continuous2D environment = null;
	// public Network network = null;

	/**
	 * Creates a NetworkTest simulation with the given random number seed.
	 * 
	 */
	public TrafficTest(long seed) {
		super(seed);
		setCity(new DirectedSparseGraph<StreetXing, Street>()); 
	}

	/**
	 * 
	 * @author biggie
	 */
	StreetXing makeNode(String name, Double2D _location) {

		StreetXing xingNode = new StreetXing(name);
		// environment.setObjectLocation(xingNode, _location);
		// network.addNode(xingNode);
		return xingNode;
	}

	/**
	 * Start the Simulation.
	 */
	@Override
	public void start() {
		super.start(); // clear out the schedule

		schedule.reset();
		
		for (int i = 0; i < CITY_SIZE; i++) {
			getCity().addVertex(new StreetXing("node" + i));
		}

	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		doLoop(TrafficTest.class, args);
	}
}
