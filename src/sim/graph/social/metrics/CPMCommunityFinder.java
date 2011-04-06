/**
 * 
 */
package sim.graph.social.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;

import sim.agents.Agent;

/**
 * @author biggie
 * 
 */
public class CPMCommunityFinder {

    private final UndirectedGraph<Set<Agent>, Boolean> _cliqueGraph;

    /**
     * 
     * @author biggie CPMCommunityFinder
     */
    public CPMCommunityFinder(Collection<Set<Agent>> kCliques_) {
	EdgeFactory<Set<Agent>, Boolean> edge = new EdgeFactory<Set<Agent>, Boolean>() {

	    @Override
	    public Boolean createEdge(Set<Agent> sourceVertex_, Set<Agent> targetVertex_) {
		return true;
	    }
	};
	_cliqueGraph = new SimpleGraph<Set<Agent>, Boolean>(edge);
	initializeCliqueGraph(kCliques_);
    }

    /**
     * @author biggie
     * @name initializeCliqueGraph Purpose TODO
     * 
     * @param
     * @return void
     */
    private void initializeCliqueGraph(Collection<Set<Agent>> kCliques_) {
	if (kCliques_ != null) {
	for (Set<Agent> kClique : kCliques_) {
	    _cliqueGraph.addVertex(kClique);
	    }
	}

    }

    /**
     * 
     * @author biggie
     * @name getCPMCommunities Purpose TODO
     * 
     * @param
     * @return Collection<Set<Agent>>
     */
    public Collection<Set<Agent>> findCommunities() {

	for (Set<Agent> kCliqueA : _cliqueGraph.vertexSet()) {
	    for (Set<Agent> kCliqueB : _cliqueGraph.vertexSet()) {		
		
		if (kCliqueA != kCliqueB && adjecent(kCliqueA, kCliqueB)) {
			_cliqueGraph.addEdge(kCliqueA, kCliqueB);
		    }
		}
	    }

	ConnectivityInspector<Set<Agent>, Boolean> conInsp = new ConnectivityInspector<Set<Agent>, Boolean>(
		_cliqueGraph);

	return unifyConnectedComponents(conInsp.connectedSets());
    }

    /**
     * @author biggie
     * @name unifyConnectedComponents Purpose TODO
     * 
     * @param
     * @return Collection<Set<Agent>>
     */
    private Collection<Set<Agent>> unifyConnectedComponents(List<Set<Set<Agent>>> connectedSets_) {
	Collection<Set<Agent>> unifiedSet = new ArrayList<Set<Agent>>();
	
	Iterator<Set<Set<Agent>>> comIter = connectedSets_.iterator();
	while(comIter.hasNext()){
	    Set<Set<Agent>> currCom = comIter.next();
	    //Unify Community
	    unifiedSet.add(unifyCommunity(currCom));
	}
	return unifiedSet;
    }

    /**
     * @author biggie
     * @name unifyCommunity Purpose TODO
     * 
     * @param
     * @return Set<Agent>
     */
    private Set<Agent> unifyCommunity(Set<Set<Agent>> currCom_) {
	Set<Agent> community = new HashSet<Agent>();

	for (Set<Agent> currClique : currCom_) {
	    community.addAll(currClique);
	}
	return community;
    }

    /**
     * @author biggie
     * @name adjecent Purpose Compares two sets and deems them adjecent if they
     *       share n-1 elements.
     * 
     * @param
     * @return boolean
     */
    private boolean adjecent(Set<Agent> currClq_, Set<Agent> nxtClq_) {
	int k = currClq_.size();
	int matched = 0;
	boolean isAdj = false;
	for (Agent ag : currClq_) {
	    if (nxtClq_.contains(ag)) {
		matched++;
		if (matched == (k - 1)) {
		    isAdj = true;
		    break;
		}
	    }
	}
	return isAdj;
    }

}
