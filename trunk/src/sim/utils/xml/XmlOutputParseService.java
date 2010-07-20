/**
 * 
 */
package sim.utils.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sim.geo.Road;
import sim.geo.StreetXing;
import sim.utils.xml.data.OutputSection;
import sim.utils.xml.data.SectionStartComparator;
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

    public List<OutputSection> getSectionStartSeries(String sectionName) {
	List<OutputSection> sectionList = new ArrayList<OutputSection>();
	NodeList nl = _doc.getElementsByTagName(NODE_SECTION);

	for(int i = 0; i< nl.getLength(); i++)
	{
	    Node nd = nl.item(i);
	    String name = nd.getAttributes().getNamedItem("name").getNodeValue();
	    double start = Double.parseDouble(nd.getAttributes().getNamedItem("start").getNodeValue());
	    double end = Double.parseDouble(nd.getAttributes().getNamedItem("end").getNodeValue());
	    double speed = Double.parseDouble(nd.getAttributes().getNamedItem("speed").getNodeValue());
	    OutputSection os = new OutputSection(name, start, end, speed);
	    sectionList.add(os);
	}
	Collections.sort(sectionList, new SectionStartComparator());
	System.out.println("######## Name: " + sectionName);
	for (OutputSection sect : sectionList) {
	    System.out.println(sect.getStart() + "\t" + sect.getSpeed());
	}
	return sectionList;
    }
}
