/**
 * 
 */
package sim.app.simulations;

import static java.lang.Math.floor;
import static java.lang.Math.sin;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.TrafficSim;
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
public class Sim2 extends TrafficSim {
    public static final double NEW_VHCL_RATIO = .4;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int FRAME_RATE = 30;
    private final int SIM_DURATION = 1800;
    private static String _cityDir = "/../src/xml/OneStreet.xml";

    private final PApplet _applet;
    private LinkedList<Road> _roads;
    private final Logger _log;
    private List<Agent> _agentsList;

    public PApplet getApplet() {
	return _applet;
    }

    public Sim2(PApplet applet_, Logger log_) {
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
	super.update();

	if (addVhclP()) {
	    Agent agent = generateVehicle();
	    getAgents().add(agent);
	}
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
     * @see sim.app.TrafficSim#getWidth()
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

	LinkedList<Point2D> streetPoints1 = (LinkedList<Point2D>) getPointList(false);
	LinkedList<Point2D> streetPoints2 = (LinkedList<Point2D>) getPointList(true);
	Road streetNS = new Street("NS", streetPoints1, _applet, _log);
	Road streetEW = new Street("EW", streetPoints2, _applet, _log);
	// Create Xing obj
	StreetXing startXingNS = new StreetXing(streetNS.startLoc(), streetNS);
	StreetXing endXingNS = new StreetXing(streetNS.endLoc(), streetNS);
	StreetXing startXingEW = new StreetXing(streetEW.startLoc(), streetEW);
	StreetXing endXingEW = new StreetXing(streetEW.endLoc(), streetEW);
	StreetXing intersect = new StreetXing(streetNS, streetEW);

	processRoad(streetNS, intersect);
	processRoad(streetEW, intersect);

	/*
	 * Create traffic light and add it to Xing
	 */
//	TrafficLightAgent light = new TrafficLightAgent(getApplet(), 100, 0.5, _log);
//	streetNS.getSubRoad(1).setTf(light.getTf(Orientation.NORTH_SOUTH));
//	_agentsList.add(light);
//	endXingNS.setTrafficLight(light);
//
//	TrafficLightAgent light2 = new TrafficLightAgent(getApplet(), 100, 0.5, _log);
//	streetEW.getSubRoad(1).setTf(light2.getTf(Orientation.NORTH_SOUTH));
//	_agentsList.add(light2);
//	endXingEW.setTrafficLight(light2);

	
	// Create trafic light for intersection
	TrafficLightAgent xingLight = new TrafficLightAgent(getApplet(), 100, 0.5, _log);
	streetNS.getSubRoad(0).setTf(xingLight.getTf(Orientation.NORTH_SOUTH));
	streetEW.getSubRoad(0).setTf(xingLight.getTf(Orientation.EAST_WEST));
	_agentsList.add(xingLight);
	intersect.setTrafficLight(xingLight);

	// Set start/end odds for each Xing
	setSrcXingOdds(startXingNS, 50);
	setSrcXingOdds(startXingEW, 50);
	
	setDestXingOdds(endXingNS, 50);	
	setDestXingOdds(endXingEW, 50);

	// Finally, add the edge
	 _city.addEdge(streetNS.getSubRoad(0), new Pair<StreetXing>(startXingNS,
	 intersect));
	_city.addEdge(streetNS.getSubRoad(1), new Pair<StreetXing>(intersect, endXingNS));
	
	_city.addEdge(streetEW.getSubRoad(0), new Pair<StreetXing>(startXingEW, intersect));
	_city.addEdge(streetEW.getSubRoad(1), new Pair<StreetXing>(intersect, endXingEW));

    }

    /**
     * @param xing_
     */
    private void setDestXingOdds(StreetXing xing_, int odds_) {
	_destXings.add(xing_);
	xing_.setEndOdds(odds_);
    }

    private void setSrcXingOdds(StreetXing xing_, int odds_) {
	xing_.setStartOdds(odds_);
	_sourceXings.add(xing_);
    }

    private void processRoad(Road rd_, StreetXing intersect) {
	Road rd1 = new Street(rd_.ID+intersect.getId(), rd_.getSubPointList(rd_.getP1(), intersect.getLocation()),
		_applet, _log);
	Road rd2 = new Street(intersect.getId()+rd_.ID, rd_.getSubPointList(intersect.getLocation(), rd_.getP2()),
		_applet, _log);
	List<Road> rdList = new LinkedList<Road>();
	rdList.add(rd1);
	rdList.add(rd2);
	rd_.setSubRoad(rdList);
	rd_.processRoadSegments();
    }

    @Override
    protected boolean addVhclP() {

	if (Math.random() < NEW_VHCL_RATIO)
	    return true;
	return false;
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
	DijkstraShortestPath<StreetXing, Road> routeMap = new DijkstraShortestPath<StreetXing, Road>(_city);
	List<Road> trayectory = null;
	while (null == trayectory || trayectory.isEmpty()) {
	    StreetXing source = getSource();
	    StreetXing target = getDest();
	    // Make sure start and end are different.
	    // Fixed nullpointer exception by checking target=?null.
	    while ((target == null || target.getId() == null) || source.getId().equals(target.getId())) {
		target = getDest();
	    }
	    trayectory = routeMap.getPath(source, target);
	}
	vcle = new Car(trayectory, _city, _log, getApplet());
	return vcle;
    }

    /**
     * TODO helper method, remove
     * 
     * @return
     */
    private List<Point2D> getPointList(boolean isNS_) {
	List<Point2D> points = new LinkedList<Point2D>();
	PApplet ap = getApplet();
	if (isNS_) {
	    // Seg 1
	    points.add(new Point2D.Float(20, 300));
	    points.add(new Point2D.Float(getWidth() - 300, 300));
	    points.add(new Point2D.Float(700, ap.getHeight() - 20));
	} else {
	    points.add(new Point2D.Float(300, 20));
	    points.add(new Point2D.Float(400, 80));
	    points.add(new Point2D.Float(ap.getHeight() - 20, 500));
	}
	return points;
    }

    @Override
    public int getSimDuration() {
	return SIM_DURATION;
    }

    @Override
    protected String getOutputFolderName() {
	// TODO Auto-generated method stub
	return null;
    }
}
