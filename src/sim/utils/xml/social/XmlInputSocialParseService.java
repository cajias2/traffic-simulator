/**
 * 
 */
package sim.utils.xml.social;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

import sim.agents.Agent;
import sim.graph.social.edge.FriendLink;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 */
public class XmlInputSocialParseService {
    /**
     * 
     */
    private static final String ATTR_AG_CLASS = "class";
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
    private static final String ATT_AG_PCNT = "percent";
    private static final String TYPE_ACTOR = "actor";
    private static final String ATTR_TYPE = "type";

    private static Logger _logger;
    private final String _fileName;
    private final Graph<Agent, FriendLink> _g = new DirectedSparseGraph<Agent, FriendLink>();
    private final List<Agent> _agents = new LinkedList<Agent>();
    private int _agentNum;
    private int _simDuration;

    /**
     * Class Constructor
     * 
     * @param fileName_
     */
    public XmlInputSocialParseService(String fileName_, Logger logger_) {
	_fileName = fileName_;
	_logger = logger_;
    }

    /**
     * Return the graph generated from the xml file passed in constructor.
     * 
     * @return
     */
    public Graph<Agent, FriendLink> getGraph() {
	return _g;
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
	return _simDuration;
    }

    public List<Agent> getTlAgents() {
	return _agents;
    }



    /**
     * Creates a graph based on the xml file.
     */
    public Graph<Agent, FriendLink> createGraph() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;

	try {
	    db = dbf.newDocumentBuilder();
	    Document doc = db.parse(_fileName);
	    doc.getDocumentElement().normalize();
	    parseSim(doc);
	    parseAgents(doc);

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
	return _g;
    }

    /**
     * Parse attributes of the {@code simulation} node
     * 
     * @param doc_
     */
    private void parseSim(Document doc_) {
	NodeList simNodes = doc_.getElementsByTagName(NODE_SIM);
	for (int i = 0; i < simNodes.getLength(); i++) {
	    Node simNode = simNodes.item(i);
	    _agentNum = Integer.parseInt(simNode.getAttributes().getNamedItem(ATTR_SIM_AGENT_NUM).getNodeValue());
	    _simDuration = Integer.parseInt(simNode.getAttributes().getNamedItem(ATTR_SIM_DURATION).getNodeValue());
	}
    }

    /**
     * Parse attributes of the {@code <agents/>} node
     * 
     * @param doc_
     */
    private Map<String, Object[]> parseAgents(Document doc_) {
	NodeList agentList = doc_.getElementsByTagName(NODE_AGENTS).item(0).getChildNodes();
	Map<String, Object[]> classMap = new HashMap<String, Object[]>();
	for (int i = 0; i < agentList.getLength(); i++) {

	    if (NODE_AGENT.equals(agentList.item(i).getNodeName())) {
		Node agent = agentList.item(i);
		boolean isActor = TYPE_ACTOR.equals(agent.getAttributes().getNamedItem(ATTR_TYPE).getNodeValue());
		double pcnt = -1;		
		if(isActor){
		  pcnt = Double.parseDouble(agent.getAttributes().getNamedItem(ATT_AG_PCNT).getNodeValue());
		}
		String className = agent.getAttributes().getNamedItem(ATTR_AG_CLASS).getNodeValue();
		classMap.put(className, new Object[] { isActor, pcnt });
	    }
	}
	return classMap;
    }

    /**
     * Creates a new Agent object
     * 
     * @param childNodes_
     */
    private void createAgent(String className_, double split_) {
	Object[] agentArgs = new Object[] { split_, _logger };
	Class<?>[] agentArgsClass = new Class[] { int.class, double.class, Logger.class };
	Agent agent = null;
	try {
	    agent = instantiateAgentObj(className_, agentArgs, agentArgsClass);
	} catch (Exception e) {

	    System.err.print(e.getMessage());
	    e.printStackTrace();
	    System.exit(-1);
	}
	_agents.add(agent);

    }


    /**
     * @param className_
     * @param agentArgs_
     * @param agentArgsClass_
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Agent instantiateAgentObj(String className_, Object[] agentArgs_, Class[] agentArgsClass_)
	    throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
	    InstantiationException, IllegalAccessException, InvocationTargetException {
	Class<?> agClazz = Class.forName(className_);
	Constructor<?> cons = agClazz.getConstructor(agentArgsClass_);

	Object obj = cons.newInstance(agentArgs_);
	assert obj instanceof Agent;
	return (Agent) obj;
    }
}
