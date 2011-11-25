package sim.agents.social;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sim.agents.Agent;
import sim.app.social.SocialSimBatchRunner;
import sim.engine.SimState;
import sim.graph.utils.GraphUtils;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * Kills the simulation after <code>SIM_TIME</code> steps
 * 
 * @author biggie
 * @param <V>
 * @param <E>
 */
public class GraphGatherer extends Agent {
    private static final long serialVersionUID = -1284600767084977696L;
    private final String OUT_FOLDR = System.getProperty("user.dir") + "/tmp";
    private final File _outDir;
    List<Graph<Agent, String>> _graphEvol = new ArrayList<Graph<Agent, String>>();

    public GraphGatherer(SimState state_) {
	super(state_);
	_outDir = new File(OUT_FOLDR);
	if (_outDir.exists()) {
	    _outDir.delete();
	}
	_outDir.mkdir();
    }


        /**
     * 
     */
    @Override
    public void step(SimState state_) {
	@SuppressWarnings("unchecked")
	SocialSimBatchRunner<Agent, String> socSim = (SocialSimBatchRunner<Agent, String>) state_;
	if (0 == socSim.schedule.getSteps() % SNAPSHOT) {
	    if (null != getSocGraph() && null != getSocGraph()) {
		writeGraph(getSocGraph());
		_graphEvol.add(GraphUtils.cloneGraph(getSocGraph()));
	    } else {
		writeGraph(null);
		_graphEvol.add(null);
	    }

	}
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author biggie
     */
    private void writeGraph(Graph<Agent, String> g_) {
	FileWriter outFileWrt;
	GraphMLWriter<Agent, String> gWriter = new GraphMLWriter<Agent, String>();
	try {
	    outFileWrt = new FileWriter(OUT_FOLDR + System.getProperty("file.separator") + System.currentTimeMillis()
		    + ".xml");
	    BufferedWriter outWrt;
	    outWrt = new BufferedWriter(outFileWrt);
	    gWriter.save(g_, outWrt);
	    outWrt.close();
	} catch (IOException e) {

	}
    }

    /**
     * @return the graphEvol
     * @author biggie
     */
    public List<Graph<Agent, String>> getGraphEvol() {
	return _graphEvol;
    }
}