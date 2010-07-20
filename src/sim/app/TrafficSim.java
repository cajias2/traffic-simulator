package sim.app;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sim.agents.lights.TrafficLightAgent;
import sim.agents.vehicle.Car;
import sim.agents.vehicle.Vehicle;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.geo.Road;
import sim.geo.RoadWeightTransformer;
import sim.geo.StreetXing;
import sim.utils.xml.XmlInputParseService;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

@SuppressWarnings("serial")
public class TrafficSim extends CitySimState {
    private static Logger _log;
    private static String clazz = TrafficSim.class.getSimpleName();
    private static String _cityXml;
    private static Random _rand = new Random(System.currentTimeMillis());
    private final List<StreetXing> _sourceXings;
    private final List<StreetXing> _destXings;
    private final List<TrafficLightAgent> _tlAgents;
    private final Map<String, BufferedWriter> _fileWriterMap = new HashMap<String, BufferedWriter>();
    private final String OUT_FOLDER;

    private final Map<String, Document> _outputDocMap;
    public static final double XMIN = 0;
    public static final double XMAX = 800;
    public static final double YMIN = 0;
    public static final double YMAX = 600;
    public static int MAX_CAR_COUNT;
    public static int SIM_TIME;


    /**
     * Creates a TrafficSim simulation with the given random number seed.
     */
    public TrafficSim(long seed, String cityXmlFileName_, Logger log_) {
	super(seed);
	cityXmlFileName_ = System.getProperty("user.dir") + cityXmlFileName_;
	XmlInputParseService parsedGraph = new XmlInputParseService(cityXmlFileName_, log_);
	setCity(parsedGraph.getGraph());
	MAX_CAR_COUNT = parsedGraph.getMaxCars();
	_sourceXings = parsedGraph.getSourceXings();
	_destXings = parsedGraph.getDestXings();
	_tlAgents = parsedGraph.getTlAgents();
	SIM_TIME = parsedGraph.getSimDuration();
	OUT_FOLDER = "output";
	_outputDocMap = new HashMap<String, Document>();
	_log = log_;
    }

    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     */
    public TrafficSim(long seed) {
	super(seed);
	XmlInputParseService parsedGraph = new XmlInputParseService(_cityXml, _log);
	setCity(parsedGraph.getGraph());
	MAX_CAR_COUNT = parsedGraph.getMaxCars();
	SIM_TIME = parsedGraph.getSimDuration();
	_sourceXings = parsedGraph.getSourceXings();
	_destXings = parsedGraph.getDestXings();
	_tlAgents = parsedGraph.getTlAgents();
	_outputDocMap = new HashMap<String, Document>();
	OUT_FOLDER = "output";

    }

    /**
     * Start the Simulation.
     */
    @Override
    public void start() {
	super.start();
	schedule.reset(); // clear out the schedule


	scheduleTrafficLights();

	Steppable carGenerator = new Steppable() {
	    org.apache.commons.collections15.Transformer<Road, Number> rdTrans = new RoadWeightTransformer();
	    DijkstraShortestPath<StreetXing, Road> routeMap = new DijkstraShortestPath<StreetXing, Road>(getCity(),
		    rdTrans, false);
	    int carOrder = 0;
	    public void step(SimState state) {
		if (SIM_TIME <= schedule.getSteps()) {
		    for (Vehicle v : Vehicle.getActiveVhcl()) {
			v.finalizeLog(schedule.getSteps());
		    }
		    printOutput();
		    state.finish();
		    System.exit(0);
		}
		double vhclThisStep = carFlow();
		for (int i = 0; Vehicle.count() < MAX_CAR_COUNT && i < vhclThisStep; i++) {
		    StreetXing source = getSource();
		    StreetXing target = getDest();
		    // Make sure start and end are different and that there is a
		    // path
		    List<Road> route = null;
		    while ((source.getId().equals(target.getId()) && route == null)
			    || !source.getId().equals(target.getId()) && route == null) {
			target = getDest();
			route = routeMap.getPath(source, target);
			if (null == route || route.isEmpty()) {
			    route = null;
			}
		    }
		    String routeName = route.get(0).ID + "_" + route.get(route.size() - 1).ID;
		    if (!_outputDocMap.containsKey(routeName)) {
			_outputDocMap.put(routeName, createDom(routeName));
		    }
		    Vehicle vhcl = new Car(route, getCity(), _outputDocMap.get(routeName), _log);
		    vhcl.toDiePointer = schedule.scheduleRepeating(schedule.getTime(), carOrder++, vhcl);
		}
	    }
	};
	// Schedule the car Generator
	schedule.scheduleRepeating(Schedule.EPOCH, 1, carGenerator, 1);

    }

    /**
     * @param _route
     * @return
     */
    private Document createDom(String _route) {
	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder documentBuilder = null;
	try {
	    documentBuilder = documentBuilderFactory.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
	Document document = documentBuilder.newDocument();
	Element rootElement = document.createElement("output");
	rootElement.setAttribute("route", _route);
	document.appendChild(rootElement);
	return document;
    }

    /**
     * Cicle through each street crossing and schedule traffic lights if found
     * O(# of street xings)
     */
    private void scheduleTrafficLights() {
	for (TrafficLightAgent tl : _tlAgents) {
	    schedule.scheduleRepeating(tl);
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
    protected StreetXing getDest() {
	StreetXing pickedXing = null;
	int targetIndex = _rand.nextInt(100);
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
     * Return a random car begining based on the {@code startingOdds} attribute
     * in the city xml.
     * <p/>
     * See {@link TraffiSimulation.xsd} for more info
     * 
     * @return
     */
    private StreetXing getSource() {
	StreetXing pickedXing = null;
	int targetIndex = _rand.nextInt(100);
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
     * TODO
     */
    private void printOutput() {
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = null;
	try {
	    transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	} catch (TransformerConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	for (Document document : _outputDocMap.values()) {

	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(new StringWriter());

	    try {
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);
		System.out.println("++++++++++");
	    } catch (TransformerException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
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
    public static void main(String[] args) {
	_log = Logger.getLogger("SimLogger");
	_log.setLevel(Level.SEVERE);

	if (args.length < 2 || "city".equals(args[0])) {
	    System.err.println("Usage: java -jar " + clazz + ".jar -city [xml file]\n"
		    + "See TrafficSimulation.xsd for details");
	    System.exit(1);

	}
	for (int i = 0; i < args.length; i++) {
	    if ("-city".equals(args[i])) {
		_cityXml = args[++i];
	    } else if ("-verbose".equals(args[i]) || "-v".equals(args[i])) {
		_log.setLevel(Level.INFO);
	    } else if ("-debug".equals(args[i])) {
		_log.setLevel(Level.FINE);
	    }
	}
	if (null == _cityXml || "".equals(_cityXml)) {
	    System.err.println("Usage: java -jar " + clazz + ".jar -city [xml file]\n"
		    + "See TrafficSimulation.xsd for details");
	    System.exit(1);
	}
	_cityXml = System.getProperty("user.dir") + _cityXml;
	doLoop(TrafficSim.class, args);
    }
}
