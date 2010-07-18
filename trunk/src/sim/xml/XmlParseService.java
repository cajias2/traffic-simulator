/**
 * 
 */
package sim.xml;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sim.app.agents.display.lights.TrafficLightAgent;
import sim.app.road.Road;
import sim.app.road.Street;
import sim.app.road.StreetXing;
import sim.app.utils.Orientation;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author biggie
 */
public class XmlParseService {
    /*
     * Begin constant declaration. These constants should match refs in
     * TrafficSimulation.xsd
     */
    final static String NODE_SIM = "simulation";
    final static String NODE_TRAFFICLIGHT = "trafficLights";
    final static String NODE_TL = "tl";
    final static String NODE_TRAYECTORY = "trayectory";
    final static String NODE_STREET = "street";
    final static String NODE_STREETNODE = "streetNode";

    final static String NODE_TRAY_ORIG = "origin";
    final static String NODE_TRAY_DEST = "destination";
    final static String ATTR_SIM_MAXCARS = "maxCars";
    final static String ATTR_NAME = "name";
    final static String ATTR_STR_OR = "orientation";
    final static String ATTR_TRAY_PERCENT = "percent";
    final static String ATTR_XPOS = "x";
    final static String ATTR_YPOS = "y";

    final static String ATTR_TL_FROM = "from";
    final static String ATTR_TL_TO = "to";
    final static String ATT_TL_DUR = "duration";
    final static String ATT_TL_SPLIT = "split";
    final static String ATTR_CONN_OR = "orientation";
    final static String ATTR_CONN_DIR = "dir";
    final static String ONE_WAY = "1way";
    final static String TWO_WAY = "2way";

    private static Logger _logger;
    private static Map<String, Road> _roadMap = new HashMap<String, Road>();
    private final String _fileName;
    private final Graph<StreetXing, Road> _g = new DirectedSparseGraph<StreetXing, Road>();
    private final List<StreetXing> _sourceXings = new LinkedList<StreetXing>();
    private final List<StreetXing> _destXings = new LinkedList<StreetXing>();
    private final List<TrafficLightAgent> _tlAgents = new LinkedList<TrafficLightAgent>();
    private int _maxCarsInSim;

    /**
     * Class Constructor
     * 
     * @param fileName_
     */
    public XmlParseService(String fileName_, Logger logger_) {
	_fileName = fileName_;
	_logger = logger_;
	// use a unique display
	createGraph();
    }

    /**
     * Return the graph generated from the xml file passed in constructor.
     * 
     * @return
     */
    public Graph<StreetXing, Road> getGraph() {
	return _g;
    }

    /**
     * Return the max number of cars defined by the user.
     * 
     * @return
     */
    public int getMaxCars() {
	return _maxCarsInSim;
    }

    public List<TrafficLightAgent> getTlAgents() {
	return _tlAgents;
    }

    public List<StreetXing> getSourceXings() {
	return _sourceXings;
    }

    public List<StreetXing> getDestXings() {
	return _destXings;
    }

    /**
     * Creates a graph based on the xml file.
     */
    private void createGraph() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;

	try {
	    db = dbf.newDocumentBuilder();
	    Document doc = db.parse(_fileName);
	    doc.getDocumentElement().normalize();
	    parseSim(doc);
	    parseStreets(doc);
	    parseTrayectories(doc);
	    parseTrafficLights(doc);

	} catch (SAXException e) {
	    System.err.println("File: " + _fileName + " Does not conform to xsd."
		    + "See TrafficSimulation.xsd for details");
	    e.printStackTrace();
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("File: " + _fileName + " Could not be opened.");
	    System.exit(1);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Parse attributes of the {@code simulation} node
     * 
     * @param doc_
     */
    private void parseSim(Document doc_) {
	NodeList simNodes = doc_.getElementsByTagName(NODE_SIM);
	// Only one node per xml is expected. TODO allow for more than one
	// simulation node
	for (int i = 0; i < simNodes.getLength(); i++) {
	    Node simNode = simNodes.item(i);
	    // String simName =
	    // simNode.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
	    _maxCarsInSim = Integer.parseInt(simNode.getAttributes().getNamedItem(ATTR_SIM_MAXCARS).getNodeValue());
	}
    }

    /**
     * Parse attributes of the {@code <street/>} node
     * 
     * @param doc_
     * @param xingMap_
     */
    private void parseStreets(Document doc_) {
	NodeList streetNodes = doc_.getElementsByTagName(NODE_STREET);
	List<Road> strList = new LinkedList<Road>();
	for (int i = 0; i < streetNodes.getLength(); i++) {
	    Node streetNode = streetNodes.item(i);
	    Street street = parseStreet(streetNode);
	    strList.add(street);
	}

	// Find all intersections between streets
	Map<Road, List<StreetXing>> xingMap = findIntersection(strList);
	sortXingList(xingMap); // Sort xings from closer to farther from road
			       // origin.

	for (Entry<Road, List<StreetXing>> e : xingMap.entrySet()) {
	    Road rd = e.getKey();
	    List<StreetXing> xingList = e.getValue();
	    _roadMap.put(rd.ID, rd);
	    processRoad(rd, xingList);
	}
	// Add all the subroads once we are done processing. (Subroads are the
	// actual roads to the xing

	for (StreetXing xing : getGraph().getVertices()) {
	    xing.setSubRoads(getGraph().getInEdges(xing));
	}
    }

    /**
     * Uses the xing list to cut the road into smaller chunks and set the
     * roadSubList.<br>
     * Also sets the internal data structures of the road by invoking
     * {@code Road.processRoadSegments()}
     * 
     * @param rd_
     * @param xingList_
     */
    private void processRoad(Road rd_, List<StreetXing> xingList_) {
	List<Road> rdList = new LinkedList<Road>();
	StreetXing currXing = xingList_.remove(0);
	while (!xingList_.isEmpty()) {
	    StreetXing nextXing = xingList_.remove(0);	    
	    if (currXing.getLocation() != nextXing.getLocation()) {

		Pair<StreetXing> xingPair = new Pair<StreetXing>(currXing, nextXing);
		String thereId = rd_.ID + "(" + currXing.getId() + "->" + nextXing.getId() + ")";
		List<Point2D> subList = rd_.getSubPointList(currXing.getLocation(), nextXing.getLocation());
		Road there = new Street(thereId, subList, _logger);
		there.processRoadSegments();
		there.setOr(rd_.getOr());
		rdList.add(there);
		getGraph().addEdge(there, xingPair);
		// if(true)
		// {
		// String backId = rd_.ID+"("+nextXing.getId()+"->"+
		// currXing.getId() +")";
		// List<Point2D> reversedList = new LinkedList<Point2D>();
		// Iterator
		// for()
		// Road andBack = new Street(backId,
		// rd_.getSubPointList(nextXing.getLocation(),
		// currXing.getLocation())., _logger);
		// rdList.add(andBack);
		// }
	    }
	    currXing = nextXing;
	}
	rd_.setSubRoad(rdList);
	rd_.processRoadSegments();
    }

    /**
     * bubble sort.TODO change to priority queue
     * 
     * @param xingMap
     */
    private void sortXingList(Map<Road, List<StreetXing>> xingMap) {
	for (Entry<Road, List<StreetXing>> e : xingMap.entrySet()) {
	    Road str = e.getKey();
	    List<StreetXing> xingList = e.getValue();
	    int i = 0;
	    while (i < xingList.size()) {
		boolean skip = false;
		StreetXing xingA = xingList.get(i);
		for (int j = i + 1; j < xingList.size(); j++) {
		    StreetXing xingB = xingList.get(j);
		    if (str.getP1().distance(xingA.getLocation()) > str.getP1().distance(xingB.getLocation())) {
			xingList.set(j, xingA);
			xingList.set(i, xingB);
			i = 0;
			skip = true;
			break;
		    }
		}
		if (!skip)
		    i++;
	    }
	}
    }

    /**
     * @param strList
     * @return
     */
    private Map<Road, List<StreetXing>> findIntersection(List<Road> strList) {
	Map<Road, List<StreetXing>> xingMap = new HashMap<Road, List<StreetXing>>();
	for (Road rdA : strList) {
	    // The node may already have a map, if we have seen it before
	    if (null == xingMap.get(rdA)) {
		xingMap.put(rdA, new LinkedList<StreetXing>());
	    }
	    // Add the origin intersection
	    xingMap.get(rdA).add(new StreetXing(rdA.getP1(), rdA));
	    for (Road rdB : strList) {
		if (null == xingMap.get(rdB)) {
		    xingMap.put(rdB, new LinkedList<StreetXing>());
		}
		if (!xingExists(xingMap.get(rdB), rdA)) {
		    Point2D intersect = rdA.findIntersection(rdB);
		    if (null != intersect) {
			StreetXing xing = new StreetXing(rdA, rdB);
			xingMap.get(rdA).add(xing);
			xingMap.get(rdB).add(xing);
		    }
		}
	    }
	    // Add the endpoint intersection
	    xingMap.get(rdA).add(new StreetXing(rdA.getP2(), rdA));
	}
	return xingMap;
    }

    /**
     * @param _xingList
     * @param _str
     * @return
     */
    private boolean xingExists(List<StreetXing> _xingList, Road _str) {
	boolean found = false;
	for (StreetXing x : _xingList) {
	    if (x.getRoads().contains(_str)) {
		found = true;
		break;
	    }
	}
	return found;
    }

    /**
     * @param streetNode
     * @return
     */
    private Street parseStreet(Node streetNode) {
	String strName = streetNode.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
	Orientation or = translateOr(streetNode.getAttributes().getNamedItem(ATTR_STR_OR).getNodeValue());

	NodeList geoNL = streetNode.getChildNodes();
	List<Point2D> pList = new LinkedList<Point2D>();
	for (int i = 0; i < geoNL.getLength(); i++) {
	    Node geoNode = geoNL.item(i);
	    if (geoNode.hasAttributes()) {
		Double x = Double.parseDouble(geoNode.getAttributes().getNamedItem(ATTR_XPOS).getNodeValue());
		Double y = Double.parseDouble(geoNode.getAttributes().getNamedItem(ATTR_YPOS).getNodeValue());
		pList.add(new Point2D.Double(x, y));
	    }
	}
	Street street = new Street(strName, pList, _logger);
	street.setOr(or);
	return street;
    }

    /**
     * Parse attributes of the {@code <connection/>} node
     * 
     * @param doc_
     * @param xingMap_
     */
    private void parseTrayectories(Document doc_) {
	NodeList trayNodes = doc_.getElementsByTagName(NODE_TRAYECTORY).item(0).getChildNodes();
	for (int i = 0; i < trayNodes.getLength(); i++) {

	    if (NODE_TRAY_ORIG.equals(trayNodes.item(i).getNodeName())) {
		parseTrayNode(trayNodes.item(i).getChildNodes(), true);

	    } else if (NODE_TRAY_DEST.equals(trayNodes.item(i).getNodeName())) {
		parseTrayNode(trayNodes.item(i).getChildNodes(), false);

	    }

	}
    }

    /**
     * Parse the trayectory nodes. <br>
     * If {@code isOrigin == true}, use the first xing of the subRoadList, i.e
     * the origing xing
     * else, use the last xing of the last subroad list idx.
     * 
     * @param childNodes_
     * @param isOrigin
     */
    private void parseTrayNode(NodeList childNodes_, boolean isOrigin) {
	for (int i = 0; i < childNodes_.getLength(); i++) {
	    Node nd = childNodes_.item(i);
	    if (NODE_STREETNODE.equals(nd.getNodeName())) {
		String name = nd.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
		Double percent = Double.parseDouble(nd.getAttributes().getNamedItem(ATTR_TRAY_PERCENT).getNodeValue());
		Road rd = _roadMap.get(name);
		if(isOrigin)
		{
		    Pair<StreetXing> pair = getGraph().getEndpoints(rd.getSubRoad(0));
		    StreetXing orig = pair.getFirst();
		    orig.setStartOdds(percent);
		    _sourceXings.add(orig);
		}else
		{
		    int lastIdx = rd.getSubRoadList().size() - 1;
		    Pair<StreetXing> pair = getGraph().getEndpoints(rd.getSubRoad(lastIdx));
		    StreetXing dest = pair.getSecond();
		    dest.setEndOdds(percent);
		    _destXings.add(dest);
		}
	    }
	}
    }

    /**
     * Parse attributes of the {@code <connection/>} node
     * 
     * @param doc_
     * @param xingMap_
     */
    private void parseTrafficLights(Document doc_) {
	NodeList tfNodeList = doc_.getElementsByTagName(NODE_TRAFFICLIGHT).item(0).getChildNodes();
	for (int i = 0; i < tfNodeList.getLength(); i++) {

	    if (NODE_TL.equals(tfNodeList.item(i).getNodeName())) {
		createTl(tfNodeList.item(i));

	    }
	}
    }

    /**
     * Finds the intersections specified in the node, and creates a traffic
     * light
     * 
     * @param childNodes_
     */
    private void createTl(Node tlNode_) {
	String from = tlNode_.getAttributes().getNamedItem(ATTR_TL_FROM).getNodeValue();
	String to = tlNode_.getAttributes().getNamedItem(ATTR_TL_TO).getNodeValue();
	int duration = Integer.parseInt(tlNode_.getAttributes().getNamedItem(ATT_TL_DUR).getNodeValue());
	double split = Double.parseDouble(tlNode_.getAttributes().getNamedItem(ATT_TL_SPLIT).getNodeValue());
	TrafficLightAgent tl = new TrafficLightAgent(duration, split, _logger);
	_tlAgents.add(tl);
	Road rdA = _roadMap.get(from);
	Road rdB = _roadMap.get(to);

	for (Road subRd : rdA.getSubRoadList()) {
	    Pair<StreetXing> pair = getGraph().getEndpoints(subRd);
	    Collection<Road> rdAlist = getGraph().getInEdges(pair.getSecond());
	    boolean found = false;

	    for (Road subRdB : rdB.getSubRoadList()) {
		if (rdAlist.contains(subRdB)) {
		    found = true;
		    break;
		}
	    }
		// If the xing has both street intersections, Set traffic light
		if (found) {
		    for (Road rd : rdAlist) {
			rd.setTL(tl.getTf(rd.getOr()));
		    }
		    break;
		}

	    }
    }

    /**
     * Translates orientation ENUM from {@link TrafficSimulation.xsd}
     * 
     * @param orString
     *            'NS' || 'EW'
     * @return
     */
    private Orientation translateOr(String orString) {
	Orientation or;
	if ("NS".equals(orString)) {
	    or = Orientation.NS;
	} else {
	    or = Orientation.EW;
	}
	return or;
    }
}
