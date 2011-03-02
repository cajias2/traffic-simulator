/**
 * 
 */
package sim.utils.xml.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sim.graph.traffic.Road;
import sim.graph.traffic.StreetXing;
import sim.utils.xml.data.OutputSection;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
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

    public Map<String, List<OutputSection>> getSectionStartSeries() {
	Map<String, List<OutputSection>> sectionMap = new HashMap<String, List<OutputSection>>();
	NodeList nl = _doc.getElementsByTagName(NODE_SECTION);

	for (int i = 0; i < nl.getLength(); i++) {
	    Node nd = nl.item(i);
	    String name = nd.getAttributes().getNamedItem("name").getNodeValue();
	    if (!sectionMap.containsKey(name))
 {
		sectionMap.put(name, new ArrayList<OutputSection>());
	    }
	    double start = Double.parseDouble(nd.getAttributes().getNamedItem("start").getNodeValue());
	    double end = Double.parseDouble(nd.getAttributes().getNamedItem("end").getNodeValue());
	    double speed = Double.parseDouble(nd.getAttributes().getNamedItem("speed").getNodeValue());
	    OutputSection os = new OutputSection(name, start, end, speed);
	    sectionMap.get(name).add(os);
	}

	return sectionMap;
    }
}
