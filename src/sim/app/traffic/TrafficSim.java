package sim.app.traffic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import sim.agents.traffic.TLAgent;
import sim.agents.traffic.vhcl.Car;
import sim.agents.traffic.vhcl.Vehicle;
import sim.app.CitySimState;
import sim.app.geo.distance.traffic.transform.RoadWeightTransformer;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.traffic.Road;
import sim.graph.traffic.StreetXing;
import sim.utils.xml.data.OutputSection;
import sim.utils.xml.data.SectionStartComparator;
import sim.utils.xml.traffic.XmlInputTrafficParseService;
import sim.utils.xml.traffic.XmlOutputParseService;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

@SuppressWarnings("serial")
public class TrafficSim extends CitySimState {
    private static Logger _log;
    private static String clazz = TrafficSim.class.getSimpleName();
    private static String _cityXml;
    private static Random _rand = new Random(System.currentTimeMillis());
    private final List<StreetXing> _sourceXings;
    private final List<StreetXing> _destXings;
    private final List<TLAgent> _tlAgents;

    private final Map<String, Document> _outputDocMap;
    public static int MAX_CAR_COUNT;
    public static int SIM_TIME;

    /**
     * Creates a TrafficSim simulation with the given random number seed.
     */
    public TrafficSim(long seed, String cityXmlFileName_, Logger log_) {
	super(seed);
	cityXmlFileName_ = System.getProperty("user.dir") + cityXmlFileName_;
	XmlInputTrafficParseService parsedGraph = new XmlInputTrafficParseService(cityXmlFileName_, log_);
	setCity(parsedGraph.getGraph());
	MAX_CAR_COUNT = parsedGraph.getMaxCars();
	_sourceXings = parsedGraph.getSourceXings();
	_destXings = parsedGraph.getDestXings();
	_tlAgents = parsedGraph.getTlAgents();
	SIM_TIME = parsedGraph.getSimDuration();
	_outputDocMap = new HashMap<String, Document>();
	_log = log_;
    }

    /**
     * Creates a NetworkTest simulation with the given random number seed.
     * _cityXml must be set first!
     */
    public TrafficSim(long seed) {
	super(seed);
	XmlInputTrafficParseService parsedGraph = new XmlInputTrafficParseService(_cityXml, _log);
	setCity(parsedGraph.getGraph());
	MAX_CAR_COUNT = parsedGraph.getMaxCars();
	SIM_TIME = parsedGraph.getSimDuration();
	_sourceXings = parsedGraph.getSourceXings();
	_destXings = parsedGraph.getDestXings();
	_tlAgents = parsedGraph.getTlAgents();
	_outputDocMap = new HashMap<String, Document>();

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
	    // DijkstraShortestPath<StreetXing, Road> routeMap = new
	    // DijkstraShortestPath<StreetXing, Road>(getCity(),
	    // rdTrans, false);
	    DijkstraShortestPath<StreetXing, Road> routeMap = new DijkstraShortestPath<StreetXing, Road>(getCity());

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
     * Cycle through each street crossing and schedule traffic lights if found
     * O(# of street xings)
     */
    private void scheduleTrafficLights() {
	for (TLAgent tl : _tlAgents) {
	    schedule.scheduleRepeating(tl);
	}

    }

    /**
     * Return a random car begining based on the {@code startingOdds} attribute
     * in the city sim.xml.
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
     * in the city sim.xml.
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
	File outDir = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "output");
	if (!outDir.exists())
	    outDir.mkdir();
	File outXmlDir = new File(outDir.getAbsoluteFile() + System.getProperty("file.separator") + "sim.xml");
	if (!outXmlDir.exists())
	    outXmlDir.mkdir();
	File outTxtDir = new File(outDir.getAbsoluteFile() + System.getProperty("file.separator") + "txt");
	if (!outTxtDir.exists())
	    outTxtDir.mkdir();

	Map<String, List<OutputSection>> tsMap = new HashMap<String, List<OutputSection>>();
	for (Entry<String, Document> entrySet : _outputDocMap.entrySet()) {
	    String docName = entrySet.getKey();
	    Document document = entrySet.getValue();
	    getCity().getEdges();
	    XmlOutputParseService outParser = new XmlOutputParseService(document, getCity());
	    Map<String, List<OutputSection>> docMap = outParser.getSectionStartSeries();
	    // Copy the sections of each out to the map
	    for (Entry<String, List<OutputSection>> ts : docMap.entrySet()) {
		if (tsMap.containsKey(ts.getKey())) {
		    tsMap.get(ts.getKey()).addAll(ts.getValue());
		} else {
		    tsMap.put(ts.getKey(), ts.getValue());
		}
	    }
	    printXml(docName + "_out", document, outXmlDir.getAbsolutePath());
	}
	printTxt(tsMap, outTxtDir.getAbsolutePath());
    }

    /**
     * @param tsMap
     */
    private void printTxt(Map<String, List<OutputSection>> tsMap, String path_) {

	for (Entry<String, List<OutputSection>> ts : tsMap.entrySet()) {
	    Collections.sort(ts.getValue(), new SectionStartComparator());
	    double lastStart = -1;

	    // Remove duplicates
	    try {
		// Create file
		FileWriter speedWrt = new FileWriter(path_ + "/speed_" + ts.getKey() + ".txt");
		FileWriter timeWrt = new FileWriter(path_ + "/time_" + ts.getKey() + ".txt");

		BufferedWriter speedOut = new BufferedWriter(speedWrt);
		BufferedWriter timeOut = new BufferedWriter(timeWrt);
		for (OutputSection os : ts.getValue()) {
		    speedOut.write(os.getStart() + "\t" + os.getSpeed() + "\n");
		    timeOut.write(os.getStart() + "\t" + os.getEnd() + "\t" + (os.getEnd() - os.getStart()) + "\n");
		}
		speedOut.close();
		timeOut.close();
	    } catch (Exception e) {// Catch exception if any
		System.err.println("Error: " + e.getMessage());
	    }
	}
    }

    /**
     * @author biggie
     * @name printXml Purpose TODO
     * 
     * @param
     * @return void
     */
    private void printXml(String docName, Document document, String path_) {
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = null;
	try {
	    transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	} catch (TransformerConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	DOMSource source = new DOMSource(document);
	try {
	    // Create file
	    FileWriter fstream = new FileWriter(path_ + System.getProperty("file.separator") + docName + ".sim.xml");
	    BufferedWriter out = new BufferedWriter(fstream);
	    StreamResult result = new StreamResult(out);
	    transformer.transform(source, result);
	    out.close();
	} catch (TransformerException e) {
	    e.printStackTrace();
	} catch (Exception e) {// Catch exception if any
	    System.err.println("Error: " + e.getMessage());
	}
    }

    /**
     * Return number of cars to generate per step... Should follow a sinoidal
     * function to mimic traffic waves.
     * 
     * @return cars to generate per step
     */
    protected int carFlow() {
	int carPerStep = 10;
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
	    System.err.println("Usage: java -jar " + clazz + ".jar -city [sim.xml file]\n"
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
	    System.err.println("Usage: java -jar " + clazz + ".jar -city [sim.xml file]\n"
		    + "See TrafficSimulation.xsd for details");
	    System.exit(1);
	}
	_cityXml = System.getProperty("user.dir") + _cityXml;
	doLoop(TrafficSim.class, args);
    }
}
