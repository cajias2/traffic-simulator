package sim.agents.social;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.engine.Steppable;
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
public class GraphGatherer<V, E> implements Steppable {
    private static final long serialVersionUID = -1284600767084977696L;
    private final int SNAPSHOT;
    private final String OUT_FOLDR = System.getProperty("user.dir") + "/tmp";
    private final File _outDir;
    List<Graph<V, E>> _graphEvol = new ArrayList<Graph<V, E>>();

    public GraphGatherer(int snapshotSize_) {
	SNAPSHOT = snapshotSize_;
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
	SocialSim<V, E> socSim = (SocialSim<V, E>) state_;
	if (0 == socSim.schedule.getSteps() % SNAPSHOT) {
	    if (null != socSim.network && null != socSim.network) {
		writeGraph(socSim.network);
		_graphEvol.add(GraphUtils.cloneGraph(socSim.network));
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
    private void writeGraph(Graph<V, E> g_) {
	FileWriter outFileWrt;
	GraphMLWriter<V, E> gWriter = new GraphMLWriter<V, E>();
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
    public List<Graph<V, E>> getGraphEvol() {
	return _graphEvol;
    }
}