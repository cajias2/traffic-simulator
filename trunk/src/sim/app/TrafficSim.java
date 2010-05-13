package sim.app;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import processing.core.PApplet;
import sim.app.agents.Agent;
import sim.app.agents.display.DisplayableAgent;
import sim.app.agents.display.vehicle.Vehicle;
import sim.app.geo.Road;
import sim.app.geo.StreetXing;
import sim.app.processing.Displayable;
import sim.app.xml.XmlParseService;
import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public abstract class TrafficSim {

    public static int MAX_CAR_COUNT = 100;
    protected Graph<StreetXing, Road> _city = new DirectedSparseGraph<StreetXing, Road>();
    protected List<StreetXing> _sourceXings = new LinkedList<StreetXing>();
    protected List<StreetXing> _destXings = new LinkedList<StreetXing>();

    /**
     * 
     * @param cityDir
     */
    @Deprecated
    protected void parseGraph(String cityDir) {
	XmlParseService parsedGraph = new XmlParseService(System.getProperty("user.dir") + cityDir, getLogger());
	_city = parsedGraph.getGraph();
	MAX_CAR_COUNT = parsedGraph.getMaxCars();
	_sourceXings = parsedGraph.getSourceXings();
	_destXings = parsedGraph.getDestXings();

    }

    /**
     * Display all displayable elements
     * 
     * @throws Exception
     * 
     */
    public void display() {

	for (Displayable disp : getRoads()) {
	    disp.display();
	}
	// Display Agents
	Iterator<Agent> iter = getAgents().iterator();
	while (iter.hasNext()) {
	    Agent agent = iter.next();
	    if (agent instanceof DisplayableAgent) {
		((DisplayableAgent) agent).display();
	    }
	}
    }

    /**
     * Update Agents
     */
    public void update() {
	Iterator<Agent> iter = getAgents().iterator();
	while (iter.hasNext()) {
	    Agent agent = iter.next();
	    // If we're dealing with a car, remove it if it's dead.
	    if (agent instanceof Vehicle && !((Vehicle) agent).isAlive()) {
		iter.remove();
	    } else {
		agent.move();
	    }
	}
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getFrameRate();

    /**
     * 
     * @return
     */
    protected StreetXing getSource() {
	StreetXing pickedXing = null;
	Random rand = new Random(System.currentTimeMillis());
	int targetIndex = rand.nextInt(100);
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
     * Return a random car begining based on the {@code startingOdds} attribute
     * in the city xml.
     * <p/>
     * See {@link TraffiSimulation.xsd} for more info
     * 
     * @return
     */
    protected StreetXing getDest() {
	StreetXing pickedXing = null;
	Random rand = new Random(System.currentTimeMillis());
	int targetIndex = rand.nextInt(100);
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

    protected abstract Logger getLogger();

    protected abstract PApplet getApplet();

    /**
     * Implement a time series to determine wether to create add a new vehicle
     * in time T
     */
    protected abstract boolean addVhclP();

    /**
     * Create all the geo objs needed to describe the city
     */
    protected abstract void generateCity();

    protected abstract List<Agent> getAgents();

    protected abstract List<Road> getRoads();
}
