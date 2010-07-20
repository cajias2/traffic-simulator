/**
 * 
 */
package sim.utils.xml;

import org.w3c.dom.Document;

import sim.geo.Road;
import sim.geo.StreetXing;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 *
 */
public class XmlOutputParseService {

    /**
     * 
     */
    private static final String NODE_SECTION = "section";
    Document _doc;
    Graph<StreetXing, Road> _city;


    /**
     * 
     */
    public XmlOutputParseService(Document doc_, Graph<StreetXing, Road> city_) {
	_doc = doc_;
	_city = city_;
    }

    // public List<Double> getSectionTimeSeries(String sectionName)
    // {
    // List<Double> timeSer = new ArrayList<Double>();
    // List<Node> nodeList = new LinkedList<Node>();
    // for(Node nd: getElementsByTagName(NODE_SECTION);
    //
    // return timeSer;
    //
    // }
}
