package sim.agents.social;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.AsynchronousSteppable;
import sim.graph.utils.Edge;
import edu.uci.ics.jung.graph.Graph;

public class DBWriterAgent extends AsynchronousSteppable {

    /**
     * 
     */
    private static final long serialVersionUID = 4420057740757549336L;
    private final int SNAPSHOT;

    /**
     * 
     * TODO Purpose
     * 
     * @param Number
     *            of steps to capture in each write.
     * @author biggie
     */
    public DBWriterAgent(int ss_) {
	SNAPSHOT = ss_;
    }



    /**
     * @param socSim
     */
    private void writeDeltaToDB(SocialSim<Agent, String> socSim) {
	Graph<Agent, Edge> deltaGraph = socSim._temporalNetwork;
	int id = socSim.getSimID();
	int step = (int) socSim.schedule.getSteps();


	for (Edge e : deltaGraph.getEdges()) {
	    socSim.getDBManager().addEdge(id, step, deltaGraph.getEndpoints(e).getFirst(),
		    deltaGraph.getEndpoints(e).getSecond(), e.isCreate());
	}
	socSim.getDBManager().insertEdges();
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.engine.AsynchronousSteppable#halt(boolean)
     */
    @Override
    protected void halt(boolean arg0_) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.engine.AsynchronousSteppable#run(boolean)
     */
    @Override
    protected void run(boolean arg0_) {
	@SuppressWarnings("unchecked")
	SocialSim<Agent, String> socSim = (SocialSim<Agent, String>) state;
	if (0 == (socSim.schedule.getSteps() % SNAPSHOT)) {
	    writeDeltaToDB(socSim);
	    socSim.resetTemporalNetwork();
	}

    }
}
