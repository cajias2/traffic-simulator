/**
 * 
 */
package sim.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sim.app.road.Road;
import sim.app.road.StreetXing;
import sim.app.utils.Orientation;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 */
public class XmlParseService {
    /*
     * Begin constant declaration. These constants should match refs in
     * TrafficSimulation.xsd
     */
    final static String NODE_SIM = "simulation";
    final static String NODE_XINGS = "crossing";
    final static String NODE_CONS = "connection";

    final static String ATTR_SIM_MAXCARS = "maxCars";
    final static String ATTR_NAME = "name";
    final static String ATTR_XING_HASTF = "hasTrafficLight";
    final static String ATTR_XING_START_ODDS = "startingOdds";
    final static String ATTR_XING_END_ODDS = "endingOdds";
    final static String ATTR_CONN_FROM = "from";
    final static String ATTR_CONN_TO = "to";
    final static String ATTR_CONN_OR = "orientation";
    final static String ATTR_CONN_LEN = "length";
    final static String ATTR_CONN_DIR = "dir";
    final static String ONE_WAY = "1way";
    final static String TWO_WAY = "2way";

    private static Logger _logger;
    private final String _fileName;
    private final Graph<StreetXing, Road> _g = new DirectedSparseGraph<StreetXing, Road>();
    private final List<StreetXing> _sourceXings = new LinkedList<StreetXing>();
    private final List<StreetXing> _destXings = new LinkedList<StreetXing>();
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
     * Creates a graph based on the xml file.
     */
    private void createGraph() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;

	try {
	    db = dbf.newDocumentBuilder();
	    Document doc = db.parse(_fileName);
	    doc.getDocumentElement().normalize();
	    Map<String, StreetXing> xingMap = new HashMap<String, StreetXing>();
	    parseSim(doc);
	    parseConnections(_g, doc, xingMap);
	    // parseXings( _g, doc, xingMap );

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
	    String simName = simNode.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
	    _maxCarsInSim = Integer.parseInt(simNode.getAttributes().getNamedItem(ATTR_SIM_MAXCARS).getNodeValue());
	}

    }

    /**
     * Parse attributes of the {@code crossing} node
     * 
     * @param g_
     * @param doc_
     * @param xingMap_
     */
    // private void parseXings ( Graph<StreetXing, Road> g_, Document doc_,
    // Map<String, StreetXing> xingMap_ )
    // {
    // NodeList xingNodes = doc_.getElementsByTagName( NODE_XINGS );
    // for ( int i = 0; i < xingNodes.getLength(); i++ )
    // {
    // Node xingNode = xingNodes.item( i );
    // String xingName = xingNode.getAttributes().getNamedItem( ATTR_NAME
    // ).getNodeValue();
    // StreetXing xing = new StreetXing( xingName);
    //            
    // Node tfNode = xingNode.getAttributes().getNamedItem( ATTR_XING_HASTF );
    // if ( null != tfNode
    // && ( ( null != tfNode.getNodeValue() || !"".equals( tfNode.getNodeValue()
    // ) ) && true == Boolean
    // .parseBoolean( tfNode.getNodeValue() ) ) )
    // {
    // xing.setTrafficLight( new TrafficLight(_logger) );
    // }
    //            
    // Node startNode = xingNode.getAttributes().getNamedItem(
    // ATTR_XING_START_ODDS );
    // if ( null != startNode && ( null != startNode.getNodeValue() ||
    // !"".equals( startNode.getNodeValue() ) ) )
    // {
    // double startOdds = Double.parseDouble( startNode.getNodeValue() );
    // xing.setStartOdds( startOdds );
    // _sourceXings.add( xing );
    // }
    // Node endNode = xingNode.getAttributes().getNamedItem( ATTR_XING_END_ODDS
    // );
    // if ( null != endNode && ( null != endNode.getNodeValue() || !"".equals(
    // endNode.getNodeValue() ) ) )
    // {
    // double endOdds = Double.parseDouble( endNode.getNodeValue() );
    // xing.setEndOdds( endOdds );
    // _destXings.add( xing );
    // }
    // xingMap_.put( xingName, xing );
    // // Finally, add the vertex.
    // g_.addVertex( xing );
    // }
    // }
    //    
    /**
     * Parse attributes of the {@code connection} node TODO fix this shitty
     * logic
     * 
     * @param g_
     * @param doc_
     * @param xingMap_
     */
    private void parseConnections(Graph<StreetXing, Road> g_, Document doc_, Map<String, StreetXing> xingMap_) {
	NodeList connNodes = doc_.getElementsByTagName(NODE_CONS);
	for (int i = 0; i < connNodes.getLength(); i++) {
	    Node connNode = connNodes.item(i);
	    String fromXing = connNode.getAttributes().getNamedItem(ATTR_CONN_FROM).getNodeValue();
	    String toXing = connNode.getAttributes().getNamedItem(ATTR_CONN_TO).getNodeValue();
	    Orientation or = translateOr(connNode.getAttributes().getNamedItem(ATTR_CONN_OR).getNodeValue());
	    String dir = connNode.getAttributes().getNamedItem(ATTR_CONN_DIR).getNodeValue();
	    double length = Double.parseDouble(connNode.getAttributes().getNamedItem(ATTR_CONN_LEN).getNodeValue());

	    // LinkedList<Point2D> pointList =
	    // (LinkedList<Point2D>)getPointList();
	    // Road street = new Street("test", pointList, _pDisplay, _logger );
	    // StreetXing startXing = new StreetXing(street.ID+"_start",
	    // street.startLoc());
	    // StreetXing endXing = new StreetXing(street.ID+"_end",
	    // street.endLoc());
	    // startXing.setStartOdds(100);
	    // _sourceXings.add(startXing);
	    // endXing.setEndOdds(100);
	    // _destXings.add(endXing);
	    // Pair<StreetXing> vertexCollection = new Pair<StreetXing>(
	    // startXing, endXing );
	    //            
	    // // Finally, add the edge
	    // g_.addEdge( street, vertexCollection );
	    // // Add edge other way around for 2way
	    // if(TWO_WAY.equals( dir ))
	    // {
	    // Pair<StreetXing> edge2 = new Pair<StreetXing>( endXing, startXing
	    // );
	    // Iterator<Point2D> iter =
	    // (Iterator<Point2D>)pointList.descendingIterator();
	    // LinkedList<Point2D> reversePointList = new LinkedList<Point2D>();
	    // while(iter.hasNext())
	    // {
	    // reversePointList.add(iter.next());
	    // }
	    // Street street2 = new Street( "test",reversePointList, _pDisplay,
	    // _logger );
	    // // Finally, add the edge
	    // g_.addEdge( street2, edge2 );
	    // }
	}
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
     * Translates orientation ENUM from {@link TrafficSimulation.xsd}
     * 
     * @param orString
     *            'NS' || 'EW'
     * @return
     */
    private Orientation translateOr(String orString) {
	Orientation or;
	if ("NS".equals(orString)) {
	    or = Orientation.NORTH_SOUTH;
	} else {
	    or = Orientation.EAST_WEST;
	}
	return or;
    }

    /**
     * Return the max number of cars defined by the user.
     * 
     * @return
     */
    public int getMaxCars() {
	return _maxCarsInSim;
    }

    public List<StreetXing> getSourceXings() {
	return _sourceXings;
    }

    public List<StreetXing> getDestXings() {
	return _destXings;
    }

}
