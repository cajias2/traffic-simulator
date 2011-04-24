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

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;

import sim.graph.social.link.FriendLink;
import social.links.SimpleFriendLink;

/**
 * TODO Purpose
 * 
 * @author biggie
 * @param <V>
 */
public class CPMCommunityFinder<V> {

    private final UndirectedGraph<Set<V>, FriendLink> _cliqueGraph;

    /**
     * TODO Purpose
     * 
     * @param
     * @author biggie
     */
    public CPMCommunityFinder(Collection<Set<V>> kCliques_) {
	_cliqueGraph = new SimpleGraph<Set<V>, FriendLink>(SimpleFriendLink.class);
	initializeCliqueGraph(kCliques_);
    }

    /**
     * TODO Purpose
     * 
     * @param k_
     *            TODO
     * @param
     * @return Collection<Set<V>>
     * @author biggie
     */
    public Collection<Set<V>> findCommunities(int k_) {

	for (Set<V> kCliqueA : _cliqueGraph.vertexSet()) {
	    for (Set<V> kCliqueB : _cliqueGraph.vertexSet()) {

		if (kCliqueA != kCliqueB && adjecent(kCliqueA, kCliqueB, k_)) {
		    try {
			_cliqueGraph.addEdge(kCliqueA, kCliqueB);
		    } catch (OutOfMemoryError e) {
			System.err.println(e.getMessage());
		    }
		}
	    }
	}

	ConnectivityInspector<Set<V>, FriendLink> conInsp = new ConnectivityInspector<Set<V>, FriendLink>(_cliqueGraph);

	return unifyConnectedComponents(conInsp.connectedSets());
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     */
    private void initializeCliqueGraph(Collection<Set<V>> kCliques_) {
	if (kCliques_ != null) {
	    for (Set<V> kClique : kCliques_) {
		_cliqueGraph.addVertex(kClique);
	    }
	}

    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return Collection<Set<V>>
     * @author biggie
     */
    private Collection<Set<V>> unifyConnectedComponents(List<Set<Set<V>>> connectedSets_) {
	Collection<Set<V>> unifiedSet = new ArrayList<Set<V>>();

	Iterator<Set<Set<V>>> comIter = connectedSets_.iterator();
	while (comIter.hasNext()) {
	    Set<Set<V>> currCom = comIter.next();
	    // Unify Community
	    unifiedSet.add(unifyCommunity(currCom));
	}
	return unifiedSet;
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return Set<V>
     * @author biggie
     */
    private Set<V> unifyCommunity(Set<Set<V>> currCom_) {
	Set<V> community = new HashSet<V>();

	for (Set<V> currClique : currCom_) {
	    community.addAll(currClique);
	}
	return community;
    }

    /**
     * Compares two sets and deems them adjecent if they share n-1 elements.
     * 
     * @param k_
     *            TODO
     * @param
     * @return boolean
     * @author biggie
     */
    private boolean adjecent(Set<V> currClq_, Set<V> nxtClq_, int k_) {
	int matched = 0;
	boolean isAdj = false;
	for (V ag : currClq_) {
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
