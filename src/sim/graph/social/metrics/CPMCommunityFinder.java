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

/**
 * @author biggie
 * 
 */
public class CPMCommunityFinder<T> {

    private final UndirectedGraph<Set<T>, Boolean> _cliqueGraph;

    /**
     * 
     * @author biggie CPMCommunityFinder
     */
    public CPMCommunityFinder(Collection<Set<T>> kCliques_) {
	EdgeFactory<Set<T>, Boolean> edge = new EdgeFactory<Set<T>, Boolean>() {

	    @Override
	    public Boolean createEdge(Set<T> sourceVertex_, Set<T> targetVertex_) {
		return true;
	    }
	};
	_cliqueGraph = new SimpleGraph<Set<T>, Boolean>(edge);
	initializeCliqueGraph(kCliques_);
    }

    /**
     * @author biggie
     * @name initializeCliqueGraph Purpose TODO
     * 
     * @param
     * @return void
     */
    private void initializeCliqueGraph(Collection<Set<T>> kCliques_) {
	if (kCliques_ != null) {
	    for (Set<T> kClique : kCliques_) {
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
    public Collection<Set<T>> findCommunities(int k_) {

	for (Set<T> kCliqueA : _cliqueGraph.vertexSet()) {
	    for (Set<T> kCliqueB : _cliqueGraph.vertexSet()) {
		
		if (kCliqueA != kCliqueB && adjecent(kCliqueA, kCliqueB, k_)) {
			_cliqueGraph.addEdge(kCliqueA, kCliqueB);
		    }
		}
	    }

	ConnectivityInspector<Set<T>, Boolean> conInsp = new ConnectivityInspector<Set<T>, Boolean>(
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
    private Collection<Set<T>> unifyConnectedComponents(List<Set<Set<T>>> connectedSets_) {
	Collection<Set<T>> unifiedSet = new ArrayList<Set<T>>();
	
	Iterator<Set<Set<T>>> comIter = connectedSets_.iterator();
	while(comIter.hasNext()){
	    Set<Set<T>> currCom = comIter.next();
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
    private Set<T> unifyCommunity(Set<Set<T>> currCom_) {
	Set<T> community = new HashSet<T>();

	for (Set<T> currClique : currCom_) {
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
    private boolean adjecent(Set<T> currClq_, Set<T> nxtClq_, int k_) {

	int matched = 0;
	boolean isAdj = false;
	for (T ag : currClq_) {
	    if (nxtClq_.contains(ag)) {
		matched++;
		if (matched == (k_ - 1)) {
		    isAdj = true;
		    break;
		}
	    }
	}
	return isAdj;
    }

}
