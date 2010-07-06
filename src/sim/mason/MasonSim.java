package sim.mason;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.app.agents.Agent;
import sim.app.agents.display.lights.TrafficLightAgent;
import sim.app.agents.display.vehicle.Car;
import sim.app.agents.display.vehicle.Vehicle;
import sim.app.road.Road;
import sim.app.road.Street;
import sim.app.road.StreetXing;
import sim.app.utils.Orientation;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.xml.XmlParseService;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.util.Pair;

@SuppressWarnings("serial")
public class MasonSim extends CitySimState
{
    private static Logger _log;
    private static String clazz = MasonSim.class.getSimpleName();
    private static String _cityXml;
    private static Random _rand = new Random(System.currentTimeMillis());
    private final List<StreetXing> _sourceXings;
    private final List<StreetXing> _destXings;
    private List<Agent> _agentsList;

    public static final double XMIN = 0;
    public static final double XMAX = 800;
    public static final double YMIN = 0;
    public static final double YMAX = 600;
    public static int MAX_CAR_COUNT;

    /**
     * Creates a MasonSim simulation with the given random number seed.
     */
    public MasonSim(long seed, String cityXmlFileName_, Logger log_)
    {
	super(seed);
	XmlParseService parsedGraph = new XmlParseService(cityXmlFileName_,
		log_);
	setCity(parsedGraph.getGraph());
	MAX_CAR_COUNT = parsedGraph.getMaxCars();
	_sourceXings = parsedGraph.getSourceXings();
	_destXings = parsedGraph.getDestXings();
	// Helper method TODO get all vals from parsed graph
	generateCity();

	_log = log_;
    }

    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     */
    public MasonSim(long seed)
    {
        super(seed);
        XmlParseService parsedGraph = new XmlParseService(_cityXml, _log);
        setCity(parsedGraph.getGraph());
        MAX_CAR_COUNT = parsedGraph.getMaxCars();
        _sourceXings = parsedGraph.getSourceXings();
        _destXings = parsedGraph.getDestXings();
	_agentsList = new LinkedList<Agent>();
	// _roads = new LinkedList<Road>(_city.getEdges());
	// Helper method TODO get all vals from parsed graph
	generateCity();
    }

    // TODO refactored, sim1
    private void generateCity()
    {

	LinkedList<Point2D> streetPoints1 = (LinkedList<Point2D>) getPointList(false);
	LinkedList<Point2D> streetPoints2 = (LinkedList<Point2D>) getPointList(true);
	Road streetNS = new Street("NS", streetPoints1, _log);
	Road streetEW = new Street("EW", streetPoints2, _log);
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
	TrafficLightAgent light = new TrafficLightAgent(100, 0.7, _log);
	streetNS.getSubRoad(1).setTf(light.getTf(Orientation.NORTH_SOUTH));
	_agentsList.add(light);
	endXingNS.setTrafficLight(light);

	TrafficLightAgent light2 = new TrafficLightAgent(100, 0.5, _log);
	streetEW.getSubRoad(1).setTf(light2.getTf(Orientation.NORTH_SOUTH));
	_agentsList.add(light2);
	endXingEW.setTrafficLight(light2);

	// Create trafic light for intersection
	TrafficLightAgent xingLight = new TrafficLightAgent(100, 0.5, _log);
	streetNS.getSubRoad(0).setTf(xingLight.getTf(Orientation.NORTH_SOUTH));
	streetEW.getSubRoad(0).setTf(xingLight.getTf(Orientation.EAST_WEST));
	_agentsList.add(xingLight);
	intersect.setTrafficLight(xingLight);

	// Set start/end odds for each Xing
	setSrcXingOdds(startXingNS, 60);
	setSrcXingOdds(startXingEW, 40);

	setDestXingOdds(endXingNS, 25);
	setDestXingOdds(endXingEW, 75);

	// Finally, add the edge
	getCity().addEdge(streetNS.getSubRoad(0),
		new Pair<StreetXing>(startXingNS,
		intersect));
	getCity().addEdge(streetNS.getSubRoad(1),
		new Pair<StreetXing>(intersect,
		endXingNS));

	getCity().addEdge(streetEW.getSubRoad(0),
		new Pair<StreetXing>(startXingEW,
		intersect));
	getCity().addEdge(streetEW.getSubRoad(1),
		new Pair<StreetXing>(intersect,
		endXingEW));

    }

    /**
     * TODO refactored, move. SIM1
     * 
     * @param xi
     *            ng_
     */
    private void setDestXingOdds(StreetXing xing_, int odds_)
    {
	_destXings.add(xing_);
	xing_.setEndOdds(odds_);
    }

    /**
     * TODO refactored, move. SIM1
     * 
     * @author biggie
     */
    private void setSrcXingOdds(StreetXing xing_, int odds_)
    {
	xing_.setStartOdds(odds_);
	_sourceXings.add(xing_);
    }

    /**
     * TODO refactored, move. SIM1
     * 
     * @author biggie
     */
    private void processRoad(Road rd_, StreetXing intersect)
    {
	Road rd1 = new Street(rd_.ID + intersect.getId(), rd_.getSubPointList(
		rd_.getP1(), intersect.getLocation()), _log);
	Road rd2 = new Street(intersect.getId() + rd_.ID, rd_.getSubPointList(
		intersect.getLocation(), rd_.getP2()), _log);
	List<Road> rdList = new LinkedList<Road>();
	rdList.add(rd1);
	rdList.add(rd2);
	rd_.setSubRoad(rdList);
	rd_.processRoadSegments();
    }

    /**
     * TODO helper method, remove
     * 
     * @return
     */
    private List<Point2D> getPointList(boolean isNS_)
    {
	List<Point2D> points = new LinkedList<Point2D>();
	if (isNS_)
	{
	    // Seg 1
	    points.add(new Point2D.Float(20, 300));
	    points.add(new Point2D.Float((float) (XMAX - 300), 300));
	    points.add(new Point2D.Float(700, (float) (YMAX - 20)));
	} else
	{
	    points.add(new Point2D.Float(300, 20));
	    points.add(new Point2D.Float(400, 80));
	    points.add(new Point2D.Float((float) (YMAX - 20), 500));
	}
	return points;
    }

    /**
     * Start the Simulation.
     */
    @Override
    public void start()
    {
	super.start();
	schedule.reset(); // clear out the schedule

	scheduleTrafficLights();

	Steppable carGenerator = new Steppable()
	    {
		DijkstraShortestPath<StreetXing, Road> routeMap = new DijkstraShortestPath<StreetXing, Road>(
			getCity());

		public void step(SimState state)
		{
		    double vhclThisStep = carFlow();
		    for (int i = 0; Vehicle.getTotVhlCount() < MAX_CAR_COUNT
			    && i < vhclThisStep; i++)
		    {
			StreetXing source = getSource();
			StreetXing target = getDest();
			// Make sure start and end are different. Otherwise...
			// what's the point?
			while (source.getId().equals(target.getId()))
			{
			    target = getDest();
			}
			List<Road> trayectory = routeMap
				.getPath(source, target);
			Vehicle vhcl = new Car(trayectory, getCity(), _log);
			vhcl.toDiePointer = schedule.scheduleRepeating(
				schedule.getTime(), Vehicle.getTotVhlCount(),
				vhcl);
		    }
		}

	    };
	// Schedule the car Generator
	schedule.scheduleRepeating(Schedule.EPOCH, 1, carGenerator, 1);
    }

    /**
     * Cicle through each street crossing and schedule traffic lights if found
     * O(# of street xings)
     */
    private void scheduleTrafficLights()
    {
	Iterator<StreetXing> iter = getCity().getVertices().iterator();
	while (iter.hasNext())
	{
	    StreetXing xing = iter.next();
	    if (xing.hasTrafficLight())
	    {
		schedule.scheduleRepeating(xing.getTrafficLight());
	    }
	}

    }

    /**
     * Return a random car begining based on the {@code startingOdds} attribute
     * in the city xml.
     * <p/>
     * See {@link TraffiSimulation.xsd} for more info
     * 
     * @return
     */
    protected StreetXing getDest()
    {
	StreetXing pickedXing = null;
	int targetIndex = _rand.nextInt(100);
	int currentIndex = 0;

	for (StreetXing xing : _destXings)
	{
	    currentIndex += xing.getEndOdds();
	    if (currentIndex >= targetIndex)
	    {
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
    private StreetXing getSource()
    {
	StreetXing pickedXing = null;
	int targetIndex = _rand.nextInt(100);
	int currentIndex = 0;

	for (StreetXing xing : _sourceXings)
	{
	    currentIndex += xing.getStartOdds();
	    if (currentIndex >= targetIndex)
	    {
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
    protected int carFlow() {
	int carPerStep = 1;
	// int carPerStep = (int) (floor(sin(schedule.getTime() / 1000.0)
	// * MAX_CAR_COUNT));
	// _log.log(Level.FINER, "Creating: " + carPerStep + " cars");
	return carPerStep;

    }

    /**
     * Main
     * 
     * @param args
     */
    public static void main(String[] args)
    {
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);

	if (args.length < 2 || "city".equals(args[0]))
	{
	    System.err.println("Usage: java -jar " + clazz
		    + ".jar -city [xml file]\n"
		    + "See TrafficSimulation.xsd for details");
	    System.exit(1);

	}
	for (int i = 0; i < args.length; i++)
	{
	    if ("-city".equals(args[i]))
	    {
		_cityXml = args[++i];
	    } else if ("-verbose".equals(args[i]) || "-v".equals(args[i]))
	    {
		_log.setLevel(Level.INFO);
	    } else if ("-debug".equals(args[i]))
	    {
		_log.setLevel(Level.FINE);
	    }
	}
	if (null == _cityXml || "".equals(_cityXml))
	{
	    System.err.println("Usage: java -jar " + clazz
		    + ".jar -city [xml file]\n"
		    + "See TrafficSimulation.xsd for details");
	    System.exit(1);
	}
	_cityXml = System.getProperty("user.dir") + _cityXml;
	// System.out.println(System.getProperty("user.dir"));
	doLoop(MasonSim.class, args);
    }
}
