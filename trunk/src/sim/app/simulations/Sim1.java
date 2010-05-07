/**
 * 
 */
package sim.app.simulations;

import static java.lang.Math.floor;
import static java.lang.Math.sin;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.agents.Agent;
import sim.app.agents.display.lights.TrafficLightAgent;
import sim.app.agents.display.vehicle.Car;
import sim.app.agents.display.vehicle.Vehicle;
import sim.app.geo.Road;
import sim.app.geo.Street;
import sim.app.geo.StreetXing;
import sim.app.utils.Orientation;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author biggie
 * 
 */
public class Sim1 extends TrafficSim {
	public static final double NEW_VHCL_RATIO = .1;
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	private final int FRAME_RATE = 30;
	private static String _cityDir = "/../src/xml/OneStreet.xml";

	private final PApplet _applet;
	private LinkedList<Road> _roads;
	private final Logger _log;
	private List<Agent> _agentsList;

	public PApplet getApplet() {
		return _applet;
	}

	public Sim1(PApplet applet_, Logger log_) {
		_applet = applet_;
		_log = log_;
		_agentsList = new LinkedList<Agent>();
		generateCity();
		// parseGraph(_cityDir);
		for (int i = 0; i < 1; i++) {
			Agent agent = generateVehicle();
			getAgents().add(agent);
			// addVehicle().add((Vehicle)agent);
		}

		_roads = new LinkedList<Road>(_city.getEdges());
	}

	@Override
	public void update() {
		if (addVhclP()) {
			Agent agent = generateVehicle();
			getAgents().add(agent);
			// addVehicle().add((Vehicle)agent);
		}
		// int carsGenerated = carFlow();
		// System.out.println("Cars this step: " +carsGenerated);
		// for (int i = 0; i < carsGenerated; i++)
		// {
		// getVehicles().add(generateVehicle());
		// }
	}

	@Override
	public int getFrameRate() {
		return FRAME_RATE;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see sim.app.simulations.TrafficSim#getWidth()
	 */
	@Override
	public int getWidth() {
		return WIDTH;
	}

	/**
	 * 
	 * @return
	 */
	protected int carFlow() {
		int carPerStep = (int) (floor(sin(getApplet().frameCount)) * (MAX_CAR_COUNT / 2 + MAX_CAR_COUNT / 2));
		_log.log(Level.FINE, "Will try to create: " + carPerStep + "cars");
		return carPerStep;

	}

	@Override
	protected void generateCity() {

		LinkedList<Point2D> pointList = (LinkedList<Point2D>) getPointList();
		Road street = new Street("test", pointList, _applet, _log);

		// Create Xing obj
		StreetXing startXing = new StreetXing(street.startLoc(), street);
		StreetXing endXing = new StreetXing(street.endLoc(), street);

		// Create traffic light and add it to Xing
		TrafficLightAgent light = new TrafficLightAgent(getApplet(), _log);
		street.setTf(light.getTf(Orientation.NORTH_SOUTH));
		_agentsList.add(light);
		endXing.setTrafficLight(light);

		// Set start/end odds for each Xing
		startXing.setStartOdds(100);
		_sourceXings.add(startXing);
		endXing.setEndOdds(100);
		_destXings.add(endXing);

		// Finally, add the edge
		Pair<StreetXing> vertex = new Pair<StreetXing>(startXing, endXing);
		_city.addEdge (street, vertex);
	}

	@Override
	protected boolean addVhclP() {

		if (Math.random() < NEW_VHCL_RATIO)
			return true;
		return false;
		// double carPerStep = Math.floor(Math
		// .sin((System.currentTimeMillis() + 473) / 100)
		// * (MAX_CAR_COUNT / 2) + MAX_CAR_COUNT / 2);
		// _log.log( Level.FINE, "Will try to create: "+carPerStep+"cars" );
		// return carPerStep;

	}

	@Override
	protected List<Road> getRoads() {
		return _roads;
	}

	@Override
	protected List<Agent> getAgents() {
		return _agentsList;
	}

	@Override
	protected Logger getLogger() {
		return _log;
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
		StreetXing target = getDest();
		// Make sure start and end are different.
		// Fixed nullpointer exception by checking target=?null.
		while ((target == null || target.getId() == null)
				|| source.getId().equals(target.getId())) {
			target = getDest();
		}
		List<Road> trayectory = routeMap.getPath(source, target);
		vcle = new Car(trayectory, _city, _log, getApplet());
		return vcle;
	}

	/**
	 * TODO helper method, remove
	 * 
	 * @return
	 */
	private List<Point2D> getPointList() {
		List<Point2D> points = new LinkedList<Point2D>();
		PApplet ap = getApplet();
		// Seg 1
		points.add(new Point2D.Float(20, ap.random(getHeight() - 20)));
		points.add(new Point2D.Float(getWidth() - 600, ap.random(20,
				getHeight() - 600)));
		points.add(new Point2D.Float(20, ap.random(getHeight() - 20)));
		return points;
	}
}
