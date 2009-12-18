package sim.app;

import java.util.ArrayList;
import java.util.List;

import sim.app.graph.CitySimState;
import sim.app.graph.Street;
import sim.app.graph.StreetXing;
import sim.util.Double2D;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

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
		List<StreetXing> xings = new ArrayList(CITY_SIZE);
		for (int i = 0; i < CITY_SIZE; i++) {
			xings.add(new StreetXing("node" + i));
			getCity().addVertex(xings.get(i));
		}
		
		Pair edge1 = new Pair(xings.get(0), xings.get(1) );
		Pair edge2 = new Pair(xings.get(0), xings.get(2) );
		Pair edge3 = new Pair(xings.get(0), xings.get(3) );
		Pair edge4 = new Pair(xings.get(0), xings.get(4) );
		
		Pair edge5 = new Pair(xings.get(1), xings.get(0) );
		Pair edge6 = new Pair(xings.get(2), xings.get(1) );
		Pair edge7 = new Pair(xings.get(3), xings.get(2) );
		Pair edge8 = new Pair(xings.get(4), xings.get(3) );
		
		getCity().addEdge(new Street("0", 10), edge1);
		getCity().addEdge(new Street("0", 10), edge2);
		getCity().addEdge(new Street("0", 10), edge3);
		getCity().addEdge(new Street("0", 10), edge4);
		
		getCity().addEdge(new Street("0", 10), edge5);
		getCity().addEdge(new Street("0", 10), edge6);
		getCity().addEdge(new Street("0", 10), edge7);
		getCity().addEdge(new Street("0", 10), edge8);
		

	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		doLoop(TrafficTest.class, args);
	}
}
