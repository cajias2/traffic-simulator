/**
 * 
 */
package sim.utils.xml.social;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sim.agents.Agent;

/**
 * @author biggie
 */
public class SocialInputParseService {
    /**
     * 
     */
    private static final String ATTR_AG_CLASS = "classpath";
    /*
     * Begin constant declaration. These constants should match refs in
     * SocialSimulation.xsd
     */
    final static String NODE_SIM = "simulation";
    final static String ATTR_SIM_DURATION = "simDuration";

    final static String NODE_AGENTS = "agents";
    final static String NODE_AGENT = "agent";

    final static String ATTR_SIM_AGENT_NUM = "agents";
    final static String ATTR_NAME = "name";
    private static final String ATT_AG_PCT = "pct";
    private static final String ATTR_SIM_WIDTH = "width";
    private static final String ATTR_SIM_LEN = "length";

    private final String _filename;
    private int _agentNum;
    private int _duration;
    private int _envW;
    private int _envLen;

    /**
     * Class Constructor
     * 
     * @param fileName_
     */
    public SocialInputParseService(String fileName_, Logger logger_) {
	_filename = fileName_;
    }

    /**
     * Return the max number of cars defined by the user.
     * 
     * @return
     */
    public int getAgentNum() {
	return _agentNum;
    }

    public int getSimDuration() {
	return _duration;
    }

    public int getWidth() {
	return _envW;
    }

    public int getLen() {
	return _envLen;
    }

    /**
     * Parse attributes of the {@code simulation} node
     * 
     * @param doc_
     */
    public void parseSim() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;
	Document doc;

	try {
	    db = dbf.newDocumentBuilder();
	    doc = db.parse(_filename);
	    doc.getDocumentElement().normalize();
	    NodeList simNodes = doc.getElementsByTagName(NODE_SIM);
	    for (int i = 0; i < simNodes.getLength(); i++) {
		Node simNode = simNodes.item(i);
		_duration = Integer.parseInt(simNode.getAttributes().getNamedItem(ATTR_SIM_DURATION).getNodeValue());
		_agentNum = Integer.parseInt(simNode.getAttributes().getNamedItem(ATTR_SIM_AGENT_NUM).getNodeValue());
		_envW = Integer.parseInt(simNode.getAttributes().getNamedItem(ATTR_SIM_WIDTH).getNodeValue());
		_envLen = Integer.parseInt(simNode.getAttributes().getNamedItem(ATTR_SIM_LEN).getNodeValue());

	    }
	} catch (SAXException e) {
	    System.err.println("File: " + _filename + " Does not conform to xsd. See Simulation.xsd for details");
	    e.printStackTrace();
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("File: " + _filename + " Could not be opened.");
	    System.exit(1);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /**
     * Parse attributes of the {@code <agents/>} node
     * 
     * @param doc_
     * @throws ClassNotFoundException
     * @throws DOMException
     */
    public Map<Class<Agent>, Double> parseAgents() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;
	Document doc;
	Map<Class<Agent>, Double> classMap = new HashMap<Class<Agent>, Double>();

	try {
	    db = dbf.newDocumentBuilder();
	    doc = db.parse(_filename);
	    doc.getDocumentElement().normalize();
	    NodeList agentList = doc.getElementsByTagName(NODE_AGENTS).item(0).getChildNodes();
	    for (int i = 0; i < agentList.getLength(); i++) {
		if (NODE_AGENT.equals(agentList.item(i).getNodeName())) {
		    Node agent = agentList.item(i);
		    double pcnt = 0;
		    Class<Agent> className = (Class<Agent>) Class.forName(agent.getAttributes()
			    .getNamedItem(ATTR_AG_CLASS).getNodeValue());
		    pcnt = Double.parseDouble(agent.getAttributes().getNamedItem(ATT_AG_PCT).getNodeValue());
		    classMap.put(className, pcnt);
		}
	    }
	} catch (SAXException e) {
	    System.err.println("File: " + _filename + " Does not conform to xsd. See Simulation.xsd for details");
	    e.printStackTrace();
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("File: " + _filename + " Could not be opened.");
	    System.exit(1);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return classMap;
    }
}
