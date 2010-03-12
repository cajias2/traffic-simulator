package sim.app.processing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.agents.Car;
import sim.app.agents.Vehicle;
import sim.app.geo.Road;
import sim.app.geo.StreetXing;
import sim.app.xml.XmlParseService;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;

@SuppressWarnings("serial")
public class TrafficSim extends PApplet {
	private static final int FRAME_RATE = 30;
	private static final double NEW_VHCL_RATIO = .1;
	private static Graph<StreetXing, Road> _city;
	private static Logger _log;
	private static String clazz = TrafficSim.class.getSimpleName();
	private static String _cityXml = "/../src/xml/OneStreet.xml";
	private List<StreetXing> _sourceXings;
	private List<StreetXing> _destXings;

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static int MAX_CAR_COUNT = 3;

	private static LinkedList<Vehicle> _vehicleAgents;
	private static LinkedList<Road> _roads;

	// *************
	/**
	 * 
	 */
	public void setup() {
		size(WIDTH, HEIGHT);
		frameRate(FRAME_RATE);
		_vehicleAgents = new LinkedList<Vehicle>();
		// Create first batch of cars
		parseGraph();
		for (int i = 0; i < 1; i++) {
			_vehicleAgents.add(generateVehicle());
		}

		_roads = new LinkedList<Road>(_city.getEdges());

	}

	/**
	 * 
	 */
	private void parseGraph() {
		XmlParseService parsedGraph = new XmlParseService(System
				.getProperty("user.dir")
				+ _cityXml, this, _log);
		_city = parsedGraph.getGraph();
		MAX_CAR_COUNT = parsedGraph.getMaxCars();
		_sourceXings = parsedGraph.getSourceXings();
		_destXings = parsedGraph.getDestXings();

	}

	/**
	 * TODO expand for all kinds of vehicles.. right now hardcoded to cars :(
	 * 
	 * @return
	 */
	private Vehicle generateVehicle() {
		Vehicle vcle;
		DijkstraShortestPath<StreetXing, Road> routeMap = new DijkstraShortestPath<StreetXing, Road>(
				_city);
		StreetXing source = getSource();
		StreetXing target = getTarget();
		// Make sure start and end are different. Otherwise...
		// what's the point?
		// Fixed nullpointer exception by checking target=?null.
		while ((target == null || target.getId() == null)
				|| source.getId().equals(target.getId())) {
			target = getTarget();
		}
		List<Road> trayectory = routeMap.getPath(source, target);
		vcle = new Car(trayectory, _city, _log, this);
		return vcle;
	}

	/**
	 * 
	 */
	public void draw() {
		background(100);
		// Move and display all "stripes"

		/*
		 * Display roads
		 */
		for (Road rd : _roads) {
			rd.display();
		}
		/*
		 * Display cars
		 */
		Iterator<Vehicle> iter = _vehicleAgents.iterator();
		while (iter.hasNext()) {
			Vehicle vhcl = iter.next();
			if (vhcl.isAlive()) {
				vhcl.move();
				vhcl.display();
			} else {
				iter.remove();
			}
		}
		/*
		 * Add new cars to the mix?
		 */
		if (addVhclP()) {
			_vehicleAgents.add(generateVehicle());
		}
	}

	/**
	 * 
	 * @return
	 */
	private StreetXing getTarget() {
		StreetXing pickedXing = null;
		Random rand = new Random(System.currentTimeMillis());
		int targetIndex = rand.nextInt(100);
		int currentIndex = 0;

		for (StreetXing xing : _sourceXings) {
			currentIndex += xing.getStartOdds();
			if (currentIndex >= targetIndex) {
				pickedXing = xing;
				break;
			}
		}
		return pickedXing;
	}

	/**
	 * Return a random car begining based on the {@code startingOdds} attribute
	 * in the city xml.
	 * <p/>
	 * See {@link TraffiSimulation.xsd} for more info
	 * 
	 * @return
	 */
	private StreetXing getSource() {
		StreetXing pickedXing = null;
		Random rand = new Random(System.currentTimeMillis());
		int targetIndex = rand.nextInt(100);
		int currentIndex = 0;

		for (StreetXing xing : _destXings) {
			currentIndex += xing.getEndOdds();
			if (currentIndex >= targetIndex) {
				pickedXing = xing;
				break;
			}
		}

		return pickedXing;
	}

	/**
	 * Return number of cars to generate per step... Should follow a sinoidal
	 * function to mimic traffic waves.
	 * 
	 * @return cars to generate per step
	 */
	private boolean addVhclP() {
		if (Math.random() < NEW_VHCL_RATIO)
			return true;
		return false;
		// double carPerStep = Math.floor(Math
		// .sin((System.currentTimeMillis() + 473) / 100)
		// * (MAX_CAR_COUNT / 2) + MAX_CAR_COUNT / 2);
		// _log.log( Level.FINE, "Will try to create: "+carPerStep+"cars" );
		// return carPerStep;

	}
}
