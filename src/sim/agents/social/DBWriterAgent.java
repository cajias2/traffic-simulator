package sim.agents.social;

import sim.agents.Agent;
import sim.app.social.SocialSimBatchRunner;
import sim.engine.SimState;
import sim.field.network.Edge;

public class DBWriterAgent extends Agent {

    /**
     * 
     */
    private static final long serialVersionUID = 4420057740757549336L;
    private final int SNAPSHOT;

    /**
     * TODO Purpose
     * 
     * @param Number
     *            of steps to capture in each write.
     * @author biggie
     */
    public DBWriterAgent(SimState state_) {
	super(state_);
	SNAPSHOT = ((SocialSimBatchRunner) state_).getSnapshotInterval();
    }

    /**
     * @param socSim
     */
    private void writeDeltaToDB(SocialSimBatchRunner<Agent, String> socSim) {
	if (_deltaGraph.getEdgeCount() > 0) {
	    socSim.getDBManager().insertNewSimStep(socSim.getSimID(), socSim.schedule.getSteps());
	    for (Object obj : _deltaGraph.getEdges()) {
		Edge e = (Edge) obj;
		socSim.getDBManager().addEdgeToBatch(socSim.getSimID(), socSim.schedule.getSteps(),
			((Agent) e.getFrom()).getID(), ((Agent) e.getTo()).getID(), (Boolean) e.getInfo());
	    }
	    socSim.getDBManager().insertEdges();
	}
    }

    @Override
    protected void beforeStep(SocialSimBatchRunner<Agent, String> state_) {
	if (0 == (state_.schedule.getSteps() % SNAPSHOT)) {
	    writeDeltaToDB(state_);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#afterStep(sim.app.social.SocialSimBatchRunner)
     */
    @Override
    protected void afterStep(SocialSimBatchRunner<Agent, String> state_) {
	resetDeltaGraph();
    }
}
